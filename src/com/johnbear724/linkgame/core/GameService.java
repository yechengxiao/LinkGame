package com.johnbear724.linkgame.core;

import java.util.List;

import android.util.Log;

import com.johnbear724.linkgame.object.Map;
import com.johnbear724.linkgame.object.Piece;
import com.johnbear724.linkgame.util.ImageUtil;


public class GameService {
    
    private GameConfig gameConfig;
    private Piece[][] map;
    
    public GameService(GameConfig gameConfig) {
        // TODO Auto-generated constructor stub
        this.gameConfig = gameConfig;
        createMap();
    }
    
    public Piece[][] createMap() {
        List<Integer> mapList = new Map(gameConfig.getRows(), gameConfig.getColumns(), ImageUtil.getImageValues().size()).getMap();
        Piece[][] newMap = new Piece[gameConfig.getRows()][gameConfig.getColumns()]; 
//        Log.e("createMap", newMapList.getMap().toString());
//        Log.e("createMap", newMapList.getMap().size() + "");
        
        int y = gameConfig.getBeginY();
        int x = gameConfig.getBeginX();
        int location = 0;
        for(int i = 0; i < gameConfig.getRows(); i++) {
            x = gameConfig.getBeginX();
            for(int j = 0; j < gameConfig.getColumns(); j++) {
//                Log.e("createMap", "" + location);
                Piece piece = new Piece(x, y, mapList.get(location++));
//                Piece piece = new Piece(i, j, 3);ac
                newMap[i][j] = piece;
                x += gameConfig.PIECE_WIDTH;
            }
            y += gameConfig.PIECE_HEIGHT;
        }
        
        this.map = newMap;
        return this.map;
    }
    
    public Piece checkSelected(float x, float y) {
        int selectedColumn = (int) ((x - gameConfig.getBeginX()) / gameConfig.PIECE_WIDTH);    
        int selectedRow = (int) ((y - gameConfig.getBeginY()) / gameConfig.PIECE_HEIGHT);    
        selectedColumn = selectedColumn < 0 ? 0 : selectedColumn;
        selectedColumn = selectedColumn > (gameConfig.getColumns() - 1) ? (gameConfig.getColumns() - 1) : selectedColumn;
        selectedRow = selectedRow < 0 ? 0 : selectedRow;
        selectedRow = selectedRow > (gameConfig.getRows() - 1) ? (gameConfig.getRows() - 1) : selectedRow;
        return map[selectedRow][selectedColumn];
    }
    
    public GameConfig getGameConfig() {
        return gameConfig;
    }
    
    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }
    
    public void setMap(Piece[][] map) {
        this.map = map;
    }
    
    public Piece[][] getMap() {
        return map;
    }
    
}
