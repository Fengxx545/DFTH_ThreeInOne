package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.api.TouchEventHelper;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.measure.listener.ECGMeasureConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGScreenConduct;
import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.WaveViewMediator;
import com.dftaihua.dfth_threeinone.utils.MathUtils;
import com.dfth.sdk.Others.Utils.LockLinkedList;
import com.dfth.sdk.bluetooth.Action;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.model.ecg.ECGMeasureData;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by leezhiqiang on 2017/3/21.
 */

public class ECGMeasureWaveView extends ViewGroup implements Component<WaveViewMediator>,ECGMeasureConduct,ECGScreenConduct{
    private HorizitalScrollTextView mLeaderOutView;//导联脱落
    private ECGWaveView mWaveView;//心电图
    private ECGLeaderView mLeaderView;//导联显示
    private LockLinkedList<ECGMeasureData> mDatas = new LockLinkedList<>(100);
    private DrawAction mDrawAction;
    private TouchEventHelper mTouchHelper;
    private WaveViewMediator mWaveViewMediator;
    private int mGrids = 15;
    public ECGMeasureWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ecg_measure_view,this,true);
        mLeaderView = (ECGLeaderView) findViewById(R.id.measure_view_leader);
        mWaveView = (ECGWaveView) findViewById(R.id.measure_view_wave);
        mLeaderOutView = (HorizitalScrollTextView) findViewById(R.id.measure_view_leader_out);
        mTouchHelper = new TouchEventHelper(context,mListener);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            mLeaderView.layout(0,0,getWidth() / mGrids,getHeight());
            mWaveView.setOriginPoint(mLeaderView.getVisibility() == VISIBLE ? getWidth() / mGrids : 0);
            mWaveView.setGrids(mGrids);
            mWaveView.layout(0,0,getWidth(),getHeight());
            int gridWidth = getWidth() / mGrids;
            mLeaderOutView.layout(gridWidth * 3 / 2, gridWidth / 2, getWidth() - gridWidth * 3 / 2, 2 * getWidth() / mGrids);
//            mLeaderOutView.layout(0,0,getWidth(),2 * getWidth() / mGrids);
        }
    }

    @Override
    public void bindMediator(WaveViewMediator mediator) {
        mWaveViewMediator = mediator;
    }

    public void setIsDisplayLeaders(boolean display){
        mLeaderView.setVisibility(display?VISIBLE:GONE);
    }

    public void resumeDraw(){
        mDatas.clear();
        if(mDrawAction != null){
            mWaveView.waveViewReset();
            mDrawAction.mPause = false;
            mLeaderView.startDraw();
        }
    }

    public void startDraw(){
        if(mDrawAction == null){
            mDrawAction = new DrawAction();
            DispatchUtils.performAsnycAction(mDrawAction, Schedulers.newThread());
        }
        mDrawAction.mPause = false;
        mWaveView.waveViewReset();
        mLeaderView.startDraw();
    }

    public void endDraw(){
        mDatas.clear();
        if(mDrawAction != null){
            mDrawAction.cancel();
            mDrawAction = null;
        }
        mWaveView.waveViewReset();
        mLeaderView.endDraw();
    }
    public void pauseDraw(){
        if(mDrawAction != null){
            mDrawAction.mPause = true;
        }
    }
    public void drawWave(ECGMeasureData data){
        mDatas.addObject(data);
    }

    public void leaderChange(boolean[] leaders){
        mWaveView.leaderChange(leaders);
        mLeaderView.setLeaders(mWaveView.getWaveFormInfos());
    }

    public boolean[] getLeaders(){
        return mWaveView.getLeaders();
    }

    private final class DrawAction extends Action{
        private boolean mRunner;
        private boolean mPause;
        public DrawAction() {
            super(0);
        }
        @Override
        protected void perform() {
            mRunner = true;
            while (mRunner){
                try {
                    Thread.sleep(50);
                    if(mPause) continue;
                    ECGMeasureData data = mDatas.getObject();
                    if(data != null){
                        mWaveView.drawWave(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        public void cancel(){
            mRunner = false;
            stop();
        }
    }

    public void drawLeaderOut(boolean[] leaderOut){
        String[] leaderOutNames = ThreeInOneApplication.getStrings(R.array.leader_out_names);
        if(MathUtils.binarySearch(leaderOut,true) >= 0){
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < leaderOut.length; i++) {
                if (leaderOut[i]) {
                    builder.append(leaderOutNames[i]).append(",");
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(ThreeInOneApplication
                    .getStringRes(R.string.ecg_leader_out));
            mLeaderOutView.setVisibility(VISIBLE);
            mLeaderOutView.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
            mLeaderOutView.setText(builder.toString());
        }else{
            mLeaderOutView.setVisibility(GONE);
            mLeaderOutView.setText("");
        }
    }

    GestureDetector.SimpleOnGestureListener mListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            if(mWaveViewMediator != null){
                mWaveViewMediator.longPressScreen();
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(mWaveViewMediator != null){
                mWaveViewMediator.singlePressScreen();
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchHelper.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean isDrawing() {
        return mDrawAction != null;
    }

    @Override
    public void destroyDraw() {
        DispatchUtils.performMainThreadDelay(new DispatchTask() {
            @Override
            public void accept(Object o) throws Exception {
                mWaveView.setVisibility(GONE);
            }
        },300);
    }

    @Override
    public void orientationChange(int orientation) {
        mGrids = orientation == Configuration.ORIENTATION_LANDSCAPE ? 25 : 15;
        requestLayout();
    }

    @Override
    public void setLine(int line) {
        mWaveView.setLine(line);
    }
}
