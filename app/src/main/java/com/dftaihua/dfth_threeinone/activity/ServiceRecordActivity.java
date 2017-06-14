package com.dftaihua.dfth_threeinone.activity;

import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;

/**
 * Created by Administrator on 2017/1/23 0023.
 */
public class ServiceRecordActivity extends BaseActivity{

    public ServiceRecordActivity() {
        mTitleNameRes = R.string.title_activity_service_record;
        mTitleColorRes = R.color.google_white;
        mTitleNameColorRes  = R.color.google_black;
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
    }

    @Override
    public View initView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_to_expect,null);
    }

    @Override
    public void initData() {

    }
}
