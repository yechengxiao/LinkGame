package com.johnbear724.linkgame.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import com.johnbear724.linkgame.R;
import com.johnbear724.linkgame.animation.AnimationPiece;
import com.johnbear724.linkgame.animation.LinkUpAnimation;
import com.johnbear724.linkgame.animation.RemoveAnimationPiece;
import com.johnbear724.linkgame.animation.SearchAnimation;
import com.johnbear724.linkgame.core.GameConfig;
import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.object.LinkInfo;
import com.johnbear724.linkgame.object.Piece;
import com.johnbear724.linkgame.sound.GameSound;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
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
    private Paint paint;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.isStart = false;
        this.selector = BitmapFactory.decodeResource(this.getResources(), R.drawable.selector);
        this.selectedPiece = null;
        this.paint = new Paint();
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
        if(!isStart) {
            return false;
        }
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
    
    public void setGame(GameService gameService, GameSound gameSound, Handler handler) {
        this.gameService = gameService;
        this.gameSound = gameSound;
        this.handler = handler;
        this.map = gameService.getMap();
    }
    
    public void startGame() {
        isStart = true;
        if(!gameSound.getPlayer().isPlaying()) {
            gameSound.getPlayer().start();
        }
        handler.sendEmptyMessage(GameConfig.START_TEXT_INVISIBLE);
        map = gameService.createMap();
        selectedPiece = null;
        startAnimator();
    }
    
    private void drawMap(Canvas canvas) {
        for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
            for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                if(map[i][j].getImageId() != -1) {
                    canvas.drawBitmap(map[i][j].getBitmap(), map[i][j].getX(), map[i][j].getY(), paint);
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
                linkUpAni.setAlpha(currentAlpha);
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
                    handler.sendEmptyMessage(GameConfig.Victory);
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
        if(countStreamID != 0) {
            gameSound.stop(countStreamID);
            countStreamID = 0;
        }
        this.isStart = false;
        aniList.clear();
        invalidate();
    }
    
    public void score(int size) {
        switch(size) {
        case 2:
            gameSound.play(GameSound.COMB_1, 1, 1, 0, 0, 1);
            handler.sendEmptyMessage(GameConfig.SCOUR_1);
            break;
        case 3:
            gameSound.play(GameSound.COMB_3, 1, 1, 0, 0, 1);
            handler.sendEmptyMessage(GameConfig.SCOUR_2);
            break;
        case 4:
            gameSound.play(GameSound.COMB_5, 1, 1, 0, 0, 1);
            handler.sendEmptyMessage(GameConfig.SCOUR_3);
            break;
        }
    }
    
    public void refresh() {
        aniList.clear();
        ValueAnimator fadingAni = ObjectAnimator.ofInt(this, "paintAlpha", 255, 0);
        fadingAni.addListener(new AnimatorListener() {
            
            @Override
            public void onAnimationStart(Animator arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationRepeat(Animator arg0) {
                // TODO Auto-generated method stub
                mapShuffle();
            }
            
            @Override
            public void onAnimationEnd(Animator arg0) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void onAnimationCancel(Animator arg0) {
                // TODO Auto-generated method stub
                
            }
        });
        fadingAni.setDuration(300);
        fadingAni.setRepeatMode(Animation.REVERSE);
        fadingAni.setRepeatCount(1);
        fadingAni.start();
    }
    
    public void setPaintAlpha(int a) {
        paint.setAlpha(a);
        invalidate();
    }
    
    public void mapShuffle() {
        List<Integer> mapList = new ArrayList<Integer> ();
        for(int i = 0; i < map.length; i ++) {
            for(int j = 0; j < map[0].length; j++) {
                mapList.add(map[i][j].getImageId());
            }
        }
        Collections.shuffle(mapList);
        for(int i = 0; i < map.length; i ++) {
            for(int j = 0; j < map[0].length; j++) {
                map[i][j].setImage(mapList.get(i * map[0].length + j), getResources());
            }
        }
    }
    
    public void search() {
        final LinkInfo linkI = gameService.findLinkablePiece();
        if(linkI != null) {
            final SearchAnimation sAni = new SearchAnimation();
            sAni.setPList(linkI.getFindPoint(map));
            sAni.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hint));
            ValueAnimator ani = ObjectAnimator.ofFloat(sAni, "scale", 0.7f, 1.2f);
            ani.setDuration(400);
            ani.addListener(new AnimatorListener() {
                
                @Override
                public void onAnimationStart(Animator arg0) {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void onAnimationRepeat(Animator arg0) {
                    // TODO Auto-generated method stub
                    if(map[linkI.getPointOne().x][linkI.getPointOne().y].getImageId() == -1) {
                        aniList.remove(sAni);
                    }
                }
                
                @Override
                public void onAnimationEnd(Animator arg0) {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void onAnimationCancel(Animator arg0) {
                    // TODO Auto-generated method stub
                    
                }
            });
            ani.addUpdateListener(new AnimatorUpdateListener() {
                
                @Override
                public void onAnimationUpdate(ValueAnimator arg0) {
                    // TODO Auto-generated method stub
                    postInvalidate();
                }
            });
            ani.setRepeatMode(Animation.REVERSE);
            ani.setRepeatCount(Animation.INFINITE);
            aniList.add(sAni);
            ani.start();
            invalidate();
        }
    }
    
}
