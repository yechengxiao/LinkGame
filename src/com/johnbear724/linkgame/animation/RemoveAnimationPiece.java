package com.johnbear724.linkgame.animation;

import com.johnbear724.linkgame.object.Piece;

public class RemoveAnimationPiece extends AnimationPiece{
    
    private float x;
    private float y;
    
    public RemoveAnimationPiece(Piece p) {
        // TODO Auto-generated constructor stub
        this.bitmap = p.getBitmap();
        this.x = p.getX();
        this.y = p.getY();
    }
    
    public void setScale(float sx, float sy) {
        this.matrix.setTranslate(this.x, this.y);
        this.matrix.preScale(sx, sy, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
    }
    
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }
}
