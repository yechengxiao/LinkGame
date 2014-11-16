package com.johnbear724.linkgame.object;

import android.graphics.Point;
import com.johnbear724.linkgame.core.GameConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理两个元素连线的信息
 */
public class LinkInfo {
    
    private List<Point> pointList = new ArrayList<Point> ();
    
    
    public LinkInfo(Point begin, Point p1, Point p2, Point end) {
        // TODO Auto-generated constructor stub
        if(begin.equals(p1)) {
            pointList.add(begin);
        } else {
            pointList.add(begin);
            pointList.add(p1);
        }
        if(p2.equals(end)) {
            pointList.add(end);
        } else {
            pointList.add(p2);
            pointList.add(end);
        }
    }
    
    public LinkInfo(Point begin, Point end) {
        pointList.add(begin);
        pointList.add(end);
    }
    
    public int getSize() {
        return pointList.size();
    }
    
    public List<Point> getLocationPoint(Piece[][] map, GameConfig gameConfig) {
        List<Point> pList = new ArrayList<Point> ();
        for(Point p : pointList) {
            pList.add( new Point((int)map[p.x][p.y].getX() + gameConfig.getPieceWidth() / 2 ,
                    (int)map[p.x][p.y].getY() + gameConfig.getPieceHeight() / 2));
        }
        return pList; 
    }
    
    public List<Point> getFindPoint(Piece[][] map) {
        List<Point> pList = new ArrayList<Point> ();
        for(Point p : pointList) {
            pList.add( new Point((int)map[p.x - 1][p.y - 1].getX() , (int)map[p.x - 1][p.y - 1].getY()));
        }
        return pList;  
    }
    
    public List<Point> getPList() {
        return pointList;
    }
    
    public Point getPointOne() {
        return new Point(pointList.get(0).x - 1, pointList.get(0).y - 1);
    }
}
