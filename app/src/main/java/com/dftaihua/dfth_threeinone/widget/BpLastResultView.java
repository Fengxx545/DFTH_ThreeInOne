package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.BpResultActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dfth.sdk.model.bp.BpResult;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/2/13 0013.
 */
public class BpLastResultView extends LinearLayout implements View.OnClickListener{
    private LinearLayout mResultLl;
    private LinearLayout mNoResultLl;
    private Button mCheckMoreBtn;
    private TextView mHighTv;
    private TextView mLowTv;
    private TextView mRateTv;
    private TextView mDesTv;
//    private TextView mTimeTv;
    private TextView mHighUnitTv;
    private TextView mLowUnitTv;
    private int mDataType;
    private int mUnit;
    private HashMap<String,Integer> mHashMap = new HashMap<>();

    public BpLastResultView(Context context) {
        this(context, null);
    }

    public BpLastResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_last_bp_result,this,true);
        mResultLl = (LinearLayout) findViewById(R.id.view_last_bp_result_ll);
        mNoResultLl = (LinearLayout) findViewById(R.id.view_last_bp_no_result_ll);
        mCheckMoreBtn = (Button) findViewById(R.id.view_last_bp_check_more_btn);
        mHighTv = (TextView) findViewById(R.id.view_last_bp_result_high_tv);
        mLowTv = (TextView) findViewById(R.id.view_last_bp_result_low_tv);
        mRateTv = (TextView) findViewById(R.id.view_last_bp_result_rate_tv);
        mDesTv = (TextView) findViewById(R.id.view_last_bp_result_des_tv);
//        mTimeTv = (TextView) findViewById(R.id.view_last_bp_result_measure_time_tv);
        mHighUnitTv = (TextView) findViewById(R.id.view_last_bp_result_high_unit_tv);
        mLowUnitTv = (TextView) findViewById(R.id.view_last_bp_result_low_unit_tv);
        mUnit = (int) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.BP_VALUE_UNIT, 1);
        if(mUnit == Constant.BP_UNIT_MMHG){
            mHighUnitTv.setText(R.string.bp_result_unit_mmHg);
            mLowUnitTv.setText(R.string.bp_result_unit_mmHg);
        } else{
            mHighUnitTv.setText(R.string.bp_result_unit_kPa);
            mLowUnitTv.setText(R.string.bp_result_unit_kPa);
        }
        mCheckMoreBtn.setOnClickListener(this);
    }

    public void setLastResult(BpResult result){
        if(result != null){
            mDataType = result.getPlanId() > 0 ? Constant.BP_PLAN_DATA : Constant.BP_MANUAL_DATA;
            int[] avers = {result.getSbp(), result.getDbp(), result.getPulseRate()};
            int res = !result.isValidData() ? R.string.bp_result_invalid : BpDataUtils.getLevel(avers);
            String des = ThreeInOneApplication.getStringRes(res);
            mDesTv.setText(des);
            mDesTv.setTextColor(Color.BLACK);
            if(result.isValidData() && (res == R.string.bp_result_higher || res == R.string.bp_result_highest)){
                mDesTv.setTextColor(Color.RED);
            }
            mResultLl.setVisibility(VISIBLE);
            mNoResultLl.setVisibility(GONE);
            mHighTv.setText(result.getSbp() == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(getContext(), result.getSbp()));
            mLowTv.setText(result.getDbp() == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(getContext(), result.getDbp()));
            mRateTv.setText(result.getPulseRate() == 0 ? "- -" : String.valueOf(result.getPulseRate()));
//            mTimeTv.setText(TimeUtils.getTimeStr(result.getMeasureTime(),"yyyy.MM.dd HH:mm:ss"));
        } else{
            mResultLl.setVisibility(GONE);
            mNoResultLl.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_last_bp_check_more_btn:
                mHashMap.put(Constant.BP_DATA_TYPE, 2);
                ActivitySkipUtils.skipAnotherActivity(getContext(), BpResultActivity.class, mHashMap);
                break;
        }
    }
}
