package com.johnbear724.linkgame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.object.Piece;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class GameView extends View {
    
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
                if(map[i][j].getImageId() != -1) {
                    canvas.drawBitmap(map[i][j].getBitmap(this.getResources()), map[i][j].getX(), map[i][j].getY(), null);
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
        final float distance = 70;
        final float difference =  yArray[0][0] + distance * gameService.getGameConfig().getRows() * gameService.getGameConfig().getColumns();
        ani.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                // TODO Auto-generated method stub
                float beginY = -distance * gameService.getGameConfig().getRows() * gameService.getGameConfig().getColumns() + difference * (Float)arg0.getAnimatedValue();
                int index = 0;
                for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
                    for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                        if(map[i][j].getImageId() != -1) {
                            float location = beginY + distance * index;
                            if(location >= yArray[i][j]) {
                                location = yArray[i][j];
                            }
                            map[i][j].setY(location);
                        }
                        index ++;
                    }
                }
                postInvalidate();
            }
        });
        ani.setFrameDelay(50);
        ani.setDuration(1000);
        ani.start();
    }

}
