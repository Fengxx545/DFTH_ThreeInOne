package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.TextViewUtils;

/**
 * Created by Administrator on 2017/3/18 0018.
 */
public class BpMeasurePlanView extends LinearLayout {

    private TextView planTime;

    public BpMeasurePlanView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BpMeasurePlanView(Context context) {
        this(context, null);
        planTime = new TextView(context);
        float size = DisplayUtils.sp2px(context,30);
        planTime.setTextSize(size);
        planTime.setTextColor(Color.WHITE);
        planTime.setSingleLine(true);
        this.addView(planTime);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = this.getWidth();
        int everyHight = this.getHeight() / 3;
        for(int i = 0 ; i < this.getChildCount(); i++){
            TextView child = (TextView) this.getChildAt(i);
            int height = child.getLineHeight();
            int childDis[] = TextViewUtils.getTextViewWidth((TextView) child);
            if(i == 0)child.layout((width - childDis[0] ) / 2, (int)(everyHight - height), width,1 * everyHight);
            if(i == 1)child.layout((width - childDis[0] ) / 2, everyHight, width, (int)(everyHight + height));
            if(i == 2)child.layout((width - childDis[0] ) / 2, (int)(everyHight + height), width, 3 * everyHight);
        }
    }
}
