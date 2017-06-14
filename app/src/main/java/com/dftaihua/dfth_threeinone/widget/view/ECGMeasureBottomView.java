package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.measure.listener.ECGMeasureControllerConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGRhythmConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGScreenConduct;
import com.dftaihua.dfth_threeinone.mediator.MeasureMediator;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public class ECGMeasureBottomView extends LinearLayout implements ECGMeasureControllerConduct,ECGRhythmConduct,View.OnClickListener,ECGScreenConduct {
    private ImageView mStartView;
    private TextView mHeartRate;
    private TextView mRecordTime;
    private MeasureMediator mMediator;
    private int mOrientation;
    private TextView mPosition1;
    private TextView mPosition2;
    private long mLastClickTime = 0;
    public ECGMeasureBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ecg_measure_bottom,this);
        mStartView = (ImageView) findViewById(R.id.ecg_pause_image_horizontal);
        mHeartRate = (TextView) findViewById(R.id.h_show_beat);
        mRecordTime = (TextView) findViewById(R.id.h_show_measure_time);
        mPosition1 = (TextView) findViewById(R.id.position1);
        mPosition2 = (TextView) findViewById(R.id.position2);
        mStartView.setOnClickListener(this);
    }
    @Override
    public void bindMediator(MeasureMediator mediator) {
        mMediator = mediator;
    }

    @Override
    public void setStart(boolean start) {
        int res = start ? R.drawable.measure_stop : R.drawable.measure_start;
        mStartView.setImageResource(res);
    }

    @Override
    public void setHeartRate(int rate) {
        String hr = rate <= 0 ? "--" : String.valueOf(rate);
        mHeartRate.setText(ThreeInOneApplication.getInstance().getString(R.string.BPM,hr));
    }

    @Override
    public void setMeasureTime(String time) {
        mRecordTime.setText(time);
    }


    @Override
    public void reset() {
        setHeartRate(0);
        mRecordTime.setText("--");
    }

    @Override
    public void onClick(View v) {
        if(mLastClickTime == 0){
            mLastClickTime = System.currentTimeMillis();
            if(mMediator != null){
                mMediator.measureButtonClick();
            }
        } else{
            if (System.currentTimeMillis() - mLastClickTime < 1000) {
                mLastClickTime = System.currentTimeMillis();
            } else{
                if(mMediator != null){
                    mMediator.measureButtonClick();
                }
            }
        }
    }

    @Override
    public void orientationChange(int orientation) {
        mOrientation = orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            setVisibility(VISIBLE);
            findViewById(R.id.h_beat).setVisibility(VISIBLE);
            findViewById(R.id.h_time).setVisibility(VISIBLE);
            mPosition1.setVisibility(GONE);
            mPosition2.setVisibility(GONE);
        }else{
            findViewById(R.id.h_beat).setVisibility(GONE);
            findViewById(R.id.h_time).setVisibility(GONE);
            mPosition1.setVisibility(VISIBLE);
            mPosition2.setVisibility(VISIBLE);
        }
    }

    public void setViewVisibility(int visibility){
//        if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
//            this.setVisibility(visibility);
//        }
    }
}
