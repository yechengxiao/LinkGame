package com.johnbear724.linkgame.core;

import android.content.Context;
import android.view.View;
import com.johnbear724.linkgame.R;

/**
 * 游戏配置类
 */
public class GameConfig {

    public static boolean OUTER_LINK = true; //是否可以从地图外层连线
    private int rows;
    private int columns;
    private int beginX;
    private int beginY;
    private int pieceSize;
    private Context context;

    public GameConfig(View gameView) {
        this.context = gameView.getContext();
        pieceSize = context.getResources().getDimensionPixelSize(R.dimen.piece_size);
//        int margin = context.getResources().getDimensionPixelOffset(R.dimen.game_view_margin);
        int viewWidth = gameView.getWidth();
        int viewHeight = gameView.getHeight();
        this.rows = viewHeight / pieceSize;
        this.columns = viewWidth / pieceSize;
        if ((rows * columns) % 2 != 0) {
            rows--;
        }
        int gameWidth = pieceSize * columns;
        int gameHeight = pieceSize * rows;
        this.beginX = (viewWidth - gameWidth) / 2;
        this.beginY = (viewHeight - gameHeight)/ 2;
    }

    public int getPieceWidth() {
        return pieceSize;
    }

    public int getPieceHeight() {
        return pieceSize;
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
