package com.dftaihua.dfth_threeinone.activity;

import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;

/**
 * Created by Administrator on 2017/1/23 0023.
 */
public class MyServiceActivity extends BaseActivity{

    public MyServiceActivity() {
        mTitleNameRes = R.string.title_activity_my_service;
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
