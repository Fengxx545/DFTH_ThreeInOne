package com.dftaihua.dfth_threeinone.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;

/**
 * Created by Administrator on 2017/5/23 0023.
 */

public class ToastDrawable extends ShapeDrawable {
    private final Paint mPaint = new Paint();

    public ToastDrawable() {
        final int r = DisplayUtils.dip2px(ThreeInOneApplication.getInstance(), 5);
        float[] outter = {r, r, r, r, r, r, r, r};
        setShape(new RoundRectShape(outter, null, null));
        mPaint.setColor(ThreeInOneApplication.getColorRes(R.color.google_black));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        super.onDraw(shape, canvas, mPaint);
    }
}
