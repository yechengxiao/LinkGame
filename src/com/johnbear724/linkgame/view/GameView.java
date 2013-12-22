package com.johnbear724.linkgame.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.johnbear724.linkgame.R;
import com.johnbear724.linkgame.animation.AnimationPiece;
import com.johnbear724.linkgame.animation.RemoveAnimationPiece;
import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.object.Piece;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class GameView extends View {
    
    private Piece[][] map; 
    private Piece selectedPiece;
    private GameService gameService;
    private boolean isStart;
    private Bitmap selector;
    private List<AnimationPiece> aniList = new ArrayList<AnimationPiece> ();

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
        } else {
            linkUpAnimation(selectedPiece, compared);
            selectedPiece.setImage(-1, getResources());
            compared.setImage(-1, getResources());
            selectedPiece = null;
        }
        postInvalidate();
        return true;
    }
    
    public void setGameService(GameService gameService) {
        this.gameService = gameService;
        this.map = gameService.getMap();
    }
    
    public void startGame() {
        isStart = true;
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

    public void linkUpAnimation(Piece one, Piece two) {
        final RemoveAnimationPiece removeAP1 = new RemoveAnimationPiece(one);
        final RemoveAnimationPiece removeAP2 = new RemoveAnimationPiece(two);
        aniList.add(removeAP1);
        aniList.add(removeAP2);
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
                
            }
            
            @Override
            public void onAnimationCancel(Animator arg0) {
                // TODO Auto-generated method stub
                aniList.remove(removeAP1);
                aniList.remove(removeAP2);
            }
        });
        ani.setDuration(500);
        ani.start();
    }
}
