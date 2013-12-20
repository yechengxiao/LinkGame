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
    private boolean isSelected;
    
    public Piece(int x, int y, int imageId) {
        // TODO Auto-generated constructor stub
        this.x = x;
        this.y= y;
        this.imageId = imageId;
    }
    
    public Bitmap getBitmap(Resources r) {
        if(imageId != -1) {
            return BitmapFactory.decodeResource(r, ImageUtil.getImageValues().get(imageId));
        } else {
            return null;
        }
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

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
