package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.AmbChartActivity;
import com.dftaihua.dfth_threeinone.activity.BpResultActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dfth.sdk.config.DfthConfig;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/2/16 0016.
 */
public class AMBChartView extends ChartView implements ChartView.BottomViewChangeListener {
    private boolean[] bottom = {false, false, false, false};
    private List<BpResult> mDatas;
    private String mSelectDay;
    private boolean isLandscape = false;
    private BpPlan mBpPlan = null;
    private boolean isHigh = false;
    private OnDoubleClickListener mDoubleClickListener;
    private OnSlipListener onSlipListener;
    private int standardLineHigh;
    private int standardLineLow;
    private boolean isFromResult = true;

    public AMBChartView(Context context) {
        super(context);
        chartView = new ScrollChart(getContext());
        bottomView = new ChartBottomView(getContext(), new int[]{R.color.high_color, R.color.low_color, R.color.beat_color, R.color.standard_color},
                new int[]{R.string.bp_result_ssy_text_line, R.string.bp_result_szy_text_line, R.string.bp_result_beat_text_line, R.string.bp_result_reach_text_line,});
        bottomView.setBottomViewChangeListener(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.topMargin = DisplayUtils.dip2px(getContext(), 5);
        params.weight = 10;
        addView(chartView, params);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        params.bottomMargin = DisplayUtils.dip2px(getContext(), 5);
        addView(bottomView, params);
        standardLineHigh = DfthConfig.getConfig().bpConfig.analysisConfig.normalStandardJudgeConfig.maxHighPressure;
        standardLineHigh = DfthConfig.getConfig().bpConfig.analysisConfig.normalStandardJudgeConfig.minLowPressure;
    }

    public AMBChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        chartView = new ScrollChart(getContext());
        bottomView = new ChartBottomView(getContext(), new int[]{R.color.high_color, R.color.low_color, R.color.beat_color, R.color.standard_color},
                new int[]{R.string.bp_result_ssy_text_line, R.string.bp_result_szy_text_line, R.string.bp_result_beat_text_line, R.string.bp_result_reach_text_line,});
        bottomView.setBottomViewChangeListener(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.topMargin = DisplayUtils.dip2px(getContext(), 5);
        params.weight = 10;
        addView(chartView, params);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        params.topMargin = DisplayUtils.dip2px(getContext(), 5);
        params.bottomMargin = DisplayUtils.dip2px(getContext(), 5);
        addView(bottomView, params);
        standardLineHigh = DfthConfig.getConfig().bpConfig.analysisConfig.normalStandardJudgeConfig.maxHighPressure;
        standardLineHigh = DfthConfig.getConfig().bpConfig.analysisConfig.normalStandardJudgeConfig.minLowPressure;
    }

    public void setDatas(List<BpResult> datas, String selectDay, BpPlan plan, boolean isHighValue, boolean fromResult) {
        mDatas = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).isValidData()) {
                mDatas.add(datas.get(i));
            }
        }
        mSelectDay = selectDay;
        mBpPlan = plan;
        isHigh = isHighValue;
        isFromResult = fromResult;
        chartView.postInvalidate();
    }

    @Override
    public void change(View view) {
        ChartBottomView.ViewHolder holder = (ChartBottomView.ViewHolder) view.getTag();
        bottom[holder.id] = holder.status;
        chartView.invalidate();
    }

    public void setIsLandscape(boolean isLandscape) {
        this.isLandscape = isLandscape;
    }

    public void setDoubleClickListener(OnDoubleClickListener listener) {
        mDoubleClickListener = listener;
    }

    public void setOnSlipListener(OnSlipListener onSlipListener) {
        this.onSlipListener = onSlipListener;
    }

    final class ScrollChart extends View {
        private float oldX;
        private float height;
        private float width;
        private float everyWidth;
        private float everyHeight;
        private final int POINT_COUNT = 26;

        private final int Y_COUNT_NORMAL = 8;
        private final int Y_COUNT_HIGH = 11;
        private final int Y_SCALE_HIGH = 28;
        private final int Y_SCALE_NORMAL = 20;
        private final int Y_SCALE_NORMAL_BEGIN = 40;

        private int Y_COUNT = Y_COUNT_HIGH;
        private int Y_SCALE = Y_SCALE_NORMAL;

        private float Y_MARGIN;
        private Paint gridPaint;
        private Paint linePaint;
        private Paint scalePaint;
        private Paint ssyPaint;
        private Paint szyPaint;
        private Paint beatPaint;
        private Paint redPaint;
        private Paint nightPaint;
        private Paint rectPaint;
        private int currentNumber = 0;//当前点的位置
        private int currentLineNumber = 3;//当前游标的位置
        private PointF[] ssyLocations;
        private PointF[] szyLocations;
        private PointF[] beatLocations;
        private PointF[] pillLocations;
        private boolean isTouchChart = false;
        private boolean isTouchLine = false;
        private int mCurrentPill = -1;
        private int currentPointCount = 0;
        private Paint titlePaint;
        private PopupWindow mWindow;

        public ScrollChart(Context context) {
            super(context);
        }

        private void initalize() {
            height = this.getHeight();
            width = this.getWidth();
            everyWidth = width / POINT_COUNT;
            everyHeight = height / Y_COUNT;
            Y_MARGIN = everyHeight * 0.5f;
            gridPaint = new Paint();
            gridPaint.setColor(ThreeInOneApplication.getColorRes(R.color.amb_grid_color));
            gridPaint.setStrokeWidth(1);
            linePaint = new Paint();
            linePaint.setColor(ThreeInOneApplication.getColorRes(R.color.google_red));
            linePaint.setStrokeWidth(2);
            linePaint.setStyle(Paint.Style.FILL);
            linePaint.setStrokeJoin(Paint.Join.BEVEL);
            scalePaint = new Paint();
            scalePaint.setColor(Color.BLACK);
            scalePaint.setAntiAlias(true);
            setScaleTextSize();
            ssyPaint = new Paint();
            ssyPaint.setColor(ThreeInOneApplication.getColorRes(R.color.high_color));
            ssyPaint.setAntiAlias(true);
            ssyPaint.setStrokeWidth(2);

            szyPaint = new Paint();
            szyPaint.setColor(ThreeInOneApplication.getColorRes(R.color.low_color));
            szyPaint.setAntiAlias(true);
            szyPaint.setStrokeWidth(2);


            beatPaint = new Paint();
            beatPaint.setColor(ThreeInOneApplication.getColorRes(R.color.beat_color));
            beatPaint.setAntiAlias(true);
            beatPaint.setStrokeWidth(2);

            titlePaint = new Paint();
            titlePaint.setColor(ThreeInOneApplication.getColorRes(R.color.google_black));
            titlePaint.setAntiAlias(true);
            titlePaint.setTextSize(everyWidth * 3 / 5);
            titlePaint.setTextAlign(Paint.Align.LEFT);

            nightPaint = new Paint();
            nightPaint.setColor(ThreeInOneApplication.getColorRes(R.color.amb_night_grid_color));

            rectPaint = new Paint();
            rectPaint.setAntiAlias(true);
            rectPaint.setColor(ThreeInOneApplication.getColorRes(R.color.chart_rect_s_a_s));

            redPaint = new Paint();
            redPaint.setColor(ThreeInOneApplication.getColorRes(R.color.standard_color));
            redPaint.setAntiAlias(true);
            redPaint.setStrokeWidth(2);
            if (ActivityCollector.getActivity() instanceof AmbChartActivity) {
                redPaint.setTextSize(18);
            } else {
                redPaint.setTextSize(13);
            }
            oldX = everyWidth;
        }

        private void initHeight() {
            Y_COUNT = isHigh ? Y_COUNT_HIGH : Y_COUNT_NORMAL;
            Y_SCALE = isHigh ? Y_SCALE_HIGH : Y_SCALE_NORMAL;
            everyHeight = height / Y_COUNT;
            Y_MARGIN = everyHeight * 0.5f;
        }

        private void setScaleTextSize() {
            scalePaint.setTextSize(20);
            int offset = 20;
            while (scalePaint.measureText("00:00") > 0.9f * everyWidth) {
                scalePaint.setTextSize(--offset);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (getWidth() <= getHeight()) {
                return;
            }
            initHeight();
            if (width < 1) {
                initalize();
            }
            canvas.drawColor(ThreeInOneApplication.getColorRes(R.color.google_white));
            drawNightBack(canvas);
            drawGrid(canvas, POINT_COUNT - 1);
            //drawLine(canvas,currentLineNumber);
            drawScale(canvas);
//            drawPill(canvas);
            if (currentNumber < 0) {//左边距的判断
                currentNumber = 0;
            }
            if (mDatas == null || mDatas.size() == 0) {
//                Logger.e("Datas size = 0");
                return;
            }
            int end = mDatas.size() > currentNumber + POINT_COUNT ? currentNumber + POINT_COUNT : mDatas.size();
            if (currentNumber >= mDatas.size() - POINT_COUNT) {
                currentNumber = mDatas.size() - POINT_COUNT >= 0 ? mDatas.size() - POINT_COUNT : 0;
            }
            List<BpResult> ds = mDatas.subList(0, mDatas.size());
            currentLineNumber = currentLineNumber < 0 ? 0 : currentLineNumber >= ds.size() ? ds.size() - 1 : currentLineNumber;
            currentPointCount = ds.size();
            drawValue(canvas, ds);
            if (!bottom[0] && !bottom[1]) {
                drawSAS(canvas);
            }
        }

        private void drawNightBack(Canvas canvas) {
            Calendar c = Calendar.getInstance();
            if (mBpPlan == null) {
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
            } else {
                long measureTime = mBpPlan.getStartTime() * 1000;
                c.setTimeInMillis(measureTime);
            }
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            minute = minute >= 28 ? 30 : 0;
            int i = 0;
            int left = -1;
            Rect r = new Rect();
//            r.top = (int) (Y_MARGIN + 5);
//            r.bottom = (int) (Y_COUNT * everyHeight - Y_MARGIN - 5);
            r.top = (int) (Y_MARGIN + 10);
            r.bottom = (int) (Y_COUNT * everyHeight - Y_MARGIN + 10);
            while (i < 50) {
                hour %= 24;
                if (hour <= 6 || hour >= 22) {
                    if (left == -1) {
                        left = i;
                    }
                } else {
                    if (left != -1) {
                        r.left = (int) (left * everyWidth / 2f + everyWidth);
                        r.right = (int) (i * everyWidth / 2f);
                        canvas.drawRect(r, nightPaint);
                        left = -1;
                    }
                }
                i++;
                minute = (minute + 30) % 60;
                if (minute == 0) {
                    hour++;
                }
            }
            if (left != -1) {
                r.left = (int) (left * everyWidth / 2f + everyWidth);
                r.right = (int) (i * everyWidth / 2f);
                canvas.drawRect(r, nightPaint);
            }
        }

        private void drawGrid(Canvas canvas, int numbers) {
            int k = 7;
            for (int i = 1; i <= numbers; i++) {
                canvas.drawLine(i * everyWidth, Y_MARGIN + 10, i * everyWidth, Y_COUNT * everyHeight - Y_MARGIN + 10, gridPaint);
            }
            for (int j = 0; j <= Y_COUNT; j++) {
//                if (j == k && !bottom[3]) {
//                    canvas.drawLine(everyWidth, j * everyHeight + Y_MARGIN, (numbers) * everyWidth, j * everyHeight + Y_MARGIN, redPaint);
//                } else {
//                    canvas.drawLine(everyWidth, j * everyHeight + Y_MARGIN, (numbers) * everyWidth, j * everyHeight + Y_MARGIN, gridPaint);
//                }
                canvas.drawLine(everyWidth, j * everyHeight + Y_MARGIN + 10, (numbers) * everyWidth, j * everyHeight + Y_MARGIN + 10, gridPaint);
            }
            if (!bottom[3]) {
                float y1 = isHigh ? 5.0f * everyHeight + Y_MARGIN + 10 : 2.0f * everyHeight + Y_MARGIN + 10;
                float y2 = isHigh ? 6.7f * everyHeight + Y_MARGIN + 10 : 4.5f * everyHeight + Y_MARGIN + 10;
                canvas.drawLine(everyWidth, y1, (numbers) * everyWidth, y1, redPaint);
                canvas.drawLine(everyWidth, y2, (numbers) * everyWidth, y2, redPaint);
            }
        }

        private void drawTitleValue(Canvas canvas, BpResult data) {
            String time = ThreeInOneApplication.getStringRes(R.string.bp_result_time_text_line) + ":";
            String ssy = ThreeInOneApplication.getStringRes(R.string.bp_result_ssy_text_line) + ":";
            String szy = ThreeInOneApplication.getStringRes(R.string.bp_result_szy_text_line) + ":";
            String beat = ThreeInOneApplication.getStringRes(R.string.bp_result_beat_text_line) + ":";
//            String text =time  + data.getmMeasureTime().substring(0,19) + ssy + data.getmHigh()
//                    + szy+ data.getmLow() + beat + data.getmPulse();
            String high = data.getSbp() == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(getContext(), data.getSbp());
            String low = data.getDbp() == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(getContext(), data.getDbp());
            String rate = data.getPulseRate() == 0 ? "- -" : data.getPulseRate() + "";
            String text = time + TimeUtils.getTimeStr(data.getMeasureTime(), "yyyy-MM-dd HH:mm:ss").substring(0, 19) + " "
                    + ssy + high + " "
                    + szy + low + " "
                    + beat + rate;
            canvas.drawText(text, everyWidth, DisplayUtils.getTextHeight(titlePaint, Y_MARGIN + 5), titlePaint);
        }

        private void drawValue(Canvas canvas, List<BpResult> mDatas) {
            ssyLocations = new PointF[mDatas.size()];
            szyLocations = new PointF[mDatas.size()];
            beatLocations = new PointF[mDatas.size()];
//            long startTime = mPlan.getStartLongTime();
            int year = Integer.valueOf(mSelectDay.substring(0, 4));
            int month = Integer.valueOf(mSelectDay.substring(5, 7));
            int day = Integer.valueOf(mSelectDay.substring(8, 10));
            Calendar c = Calendar.getInstance();
            long startTime;
            if (mBpPlan == null) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month - 1);
                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                startTime = c.getTime().getTime();
            } else {
                startTime = mBpPlan.getStartTime() * 1000;
                c.setTimeInMillis(startTime);
            }
//            String startTimeStr = TimeUtils.getTimeStr(startTime,"yyyy-MM-dd HH:mm:ss");
//            Logger.e("startTimeStr = " + startTimeStr);
            for (int i = mDatas.size() - 1; i >= 0; i--) {
                BpResult data = mDatas.get(i);
                int ssy = data.getSbp();
                int szy = data.getDbp();
                int beat = data.getPulseRate();
                ssyLocations[mDatas.size() - 1 - i] = drawValue(canvas, ssy, data.getMeasureTime() - startTime, TYPE.SSY);
                szyLocations[mDatas.size() - 1 - i] = drawValue(canvas, szy, data.getMeasureTime() - startTime, TYPE.SZY);
                beatLocations[mDatas.size() - 1 - i] = drawValue(canvas, beat, data.getMeasureTime() - startTime, TYPE.BEAT);
            }
            drawValueLine(canvas, mDatas);
        }

        private PointF drawValue(Canvas canvas, int value, long measureTime, TYPE type) {
            PointF point = new PointF();
            float everyTime = everyWidth / TimeUtils.ONE_HOUR;
            switch (type) {
                case BEAT: {
                    if (!bottom[2]) {
                        point.y = isHigh ? Y_COUNT * everyHeight - Y_MARGIN + 10 - value * everyHeight / Y_SCALE
                                : (Y_COUNT + 2) * everyHeight - Y_MARGIN + 10 - value * everyHeight / Y_SCALE;
                        point.x = everyWidth + measureTime * everyTime;
                        canvas.drawCircle(point.x, point.y, everyHeight / 7, beatPaint);
                    }
                }
                break;
                case SSY: {
                    if (!bottom[0]) {
                        point.y = isHigh ? Y_COUNT * everyHeight - Y_MARGIN + 10 - value * everyHeight / Y_SCALE
                                : (Y_COUNT + 2) * everyHeight - Y_MARGIN + 10 - value * everyHeight / Y_SCALE;
                        point.x = everyWidth + measureTime * everyTime;
                        canvas.drawCircle(point.x, point.y, everyHeight / 7, ssyPaint);
                    }
                }
                break;
                case SZY: {
                    if (!bottom[1]) {
                        point.y = isHigh ? Y_COUNT * everyHeight - Y_MARGIN + 10 - value * everyHeight / Y_SCALE
                                : (Y_COUNT + 2) * everyHeight - Y_MARGIN + 10 - value * everyHeight / Y_SCALE;
                        point.x = everyWidth + measureTime * everyTime;
                        canvas.drawCircle(point.x, point.y, everyHeight / 7, szyPaint);
                    }
                }
                break;
            }
            return point;
        }

        private void drawLine(Canvas canvas, int number) {
            Path path = new Path();
            path.moveTo((number + 1) * everyWidth, Y_COUNT * everyHeight - Y_MARGIN);
            path.lineTo((float) ((number + 1) * everyWidth - everyWidth * Math.cos(Math.PI / 3)), (float) (Y_COUNT * everyHeight - Y_MARGIN + Y_MARGIN * Math.sin(Math.PI / 6)));
            path.lineTo((float) ((number + 1) * everyWidth + everyWidth * Math.cos(Math.PI / 3)), (float) (Y_COUNT * everyHeight - Y_MARGIN + Y_MARGIN * Math.sin(Math.PI / 6)));
            path.lineTo((float) ((number + 1) * everyWidth), Y_COUNT * everyHeight - Y_MARGIN);
            path.close();
            canvas.drawPath(path, linePaint);
            canvas.drawLine((number + 1) * everyWidth, Y_MARGIN, (number + 1) * everyWidth, Y_COUNT * everyHeight - Y_MARGIN, linePaint);
        }

        private void drawScale(Canvas canvas) {
            String scale;
            scalePaint.setTextAlign(Paint.Align.LEFT);
            int type = isHigh ? 5 : 5;
            for (int i = 0; i < Y_COUNT; i++) {
                if (isHigh) {
                    scale = BpDataUtils.getDefaultUnitValue(getContext(), i * Y_SCALE);
                } else {
                    scale = BpDataUtils.getDefaultUnitValue(getContext(), i * Y_SCALE + Y_SCALE_NORMAL_BEGIN);
                }
//                scale = isHigh ? i * Y_SCALE + "" : i * Y_SCALE + Y_SCALE_NORMAL_BEGIN + "";
                if(i == 0){
                    int width = getTextWidth(redPaint, scale) + 5;
                    canvas.drawText(scale, everyWidth - width, (Y_COUNT - i - 1) * everyHeight + Y_MARGIN + redPaint.getTextSize() / 2, scalePaint);
                } else if (i == type && !bottom[3]) {
                    int width = getTextWidth(redPaint, scale) + 5;
                    canvas.drawText(scale, everyWidth - width, (Y_COUNT - i - 1) * everyHeight + Y_MARGIN + 10 + redPaint.getTextSize() / 2, redPaint);
                } else {
                    int width = getTextWidth(scalePaint, scale) + 5;
                    canvas.drawText(scale, everyWidth - width, (Y_COUNT - i - 1) * everyHeight + Y_MARGIN + 10 + scalePaint.getTextSize() / 2, scalePaint);
                }
            }
            if (!bottom[3]) {
                String highValue = BpDataUtils.getDefaultUnitValue(getContext(), 90);
                int width = getTextWidth(redPaint, highValue) + 5;
                float y1 = isHigh ? 6.7f * everyHeight + Y_MARGIN : 4.5f * everyHeight + Y_MARGIN + 10;
                canvas.drawText(highValue, everyWidth - width, y1, redPaint);
            }

            Calendar c = Calendar.getInstance();
            if (mBpPlan == null) {
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
            } else {
                long measureTime = mBpPlan.getStartTime() * 1000;
//                String time = TimeUtils.getTimeStr(measureTime,"yyyy-MM-dd HH:mm:ss");
                c.setTimeInMillis(measureTime);
            }
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            scalePaint.setTextAlign(Paint.Align.CENTER);
            for (int i = 1; i < POINT_COUNT; i++) {
                int h = hour % 24;
                minute = minute >= 28 ? 30 : 0;
                String text = (h < 10 ? "0" + h : "" + h) + (minute == 30 ? ":" + (minute >= 10 ? minute : "0" + minute) : ":00");
                canvas.drawText(text, i * everyWidth, DisplayUtils.getTextHeight(scalePaint, 2 * (Y_COUNT * everyHeight - (Y_MARGIN - 10) / 2f)), scalePaint);
                hour++;
            }
        }

        private void drawValueLine(Canvas canvas, List<BpResult> mDatas) {
            for (int i = 0; i < currentPointCount - 1; i++) {
                if (!bottom[0]) {
                    canvas.drawLine(ssyLocations[i].x, ssyLocations[i].y, ssyLocations[i + 1].x, ssyLocations[i + 1].y, ssyPaint);
                }
                if (!bottom[1]) {
                    canvas.drawLine(szyLocations[i].x, szyLocations[i].y, szyLocations[i + 1].x, szyLocations[i + 1].y, szyPaint);
                }
                if (!bottom[2]) {
                    canvas.drawLine(beatLocations[i].x, beatLocations[i].y, beatLocations[i + 1].x, beatLocations[i + 1].y, beatPaint);
                }
            }
            if (currentLineNumber <= currentPointCount - 1) {
                if (AMBChartView.this.getContext() instanceof BpResultActivity) {
                    BpResultActivity activity = (BpResultActivity) AMBChartView.this.getContext();
                    if (activity.getDataType() == 1) {
                        if (!bottom[0]) {
                            canvas.drawCircle(ssyLocations[currentLineNumber].x, ssyLocations[currentLineNumber].y, everyWidth / 7, linePaint);
                        }
                        if (!bottom[1]) {
                            canvas.drawCircle(szyLocations[currentLineNumber].x, szyLocations[currentLineNumber].y, everyWidth / 7, linePaint);
                        }
                        if (!bottom[2]) {
                            canvas.drawCircle(beatLocations[currentLineNumber].x, beatLocations[currentLineNumber].y, everyWidth / 7, linePaint);
                        }
                    }
                } else {
                    if (!bottom[0]) {
                        canvas.drawCircle(ssyLocations[currentLineNumber].x, ssyLocations[currentLineNumber].y, everyWidth / 7, linePaint);
                    }
                    if (!bottom[1]) {
                        canvas.drawCircle(szyLocations[currentLineNumber].x, szyLocations[currentLineNumber].y, everyWidth / 7, linePaint);
                    }
                    if (!bottom[2]) {
                        canvas.drawCircle(beatLocations[currentLineNumber].x, beatLocations[currentLineNumber].y, everyWidth / 7, linePaint);
                    }
                }
                if (!isFromResult) {
                    drawTitleValue(canvas, mDatas.get(mDatas.size() - 1 - currentLineNumber));
                }
            }
        }

        private void isTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
//            mCurrentPill = -1;
            if (x >= everyWidth && x <= width && y >= Y_MARGIN && y <= Y_COUNT * everyHeight - Y_MARGIN) {
                isTouchChart = true;
            }
            if (x >= (currentLineNumber + 1) * everyWidth - 2 * everyWidth / 3 && x <= (currentLineNumber + 1) * everyWidth + 2 * everyWidth / 3
                    && y >= Y_COUNT * everyHeight - Y_MARGIN && y <= Y_COUNT * everyHeight) {
                linePaint.setColor(Color.RED);
                isTouchLine = true;
                invalidate();
            }
//            for (int i = 0; i < pillLocations.length; i++) {
//                PointF pointF = pillLocations[i];
//                if (x >= pointF.x && x <= pointF.x + mBitmap.getWidth()
//                        && y >= pointF.y && y <= pointF.y + mBitmap.getHeight()) {
//                    mCurrentPill = i;
//                    isTouchLine = false;
//                    isTouchChart = false;
//                    break;
//                }
//            }
        }

        /*画连接图*/
        private void drawSAS(Canvas canvas) {
            Path path = new Path();
            path.moveTo(ssyLocations[ssyLocations.length - 1].x, ssyLocations[ssyLocations.length - 1].y);
            for (int i = ssyLocations.length - 1; i >= 0; i--) {
                path.lineTo(ssyLocations[i].x, ssyLocations[i].y);
            }
            for (int i = 0; i < szyLocations.length; i++) {
                path.lineTo(szyLocations[i].x, szyLocations[i].y);
            }
            path.lineTo(ssyLocations[ssyLocations.length - 1].x, ssyLocations[ssyLocations.length - 1].y);
            path.close();
            canvas.drawPath(path, rectPaint);
        }

        private int mCount = 0;              // 计算点击的次数
        private long mFirstClickTime = 0;    // 第一次点击的时间
        private long mLastClickTime = 0;     // 最后一次点击的时间
        private float x1 = 0;
        private float x2 = 0;
        private float y1 = 0;
        private float y2 = 0;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isLandscape) {
                    x1 = event.getX();
                    y1 = event.getY();
                    // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                    if (mFirstClickTime != 0 && System.currentTimeMillis() - mFirstClickTime > 500) {
                        mCount = 0;
                    }
                    mCount++;
                    if (mCount == 1) {
                        mFirstClickTime = System.currentTimeMillis();
                    } else if (mCount == 2) {
                        mLastClickTime = System.currentTimeMillis();
                        // 两次点击小于500ms 也就是连续点击
                        if (mLastClickTime - mFirstClickTime < 500) {
                            if (mDoubleClickListener != null) {
                                mDoubleClickListener.onDoubleClick();
                            }
                        }
                        clear();
                        return false;
                    }
                }
                if (mWindow != null) {
                    mWindow.dismiss();
                    mWindow = null;
                    mCurrentPill = -1;
                    invalidate();
                    return false;
                }
                isTouchEvent(event);
                oldX = event.getX();
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (AMBChartView.this.getContext() instanceof BpResultActivity) {
                    BpResultActivity activity = (BpResultActivity) AMBChartView.this.getContext();
                    if (activity.getDataType() == 2) {
                        return true;
                    }
                }
                float x = event.getX();
//                    if(!isTouchChart){
//                        if(x  - oldX >= everyWidth / 2){
//                            currentNumber++;
//                            oldX = x;
//                            invalidate();
//                        }
//                        if(x - oldX <= -everyWidth / 2){
//                            currentNumber--;
//                            oldX = x;
//                            invalidate();
//                        }
//                    }
//                    if(!isTouchLine){
//                        if(x  - oldX >= everyWidth){
//                            currentLineNumber++;
//                            oldX = x;
//                            invalidate();
//                        }
//                        if(x - oldX <= -everyWidth){
//                            currentLineNumber--;
//                            oldX = x;
//                            invalidate();
//                        }
//                    }
                if (isTouchChart) {
                    if (x - oldX >= everyWidth / 2) {
                        currentLineNumber++;
                        oldX = x;
                        invalidate();
                    }
                    if (x - oldX <= -everyWidth / 2) {
                        currentLineNumber--;
                        oldX = x;
                        invalidate();
                    }
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!isLandscape) {
                    if (AMBChartView.this.getContext() instanceof BpResultActivity) {
                        BpResultActivity activity = (BpResultActivity) AMBChartView.this.getContext();
                        if (activity.getDataType() == 2) {
                            x2 = event.getX();
                            y2 = event.getY();
                            if (x1 - x2 > 50) {
                                if (onSlipListener != null) {
                                    onSlipListener.onSlip(Constant.BP_SLIP_LEFT);
                                }
                            } else if (x1 - x2 < (-50)) {
                                if (onSlipListener != null) {
                                    onSlipListener.onSlip(Constant.BP_SLIP_RIGHT);
                                }
                            }
                            return true;
                        }
                    }
                }
                if (mCurrentPill != -1) {
                    isTouchEvent(event);
                }
                isTouchChart = false;
                isTouchLine = false;
                linePaint.setColor(ThreeInOneApplication.getColorRes(R.color.google_red));
                invalidate();
            }
            return true;
        }

        // 清空状态
        private void clear() {
            mCount = 0;
            mFirstClickTime = 0;
            mLastClickTime = 0;
        }
    }

    public interface OnDoubleClickListener {
        void onDoubleClick();
    }

    public interface OnSlipListener {
        void onSlip(int direction);
    }
}
