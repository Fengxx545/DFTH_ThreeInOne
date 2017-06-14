package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dfth.sdk.device.DfthDevice;


/**
 * Created by Administrator on 2015/8/31 0031.
 */
public class TabView extends RelativeLayout {
    TextView mTabContent;
    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.tab_layout, this, true);
        mTabContent = (TextView) findViewById(R.id.tab_content);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.tab_view_item);
        String s = ta.getString(R.styleable.tab_view_item_tab_view_item_content);
        mTabContent.setText(s);
        ta.recycle();
    }

    public void setTabContent(String content){
        mTabContent.setText(content);
    }


    public void setViewFocus(boolean isFocus,int deviceType){
        if (deviceType == DfthDevice.SingleDevice) {
            if (isFocus) {
                setBackgroundResource(R.color.single_color);
                mTabContent.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
            } else {
                setBackgroundResource(R.color.ecg_detail_viewpager_back);
                mTabContent.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_black));
            }
        }else{
            if (isFocus) {
                setBackgroundResource(R.color.twelve_color);
                mTabContent.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
            } else {
                setBackgroundResource(R.color.ecg_detail_viewpager_back);
                mTabContent.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_black));
            }
        }
    }

    public TextView getTextView() {
        return mTabContent;
    }
}
