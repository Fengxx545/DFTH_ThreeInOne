package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import java.util.List;

/**
 * Created by leezhiqiang on 2017/3/21.
 */

public class ECGLeaderView extends View{
    private Paint mPaint = new Paint();
    private List<ECGWaveViewHelper.ECGWaveFormInfo> mInfos;
    private static String[] LEADERS_NAME = ThreeInOneApplication.getStrings(R.array.leader_names);
    private boolean mStartDraw;
    public ECGLeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.BLACK);
    }

    public void setLeaders(List<ECGWaveViewHelper.ECGWaveFormInfo> infos){
        mInfos = infos;
        postInvalidate();
    }
    public void startDraw(){
        mStartDraw = true;
        postInvalidate();
    }

    public void endDraw(){
        mStartDraw = false;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!mStartDraw){
            return;
        }
        if(mInfos == null){
            return;
        }
        mPaint.setTextSize(getWidth() / 2.2f);
        for (int i = 0; i < mInfos.size(); i++) {
            ECGWaveViewHelper.ECGWaveFormInfo info = mInfos.get(i);
            if (info.mIsDraw) {
                String text = LEADERS_NAME[i];
                canvas.drawText(text, getWidth() / 2, info.mBaseLine,
                        mPaint);
            }
        }
    }
}
