package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.listener.BpViewClickListener;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dfth.sdk.model.bp.BpPlan;

/**
 * Created by Administrator on 2017/2/13 0013.
 */
public class BpPlanView extends LinearLayout implements View.OnClickListener {
    private Button mControlBtn;
    private LinearLayout mPlanLl;
    private LinearLayout mPlanTimeLl;
    private BpViewClickListener mListener;
    private TextView mStartTimeTv;
    private TextView mTimeCountTv;

    public BpPlanView(Context context) {
        this(context, null);
    }

    public BpPlanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_bp_plan, this, true);
        mControlBtn = (Button) findViewById(R.id.view_bp_plan_control_btn);
        mPlanLl = (LinearLayout) findViewById(R.id.view_bp_plan_ll);
        mPlanTimeLl = (LinearLayout) findViewById(R.id.view_bp_plan_time_ll);
        mTimeCountTv = (TextView) findViewById(R.id.view_bp_plan_time_tv);
        mStartTimeTv = (TextView) findViewById(R.id.view_bp_plan_start_time_tv);
        mControlBtn.setOnClickListener(this);
    }

    public void setListener(BpViewClickListener listener) {
        mListener = listener;
    }

    public void setPlanView(BpPlan plan){
        if(plan != null){
            mPlanLl.setVisibility(GONE);
            mPlanTimeLl.setVisibility(VISIBLE);
            mControlBtn.setText(R.string.bp_plan_view_stop_plan);
            mStartTimeTv.setText(ThreeInOneApplication.getStringRes(R.string.bp_plan_view_begin_time)
                    + TimeUtils.getTimeStr(plan.getSetPlanTime(),"MM.dd HH:mm"));
        } else{
            mPlanLl.setVisibility(VISIBLE);
            mPlanTimeLl.setVisibility(GONE);
            mControlBtn.setText(R.string.bp_plan_view_start_plan);
        }
    }

    public void setCountTime(long time){
        mTimeCountTv.setText(TimeUtils.getCountTime(time));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_bp_plan_control_btn:
                if(mListener != null){
                    if(mPlanLl.getVisibility() == VISIBLE){
                        mListener.onBpViewClick(1,1);
                    } else{
                        mListener.onBpViewClick(1,2);
                    }
                }
                break;
        }
    }

    public void onDestroy(){

    }
}
