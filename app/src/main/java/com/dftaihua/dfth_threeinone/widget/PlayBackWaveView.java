package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.listener.PosListener;
import com.dftaihua.dfth_threeinone.listener.ScrollerListener;
import com.dftaihua.dfth_threeinone.listener.WaveChangeListener;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dfth.mobliemonitor.measure.ecg.ECGStroageResult;
import com.dfth.sdk.file.ecg.ECGResultFile;
import com.dfth.sdk.model.ecg.ECGMeasureData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-05-08.
 */
public class PlayBackWaveView extends RelativeLayout implements PosListener, ScrollerListener {
    private PlayBackBottomView mBottomView;
    private PlayBackTopView mTopView;
    private ECGResultFile mFile;
    private PlayBackScrollView mScrollView;
    private static final int CACHE_COUNT = 4;
    private WaveChangeListener mWaveChangeListener;
    private List<NewWaveView.WaveRectChangeListener> mWaveRectChangeListener = new ArrayList<NewWaveView.WaveRectChangeListener>();
    private int mCurPos;
    private ECGDataView mV;

    public String[] leaderNames = {"I", "II", "III", "avR", "avL", "avF",
            "V1", "V2", "V3", "V4", "V5", "V6"};
    public boolean[] leaders;

    public PlayBackWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBottomView = new PlayBackBottomView(context);
        mTopView = new PlayBackTopView(context);
        leaders = new boolean[12];
        addView(mBottomView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void setECGResultFile(ECGResultFile file) {
        if (mFile != null) {
            return;
        }
        this.mFile = file;
        mScrollView = new PlayBackScrollView(getContext(), this);
        mScrollView.setHorizontalScrollBarEnabled(false);
        addView(mScrollView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LinearLayout l = new LinearLayout(getContext());
        mTopView = new PlayBackTopView(getContext());
        final float maxWidth = ThreeInOneApplication.getScreenWidth();
//        int px = (int) (maxWidth / ECGLookProgressBar.mMaxScreen * mFile.pts());
        int px = (int) (maxWidth / 100 * mFile.pts());
        l.addView(mTopView, px, LinearLayout.LayoutParams.MATCH_PARENT);
        mScrollView.addView(l, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        initDatas(0);
    }

    private void initDatas(int pos) {
//        int length = ECGLookProgressBar.mMaxScreen;
        int length = 100;
        int left = pos >= 2 * length ? pos - 2 * length : 0;
        ECGMeasureData d = (ECGMeasureData) mFile.getData(left, 4 * length);
        mV = new ECGDataView(d, left, mFile, 4 * length, 0);
        if (pos == 0) {
            if (mV.chan == 1) {
                leaders[0] = true;
            } else {
                leaders[0] = true;
                leaders[1] = true;
                leaders[2] = true;
            }
        }
        mCurPos = pos;
    }

    public void setWaveChangeListener(WaveChangeListener mWaveChangeListener) {
        this.mWaveChangeListener = mWaveChangeListener;
    }

    private int posToPx(int pos) {
        final float maxWidth = ThreeInOneApplication.getScreenWidth();
//        return (int) (maxWidth / ECGLookProgressBar.mMaxScreen * pos);
        return (int) (maxWidth / 100 * pos);
    }

    private int pxToPos(int px) {
        final float maxWidth = ThreeInOneApplication.getScreenWidth();
//        return (int) (ECGLookProgressBar.mMaxScreen  / maxWidth * px);
        return (int) (100 / maxWidth * px);
    }

    @Override
    public void onScroll(int x) {
        int pos = pxToPos(x);
//        pos = pos >= ((int)mFile.pts() - ECGLookProgressBar.mMaxScreen)?
//                (int)(mFile.pts() - ECGLookProgressBar.mMaxScreen) : pos;
//        if(pos <= mFile.pts() - ECGLookProgressBar.mMaxScreen){
//            mWaveChangeListener.waveChange(pos);
//            posChange(pos,mFile,ECGLookProgressBar.mMaxScreen,false);
//        }
    }

    @Override
    public void posChange(int pos, ECGResultFile file, int len, int hr) {
        posChange(pos, file, len, true);
    }

    public void posChange(int pos, ECGResultFile file, int len, boolean s) {
        if (s) {
            mScrollView.stopScroller();
        }
//        if(pos > mFile.pts() - ECGLookProgressBar.mMaxScreen){
//            return;
//        }
//        int px = posToPx(pos);
//        if(Math.abs(pos - mCurPos) > (ECGLookProgressBar.mMaxScreen - 50)){
//            initDatas(pos);
//            mTopView.setHistoryData(mV);
//        }
//        if(s){
//            mScrollView.scrollTo(px,0);
//        }
    }

    public void setWaveRectChangeListener(NewWaveView.WaveRectChangeListener waveRectChangeListener) {
        this.mWaveRectChangeListener.add(waveRectChangeListener);
    }

    private class PlayBackScrollView extends HorizontalScrollView {
        private ScrollerListener mListener;
        private boolean mInner = false;
        private boolean mCurrentSreen = false;//半屏
        private GestureDetector mDetector;
        private int mNew;
        private int mOld;

        private PlayBackScrollView(Context context, ScrollerListener listener) {
            super(context);
            mListener = listener;
            mDetector = new GestureDetector(context, mGestureListener);
        }

        void stopScroller() {
            Field[] mField = this.getClass().getSuperclass().getDeclaredFields();
            for (Field f : mField) {
                f.setAccessible(true);
                try {
                    Object o = f.get(this);
                    if (o instanceof Scroller) {
                        Scroller s = (Scroller) o;
                        if (!s.isFinished()) {
                            s.abortAnimation();
                        }
                    }
                    if (o instanceof OverScroller) {
                        OverScroller s = (OverScroller) o;
                        if (!s.isFinished()) {
                            s.abortAnimation();
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            if (mNew != l && mOld != oldl) {
                mNew = l;
                mOld = oldl;
                mListener.onScroll(mNew);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            mDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mInner = false;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mInner) {
                    return true;
                }
            }
            return super.onTouchEvent(event);
        }

        private void screenOut() {
            for (NewWaveView.WaveRectChangeListener l : mWaveRectChangeListener) {
                l.screenOut();
            }
        }

        private void screenIn() {
            for (NewWaveView.WaveRectChangeListener l : mWaveRectChangeListener) {
                l.screenIn();
            }
        }

        private GestureDetector.SimpleOnGestureListener mGestureListener =
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        mInner = true;
                        if (mWaveRectChangeListener != null) {
                            mTopView.perItemWidth = 0;
                            mBottomView.perItemWidth = 0;
                            if (!mCurrentSreen) {
                                screenIn();
                            } else {
                                screenOut();
                            }
                            mCurrentSreen = !mCurrentSreen;
                        }
                        return false;
                    }
                };
    }

    static int count = 0;

    private class PlayBackTopView extends View {
        private int perItemWidth;
        private int ecgViewHeight;
        private float perLineWidth;
        private PointF[] curPos;
        private float[] baseline;
        private short firstPoint;
        private final Paint mDrawLinePaint = new Paint();
        private final Paint mDrawRPeakPaint = new Paint();
        private ECGDataView mHistory;
        private Rect mInvalidRect = new Rect();

        public PlayBackTopView(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            if (perItemWidth < 0.1f) {
                baseline = null;
                perItemWidth = ThreeInOneApplication.getScreenWidth() / 15;
                perLineWidth = perItemWidth / 5f;
                mDrawLinePaint.setColor(Color.BLACK);
                mDrawLinePaint.setStrokeWidth(2);
                mDrawLinePaint.setAntiAlias(true);
                mDrawLinePaint.setStyle(Paint.Style.FILL);
                mDrawRPeakPaint.setAntiAlias(true);
                mDrawRPeakPaint.setTextSize(perItemWidth / 1.2f);

                // 心电波形的位置
                ecgViewHeight = (int) (this.getHeight() - 2 * perItemWidth);

                // 初始化当前波形的位置
                curPos = new PointF[12];
                for (int i = 0; i < curPos.length; i++) {
                    curPos[i] = new PointF(0, 0);
                    if (mV != null && mV.chan == 12) {
                        curPos[i].x = perItemWidth;
                    } else {
                        curPos[i].x = 0;
                    }
                    curPos[i].y = 0;
                }
//                curPos = new PointF(0,0);
            }
            drawBlockData(canvas);
        }

        private void setHistoryData(ECGDataView h) {
            mHistory = h;
            final int maxWidth = ThreeInOneApplication.getScreenWidth();
            if (h != null) {
                int px = posToPx(h.offset);
                invalidate(px, 0, px + 4 * maxWidth, getHeight());
            }
        }

        @Override
        public void invalidate(int l, int t, int r, int b) {
            mInvalidRect.set(l, t, r, b);
            super.invalidate(l, t, r, b);
        }

        public void reset() {
            if (curPos != null) {
                for (int i = 0; i < curPos.length; i++) {
                    curPos[i] = new PointF(0, 0);
                    curPos[i].x = perItemWidth;
                    curPos[i].y = 0;
                }
                baseline = null;
            }
            this.postInvalidate();

//            synchronized (holder) {
//                Canvas canvas = holder.lockCanvas(null);
//                if (canvas != null) {
//                    canvas.drawColor(Color.WHITE);
//                    drawEcgGrid(canvas, new Rect((int) leaderWidth, 0, this.getWidth(), this.getHeight()));
//                    drawGain(canvas);
//                    handler.obtainMessage(0, "").sendToTarget();
//                }
//                if (canvas != null) {
//                    holder.unlockCanvasAndPost(canvas);
//                }
//                if (curPos != null) {
//                    for (int i = 0; i < curPos.length; i++) {
//                        curPos[i] = new PointF(0, 0);
//                        curPos[i].x = (int) leaderWidth;
//                        curPos[i].y = 0;
//                    }
//                    baseline = null;
//                }
//            }
        }


        public synchronized void drawBlockData(Canvas canvas) {
            if (mHistory == null) {
                mHistory = mV;
            }
            if (mHistory != null) {

                short[] data = mHistory.block;
//                leaders = new boolean[12];
                int mLines = mHistory.chan;

                int row = mHistory.chan;
                int col = data.length / row;
                if (baseline == null) {
                    baseline = new float[row];
                    int visible_count = 0;
                    float step_y = 0;
                    if (mHistory.chan == 12) {
                        for (int i = 0; i < leaders.length; i++) {
                            if (leaders[i]) {
                                visible_count++;
                            }
                        }
                        step_y = ecgViewHeight / (2.0f * visible_count);
                    } else {
                        step_y = ecgViewHeight / 2.0f;
                    }

                    int visible_index = 0;
                    for (int i = 0; i < row; i++) {
                        short v = data[i];
                        curPos[i] = new PointF(0, v);

                        if (leaders[i]) {
                            baseline[i] = 2 * perItemWidth + step_y
                                    * (2 * visible_index + 1);
                            visible_index++;
                            curPos[i].y = baseline[i];
                        }
                    }
                }


//                baseline[0] = getHeight() / 2f;
                int count = 0;
                for (int i = 0; i < data.length; i++) {
                    count += data[i];
                }
                firstPoint = (short) (count / data.length);
                float step = perItemWidth / 50f;
                for (int i = 0; i < leaders.length; i++) {
                    if (leaders[i]) {
                        curPos[i].x = perItemWidth;
                    }
                }
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        float x = curPos[i].x + step;
                        short val = data[i + j * mHistory.chan];
                        float y = (float) (-((val) / mHistory.adunit) * perLineWidth * 10);
                        y += baseline[i];
                        if (leaders[i]) {
                            canvas.drawLine(curPos[i].x, curPos[i].y, x, y, mDrawLinePaint);
                        }
                        curPos[i].x = x;
                        curPos[i].y = y;
//                        if (Math.abs(curPos[i].x) >= (mBottomView.lineLeft[mBottomView.lineLeft.length - 1] - 1)) {
//                            curPos[i].x = 0;
//                            break;
//                        }
                    }
                }

            }
        }
    }

    private class PlayBackBottomView extends View {
        private int perItemWidth;
        private float perLineWidth;
        private float[] lineLeft;
        private float[] lineTop;
        private final Paint mDrawEcgGridPaint = new Paint();
        private final Paint mDrawEcgRedPaint = new Paint();
        private final Paint mDrawGainPaint = new Paint();
        private final Path mGainPath = new Path();

        public PlayBackBottomView(Context context) {
            super(context);
        }

        private void init() {
            perItemWidth = this.getWidth() / 15;
            perLineWidth = perItemWidth / 5f;
            mDrawEcgGridPaint.setColor(Color.rgb(247, 200, 200));
            mDrawEcgRedPaint.setColor(Color.RED);
            mDrawGainPaint.setAntiAlias(true);
            mDrawGainPaint.setStyle(Paint.Style.STROKE);
            lineLeft = null;
            lineTop = null;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (perItemWidth < 0.1f) {
                init();
            }
            canvas.drawColor(Color.WHITE);
            drawEcgGrid(canvas, new Rect(0, 0, this.getWidth(), this.getHeight()));
            drawBetween(canvas);
            drawGain(canvas);
        }

        /**
         * 绘制网格右上 增益 文字（心电结果）
         *
         * @param canvas
         */
        private void drawGain(Canvas canvas) {
            mGainPath.reset();
            mDrawGainPaint.setTextSize(perItemWidth / 3f);
            mDrawGainPaint.setStyle(Paint.Style.STROKE);
            int i;
            for (i = 0; i < getHeight() / perItemWidth; i++) {
                if (i * perItemWidth - (getHeight() / 2) > perItemWidth / 2f) {
                    break;
                }
            }
            mGainPath.moveTo(0, (i + 2) * perItemWidth);
            mGainPath.lineTo(perLineWidth, (i + 2) * perItemWidth);
            mGainPath.lineTo(perLineWidth, i * perItemWidth);
            mGainPath.lineTo(4 * perLineWidth, i * perItemWidth);
            mGainPath.lineTo(4 * perLineWidth, (i + 2) * perItemWidth);
            mGainPath.lineTo(5 * perLineWidth, (i + 2) * perItemWidth);
            canvas.drawPath(mGainPath, mDrawGainPaint);
            mDrawGainPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(ThreeInOneApplication.getStringRes(R.string.ecg_gain), 11.5f * perItemWidth, DisplayUtils.getTextHeight(mDrawGainPaint, perItemWidth), mDrawGainPaint);
            canvas.drawText(ThreeInOneApplication.getStringRes(R.string.ecg_rate), 11.5f * perItemWidth, DisplayUtils.getTextHeight(mDrawGainPaint, 3 * perItemWidth), mDrawGainPaint);
        }

        private void drawBetween(Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            canvas.drawLine(0, lineTop[lineTop.length - 5], 7 * perItemWidth, lineTop[lineTop.length - 5], paint);
            canvas.drawLine(0, lineTop[lineTop.length - 5], 2 * perLineWidth, lineTop[lineTop.length - 7], paint);
            canvas.drawLine(0, lineTop[lineTop.length - 5], 2 * perLineWidth, lineTop[lineTop.length - 3], paint);
            canvas.drawLine(9 * perItemWidth, lineTop[lineTop.length - 5], getWidth(), lineTop[lineTop.length - 5], paint);
            canvas.drawLine(getWidth(), lineTop[lineTop.length - 5], getWidth() - 2 * perLineWidth, lineTop[lineTop.length - 7], paint);
            canvas.drawLine(getWidth(), lineTop[lineTop.length - 5], getWidth() - 2 * perLineWidth, lineTop[lineTop.length - 3], paint);
            paint.setTextSize(40);
            paint.setStrokeWidth(2);
            paint.setTextAlign(Paint.Align.CENTER);
            float y = DisplayUtils.getTextHeight(paint, lineTop[lineTop.length - 5] * 2f);
            canvas.drawText("3s", 8 * perItemWidth, y, paint);
        }


        // 绘制指定区域的心电网格
        public synchronized void drawEcgGrid(Canvas canvas, Rect rect) {
            try {
                boolean status = false;// 判断是否是第一次绘制网格，为了网格能够重叠
                int number = (int) (rect.width() / (float) perItemWidth * 5);// 计算需要绘制网格线的数量
                int leftNumber = 0;// 从第几根线开始绘制
                if (lineLeft == null) {
                    lineLeft = new float[number + 1];
                    status = true;
                } else {// 找出当前为第几根线
                    float value = 7.2f;
                    for (int i = 0; i < lineLeft.length; i++) {// 找出最小的number
                        float value1 = rect.left - lineLeft[i];
                        if (Math.abs(value1) < Math.abs(value)) {
                            value = value1;
                            leftNumber = i;
                        }
                    }
                }
                float left = rect.left;
                if (Math.abs(lineLeft[leftNumber] - rect.left) > 3.0
                        && lineLeft[2] > 2.0) {
                    number = (int) ((rect.right - lineLeft[leftNumber])
                            / (float) perItemWidth * 5);
                }
                int i = 0;
                for (i = leftNumber; i < number + leftNumber; i++) {
                    if (status == true)
                        lineLeft[i] = left;
                    else
                        left = lineLeft[i];
                    if (i % 5 == 0) {
                        mDrawEcgGridPaint.setStrokeWidth(1);
                        canvas.drawLine(left, rect.top, left, rect.bottom,
                                mDrawEcgRedPaint);
                    } else {
                        mDrawEcgGridPaint.setStrokeWidth(1);
                        canvas.drawLine(left, rect.top, left, rect.bottom,
                                mDrawEcgGridPaint);
                    }
                    left += perLineWidth;
                }
                if (status) {
                    lineLeft[i] = left;
                }
                if (i % 5 == 0) {
                    mDrawEcgGridPaint.setStrokeWidth(1);
                    canvas.drawLine(left, rect.top, left, rect.bottom,
                            mDrawEcgRedPaint);
                } else {
                    mDrawEcgGridPaint.setStrokeWidth(1);
                    canvas.drawLine(left, rect.top, left, rect.bottom,
                            mDrawEcgGridPaint);
                }
                number = (int) (rect.height() / (float) perItemWidth * 5);
                if (lineTop == null) {
                    lineTop = new float[number + 1];
                    status = true;
                } else {
                    float value = 7.2f;
                    for (i = 0; i < lineTop.length; i++) {// 找出最小的number
                        float value1 = rect.top - lineTop[i];
                        if (Math.abs(value1) < Math.abs(value)) {
                            value = value1;
                            leftNumber = i;
                        }
                    }
                }
                left = rect.top;
                final int count = lineTop == null ? number + 1 : lineTop.length;
                for (i = leftNumber; i < number + leftNumber && i < count; i++) {
                    if (status == true) {
                        lineTop[i] = left;
                    } else {
                        left = lineTop[i];
                    }
                    if (i % 5 == 0) {
                        mDrawEcgGridPaint.setStrokeWidth(2);
                        canvas.drawLine(rect.left, left, rect.right, left,
                                mDrawEcgGridPaint);
                    } else {
                        mDrawEcgGridPaint.setStrokeWidth(1);
                        canvas.drawLine(rect.left, left, rect.right, left,
                                mDrawEcgGridPaint);
                    }
                    left += perLineWidth;
                }
                if (status == true) {
                    lineTop[i] = left;
                }
                if (i % 5 == 0) {
                    mDrawEcgGridPaint.setStrokeWidth(2);
                    canvas.drawLine(rect.left, left, rect.right, left,
                            mDrawEcgGridPaint);
                } else {
                    mDrawEcgGridPaint.setStrokeWidth(1);
                    canvas.drawLine(rect.left, left, rect.right, left,
                            mDrawEcgGridPaint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ECGDataView {
        short[] block;// 数据块（心电数据）
        //        int hr;// 心律
        float adunit;// 采样率
        //        long startMeasureTime;// 开始测量时间
        long processTime;// 记录时间
        int offset;//当前数据块的偏移量
        int left;
        int chan;
        int sampling;
        SparseArray<ECGStroageResult> ecgResult;

        ECGDataView(ECGMeasureData data, int pos, ECGResultFile file, int len, int left) {
            block = data.datas();
            adunit = data.adunit();
            chan = data.chan();
            sampling = data.sampling();
//           startMeasureTime = data.getMeasureTime();
//           hr = data.get_hr();
            offset = pos;
            ecgResult = file.getResult(pos, len);
            this.left = left;
        }
    }


    // 界面重置
    public void reset() {
        mTopView.reset();
    }

}
