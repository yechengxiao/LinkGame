package com.johnbear724.linkgame.core;

import android.graphics.Point;
import android.util.Pair;
import android.util.SparseArray;
import com.johnbear724.linkgame.object.LinkInfo;
import com.johnbear724.linkgame.object.Map;
import com.johnbear724.linkgame.object.Piece;
import com.johnbear724.linkgame.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类主要处理游戏逻辑
 */
public class GameService {
    
    private GameConfig gameConfig;
    private Piece[][] map;
    
    public GameService(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

    /**
     * 创建一个游戏地图
     * @return 储存地图信息的数组
     */
    public Piece[][] createMap() {
        int rows = gameConfig.getRows();
        int columns = gameConfig.getColumns();
        int beginY = gameConfig.getBeginY();
        int beginX = gameConfig.getBeginX();
        int pieceWidth = gameConfig.getPieceWidth();
        int pieceHeight = gameConfig.getPieceHeight();
        List<Integer> mapList = new Map(rows, columns, ImageUtil.getImageValues().size()).getMap();
        Piece[][] newMap = new Piece[rows][columns];

        int location = 0;
        int x = beginX;
        int y = beginY;
        for(int i = 0; i < rows; i++) {
            x = beginX;
            for(int j = 0; j < columns; j++) {
                Piece piece = new Piece(x, y, i, j, mapList.get(location++), gameConfig.getContext().getResources());
                newMap[i][j] = piece;
                x += pieceWidth;
            }
            y += pieceHeight;
        }
        map = newMap;
        return map;
    }
    
    public Piece checkSelected(float x, float y) {
        int rows = gameConfig.getRows();
        int columns = gameConfig.getColumns();
        int beginX = gameConfig.getBeginX();
        int beginY = gameConfig.getBeginY();
        int pieceWidth = gameConfig.getPieceWidth();
        int pieceHeight = gameConfig.getPieceHeight();
        int selectedColumn = (int) ((x - beginX) / pieceWidth);
        int selectedRow = (int) ((y - beginY) / pieceHeight);
        selectedColumn = selectedColumn < 0 ? 0 : selectedColumn;
        selectedColumn = selectedColumn > (columns - 1) ? (columns- 1) : selectedColumn;
        selectedRow = selectedRow < 0 ? 0 : selectedRow;
        selectedRow = selectedRow > (rows - 1) ? (rows - 1) : selectedRow;
        return map[selectedRow][selectedColumn];
    }
    //FIXME 有时连线不是最近距离，比较远
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
//                    if(linkUpList.keyAt(ll.getSize()) == 0) {
//                        linkUpList.put(ll.getSize(), ll);
//                    }
                    linkUpList.put(ll.getSize(), ll);
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
//                    if(linkUpList.keyAt(ll.getSize()) == 0) {
//                        linkUpList.put(ll.getSize(), ll);
//                    }
                    linkUpList.put(ll.getSize(), ll);
                }
            }
        }
        
        if(linkUpList.size() != 0) {
            return linkUpList.valueAt(0).getLocationPoint(linkMap, gameConfig);
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

    /**
     * 判断两个元素是否可连接
     * @param oneRow
     * @param oneColumn
     * @param twoRow
     * @param twoColumn
     * @param pieces
     * @return
     */
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
        int beginX = gameConfig.getBeginX();
        int beginY = gameConfig.getBeginY();
        int width = gameConfig.getPieceWidth();
        int height = gameConfig.getPieceHeight();
        int rows = pieces.length;
        int columns = pieces[0].length;

        Piece[][] outerMap = new Piece[rows + 2][columns + 2];
        outerMap[0][0] = new Piece(beginX - width + 0 * width + indent, beginY - height+ indent, 0, 0, -1, null);
        for(int i = 1; i < (outerMap[0].length - 1); i++) {
            outerMap[0][i] = new Piece(beginX - width + i * width, beginY - height + indent, 0, i, -1, null);
        }
        outerMap[0][outerMap[0].length - 1] = new Piece(beginX - width + (outerMap[0].length - 1) * width - indent, beginY - height + indent, 0, outerMap[0].length - 1, -1, null);
        int mapI = 0;
        for(int i = 1; i < (outerMap.length - 1); i++) {
            outerMap[i][0] = new Piece(beginX - width + indent, map[mapI][0].getY(), i, 0, -1, null);
            for(int j = 0; j < pieces[0].length; j++) {
                outerMap[i][j + 1] = pieces[mapI][j];
            }
            outerMap[i][outerMap[0].length - 1] = new Piece(map[mapI][map[mapI].length - 1].getX() + width - indent, map[mapI][0].getY(), i, outerMap[0].length - 1, -1, null);
            mapI++;
        }
        outerMap[outerMap.length - 1][0] = new Piece(beginX - width + 0 * width + indent, map[map.length - 1][0].getY() + height - indent, outerMap.length - 1, 0, -1, null);
        for(int i = 1; i < (outerMap[0].length - 1); i++) {
            outerMap[outerMap.length - 1][i] = new Piece(beginX - width + i * width, map[map.length - 1][0].getY() + height - indent, outerMap.length - 1, i, -1, null);
        }
        outerMap[outerMap.length - 1][outerMap[0].length - 1] = new Piece(beginX - width + (outerMap[0].length - 1) * width - indent, map[map.length - 1][0].getY() + height - indent, outerMap.length - 1, (outerMap[0].length - 1), -1, null);
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
    
    public LinkInfo findLinkablePiece() {
        Piece[][] linkMap;
        if(GameConfig.OUTER_LINK) {
            linkMap = getOuterMap(map);
        } else {
            linkMap = map;
        }
        ArrayList<Pair<Integer, Integer>>[][] linkableMap = new ArrayList[linkMap.length][linkMap[0].length];
        for(int x = 0; x < linkMap.length; x++) {
            for(int y = 0; y < linkMap[0].length; y++) {
                
                ArrayList<Pair<Integer, Integer>> linkable = new ArrayList<Pair<Integer,Integer>>();
                linkableMap[x][y] = linkable;
                int sign = 0;
                ArrayList<Pair<Integer, Integer>> xLinkable = new ArrayList<Pair<Integer,Integer>>();
                for(int i = 0; i < linkMap.length; i++) {
                    if(i == x) {
                        sign = 1;
                    } else if(linkMap[i][y].getImageId() == -1) {
                        xLinkable.add(new Pair<Integer, Integer>(i, y));
                    } else if(linkMap[i][y].getImageId() != -1) {
                        if(sign != 1) {
                            xLinkable.clear();
                            xLinkable.add(new Pair<Integer, Integer>(i, y));
                        } else {
                            xLinkable.add(new Pair<Integer, Integer>(i, y));
                            break;
                        }
                    }
                }
                ArrayList<Pair<Integer, Integer>> yLinkable = new ArrayList<Pair<Integer,Integer>>();
                for(int j = 0; j < linkMap[0].length; j++) {
                    if(j == y) {
                        sign = 2;
                    } else if(linkMap[x][j].getImageId() == -1) {
                        yLinkable.add(new Pair<Integer, Integer>(x, j));
                    } else if(linkMap[x][j].getImageId() != -1) {
                        if(sign != 2) {
                            yLinkable.clear();
                            yLinkable.add(new Pair<Integer, Integer>(x, j));
                        } else {
                            yLinkable.add(new Pair<Integer, Integer>(x, j));
                            break;
                        }
                    }
                }
                linkable.addAll(xLinkable);
                linkable.addAll(yLinkable);
            }
        }
        
        for(int i = 0; i < linkMap.length; i++) {
            for(int j = 0; j < linkMap[0].length; j++) {
                if(linkMap[i][j].getImageId() != -1) {
                    
                    for(Pair<Integer, Integer> p : linkableMap[i][j]) {
                        if(linkMap[i][j].getImageId() == linkMap[p.first][p.second].getImageId()) {
                            return new LinkInfo(new Point(i, j), new Point(p.first, p.second)); 
                        }
                    }
                    
                    
                    for(Pair<Integer, Integer> p : linkableMap[i][j]) {
                        if(linkMap[p.first][p.second].getImageId() == -1) {
                            for(Pair<Integer, Integer> p1 : linkableMap[p.first][p.second]) {
                                if(p1.first == i && p1.second == j) {
                                } else if(linkMap[i][j].getImageId() == linkMap[p1.first][p1.second].getImageId()) {
                                    return new LinkInfo(new Point(i, j), new Point(p1.first, p1.second)); 
                                }
                            }
                        }
                    }
                    
                    for(Pair<Integer, Integer> p : linkableMap[i][j]) {
                        if(p.first == i && p.second == j) {
                            
                        } else if(linkMap[p.first][p.second].getImageId() == -1) {
                            for(Pair<Integer, Integer> p1 : linkableMap[p.first][p.second]) {
                                if(p1.first == i && p1.second == j) {
                                    
                                } else if(linkMap[p1.first][p1.second].getImageId() == -1) {
                                    for(Pair<Integer, Integer> p2 : linkableMap[p1.first][p1.second]) {
                                        if(p2.first == i && p2.second == j) {
                                        } else if(linkMap[i][j].getImageId() == linkMap[p2.first][p2.second].getImageId()) {
                                            return new LinkInfo(new Point(i, j), new Point(p2.first, p2.second)); 
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                }
            }
        }
        return null;
    }
    
//    public List<Pair<Integer, Integer>> findLinkable(Piece[][] linkMap, int x, int y) {
//        for(int i = 0; i < linkMap.length; i++) {
//            if(i == x) {
//
//            } else if(linkMap[i][y].getImageId() == -1){
//
//            }
//
//        }
//        return null;
//    }
}
