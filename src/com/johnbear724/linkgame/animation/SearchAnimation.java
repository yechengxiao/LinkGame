package com.johnbear724.linkgame.animation;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

public class SearchAnimation implements AnimationPiece{

    private Paint paint;
    private Matrix matrix;
    private Bitmap bitmap;
    private float scale = 1;
    private List<Point> pList;
    
    public SearchAnimation() {
        // TODO Auto-generated constructor stub
        this.paint = new Paint();
        this.matrix = new Matrix();
    }
    
    @Override
    public void drawAnimation(Canvas canvas) {
        // TODO Auto-generated method stub
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
