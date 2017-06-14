package com.dftaihua.dfth_threeinone.widget;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.measure.listener.ECGRhythmConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGScreenConduct;


public class BpmLinearLayout extends RelativeLayout implements ECGRhythmConduct,ECGScreenConduct{
    private TextSwitcher mBpm;
    private ImageView mHeartView;
    private TextView mMeasureTime;
    private Animation mOperatingAnim;

    public BpmLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalize(context);
    }

    public BpmLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initalize(context);
    }

    public BpmLinearLayout(Context context) {
        super(context);
        initalize(context);
    }

    private void initalize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bpm, this, true);
        setMeasureType();
    }

    public void setMeasureType() {
        mOperatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.single_measure_roat_anim);
        LinearInterpolator lin = new LinearInterpolator();
        mOperatingAnim.setInterpolator(lin);
        Animation in = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_out);
        mHeartView = (ImageView) findViewById(R.id.bpm_animation);

        mBpm = (TextSwitcher) findViewById(R.id.bpm_rate);
        mMeasureTime = (TextView) findViewById(R.id.measure_time);

        mBpm.setFactory(new MyViewFactory(36, R.color.single_color));

        mHeartView.setVisibility(VISIBLE);

        mBpm.setInAnimation(in);
        mBpm.setOutAnimation(out);
        reset();
    }
    private class MyViewFactory implements ViewFactory {
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
    public void cancelAnimation() {
        mHeartView.clearAnimation();
    }

    public void startAnimation() {
        if (mOperatingAnim != null) {
            mHeartView.startAnimation(mOperatingAnim);
        }
    }
    public void reset(){
        mBpm.setText("_ _");
        setMeasureTime("00:00:00");
    }


    @Override
    public void setHeartRate(int rate) {
        String text = String.format("%2d",rate);
        mBpm.setText(text);
    }

    @Override
    public void setMeasureTime(String time) {
        mMeasureTime.setText(time);
    }


    @Override
    public void orientationChange(int orientation) {
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            setVisibility(View.VISIBLE);
        }else{
            setVisibility(View.GONE);
        }
    }
}
