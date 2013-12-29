package com.johnbear724.linkgame.animation;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;

public class LinkUpAnimation implements AnimationPiece{

    private Paint paint;
    private List<Point> pList;
    
    public LinkUpAnimation(List<Point> pList) {
        // TODO Auto-generated constructor stub
        this.pList = pList;
        this.paint = new Paint();
        this.paint.setColor(0xff33b5e6);
        this.paint.setStrokeWidth(4);
    }
    
    public void setAlpha(int a) {
        paint.setAlpha(a);
    }
    
    @Override
    public void drawAnimation(Canvas canvas) {
        // TODO Auto-generated method stub
        Point start = pList.get(0);
        for(int i = 1; i < pList.size(); i++) {
            canvas.drawLine(start.x, start.y, pList.get(i).x, pList.get(i).y, paint);
            start = pList.get(i);
        }
    }

    public void setBitmap(Bitmap b) {
        this.paint.setShader(new BitmapShader(b, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR));
        this.paint.setStrokeWidth(15);
    }
}
