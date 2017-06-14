package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.listener.BpDataItemClickListener;

public class BpDataView extends LinearLayout implements View.OnClickListener {
    private LinearLayout mHomeLl;
    private TextView mTimeTv;
    private TextView mHighTv;
    private TextView mLowTv;
    private TextView mRateTv;
    private TextView mPatternTv;
    private TextView mMeasureTimesTv;
    private TextView mCircleTv;
    private boolean isSelect = false;
    private BpDataItemClickListener mListener;

    public BpDataView(Context context) {
        super(context);
        initialize();
    }

    public BpDataView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        initialize();
    }

    private void initialize() {
        setGravity(Gravity.CENTER);
        LayoutInflater.from(getContext()).inflate(R.layout.item_bp_data, this);
        mHomeLl = (LinearLayout) findViewById(R.id.item_bp_data_ll);
        mTimeTv = (TextView) findViewById(R.id.item_bp_data_time_tv);
        mHighTv = (TextView) findViewById(R.id.item_bp_data_high_tv);
        mLowTv = (TextView) findViewById(R.id.item_bp_data_low_tv);
        mRateTv = (TextView) findViewById(R.id.item_bp_data_rate_tv);
        mPatternTv = (TextView) findViewById(R.id.item_bp_data_pattern_tv);
        mCircleTv = (TextView) findViewById(R.id.item_bp_data_circle_tv);
        mMeasureTimesTv = (TextView)findViewById(R.id.item_bp_data_measure_times_tv);
        setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        isSelect = !isSelect;
        if(isSelect){
            mHomeLl.setBackgroundResource(R.drawable.shape_bp_data_back_select);
            mCircleTv.setBackgroundResource(R.drawable.shape_bp_data_circle_select);
            if(mListener != null){
                mListener.onItemClick(this);
            }
        } else{
            mHomeLl.setBackgroundResource(R.drawable.shape_bp_data_back);
            mCircleTv.setBackgroundResource(R.drawable.shape_bp_data_circle);
            if(mListener != null){
                mListener.onItemClick(null);
            }
        }
    }

    public void setListener(BpDataItemClickListener listener){
        mListener = listener;
    }

    public void resetBackground(){
        mHomeLl.setBackgroundResource(R.drawable.shape_bp_data_back);
        mCircleTv.setBackgroundResource(R.drawable.shape_bp_data_circle);
        isSelect = false;
    }

    public TextView getTimeTv() {
        return mTimeTv;
    }

    public TextView getHighTv() {
        return mHighTv;
    }

    public TextView getLowTv() {
        return mLowTv;
    }

    public TextView getRateTv() {
        return mRateTv;
    }

    public TextView getPatternTv() {
        return mPatternTv;
    }

    public TextView getMeasureTimeTv() {
        return mMeasureTimesTv;
    }

}
