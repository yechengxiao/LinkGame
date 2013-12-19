package com.johnbear724.linkgame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.object.Piece;

public class GameView extends View{
    
    private Piece[][] map; 
    private GameService gameService;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
        this.map = gameService.getMap();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        drawMap(canvas);
    }
    
    
    private void drawMap(Canvas canvas) {
        for(int i = 0; i < gameService.getGameConfig().getRows(); i++) {
            for(int j = 0; j < gameService.getGameConfig().getColumns(); j++) {
                if(map[i][j].getImageId() != -1)
                canvas.drawBitmap(map[i][j].getBitmap(this.getResources()), map[i][j].getX(), map[i][j].getY(), null);
            }
        }
    }
    
}
