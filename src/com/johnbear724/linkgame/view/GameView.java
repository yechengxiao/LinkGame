package com.johnbear724.linkgame.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.johnbear724.linkgame.R;
import com.johnbear724.linkgame.animation.AnimationPiece;
import com.johnbear724.linkgame.animation.LinkUpAnimation;
import com.johnbear724.linkgame.animation.RemoveAnimationPiece;
import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.object.Piece;
import com.johnbear724.linkgame.sound.GameSound;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class GameView extends View {
    
    private Piece[][] map; 
    private Piece selectedPiece;
    private GameService gameService;
    private Bitmap selector;
    private List<AnimationPiece> aniList = new ArrayList<AnimationPiece> ();
    private Handler handler;
    private boolean isStart;
    private GameSound gameSound;
    private int countStreamID;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.isStart = false;
        this.selector = BitmapFactory.decodeResource(this.getResources(), R.drawable.selector);
        this.selectedPiece = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if(!isStart) return;
        super.onDraw(canvas);
        drawMap(canvas);
        if(selectedPiece != null) {
            canvas.drawBitmap(selector, selectedPiece.getX(), selectedPiece.getY(), null);
        }
        if(!aniList.isEmpty()) {
            for(AnimationPiece aniPiece : aniList) {
                aniPiece.drawAnimation(canvas);
            }
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_MOVE) {
            return false;
        }
        Piece compared = gameService.checkSelected(event.getX(), event.getY());
        if(compared.getImageId() == -1 || compared == selectedPiece) {
            return true;
        }
        if(selectedPiece == null || compared.getImageId() != selectedPiece.getImageId()) {
            selectedPiece = compared;
            gameSound.play(GameSound.CLICK, 0.7f, 0.7f, 0, 0, 1);
        } else {
            List<Point> pList = gameService.checkLinkUp(compared.getRowNum(), compared.getColumnNum(), selectedPiece.getRowNum(), selectedPiece.getColumnNum());
            if(pList != null) {
                linkUpAnimation(selectedPiece, compared, pList);
                score(pList.size());
                selectedPiece.setImage(-1, getResources());
                compared.setImage(-1, getResources());
                selectedPiece = null;
            } else {
                gameSound.play(GameSound.CLICK, 0.7f, 0.7f, 0, 0, 1);
                selectedPiece = compared;
            }
        }
        postInvalidate();
        return true;
    }
    
    public void setGameService(GameService gameService) {
        this.gameService = gameService;
        this.map = gameService.getMap();
    }
    
    public void startGame(Handler handler, GameSound gameSound) {
        isStart = true;
        this.gameSound = gameSound;
        this.handler = handler;
        gameSound.getPlayer().start();
        startAnimator();
    }
    
    public void newGame() {
        map = gameService.createMap();
        selectedPiece = null;
        startAnimator();
    }
    
    private void drawMap(Canvas canvas) {
        for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
            for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                if(map[i][j].getImageId() != -1) {
                    canvas.drawBitmap(map[i][j].getBitmap(), map[i][j].getX(), map[i][j].getY(), null);
                }
            }
        }
    }
    
    private void startAnimator() {
        gameSound.play(GameSound.REFRESH, 2, 2, 0, 0, 1.5f);
        //FIXME 当该动画还没有结束时再此调用该动画会造成冲突
        final float[][] yArray = new float[gameService.getGameConfig().getRows()][gameService.getGameConfig().getColumns()] ;
        for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
            for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                yArray[i][j] = map[i][j].getY();
            }
        };
        ValueAnimator ani = ValueAnimator.ofFloat(0, 1);
        final float distance = 64;
        final float difference =  yArray[0][0] + distance * gameService.getGameConfig().getRows() * gameService.getGameConfig().getColumns();
        ani.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                // TODO Auto-generated method stub
                float beginY = -distance * gameService.getGameConfig().getRows() * gameService.getGameConfig().getColumns() + difference * (Float)arg0.getAnimatedValue();
                int index = 0;
                boolean reverse = false;
                for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
                    for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                        int temp = j;
                        if(reverse) {
                            j = gameService.getGameConfig().getColumns() - j - 1;
                        }
                        if(map[i][j].getImageId() != -1) {
                            float location = beginY + distance * index;
                            if(location >= yArray[i][j]) {
                                location = yArray[i][j];
                            }
                            map[i][j].setY(location);
                        }
                        index ++;
                        j = temp;
                    }
                    reverse = !reverse;
                }
                postInvalidate();
            }
        });
        ani.setDuration(1000);
        ani.start();
    }

    public void linkUpAnimation(Piece one, Piece two, List<Point> pList) {
        final RemoveAnimationPiece removeAP1 = new RemoveAnimationPiece(one);
        final RemoveAnimationPiece removeAP2 = new RemoveAnimationPiece(two);
        final LinkUpAnimation linkUpAni = new LinkUpAnimation(pList);
        aniList.add(removeAP1);
        aniList.add(removeAP2);
        aniList.add(linkUpAni);
        ValueAnimator ani = ValueAnimator.ofFloat(0, 1);
        ani.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                // TODO Auto-generated method stub
                float currentValue = (Float)arg0.getAnimatedValue();
                float currentScale = 1 + currentValue * 1;
                removeAP1.setScale(currentScale, currentScale);
                removeAP2.setScale(currentScale, currentScale);
                int currentAlpha = (int) (255 - currentValue * 255);
                removeAP1.setAlpha(currentAlpha);
                removeAP2.setAlpha(currentAlpha);
                postInvalidate();
            }
        });
        ani.addListener(new AnimatorListener() {
            
            @Override
            public void onAnimationStart(Animator arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationRepeat(Animator arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationEnd(Animator arg0) {
                // TODO Auto-generated method stub
                aniList.remove(removeAP1);
                aniList.remove(removeAP2);
                aniList.remove(linkUpAni);
                if(gameService.isEmpty()) {
                    handler.sendEmptyMessage(0x123);
                }
            }
            
            @Override
            public void onAnimationCancel(Animator arg0) {
                // TODO Auto-generated method stub
            }
        });
        ani.setDuration(500);
        ani.start();
    }
    
    public boolean isStart() {
        return isStart;
    }
    
    public void countAnimation() {
        if(countStreamID == 0) {
            countStreamID = gameSound.play(GameSound.COUNT, 0.5f, 0.5f, 0, -1, 1);
        }
    }
    
    public void gameOver() {
        gameSound.stop(countStreamID);
        countStreamID = 0;
    }
    
    public void score(int size) {
        switch(size) {
        case 2:
            gameSound.play(GameSound.COMB_1, 1, 1, 0, 0, 1);
            break;
        case 3:
            gameSound.play(GameSound.COMB_3, 1, 1, 0, 0, 1);
            break;
        case 4:
            gameSound.play(GameSound.COMB_5, 1, 1, 0, 0, 1);
            break;
        }
    }
}
