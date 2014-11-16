package com.johnbear724.linkgame.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.johnbear724.linkgame.object.Piece;

/**
 * 消除动画
 */
public class RemoveAnimationPiece implements AnimationPiece{
    
    private float x;
    private float y;
    private Matrix matrix;
    private Paint paint;
    private Bitmap bitmap;
    
    public RemoveAnimationPiece(Piece p) {
        // TODO Auto-generated constructor stub
        this.bitmap = p.getBitmap();
        this.x = p.getX();
        this.y = p.getY();
        this.matrix = new Matrix();
        this.paint = new Paint();
    }
    
    public void setScale(float sx, float sy) {
        this.matrix.setTranslate(this.x, this.y);
        this.matrix.preScale(sx, sy, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
    }
    
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    @Override
    public void drawAnimation(Canvas canvas) {
        // TODO Auto-generated method stub
        canvas.drawBitmap(bitmap, matrix, paint);
    }
}
