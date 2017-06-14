package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ReviewPhotoView extends ImageView {
    private Paint mPaint = new Paint();
    private BitmapShader mBitmapShader;
    private Matrix mMatrix;
    private boolean mIsCycle = true;
    public ReviewPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setStyle(Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        resetColor();
    }

    public ReviewPhotoView(Context context) {
        super(context);
        mPaint.setStyle(Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        resetColor();
    }

    public void resetColor() {
        mMatrix = new Matrix();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!mIsCycle){
            super.onDraw(canvas);
            return;
        }
        final int width = this.getWidth();
        final int height = this.getHeight();
        BitmapDrawable bd = (BitmapDrawable) getDrawable();
        if (bd != null && bd.getBitmap() != null) {
            Bitmap b = bd.getBitmap();
            mBitmapShader = new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            int bSize = Math.min(b.getWidth(), b.getHeight());
            float scale = width * 1.0f / bSize;
            mMatrix.reset();
            mMatrix.setScale(scale,scale);
            mBitmapShader.setLocalMatrix(mMatrix);
            mPaint.setShader(mBitmapShader);
            canvas.drawCircle(width / 2, height / 2, width / 2, mPaint);
        }
    }

    public void setIsCycle(boolean isCycle) {
        this.mIsCycle = isCycle;
    }
}
