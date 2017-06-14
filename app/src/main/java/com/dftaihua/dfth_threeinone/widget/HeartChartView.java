package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.listener.PosListener;
import com.dftaihua.dfth_threeinone.utils.ScreenUtils;
import com.dfth.mobliemonitor.measure.ecg.ECGStroageResult;
import com.dfth.sdk.Protocol.Ecg.LocalSportAlgorithm;
import com.dfth.sdk.file.ecg.ECGResultFile;
import com.dfth.sdk.file.sport.SportStatusFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-05-11.
 * 在2015年5月11日11:04:00 重新修改
 */
public class HeartChartView extends RelativeLayout implements PosListener {
    private static final int MAX_HEART = 255;
    private static final float R1 = 10f;
    private static final float R2 = 15f;
    private static final String TAG = "HeartChartView";
    private final int margin = 10;
    private int mPreJ = 0;
    private ECGResultFile mFile;
    private boolean mNoResult = false;
    private boolean mFullScreen = false;
    private HeartChartLine mChartLine;
    private HeartChartRed mChartRed;
    private int mWidth;
    private int mHeight;
    private List<PointF> mPoints;
    private List<Point> mSportPoints;
    private float mBottomMargin;
    private float mVetriValue;
    private float mStep;
    private PointF p;
    private int mDeviceType;

    public HeartChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mChartLine = new HeartChartLine(context);
        addView(mChartLine, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setWillNotDraw(true);
    }

    public void setFullScreen(boolean s) {
        mFullScreen = s;
        int v = s ? View.GONE : View.VISIBLE;
        setVisibility(v);
    }

    public void setFile(ECGResultFile file,int deviceType) {
        this.mFile = file;
        mDeviceType = deviceType;
    }

    private void initByChild(int width, int height) {
        mWidth = width;
        mHeight = height;
        ECGStroageResult[] results = mFile.getLocal().getResults();
        mNoResult = results.length < 5;
        final float r1 = height / R1;
        final float r2 = height / R2;
        mBottomMargin = r1 + margin;
        mVetriValue = (mHeight - 2 * mBottomMargin) / MAX_HEART;
        mStep = (mWidth * 1.0f - 2 * mBottomMargin) / (mFile.pts());
        mPoints = new ArrayList<>();
        int c = results.length / 1024;
        c = c == 0 ? 1 : c;
        int prePeak = -1;
        if (!mNoResult) {
            PointF currentPoint;
            for (int i = 0; i < results.length; i = i + c) {
                int peak = results[i]._Peak <= 0 ? 0 : results[i]._Peak;
                if (peak >= prePeak) {
                    currentPoint = new PointF();
                    currentPoint.y = mBottomMargin + (MAX_HEART - results[i]._hr) * mVetriValue;
                    currentPoint.x = mBottomMargin + mStep * peak;
                    mPoints.add(currentPoint);
                    if (p == null) {
                        p = new PointF();
                        p.x = currentPoint.x;
                        p.y = currentPoint.y;
                    }
                    prePeak = peak;
                }

            }
        }
        if (p != null) {
            mChartRed = new HeartChartRed(getContext());
            LayoutParams params = new LayoutParams(0, 0);
            params.width = (int) (2 * r1);
            params.height = (int) (3 * r1 + 2 * r2);
            params.leftMargin = (int) (p.x - r1);
            params.topMargin = (int) (p.y - 3 * r1 - r2);
            addView(mChartRed, params);
        }
        SportStatusFile sportStatusFile = mFile.getSportStatusFile();
        if (sportStatusFile != null) {
            try{
                int ss = 25;
                int length = sportStatusFile.length();
                mSportPoints = new ArrayList<>();
                int sportStep = length >= 10000 ? length / 10000 : 1;
                int status = LocalSportAlgorithm.STATUS_UNKNOWN;
                for (int i = 0; i < length; i = i + sportStep) {
                    SportStatusFile.SportStatus s = sportStatusFile.getStatus(i);
                    if (s.status != status) {
                        Point p = new Point();
                        p.x = (int) (mBottomMargin + mStep * s.startOffset * ss);
                        p.y = s.status;
                        mSportPoints.add(p);
                        status = s.status;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class HeartChartLine extends View {
        private Paint mPaint = new Paint();
        private Paint mSportPaint = new Paint();
        private boolean mIsInit = false;

        private HeartChartLine(Context context) {
            super(context);
        }

        private void init() {
            mPaint.setColor(ThreeInOneApplication.getColorRes(R.color.history_details_heart_line));
            mPaint.setStrokeWidth(ThreeInOneApplication.getIntRes(R.integer.custom_line_size));
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            mSportPaint.setAntiAlias(true);
            mIsInit = true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Log.e("dfth_sdk","mFile--->" + mFile);
            if (mFile == null) {
                return;
            }
            if (!mIsInit) {
                Log.e("dfth_sdk","start_init--->");
                init();
                initByChild(getWidth(), getHeight());
                Log.e("dfth_sdk","end_init--->");
            }
            drawSportBack(canvas);
            drawChart(canvas);
        }

        private void drawSportBack(Canvas c) {
            if (mSportPoints != null && mSportPoints.size() >= 1) {
                RectF r = new RectF();
                r.top = mBottomMargin;
                r.bottom = getHeight() - mBottomMargin;
                for (int i = 1; i < mSportPoints.size(); i++) {
                    r.left = mSportPoints.get(i - 1).x;
                    r.right = mSportPoints.get(i).x;
                    int color = mSportPoints.get(i - 1).y == LocalSportAlgorithm.STATUS_PEACEFUL ? R.color.google_green
                            : mSportPoints.get(i - 1).y == LocalSportAlgorithm.STATUS_FAST_MOTION ? R.color.google_orange
                            : R.color.google_yellow;
                    mSportPaint.setColor(ThreeInOneApplication.getColorRes(color));
                    c.drawRect(r, mSportPaint);
                }
                r.left = mSportPoints.get(mSportPoints.size() - 1).x;
                r.right = getWidth() - mBottomMargin;
                int color = mSportPoints.get(mSportPoints.size() - 1).y == LocalSportAlgorithm.STATUS_PEACEFUL ? R.color.google_green
                        : mSportPoints.get(mSportPoints.size() - 1).y == LocalSportAlgorithm.STATUS_FAST_MOTION ? R.color.google_orange
                        : R.color.google_yellow;
                mSportPaint.setColor(ThreeInOneApplication.getColorRes(color));
                c.drawRect(r, mSportPaint);
            } else {
                RectF r = new RectF();
                r.top = mBottomMargin;
                r.left = 10;
                r.right = getWidth() - 10;
                r.bottom = getHeight() - mBottomMargin;
                int color = R.color.twelve_ecg_heart_chart_back;
                mSportPaint.setColor(ThreeInOneApplication.getColorRes(color));
                c.drawRect(r, mSportPaint);
            }
        }

        private void drawChart(Canvas canvas) {
            if (!mNoResult) {
                PointF prePoint = null;
                for (int i = 0; i <= mPoints.size() - 1; i++) {
                    if (prePoint == null) {
                        prePoint = new PointF(mPoints.get(i).x, mPoints.get(i).y);
                    } else {
                        PointF f = mPoints.get(i);
                        canvas.drawLine(prePoint.x, prePoint.y, f.x, f.y, mPaint);
                        prePoint.x = f.x;
                        prePoint.y = f.y;
                    }
                }
                canvas.drawLine(prePoint.x, prePoint.y, getWidth() - mBottomMargin, prePoint.y, mPaint);
            } else {
                canvas.drawLine(mBottomMargin, mBottomMargin + (MAX_HEART - 100) * mVetriValue, mBottomMargin, mBottomMargin + (MAX_HEART - 100) * mVetriValue, mPaint);
            }
        }
    }

    private class HeartChartRed extends View {
        private Paint mCyclePaint = new Paint();
        private Bitmap mBitmap;

        private HeartChartRed(Context context) {
            super(context);
            mCyclePaint.setStyle(Paint.Style.FILL);
            mCyclePaint.setAntiAlias(true);
        }

        private void init() {
            mWidth = getWidth();
            mHeight = getHeight();
            final float r2 = HeartChartView.this.getHeight() / R2;
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.heart_gps);
            if (b != null && (int) (mHeight - 2 * r2) > 0) {
                mBitmap = ThumbnailUtils.extractThumbnail(b, mWidth, (int) (mHeight - 2 * r2));
            }
        }

        private void drawCycle(Canvas canvas) {
            final float r1 = HeartChartView.this.getHeight() / R1;
            final float r2 = HeartChartView.this.getHeight() / R2;
            mCyclePaint.setColor(ThreeInOneApplication.getColorRes(R.color.history_details_heart_cycle));
            canvas.drawCircle(mWidth / 2f, mHeight - r2, r2, mCyclePaint);
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final float r2 = HeartChartView.this.getHeight() / R2;
            if (getWidth() > 0 && getHeight() > 0 && getHeight() - 2 * r2 > 0) {
                init();
                drawCycle(canvas);
            }
        }
    }

    @Override
    public void posChange(int pos, ECGResultFile file, int len, int position) {
        Log.e("dfth_sdk","posChange--->" + mNoResult);
        if(mChartLine != null && !mChartLine.mIsInit){
            mChartLine.init();
            initByChild(getWidth(), getHeight());
            mChartLine.invalidate();
        }
        if (mNoResult) {
            return;
        }
        if (mFullScreen) {
            return;
        }
        if (p == null) {
            return;
        }
        if (pos <= 10 && mPoints != null && mPoints.size() > 0) {
            p.x = mPoints.get(0).x;
            p.y = mPoints.get(0).y;
            if (mChartRed != null) {
                final float r1 = getHeight() / R1;
                final float r2 = getHeight() / R2;
                LayoutParams params = (LayoutParams) mChartRed.getLayoutParams();
                params.leftMargin = (int) (p.x - r1);
                params.topMargin = (int) (p.y - 3 * r1 - r2);
                mChartRed.setLayoutParams(params);
            }
            return;
        }
        if (pos >= mFile.pts() - 10 - len) {
            p.x = getWidth() - mBottomMargin;
            p.y = mBottomMargin + (MAX_HEART - mFile.getLocal().getResults()[mFile.getLocal().getResults().length - 1]._hr) * mVetriValue;
            if (mChartRed != null) {
                final float r1 = getHeight() / R1;
                final float r2 = getHeight() / R2;
                LayoutParams params = (LayoutParams) mChartRed.getLayoutParams();
                params.leftMargin = (int) (p.x - r1);
                params.topMargin = (int) (p.y - 3 * r1 - r2);
                mChartRed.setLayoutParams(params);
            }
            return;
        }
        ECGStroageResult result = mFile.getLocal().getResults()[position];
        int peak = mFile.getLocal().getResults()[position]._Peak <= 0 ? 0 : mFile.getLocal().getResults()[position]._Peak;
        p.x = mBottomMargin + mStep * pos;
        if (position == 0 || position == 1) {
            p.y = mBottomMargin + (MAX_HEART) * mVetriValue;
        } else {
//            p.y = mPoints.get(position).y;
            p.y = mBottomMargin + (MAX_HEART - result._hr) * mVetriValue;
        }
        if (mChartRed != null) {
            final float r1 = getHeight() / R1;
            final float r2 = getHeight() / R2;
            LayoutParams params = (LayoutParams) mChartRed.getLayoutParams();
            params.leftMargin = (int) (p.x - r1);
            params.topMargin = (int) (p.y - 3 * r1 - r2);
            mChartRed.setLayoutParams(params);
        }
    }
}
