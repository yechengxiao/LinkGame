package com.johnbear724.linkgame.core;

import java.util.ArrayList;
import java.util.List;

import com.johnbear724.linkgame.object.LinkInfo;
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
        
        int y = gameConfig.getBeginY();
        int x = gameConfig.getBeginX();
        int location = 0;
        for(int i = 0; i < gameConfig.getRows(); i++) {
            x = gameConfig.getBeginX();
            for(int j = 0; j < gameConfig.getColumns(); j++) {
                Piece piece = new Piece(x, y, i, j, mapList.get(location++), gameConfig.getContext().getResources());
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
    
    public LinkInfo checkLinkUp(int oneRow, int oneColumn, int twoRow, int twoColumn) {
        Piece[][] linkMap;
        if(GameConfig.OUTER_LINK) {
            oneRow++;
            oneColumn++;
            twoRow++;
            twoColumn++;
            linkMap = getOuterMap(map);
        } else {
            linkMap = map;
        }
        
        return null;
    }
    
    public List<Integer> horizontalRange (int row, int column, Piece[][] pieces) {
        List<Integer>  indexList = new ArrayList<Integer> ();
        for(int i = 0; i < pieces[0].length; i++) {
            if(pieces[row][i].getImageId() == -1 || i == column) {
                indexList.add(i);
            } else {
                if(indexList.contains(column)) {
                    break;
                } else {
                    indexList.clear();
                }
            }
        }
        return indexList;
    }
    
    public List<Integer> verticalRange (int row, int column, Piece[][] pieces) {
        List<Integer>  indexList = new ArrayList<Integer> ();
        for(int i = 0; i < pieces.length; i++) {
            if(pieces[i][column].getImageId() == -1 || i == row) {
                indexList.add(i);
            } else {
                if(indexList.contains(row)) {
                    break;
                } else {
                    indexList.clear();
                }
            }
        }
        return indexList;
    }
    
    public boolean isLinkUp(int oneRow, int oneColumn, int twoRow, int twoColumn, Piece[][] pieces) {
        int status = 0;
        if(oneRow == twoRow) {
            for(int i = 0; i < pieces[0].length; i++) {
                if(i == oneColumn || i == twoColumn) {
                    status ++;
                }
                if(status == 2) {
                    return true;
                } 
                if(status == 1 && pieces[oneRow][i].getImageId() != -1) {
                    return false;
                }
            }
        } else if(oneColumn == twoColumn) {
            for(int i = 0; i < pieces.length; i++) {
                if(i == oneRow || i == twoRow) {
                    status++;
                }
                if(status == 2) {
                    return true;
                }
                if(status == 1 && pieces[i][oneColumn].getImageId() != -1) {
                    return false;
                }
            }
        }
        return false;
    }
    
    public Piece[][] getOuterMap(Piece[][] pieces) {
        Piece[][] outerMap = new Piece[pieces.length + 2][pieces[0].length + 2];
        for(int i = 0; i < outerMap[0].length; i++) {
            outerMap[0][i] = new Piece(0, 0, 0, i, -1, null);
        }
        for(int i = 1; i < (outerMap.length - 1); i++) {
            outerMap[i][0] = new Piece(0, 0, i, 0, -1, null);
            int mapI = 0;
            for(int j = 0; j < pieces[0].length; j++) {
                outerMap[i][j + 1] = pieces[mapI][j];
            }
            outerMap[i][outerMap[0].length - 1] = new Piece(0, 0, i, outerMap[0].length - 1, -1, null);
            mapI++;
        }
        for(int i = 0; i < outerMap[0].length; i++) {
            outerMap[outerMap.length - 1][i] = new Piece(0, 0, outerMap.length - 1, i, -1, null);
        }
        return outerMap; 
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
