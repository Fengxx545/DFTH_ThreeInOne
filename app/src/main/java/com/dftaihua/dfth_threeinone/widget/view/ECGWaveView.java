package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dfth.sdk.model.ecg.ECGMeasureData;

import java.util.List;

public class ECGWaveView extends SurfaceView implements SurfaceHolder.Callback2 {
    private ECGWaveViewHelper mHelper;
    private WaveViewRecreate mCreate;
    private ECGLeaderView mLeaderView;//导联显示
    private int mGrids = 15;

    public interface WaveViewRecreate {
        public void recreateView();
    }

    public ECGWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHelper = new ECGWaveViewHelper();
        getHolder().addCallback(this);
    }

    public void leaderChange(boolean[] leaders) {
        if (mHelper != null) {
            mHelper.setDisplayLeader(leaders);
        }
        waveViewReset();
    }

    public void setWaveViewRecreate(WaveViewRecreate create) {
        this.mCreate = create;
    }

    public boolean initDrawOver() {
        return mHelper.isInit();
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

    public void drawWave(ECGMeasureData data) {
        final SurfaceHolder holder = getHolder();
        Canvas canvas = null;
        try {
            float posX = mHelper.getPosX();
            Rect dirt = getDirtRect(posX, data);
            canvas = holder.lockCanvas(dirt);
            if (canvas != null) {
                drawBlockData(canvas, data, dirt);
                mHelper.drawGain(canvas);
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private final Rect mDirt = new Rect();

    private Rect getDirtRect(float posX, ECGMeasureData data) {
        if (data.pts() > 400 / (1000 / data.sampling())) {
            mDirt.left = (int) posX;
            mDirt.right = (int) (posX + getWidth());
            mDirt.top = 0;
            mDirt.bottom = getHeight();
        } else {
            mDirt.left = (int) posX;
            mDirt.right = (int) (posX + getWidth() / mGrids) + 8;
            mDirt.top = 0;
            mDirt.bottom = getHeight();
        }
        return mDirt;
    }

    private void drawBlockData(Canvas canvas, ECGMeasureData data, Rect rect) {
        rect.right = rect.right - 8;
        mHelper.drawGridRefresh(canvas, rect);
        mHelper.drawBack(canvas);
        mHelper.drawGrids(canvas, rect);
        mHelper.drawBlockData(canvas, data, true);
    }

    private void initWaveView(Canvas canvas) {
        mHelper.drawBack(canvas);
        mHelper.drawGrids(canvas, new Rect(0, 0, this.getWidth(), this
                .getHeight()));
        mHelper.drawGain(canvas);
    }

    public void waveViewReset() {
        final SurfaceHolder holder = getHolder();
        synchronized (holder) {
            Canvas canvas = holder.lockCanvas(null);
            if (canvas != null) {
                initWaveView(canvas);
            }
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
            if (mHelper != null) {
                mHelper.reset();
            }
        }
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        createView(holder, getWidth(), getHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        createView(holder, getWidth(), getHeight());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        createView(holder, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void createView(SurfaceHolder holder, int width, int height) {
        if (mHelper != null) {
            mHelper.setWidthHeight(width, height);
            mHelper.setGridThickLineColor(Color.RED);
            mHelper.setGridThinLineColor(Color.rgb(247, 200, 200));
            Canvas canvas = holder.lockCanvas(null);
            if (canvas != null) {
                initWaveView(canvas);
            }
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
            if (mCreate != null) {
                mCreate.recreateView();
            }
        }
    }

    public void setIsDisplayLeaders(boolean display) {
        mLeaderView.setVisibility(display ? VISIBLE : GONE);
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
}