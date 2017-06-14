package com.dftaihua.dfth_threeinone.activity;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.debug.ChooseActivity;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;

/**
 * Created by Administrator on 2017/4/11 0011.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private int downCount = 0;

    public AboutActivity(){
        mTitleNameRes = R.string.title_activity_about_us;
        mTitleColorRes = R.color.google_white;
        mTitleNameColorRes  = R.color.google_black;
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_about_us,null);
        view.findViewById(R.id.show_about_us).setOnClickListener(this);
        return view;
    }

    @Override
    public void initData() {

    }

    private void clickEvent(){
        if(downCount >= 4){
            downCount = 0;
            handler.removeMessages(0);
            ActivitySkipUtils.skipAnotherActivity(this,ChooseActivity.class);
        }else{
            downCount ++;
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0,4000);
        }
    }

    @Override
    public void onClick(View view) {
        clickEvent();
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            downCount = 0;
        }
    };
}
