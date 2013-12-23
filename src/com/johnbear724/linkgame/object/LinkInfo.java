package com.johnbear724.linkgame.object;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.johnbear724.linkgame.core.GameConfig;


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
    
    public int getSize() {
        return pointList.size();
    }
    
    public List<Point> getLocationPoint(Piece[][] map) {
        if(GameConfig.OUTER_LINK) {
            
        } else {
            
        }
        
        List<Point> pList = new ArrayList<Point> ();
        for(Point p : pointList) {
            pList.add( new Point((int)map[p.x][p.y].getX() + GameConfig.PIECE_WIDTH / 2 , (int)map[p.x][p.y].getY() + GameConfig.PIECE_HEIGHT / 2));
        }
        return pList; 
    }
}
