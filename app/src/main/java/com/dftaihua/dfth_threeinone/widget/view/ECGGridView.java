package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;

/**
 * Created by leezhiqiang on 2017/5/12.
 */

public class ECGGridView extends View {
    private ECGWaveViewHelper mHelper;
    private ECGWaveView.WaveViewRecreate mCreate;
    private int mGrids = 15;

    public ECGGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mHelper = new ECGWaveViewHelper();
    }

    public void leaderChange(boolean[] leaders) {
        if (mHelper != null) {
            mHelper.setDisplayLeader(leaders);
        }
        waveViewReset();
    }


    public boolean[] getLeaders() {
        return mHelper.getLeaders();
    }

    public List<ECGWaveViewHelper.ECGWaveFormInfo> getWaveFormInfos() {
        return mHelper.getECGWaveFormInfos();
    }

    public void setOriginPoint(float point) {
        mHelper.setOriginPoint(point);
    }
    private void initWaveView(Canvas canvas) {
        mHelper.drawBack(canvas);
        mHelper.drawGrids(canvas, new Rect(0, 0, this.getWidth(), this
                .getHeight()));
        mHelper.drawGain(canvas);
    }

    public void waveViewReset() {
        if (mHelper != null) {
            mHelper.reset();
        }
        postInvalidate();
    }
    public void setGrids(int grids) {
        mGrids = grids;
        mHelper.setGrids(grids);
    }

    public void setLine(int line) {
        if (line == 1) {
            mHelper.setGainGrids(1);
        } else {
            mHelper.setGainGrids(1);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(Math.floor(mHelper.getHeight()) != getHeight()){
            mHelper.setWidthHeight(getWidth(), getHeight());
            mHelper.setGridThickLineColor(Color.RED);
            mHelper.setGridThinLineColor(Color.rgb(247, 200, 200));
            if (mCreate != null) {
                mCreate.recreateView();
            }
        }
        initWaveView(canvas);
    }
}
