package com.johnbear724.linkgame.core;

import android.content.Context;

public class GameConfig {
    
    public static int PIECE_WIDTH = 64;
    public static int PIECE_HEIGHT = 64;
    public static final int WIN_GAME = 0x123;
    public static final int TIMER = 0x1234;
    public static final int SCOUR_1 = 0x111;
    public static final int SCOUR_2 = 0x112;
    public static final int SCOUR_3 = 0x113;
    public static final int START_TEXT_INVISIBLE = 0x3;
    public static boolean OUTER_LINK = true;
    private int rows;
    private int columns;
    private int beginX;
    private int beginY;
    private Context context;
    
    public GameConfig(int rows, int columns, int beginX, int beginY, Context context) {
        // TODO Auto-generated constructor stub
        this.rows = rows;
        this.columns = columns;
        this.beginX = beginX;
        this.beginY = beginY;
        this.context = context;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getBeginX() {
        return beginX;
    }

    public void setBeginX(int beginX) {
        this.beginX = beginX;
    }

    public int getBeginY() {
        return beginY;
    }

    public void setBeginY(int beginY) {
        this.beginY = beginY;
    }

    public Context getContext() {
        return context;
    }

}
