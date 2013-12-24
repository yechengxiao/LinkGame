package com.johnbear724.linkgame.core;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.util.SparseArray;

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
                x += GameConfig.PIECE_WIDTH;
            }
            y += GameConfig.PIECE_HEIGHT;
        }
        
        this.map = newMap;
        return this.map;
    }
    
    public Piece checkSelected(float x, float y) {
        int selectedColumn = (int) ((x - gameConfig.getBeginX()) / GameConfig.PIECE_WIDTH);    
        int selectedRow = (int) ((y - gameConfig.getBeginY()) / GameConfig.PIECE_HEIGHT);    
        selectedColumn = selectedColumn < 0 ? 0 : selectedColumn;
        selectedColumn = selectedColumn > (gameConfig.getColumns() - 1) ? (gameConfig.getColumns() - 1) : selectedColumn;
        selectedRow = selectedRow < 0 ? 0 : selectedRow;
        selectedRow = selectedRow > (gameConfig.getRows() - 1) ? (gameConfig.getRows() - 1) : selectedRow;
        return map[selectedRow][selectedColumn];
    }
    
    public List<Point> checkLinkUp(int oneRow, int oneColumn, int twoRow, int twoColumn) {
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
        
        SparseArray<LinkInfo> linkUpList = new SparseArray<LinkInfo>();
        
        List<Integer> oneHRange = horizontalRange(oneRow, oneColumn, linkMap);
        List<Integer> twoHRange = horizontalRange(twoRow, twoColumn, linkMap);
        List<Integer> sameHList = new ArrayList<Integer> ();
        for(int i : oneHRange) {
            if(twoHRange.contains(i)) {
                sameHList.add(i);
            }
        }
        if(!sameHList.isEmpty()) {
            for(int i : sameHList) {
                if(isLinkUp(oneRow, i, twoRow, i, linkMap)) {
                    LinkInfo ll = new LinkInfo(new Point(oneRow, oneColumn), new Point(oneRow, i), new Point(twoRow, i), new Point(twoRow, twoColumn));
                    if(linkUpList.keyAt(ll.getSize()) == 0) {
                        linkUpList.put(ll.getSize(), ll);
                    }
                }
            }
        }
        
        List<Integer> oneVRange = verticalRange(oneRow, oneColumn, linkMap);
        List<Integer> twoVRange = verticalRange(twoRow, twoColumn, linkMap);
        List<Integer> sameVList = new ArrayList<Integer> ();
        for(int i : oneVRange) {
            if(twoVRange.contains(i)) {
                sameVList.add(i);
            }
        }
        if(!sameVList.isEmpty()) {
            for(int i : sameVList) {
                if(isLinkUp(i, oneColumn, i, twoColumn, linkMap)) {
                    LinkInfo ll = new LinkInfo(new Point(oneRow, oneColumn), new Point(i, oneColumn), new Point(i, twoColumn), new Point(twoRow, twoColumn));
                    if(linkUpList.keyAt(ll.getSize()) == 0) {
                        linkUpList.put(ll.getSize(), ll);
                    }
                }
            }
        }
        
        if(linkUpList.size() != 0) {
            return linkUpList.valueAt(0).getLocationPoint(linkMap);
        } else {
            return null;
        }
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
                } else if(status == 1 && pieces[oneRow][i].getImageId() != -1) {
                    return false;
                }
                if(status == 2) {
                    return true;
                } 
            }
        } else if(oneColumn == twoColumn) {
            for(int i = 0; i < pieces.length; i++) {
                if(i == oneRow || i == twoRow) {
                    status++;
                } else if(status == 1 && pieces[i][oneColumn].getImageId() != -1) {
                    return false;
                }
                if(status == 2) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Piece[][] getOuterMap(Piece[][] pieces) {
        int indent = 35;
        Piece[][] outerMap = new Piece[pieces.length + 2][pieces[0].length + 2];
        outerMap[0][0] = new Piece(gameConfig.getBeginX() - GameConfig.PIECE_WIDTH + 0 * GameConfig.PIECE_WIDTH + indent, gameConfig.getBeginY() - GameConfig.PIECE_HEIGHT + indent, 0, 0, -1, null);
        for(int i = 1; i < (outerMap[0].length - 1); i++) {
            outerMap[0][i] = new Piece(gameConfig.getBeginX() - GameConfig.PIECE_WIDTH + i * GameConfig.PIECE_WIDTH, gameConfig.getBeginY() - GameConfig.PIECE_HEIGHT + indent, 0, i, -1, null);
        }
        outerMap[0][outerMap[0].length - 1] = new Piece(gameConfig.getBeginX() - GameConfig.PIECE_WIDTH + (outerMap[0].length - 1) * GameConfig.PIECE_WIDTH - indent, gameConfig.getBeginY() - GameConfig.PIECE_HEIGHT + indent, 0, outerMap[0].length - 1, -1, null);
        int mapI = 0;
        for(int i = 1; i < (outerMap.length - 1); i++) {
            outerMap[i][0] = new Piece(gameConfig.getBeginX() - GameConfig.PIECE_WIDTH + indent, map[mapI][0].getY(), i, 0, -1, null);
            for(int j = 0; j < pieces[0].length; j++) {
                outerMap[i][j + 1] = pieces[mapI][j];
            }
            outerMap[i][outerMap[0].length - 1] = new Piece(map[mapI][map[mapI].length - 1].getX() + GameConfig.PIECE_WIDTH - indent, map[mapI][0].getY(), i, outerMap[0].length - 1, -1, null);
            mapI++;
        }
        outerMap[outerMap.length - 1][0] = new Piece(gameConfig.getBeginX() - GameConfig.PIECE_WIDTH + 0 * GameConfig.PIECE_WIDTH + indent, map[map.length - 1][0].getY() + GameConfig.PIECE_HEIGHT - indent, outerMap.length - 1, 0, -1, null);
        for(int i = 1; i < (outerMap[0].length - 1); i++) {
            outerMap[outerMap.length - 1][i] = new Piece(gameConfig.getBeginX() - GameConfig.PIECE_WIDTH + i * GameConfig.PIECE_WIDTH, map[map.length - 1][0].getY() + GameConfig.PIECE_HEIGHT - indent, outerMap.length - 1, i, -1, null);
        }
        outerMap[outerMap.length - 1][outerMap[0].length - 1] = new Piece(gameConfig.getBeginX() - GameConfig.PIECE_WIDTH + (outerMap[0].length - 1) * GameConfig.PIECE_WIDTH - indent, map[map.length - 1][0].getY() + GameConfig.PIECE_HEIGHT - indent, outerMap.length - 1, (outerMap[0].length - 1), -1, null);
        return outerMap; 
    }
    
    public boolean isEmpty() {
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[0].length; j++) {
                if(map[i][j].getImageId() != -1) {
                    return false;
                }
            }
        }
        return true;
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
