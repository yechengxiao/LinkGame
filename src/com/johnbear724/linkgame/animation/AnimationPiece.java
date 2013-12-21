package com.johnbear724.linkgame.animation;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;

public class AnimationPiece {
    
    protected Bitmap bitmap;
    protected Matrix matrix;
    protected Paint paint;
    
    public AnimationPiece() {
        // TODO Auto-generated constructor stub
        this.bitmap = null;
        this.matrix = new Matrix();
        this.paint = new Paint();
    }
    
    public Bitmap getBitmap() {
        // TODO Auto-generated method stub
        return bitmap;
    }

    public Matrix getMatrix() {
        // TODO Auto-generated method stub
        return matrix;
    }

    public Paint getPaint() {
        // TODO Auto-generated method stub
        return paint;
    }
}
