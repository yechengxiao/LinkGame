package com.johnbear724.linkgame.animation;

import android.graphics.*;

import java.util.List;

/**
 * 搜索后高亮可连接单位的动画
 */
public class SearchAnimation implements AnimationPiece{

    private Paint paint;
    private Matrix matrix;
    private Bitmap bitmap;
    private float scale = 1;
    private List<Point> pList;
    
    public SearchAnimation() {
        this.paint = new Paint();
        this.matrix = new Matrix();
    }
    
    @Override
    public void drawAnimation(Canvas canvas) {
        for(Point p : pList) {
            matrix.setTranslate(p.x, p.y);
            matrix.preScale(scale, scale, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            canvas.drawBitmap(bitmap, matrix, paint);
        }
    }
    
    public void setPList(List<Point> list) {
        this.pList = list;
    }
    
    public void setBitmap(Bitmap b) {
        this.bitmap = b;
    }
    
    public void setScale(float s) {
        this.scale = s;
    }
    
}
