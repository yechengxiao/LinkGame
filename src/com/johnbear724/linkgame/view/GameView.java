package com.johnbear724.linkgame.view;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import com.johnbear724.linkgame.MainActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 游戏的主要显示界面，主要处理棋子的绘制
 */
public class GameView extends View {
    
    private Piece[][] map; 
    private Piece selectedPiece;
    private GameService gameService;
    private Bitmap selector;
    private List<AnimationPiece> animationList = new ArrayList<AnimationPiece> ();
    private Handler handler;
    private boolean isStart;
    private GameSound gameSound;
    private int countStreamID;
    private Paint paint;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        if(!animationList.isEmpty()) {
            for(AnimationPiece aniPiece : animationList) {
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
                selectedPiece.setImageId(-1, getResources());
                compared.setImageId(-1, getResources());
                selectedPiece = null;
            } else {
                gameSound.play(GameSound.CLICK, 0.7f, 0.7f, 0, 0, 1);
                selectedPiece = compared;
            }
        }
        postInvalidate();
        return true;
    }
    
    public void setGame(GameSound gameSound, Handler handler) {
        this.gameSound = gameSound;
        this.handler = handler;
    }
    
    public void startGame() {
        if (gameService == null) {
            gameService = new GameService(new GameConfig(this));
        }
        isStart = true;
        if (!gameSound.getPlayer().isPlaying()) {
            gameSound.getPlayer().start();
        }
        handler.sendEmptyMessage(MainActivity.START_TEXT_INVISIBLE);
        map = gameService.createMap();
        selectedPiece = null;
        startAnimator();
    }
    
    private void drawMap(Canvas canvas) {
        int count = 0;
        Piece piece = null;
        for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
            for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                piece = map[i][j];
                if(map[i][j].getImageId() != -1) {
                    canvas.drawBitmap(piece.getBitmap(), piece.getX(), piece.getY(), paint);
                }
            }
        }
    }
    
    private void startAnimator() {
        gameSound.play(GameSound.REFRESH, 2, 2, 0, 0, 1.5f);
        final int rows = gameService.getGameConfig().getRows();
        final int columns = gameService.getGameConfig().getColumns();

        //FIXME 当该动画还没有结束时再此调用该动画会造成冲突
        final float[][] yArray = new float[rows][columns];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                yArray[i][j] = map[i][j].getY();
            }
        };
        ValueAnimator ani = ValueAnimator.ofFloat(0, 1);
        final float distance = 64;
        final float difference =  yArray[0][0] + distance * rows * columns;
        ani.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                float beginY = -distance * rows * columns + difference * (Float)arg0.getAnimatedValue();
                int index = 0;
                boolean reverse = false;
                for(int i = 0; i < rows; i++) {
                    for(int j = 0; j < columns; j++) {
                        int temp = j;
                        if(reverse) {
                            j = columns - j - 1;
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
        animationList.add(removeAP1);
        animationList.add(removeAP2);
        animationList.add(linkUpAni);
        ValueAnimator ani = ValueAnimator.ofFloat(0, 1);
        ani.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
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

            }
            
            @Override
            public void onAnimationRepeat(Animator arg0) {

            }
            
            @Override
            public void onAnimationEnd(Animator arg0) {
                animationList.remove(removeAP1);
                animationList.remove(removeAP2);
                animationList.remove(linkUpAni);
                if(gameService.isEmpty()) {
                    handler.sendEmptyMessage(MainActivity.Victory);
                }
            }
            
            @Override
            public void onAnimationCancel(Animator arg0) {
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
        animationList.clear();
        invalidate();
    }
    
    public void score(int size) {
        switch(size) {
        case 2:
            gameSound.play(GameSound.COMB_1, 1, 1, 0, 0, 1);
            handler.sendEmptyMessage(MainActivity.SCOUR_1);
            break;
        case 3:
            gameSound.play(GameSound.COMB_3, 1, 1, 0, 0, 1);
            handler.sendEmptyMessage(MainActivity.SCOUR_2);
            break;
        case 4:
            gameSound.play(GameSound.COMB_5, 1, 1, 0, 0, 1);
            handler.sendEmptyMessage(MainActivity.SCOUR_3);
            break;
        }
    }
    
    public void refresh() {
        animationList.clear();
        selectedPiece = null;
        gameSound.play(GameSound.SHUFFLE, 1, 1, 0, 0, 1);
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
                map[i][j].setImageId(mapList.get(i * map[0].length + j), getResources());
            }
        }
    }
    
    public void search() {
        gameSound.play(GameSound.HINT, 1, 1, 0, 0, 1);
        final LinkInfo linkInfo = gameService.findLinkablePiece();
        if(linkInfo != null) {
            final SearchAnimation searchAnimation = new SearchAnimation();
            searchAnimation.setPList(linkInfo.getFindPoint(map));
            searchAnimation.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hint));
            ValueAnimator ani = ObjectAnimator.ofFloat(searchAnimation, "scale", 0.7f, 1.2f);
            ani.setDuration(400);
            ani.addListener(new AnimatorListener() {
                
                @Override
                public void onAnimationStart(Animator arg0) {
                }
                
                @Override
                public void onAnimationRepeat(Animator arg0) {
                    if(map[linkInfo.getPointOne().x][linkInfo.getPointOne().y].getImageId() == -1) {
                        animationList.remove(searchAnimation);
                    }
                }
                
                @Override
                public void onAnimationEnd(Animator arg0) {

                }
                
                @Override
                public void onAnimationCancel(Animator arg0) {

                }
            });
            ani.addUpdateListener(new AnimatorUpdateListener() {
                
                @Override
                public void onAnimationUpdate(ValueAnimator arg0) {
                    postInvalidate();
                }
            });
            ani.setRepeatMode(Animation.REVERSE);
            ani.setRepeatCount(Animation.INFINITE);
            animationList.add(searchAnimation);
            ani.start();
            invalidate();
        }
    }
    
}
