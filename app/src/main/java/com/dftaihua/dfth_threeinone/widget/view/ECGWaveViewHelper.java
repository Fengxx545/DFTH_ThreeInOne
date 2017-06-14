package com.dftaihua.dfth_threeinone.widget.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dfth.sdk.model.ecg.ECGMeasureData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leezhiqiang on 2017/3/21.
 */

public class ECGWaveViewHelper {
    private float mWidth;//区域宽度
    private float mHeight;//区域高度
    private int mGrids = 15;//网格数
    private float mPerGridPx;//每格的像素
    private int mGainOffset;
    private float[] mHorizontalLines;//心电网络横线数组
    private float[] mVerticalLines;//心电网络横线数组
    private final Paint mGridThinLinePaint = new Paint();
    private final Paint mGridThickLinePaint = new Paint();
    private final Paint mGainPaint = new Paint();
    private final Paint mLinePaint = new Paint();
    private int mZoom = 1;
    private float mOrigin = 0;
    private List<ECGWaveFormInfo> mECGWaveFormInfos = new ArrayList<>();

    public ECGWaveViewHelper() {
    }

    public void setGrids(int grid) {
        this.mGrids = grid;
        setWidthHeight(mWidth, mHeight);
    }

    public void setGainGrids(int offset) {
        mGainOffset = offset;
    }

    public void setWidthHeight(float width, float height) {
        mWidth = width;
        mHeight = height;
        mPerGridPx = mWidth / mGrids;
        initLines();
        initECGWaveFormInfos();
        changeBaseLine(getLeaders());
    }

    private void initECGWaveFormInfos() {
        if (mECGWaveFormInfos.size() == 0) {
            for (int i = 0; i < 12; i++) {
                ECGWaveFormInfo info = new ECGWaveFormInfo();
                mECGWaveFormInfos.add(info);
            }
        }
    }

    public float getPerGridPx() {
        return mPerGridPx;
    }

    public void setGridThickLineColor(int gridThickLineColor) {
        mGridThickLinePaint.setColor(gridThickLineColor);
    }

    public void setGridThinLineColor(int gridThinLineColor) {
        mGridThinLinePaint.setColor(gridThinLineColor);
    }

    public void setGridThicklineWidth(int gridThicklineWidth) {
        mGridThickLinePaint.setStrokeWidth(gridThicklineWidth);
    }

    public void setGridThinlineWidth(int gridThinlineWidth) {
        mGridThinLinePaint.setStrokeWidth(gridThinlineWidth);
    }

    public void setOriginPoint(float point) {
        this.mOrigin = point;
    }

    public void setZoom(int zoom) {
        mZoom = zoom;
    }

    private void initLines() {
        int linesNumber = (int) (mWidth / mPerGridPx * 5) + 1;
        final float perLinePx = mPerGridPx / 5;
        mVerticalLines = new float[linesNumber];
        for (int i = 0; i < linesNumber; i++) {
            mVerticalLines[i] = perLinePx * i;
        }
        linesNumber = (int) (mHeight / mPerGridPx * 5) + 1;
        mHorizontalLines = new float[linesNumber];
        for (int i = 0; i < linesNumber; i++) {
            mHorizontalLines[i] = perLinePx * i;
        }
    }

    public void drawBack(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        canvas.drawPaint(p);
    }

    private void checkFirstPoint(ECGMeasureData blockData) {
        for (int i = 0; i < blockData.chan(); i++) {
            ECGWaveFormInfo info = mECGWaveFormInfos.get(i);
            if ((int) (info.mPosX) <= mOrigin) {
                info.mPosX = mOrigin;
                info.mPosY = info.mBaseLine;
            }
        }
    }

    public boolean[] getLeaders() {
        boolean[] leaders = new boolean[mECGWaveFormInfos.size()];
        for (int i = 0; i < leaders.length; i++) {
            ECGWaveFormInfo info = mECGWaveFormInfos.get(i);
            leaders[i] = info.mIsDraw;
        }
        return leaders;
    }

    private void changeBaseLine(boolean[] leaders) {
        int visible_count = 0;
        for (int i = 0; i < leaders.length; i++) {
            if (leaders[i]) {
                visible_count++;
            }
        }
        float step_y = (mHeight - mPerGridPx) / (2.0f * visible_count);
        int visible_index = 0;
        for (int i = 0; i < leaders.length; i++) {
            if (leaders[i]) {
                ECGWaveFormInfo info = mECGWaveFormInfos.get(i);
                info.mBaseLine = mPerGridPx + step_y
                        * (2 * visible_index + 1);
                visible_index++;
            }
        }
    }

    public void setDisplayLeader(boolean[] leaders) {
        initECGWaveFormInfos();
        for (int i = 0; i < leaders.length; i++) {
            ECGWaveFormInfo info = mECGWaveFormInfos.get(i);
            info.mIsDraw = leaders[i];
        }
        changeBaseLine(leaders);
    }

    public void reset() {
        for (int i = 0; i < mECGWaveFormInfos.size(); i++) {
            ECGWaveFormInfo info = mECGWaveFormInfos.get(i);
            info.mPosX = mOrigin;
            info.mPosY = info.mBaseLine;
        }
    }

    public void drawGridRefresh(Canvas canvas, Rect rect) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2.5f);
        paint.setAntiAlias(true);
        Rect dirt = new Rect();
        dirt.left = rect.right;
        dirt.right = rect.right + 8;
        dirt.top = 0;
        dirt.bottom = rect.height();
        canvas.drawRect(dirt, paint);
    }

    public void drawGain(Canvas canvas) {
        mGainPaint.setColor(Color.BLACK);
        mGainPaint.setAntiAlias(true);
        mGainPaint.setTextSize(mPerGridPx / 3f);
        mGainPaint.setStyle(Paint.Style.FILL);
        int i;
        for (i = 0; i < mHeight / mPerGridPx; i++) {
            if (i * mPerGridPx - (mHeight / 2) > mPerGridPx / 2f) {
                break;
            }
        }
        float l = mPerGridPx + 1;
        final float perLineWidth = mPerGridPx / 5;
        canvas.drawLine(l, (i + 2) * mPerGridPx, l + perLineWidth, (i + 2) * mPerGridPx, mGainPaint);
        canvas.drawLine(l + perLineWidth, (i + 2) * mPerGridPx, l + perLineWidth, i * mPerGridPx, mGainPaint);
        canvas.drawLine(l + perLineWidth, i * mPerGridPx, l + 4 * perLineWidth, i * mPerGridPx, mGainPaint);
        canvas.drawLine(l + 4 * perLineWidth, i * mPerGridPx, l + 4 * perLineWidth, (i + 2) * mPerGridPx, mGainPaint);
        canvas.drawLine(l + 4 * perLineWidth, (i + 2) * mPerGridPx, l + 5 * perLineWidth, (i + 2) * mPerGridPx, mGainPaint);
        float left = mGrids - 3.5f;
        canvas.drawText(ThreeInOneApplication.getStringRes(R.string.ecg_gain), left * mPerGridPx, DisplayUtils.getTextHeight(mGainPaint, (2 * (mGainOffset)  - 1) * mPerGridPx), mGainPaint);
        canvas.drawText(ThreeInOneApplication.getStringRes(R.string.ecg_rate), left * mPerGridPx, DisplayUtils.getTextHeight(mGainPaint, (2 * (mGainOffset + 1) - 1) * mPerGridPx), mGainPaint);
    }

    private float rangeY(float y){
        if(y > mHeight){
            y = mHeight + 1;
        }
        if(y < 0){
            y = -1;
        }
        return y;
    }


    public void drawBlockData(Canvas canvas, ECGMeasureData blockData, boolean isMaxOneScreen) {
        checkFirstPoint(blockData);
        float sampling = blockData.sampling();
        float adunit = blockData.adunit();
        float step = mPerGridPx * 5 / sampling;
        mLinePaint.setStrokeWidth(2.5f);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(ThreeInOneApplication.getColorRes(R.color.google_black));
        float posX = mECGWaveFormInfos.get(0).mPosX;
        for (int i = 0; i < blockData.chan(); i++) {
            ECGWaveFormInfo info = mECGWaveFormInfos.get(i);
            float curX = posX;
            float curY = info.mPosY;
            float baseLine = info.mBaseLine;
            boolean isDraw = info.mIsDraw;
            for (int j = 0; j < blockData.pts(); j++) {
                float x = curX + step * mZoom;
                short val = blockData.getData(i, j);
                float y = -((val) / adunit) * mPerGridPx * 2 * mZoom;
                y += baseLine;
                y = rangeY(y);
                if (isDraw) {
                    canvas.drawLine(curX, curY, x, y, mLinePaint);
                }
                curX = x;
                curY = y;
                if (isMaxOneScreen && Math.abs(curX) >= (mVerticalLines[mVerticalLines.length - 1] - 1)) {
                    curX = mOrigin;
                    break;
                }
            }
            info.mPosX = curX;
            info.mPosY = curY;
//            Log.e("dfth_sdk","chan = " + i + "----posx--->" + info.mPosX);
        }
    }

    public void drawGrids(Canvas canvas, Rect rect) {
        try {
            int offset = 0;// 从第几根线开始绘制
            for (int i = 0; i < mVerticalLines.length; i++) {// 找出最小的number
                float value1 = rect.left - mVerticalLines[i];
                if (Math.abs(value1) < 7.2f) {
                    offset = i;
                    break;
                }
            }
            int linesNumber = (int) (rect.width() / mPerGridPx * 5) + 1;
            for (int i = offset; i < linesNumber + offset; i++) {
                if (i % 5 == 0) {
                    canvas.drawLine(mVerticalLines[i], rect.top, mVerticalLines[i], rect.bottom,
                            mGridThickLinePaint);
                } else {
                    canvas.drawLine(mVerticalLines[i], rect.top, mVerticalLines[i], rect.bottom,
                            mGridThinLinePaint);
                }
            }
            linesNumber = (int) (rect.height() / mPerGridPx * 5);
            for (int i = 0; i < mHorizontalLines.length; i++) {// 找出最小的number
                float value1 = rect.top - mHorizontalLines[i];
                if (Math.abs(value1) < 7.2f) {
                    offset = i;
                    break;
                }
            }
            for (int i = offset; i < linesNumber + offset; i++) {
                if (i % 5 == 0) {
                    canvas.drawLine(rect.left, mHorizontalLines[i], rect.right, mHorizontalLines[i],
                            mGridThickLinePaint);
                } else {
                    canvas.drawLine(rect.left, mHorizontalLines[i], rect.right, mHorizontalLines[i],
                            mGridThinLinePaint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPosX() {
        return mECGWaveFormInfos.get(0).mPosX;
    }

    public boolean isInit() {
        return mWidth > 0;
    }

    final class ECGWaveFormInfo {
        float mPosX;//波形的x位置
        float mPosY;//波形的Y位置
        float mBaseLine;//波形基线的位置
        boolean mIsDraw;//是否绘制
    }

    public List<ECGWaveFormInfo> getECGWaveFormInfos() {
        return mECGWaveFormInfos;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }
}
