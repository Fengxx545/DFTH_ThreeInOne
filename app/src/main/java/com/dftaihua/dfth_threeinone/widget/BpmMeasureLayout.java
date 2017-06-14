package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;


/**
 * Created by Administrator on 2016/5/12 0012.
 */
public class BpmMeasureLayout extends RelativeLayout {
    private TextSwitcher mBpm;
    private TextView mMeasureTime;
    public RelativeLayout mBackLl;

    public BpmMeasureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalize(context);
    }

    public BpmMeasureLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initalize(context);
    }

    public BpmMeasureLayout(Context context) {
        super(context);
        initalize(context);
    }

    private void initalize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bpm_measure, this, true);
        mBpm = (TextSwitcher) findViewById(R.id.bpm_rate);
        mMeasureTime = (TextView) findViewById(R.id.bpm_measure_time);
        mBackLl = (RelativeLayout) findViewById(R.id.bpm_measure);
        mBpm.setFactory(new MyViewFactory(24, R.color.google_white));
        mBpm.setText("_ _ _");
        setTime("00:00:00");
    }

    public void reset() {
        mBpm.setText("_ _ _");
        setTime("00:00:00");
    }

    public void setRate(final int value) {
        handler.obtainMessage(0, value).sendToTarget();
    }

    private final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 0: {
                    int value = (Integer) msg.obj;
                    String text;
                    if (value < 100 && value > 0) {
                        text = String.format("0%d", value);
                    } else if (value <= 0) {
                        text = "_ _ _";
                    } else {
                        text = String.format("%d", value);
                    }
                    mBpm.setText(text);
                }
                break;
            }
        }
    };

    private class MyViewFactory implements ViewSwitcher.ViewFactory {
        public int mTextSize;
        private int color;

        public MyViewFactory(int textSize, int color) {
            mTextSize = textSize;
            this.color = color;
        }

        @Override
        public View makeView() {
            TextView t = new TextView(getContext());
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            t.setTextSize(mTextSize);
            t.setTextColor(ThreeInOneApplication.getColorRes(color));
            return t;
        }
    }

    public void disappear() {
        handler.removeMessages(0);
    }

    public void setTime(String time) {
        mMeasureTime.setText(time);
    }
}
