package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.widget.TextView;

public class HorizitalScrollTextView extends TextView {
    private float step = 0.5f;
    private Paint mPaint;
    private String text;
    private boolean isFirst = true;
    private long time;
    private int second = 1 * 1000;
    private int id = -1;

    public HorizitalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = getPaint();
        id = 1;
        setSingleLine(true);
    }

    public HorizitalScrollTextView(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public HorizitalScrollTextView(Context context) {
        super(context);
        mPaint = getPaint();
        id = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MeasureSpec.getSize(widthMeasureSpec);
        text = this.getText().toString();
        if (text == null || text.length() == 0) {
            return;
        }
    }

    public void onDraw(Canvas canvas) {
        if (text == null) return;
        if (isFirst == true) {
            time = System.currentTimeMillis();
            isFirst = false;
        }
        int height = id == 1 ? this.getHeight() : this.getHeight() / 2;
        canvas.drawText(text, step, getTextHeight(), getPaint());
        long time1 = System.currentTimeMillis();
        if (time1 - time < second && time > 0) {
            postInvalidate();
            return;
        }
        postInvalidate();
        step = step - 1.5f;
        if (Math.abs(step) >= mPaint.measureText(text) && step <= 0) {
            step = this.getWidth();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        this.text = this.getText().toString();
        postInvalidate();
    }

    @Override
    public void setTextSize(float size) {
        mPaint.setTextSize(size);
    }

    @Override
    public void setTextColor(int color) {
        mPaint.setColor(color);
    }

    public float getTextHeight() {
        FontMetrics fontMetrics = getPaint().getFontMetrics();
        return this.getHeight() - (this.getHeight() - fontMetrics.bottom + fontMetrics.top) / 2
                - fontMetrics.bottom;
    }
}
