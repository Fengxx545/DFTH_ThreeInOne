package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class ConnectAnimation extends View {
    public final static int ANIMATION_HELPER = 1;
    public final static int ANIMATION_DEVICE = 2;
    private final Paint mPaint = new Paint();
    private Paint mCirclePaint = new Paint();
    private boolean isBegin = false;
    private float width;
    private float height;
    private RectF rect = new RectF();
    private float mCurrentDegree = 60;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private int mAnimationType = ANIMATION_HELPER;

    public ConnectAnimation(Context context) {
        super(context);
    }

    public ConnectAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        int s = DisplayUtils.dip2px(ThreeInOneApplication.getInstance(), 20);
        mPaint.setStrokeWidth(s);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(false);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        int width = DisplayUtils.dip2px(ThreeInOneApplication.getInstance(), 2);
        mCirclePaint.setStrokeWidth(width);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setDither(true);
        mCirclePaint.setFilterBitmap(false);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width < 0.1f) {
            width = getWidth();
            height = getHeight();
            int s = DisplayUtils.dip2px(ThreeInOneApplication.getInstance(), 10);
            rect.set(s, s, width - s, height - s);
            mBitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            Shader shader;
            Canvas c = new Canvas(mBitmap);
            float radius = width > height ? height / 2 : width / 2;
            if (mAnimationType == ANIMATION_HELPER) {
                shader = new SweepGradient(rect.centerX(), rect.centerY(), new int[]{0x00ffffff, 0x00ffffff, Color.WHITE}, new float[]{0.0f, 0.7f, 1.0f});
                mPaint.setShader(shader);
                c.drawArc(rect, 10, 340, false, mPaint);
            } else {
                shader = new SweepGradient(rect.centerX(), rect.centerY(), new int[]{0x00ffffff, 0x00ffffff, Color.WHITE}, new float[]{0.0f, 0.2f, 1.0f});
                mPaint.setShader(shader);
                mPaint.setStyle(Paint.Style.STROKE);
                float radius1 = radius - DisplayUtils.dip2px(ThreeInOneApplication.getInstance(), 20);
                c.drawCircle(rect.centerX(), rect.centerY(), radius1, mPaint);
            }
            float radius2 = radius - DisplayUtils.dip2px(ThreeInOneApplication.getInstance(), 5);
            c.drawCircle(rect.centerX(), rect.centerY(), radius2, mCirclePaint);
        }
        mMatrix = new Matrix();
        mMatrix.reset();
        mMatrix.postRotate(mCurrentDegree, mBitmap.getWidth() / 2f, mBitmap.getHeight() / 2f);
        canvas.drawBitmap(mBitmap, mMatrix, null);
        if (this.isBegin) {
            mCurrentDegree += 3.6;
            postInvalidateDelayed(10);
        }
    }

    public void setAnimationType(int animationType) {
        mAnimationType = animationType;
        width = 0;
    }

    public void startAnimation() {
        if (!isBegin) {
            isBegin = true;
            postInvalidateDelayed(50);
            // mCurrentDegree = 270;
        }
    }

    public void cancelAnimation() {
        if (isBegin) {
            isBegin = false;
            //mCurrentDegree = 270;
            postInvalidateDelayed(50);
        }
    }

    public boolean isStart() {
        return isBegin;
    }

}
