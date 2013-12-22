package com.johnbear724.linkgame.object;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

public class LinkInfo {
    
    private List<Point> pointList = new ArrayList<Point> ();
    
    public LinkInfo(Point one, Point two, Point three, Point four) {
        // TODO Auto-generated constructor stub
        pointList.add(one);
        pointList.add(two);
        pointList.add(three);
        pointList.add(four);
    }
    
    public LinkInfo(Point one, Point two, Point three) {
        // TODO Auto-generated constructor stub
        pointList.add(one);
        pointList.add(two);
        pointList.add(three);
    }
    
    public LinkInfo(Point one, Point two) {
        // TODO Auto-generated constructor stub
        pointList.add(one);
        pointList.add(two);
    }
    
    public List<Point> getPoint() {
        return pointList;
    }
}
