package com.dftaihua.dfth_threeinone.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;

/**
 * Created by Administrator on 2017/2/13 0013.
 */
public class BpMeasureTimeView extends LinearLayout implements View.OnClickListener {
    private TextView mFirstTv;
    private TextView mSecondTv;
    private String mFirstText;
    private String mSecondText;
    private float mFirstTextSize;
    private float mSecondTextSize;
    private int mSelectText;

    public BpMeasureTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BpMeasureTimeView(Context context) {
        this(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_bp_measure_time, this, true);
        mFirstTv = (TextView) this.findViewById(R.id.view_bp_measure_time_first_tv);
        mSecondTv = (TextView) this.findViewById(R.id.view_bp_measure_time_second_tv);
        mFirstTv.setOnClickListener(this);
        mSecondTv.setOnClickListener(this);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.bpMeasureTime);
        mFirstText = attributes.getString(R.styleable.bpMeasureTime_bmt_FirstText);
        mSecondText = attributes.getString(R.styleable.bpMeasureTime_bmt_SecondText);
        mFirstTextSize = attributes.getDimension(R.styleable.bpMeasureTime_bmt_FirstTextSize, DisplayUtils.sp2px(context, 16.0f));
        mSecondTextSize = attributes.getDimension(R.styleable.bpMeasureTime_bmt_SecondTextSize, DisplayUtils.sp2px(context, 16.0f));
        mSelectText = attributes.getInteger(R.styleable.bpMeasureTime_bmt_SelectStatus, 1);
        attributes.recycle();
        if (!TextUtils.isEmpty(mFirstText)) {
            mFirstTv.setText(mFirstText);
        }
        if (!TextUtils.isEmpty(mSecondText)) {
            mSecondTv.setText(mSecondText);
        }
        mFirstTv.setTextSize(DisplayUtils.px2sp(context, mFirstTextSize));
        mSecondTv.setTextSize(DisplayUtils.px2sp(context, mSecondTextSize));
        switch (mSelectText) {
            case 1:
                mFirstTv.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_bp_measure_time));
                mFirstTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                break;
            case 2:
                mSecondTv.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_bp_measure_time));
                mSecondTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_bp_measure_time_first_tv:
                if (mSelectText != 1) {
                    mSelectText = 1;
                    mFirstTv.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_bp_measure_time));
                    mFirstTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                    mSecondTv.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_bp_measure_time_normal));
                    mSecondTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_black));
                }
                break;
            case R.id.view_bp_measure_time_second_tv:
                if (mSelectText != 2) {
                    mSelectText = 2;
                    mFirstTv.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_bp_measure_time_normal));
                    mFirstTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_black));
                    mSecondTv.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_bp_measure_time));
                    mSecondTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                }
                break;
        }
    }
}
