package com.dftaihua.dfth_threeinone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.widget.AMBChartView;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14 0024.
 */
public class AmbChartActivity extends BaseActivity {
    private AMBChartView mChartView;
    private List<BpResult> mResults;
    private String mSelectDay;
    private BpPlan mBpPlan;

    public AmbChartActivity() {
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
        mTitleColorRes = R.color.google_white;
        mTitleNameRes = R.string.title_activity_amb_chart;
        mTitleBackRes = R.drawable.arrow_back_black;
        mTitleNameColorRes = R.color.google_black;
    }

    @Override
    public View initView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
//        mResults = (List<BpResult>) intent.getSerializableExtra("BpResultList");
        mResults = (List<BpResult>) bundle.getSerializable("BpResultList");
        mBpPlan = (BpPlan) bundle.getSerializable("BpPlan");
        mSelectDay = bundle.getString("day");
        View view = LayoutInflater.from(this).inflate(R.layout.activity_amb_chart, null);
        mChartView = (AMBChartView) view.findViewById(R.id.activity_amb_chart_acv);
        mChartView.setIsLandscape(true);
        boolean isHighValue;
        if(mResults == null || mResults.size() == 0){
            isHighValue = false;
        } else{
            isHighValue = BpDataUtils.isHighValue(mResults);
        }
        mChartView.setDatas(mResults, mSelectDay, mBpPlan, isHighValue, false);
        return view;
    }

    @Override
    public void initData() {

    }
}
