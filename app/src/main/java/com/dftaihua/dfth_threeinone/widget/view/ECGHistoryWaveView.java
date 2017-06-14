package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.api.TouchEventHelper;
import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.WaveViewMediator;
import com.dftaihua.dfth_threeinone.utils.ScreenUtils;
import com.dfth.sdk.file.ecg.ECGResultFile;
import com.dfth.sdk.model.ecg.ECGMeasureData;

/**
 * Created by leezhiqiang on 2017/4/1.
 */
public class ECGHistoryWaveView extends ViewGroup implements Component<WaveViewMediator> {
    private ECGLeaderView mLeaderView;//导联显示
    private HistoryECGItemWaveView mECGItemWaveView;//心电图
    private ECGGridView mECGWaveView;//心电网格
    private TouchEventHelper mTouchHelper;
    private WaveViewMediator mWaveViewMediator;
    private Scroller mScroller;
    private ECGResultFile mECGResultFile;
    private String mTime;

    public ECGHistoryWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ecg_history_wave_view, this, true);
        mLeaderView = (ECGLeaderView) findViewById(R.id.ecg_history_view_leader);
        mECGItemWaveView = (HistoryECGItemWaveView) findViewById(R.id.ecg_history_view_wave);
        mECGWaveView = (ECGGridView) findViewById(R.id.ecg_history_view_grid);
        mECGWaveView.setLine(1);
        mLeaderView.startDraw();
        mTouchHelper = new TouchEventHelper(context, mListener);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if (changed) {
            mLeaderView.layout(0, t, getWidth() / 15, b);
            mECGWaveView.layout(0, t, getWidth(), b);
            int offset = mLeaderView.getVisibility() == VISIBLE ? getWidth() / 15 : 0;
            mECGWaveView.setOriginPoint(offset);
            mECGItemWaveView.layout(offset, t, getWidth(), b);
//        }
    }

    public void setIsDisplayLeaders(boolean display) {
        mLeaderView.setVisibility(display ? VISIBLE : GONE);
    }

    public void leaderChange(boolean[] leaders) {
        mECGWaveView.leaderChange(leaders);
        mLeaderView.setLeaders(mECGWaveView.getWaveFormInfos());
        mECGWaveView.postInvalidate();
    }

    public void drawWave(ECGMeasureData data) {
        mECGItemWaveView.setEcgResultFile(mECGResultFile);
        mECGItemWaveView.setParams(1, mECGWaveView.getWaveFormInfos(), data);
        mECGItemWaveView.setPosTime(mTime);
    }

    public void setEcgResultFile(ECGResultFile file){
        mECGResultFile = file;
    }
    public void setPosTime(String time){
        mTime = time;
        mECGItemWaveView.setPosTime(mTime);
    }

    @Override
    public void bindMediator(WaveViewMediator mediator) {
        mWaveViewMediator = mediator;
    }

    public boolean[] getLeaders() {
        return mECGWaveView.getLeaders();
    }

    private int getScreenPos() {
        return mLeaderView.getVisibility() == GONE ? (ScreenUtils.isScreenLandscape(getContext()) ? 25 * 50 : 15*50) : (ScreenUtils.isScreenLandscape(getContext()) ? 24 * 50 : 14 * 50);
    }

    GestureDetector.SimpleOnGestureListener mListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            if (mWaveViewMediator != null) {
                mWaveViewMediator.longPressScreen();
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mWaveViewMediator != null) {
                mWaveViewMediator.singlePressScreen();
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mWaveViewMediator != null) {
                mWaveViewMediator.doublePressScreen();
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int width = mECGItemWaveView.getWidth();
            int screenPos = getScreenPos();
            int deltaX = (int) (distanceX * screenPos / width);
            if (mWaveViewMediator != null) {
                mWaveViewMediator.waveChange(deltaX);
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("onFling", "x1->" + e1.getX() + "----x2->" + e2.getX());
            mFlingX = (int) e2.getX();
            mScroller.fling((int) e2.getX(), 0, -(int) (velocityX * 0.7f), 0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            postInvalidate();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };
    private int mFlingX;

    public void onScroll(int x) {
        int distance = x - mFlingX;
        int width = mECGItemWaveView.getWidth();
        int screenPos = getScreenPos();
        int deltaX = (int) (distance * screenPos / width);
        if (mWaveViewMediator != null) {
            mWaveViewMediator.waveChange(deltaX);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            onScroll(mScroller.getCurrX());
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchHelper.onTouchEvent(event);
        return true;
    }
}
