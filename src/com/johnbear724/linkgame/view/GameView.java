package com.johnbear724.linkgame.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.object.Piece;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class GameView extends View implements AnimatorUpdateListener{
    
    private Piece[][] map; 
    private GameService gameService;
    private boolean isNew;
    private boolean isStart;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.isNew = true;
        this.isStart = false;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
        this.map = gameService.getMap();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if(!isStart) return;
        super.onDraw(canvas);
        drawMap(canvas);
    }
    
    public void startGame() {
        isStart = true;
        startAnimator();
    }
    
    private void drawMap(Canvas canvas) {
        for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
            for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                if(map[i][j].getImageId() != -1)
                canvas.drawBitmap(map[i][j].getBitmap(this.getResources()), map[i][j].getX(), map[i][j].getY(), null);
            }
        }
    }
    
    private void startAnimator() {
        AnimatorSet aniSet = new AnimatorSet();
        List<Animator> aniList = new ArrayList<Animator> (); 
        int duration = 50;
        int distance = -50;
        for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
            for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                ValueAnimator ani = ObjectAnimator.ofFloat(map[i][j], "y", distance, map[i][j].getY());
                ani.setDuration(duration);
                ani.setInterpolator(new LinearInterpolator());
//                ani.addUpdateListener(this);
                aniList.add(ani);
                duration += 50;
            }
            distance += -100;
        }
        ValueAnimator updateAni = ValueAnimator.ofFloat(0, 1);
        updateAni.addUpdateListener(this);
        updateAni.setDuration(duration);
        aniList.add(updateAni);
        aniSet.playTogether(aniList);
        aniSet.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator arg0) {
        // TODO Auto-generated method stub
        postInvalidate();
    }
}
