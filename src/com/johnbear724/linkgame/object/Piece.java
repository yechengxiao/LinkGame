package com.johnbear724.linkgame.object;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.johnbear724.linkgame.util.ImageUtil;


public class Piece {
    
    private float x;
    private float y;
    //imageId为-1则为空，大于等于0则为对应的位图
    private int imageId;
    private Bitmap bitmap;
    private int columnNum;;
    private int rowNum;
    
    public Piece(int x, int y, int rowNum, int columnNum, int imageId, Resources r) {
        // TODO Auto-generated constructor stub
        this.x = x;
        this.y= y;
        this.rowNum = rowNum;
        this.columnNum = columnNum;
        this.imageId = imageId;
        if(imageId != -1) {
            bitmap = BitmapFactory.decodeResource(r, ImageUtil.getImageValues().get(imageId));
        } else {
            bitmap = null;
        }
    }
    
    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImage(int imageId, Resources r) {
        this.imageId = imageId;
        if(imageId != -1) {
            bitmap = BitmapFactory.decodeResource(r, ImageUtil.getImageValues().get(imageId));
        } else {
            bitmap = null;
        }
    }
    
    public int getRowNum() {
        return rowNum;
    }
    
    public int getColumnNum() {
        return columnNum;
    }
    
}
