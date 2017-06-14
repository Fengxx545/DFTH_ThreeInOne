package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.HeartTypeUtils;
import com.dftaihua.dfth_threeinone.utils.ScreenUtils;
import com.dfth.mobliemonitor.measure.ecg.ECGStroageResult;
import com.dfth.sdk.file.ecg.ECGResultFile;
import com.dfth.sdk.model.ecg.ECGMeasureData;

import java.util.List;

/**
 * Created by leezhiqiang on 2017/4/1.
 */

public class HistoryECGItemWaveView extends View{
    private int mZoom;//比例
    private ECGMeasureData mData;
    private Paint mLinePaint = new Paint();
    private List<ECGWaveViewHelper.ECGWaveFormInfo> mInfos;
    private PointF[] mPoints;
    private ECGResultFile mECGResultFile;
    private String mTime;

    public HistoryECGItemWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public void setParams(int zoom, List<ECGWaveViewHelper.ECGWaveFormInfo> leaders, ECGMeasureData data){
        this.mInfos = leaders;
        this.mZoom = zoom;
        this.mData = data;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(mInfos == null || mData == null){
            return;
        }
        drawBlockData(canvas);
    }

    public void setEcgResultFile(ECGResultFile file){
        mECGResultFile = file;
    }
    public void setPosTime(String time){
        mTime = time;
    }

    private void checkPoints(){
        float perGridPx = getWidth() / (ScreenUtils.isScreenLandscape(getContext()) ? 25 : 15);
        if(mPoints == null){
            mPoints = new PointF[mData.chan()];
            for(int i = 0; i < mPoints.length; i++) {
                mPoints[i] = new PointF();
            }
        }
        for(int i = 0; i < mPoints.length; i++) {
            mPoints[i].x = 0;
            short val = mData.getData(i, 0);
            float y = -((val) / mData.adunit()) * perGridPx * 2 * mZoom;
            y += mInfos.get(i).mBaseLine;
            mPoints[i].y = y;
        }
    }
    private void drawBlockData(Canvas canvas){
        Log.e("dfth_sdk","start_draw-->");
        checkPoints();
        float perGridPx = getWidth() / (ScreenUtils.isScreenLandscape(getContext()) ? 25 : 15);
        float sampling = mData.sampling();
        float adunit = mData.adunit();
        float step = perGridPx * 5 / sampling;
        mLinePaint.setStrokeWidth(2.5f);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(ThreeInOneApplication.getColorRes(R.color.google_black));
        for (int i = 0; i < mData.chan(); i++) {
            for (int j = 0; j < mData.pts(); j++) {
                float x = mPoints[i].x + step * mZoom;
                short val = mData.getData(i,j);
                float y = -((val) / adunit) * perGridPx * 2 * mZoom;
                y += mInfos.get(i).mBaseLine;
                if(mInfos.get(i).mIsDraw){
                    canvas.drawLine(mPoints[i].x,mPoints[i].y, x, y, mLinePaint);
                }
                mPoints[i].x = x;
                mPoints[i].y = y;
                if (mECGResultFile != null && mData.chan() == 1) {
                    SparseArray<ECGStroageResult> results = mECGResultFile.getResult(mECGResultFile.pos(),750);
                    if(results.indexOfKey(mECGResultFile.pos() + j) >= 0){
                        ECGStroageResult result = results.get(mECGResultFile.pos() + j);
                        drawHeartType(canvas, x, y,result._beat_type);
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(mTime)) {
            drawTime(canvas, 10, mTime);
        }
        Log.e("dfth_sdk","end_draw-->");
    }
    private void drawHeartType(Canvas canvas, float x, float y, short type) {
        Paint mDrawRPeakPaint = new Paint();
        float perItemWidth = ThreeInOneApplication.getScreenWidth() / (ScreenUtils.isScreenLandscape(getContext()) ? 25 : 15);
        mDrawRPeakPaint.setAntiAlias(true);
        mDrawRPeakPaint.setTextSize(perItemWidth / 1.2f);
        mDrawRPeakPaint.setColor(HeartTypeUtils.getHeartType(type));
        canvas.drawText(HeartTypeUtils.getHeartTypeName(type), x, perItemWidth, mDrawRPeakPaint);
    }

    private void drawTime(Canvas canvas, float x,String time){
        Paint mDrawRPeakPaint = new Paint();
        float perItemWidth = ThreeInOneApplication.getScreenWidth() / (ScreenUtils.isScreenLandscape(getContext()) ? 25 : 15);
        mDrawRPeakPaint.setAntiAlias(true);
        mDrawRPeakPaint.setTextSize(perItemWidth / 2.0f);
        mDrawRPeakPaint.setColor(ThreeInOneApplication.getColorRes(R.color.google_black));
        canvas.drawText(time, x, getHeight() - perItemWidth, mDrawRPeakPaint);
    }
}
