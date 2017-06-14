package com.dftaihua.dfth_threeinone.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.EcgMeasureActivity;
import com.dftaihua.dfth_threeinone.activity.HomeActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.listener.WaveChangeListener;
import com.dftaihua.dfth_threeinone.utils.DelayPerformMethod;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dfth.sdk.Others.Utils.ECGUtils;
import com.dfth.sdk.Others.Utils.LockLinkedList;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.base.BinMatrix;
import com.dfth.sdk.model.ecg.ECGMeasureData;

import java.util.ArrayList;
import java.util.List;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/15 16:28
*/
@SuppressLint("WrongCall")
public class NewWaveView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private Context mContext;
    private int orientation;// 代表当前屏幕的状态（横竖屏）
    private boolean currentViewPattern = true;// true表示为心电测量 ，false表示当前为历史心电显示
    public static final String TAG = "WaveView";
    private final int UPDATE_LEADER = 0;
    private final int UPDATE_BEAT = 1;
    private SurfaceHolder holder;
    private int perItemWidth;
    private float perLineWidth;
    private float leaderWidth;
    private TextView ecgBodyDisease;
    private Button fullScreenButton;
    private float fingerXDistance;
    private float fingerYDistance;
    private int zoomX = 1;
    private int zoomY = 1;
    private float ratio;
    private float[] baseline;
    private PointF[] curPos;
    private boolean runner = false;
    private UpdateThread thread;
    public boolean[] leaders;
    private int ecgViewHeight;
    private int currentDisplayLeader = 0;// 0表示I,II,III导
    private GestureDetector gestureDetector;
    //    private FileAcquisition acq;
    private ECGDataView historyData;
    private float[] lineLeft;
    private float[] lineTop;
    private Rect leaderRect = new Rect();
    private boolean isInter = false;// 是否打断点击事件
    public String[] leaderNames = {"I", "II", "III", "avR", "avL", "avF",
            "V1", "V2", "V3", "V4", "V5", "V6"};
    public String[] leaderNames1 = {"RL", "RA", "LA", "LL", "V1", "V2",
            "V3", "V4", "V5", "V6"};
    private boolean[] leaderOut = new boolean[10];
    private LockLinkedList<ECGDataView> blockDataList = new LockLinkedList<ECGDataView>(
            100, 500);
    private Paint mDrawGainPaint = new Paint();
    private int mLines;


    private boolean mInner = false;
    private boolean mCurrentSreen = false;//半屏
    private WaveChangeListener mWaveChangeListener;
    private List<WaveRectChangeListener> mWaveRectChangeListener = new ArrayList<>();
    private PosChangeListener posChangelistener;
    private int mOritation;
    private ECGDataView[] mDatas;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        gestureDetector.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mInner = false;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (mInner) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public interface PosChangeListener {
        public void posChange(int pos);
    }


    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            boolean status = (boolean) SharePreferenceUtils.get(mContext,SharePreferenceConstant.FULL_SCREEN, true);
            if (status) {
                ((EcgMeasureActivity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else {
                ((EcgMeasureActivity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    public NewWaveView(Context context) {
        super(context);
        orientation = getContext().getResources().getConfiguration().orientation;
        holder = getHolder();
        holder.addCallback(this);
        setBackgroundColor(Color.TRANSPARENT);
        leaders = new boolean[12];
        mContext = context;
//            mLines = mData.getLines();
//            if (mLines == ECGStorageData.LINES_SINGLE){
//                leaders[0] = true;
//            }else{
        leaders[0] = true;
        leaders[1] = true;
        leaders[2] = true;
//            }
        gestureDetector = new GestureDetector(context, listener);

        setOnTouchListener(this);


        // 添加导联脱落的view
//        leaderView = new TextView(getContext());
//        ecgBodyDisease = new TextView(context);
//        ecgBodyDisease.setText(R.string.i_have_body_disease);
//        ecgBodyDisease.setBackgroundResource(R.drawable.selector_cycle_blue_drawable);
//        ecgBodyDisease.setTextSize(18);
//        ecgBodyDisease.setTextColor(Color.WHITE);
//        ecgBodyDisease.setGravity(Gravity.CENTER);
//        ecgBodyDisease.setVisibility(GONE);
//        int p = DisplayUtils.dip2px(context, 10);
        //ecgBodyDisease.setPadding(p,p,p,p);
        // 添加全屏的按钮
//        fullScreenButton = new Button(context);
//        fullScreenButton
//                .setBackgroundResource(R.drawable.selector_circle_main_back);
        mDrawGainPaint.setAntiAlias(true);
        //设置画布上Gain和Speed颜色
        mDrawGainPaint.setColor(ThreeInOneApplication.getColorRes(R.color.google_white));
    }

    public NewWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        orientation = getContext().getResources().getConfiguration().orientation;
        holder = getHolder();
        holder.addCallback(this);
        setBackgroundColor(Color.TRANSPARENT);
        leaders = new boolean[12];
        leaders[0] = true;
        leaders[1] = true;
        leaders[2] = true;
        gestureDetector = new GestureDetector(context, listener);
    }

    private void initalize(Context context) {
        lineLeft = null;
        lineTop = null;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 初始化格子的个数
            perItemWidth = this.getWidth() / 25;
            // 计算导联显示区域的宽度
            leaderWidth = this.getWidth() - 24 * perItemWidth;
            // 计算每一小格的宽度
            perLineWidth = perItemWidth / 5f;
        } else {
            // 初始化格子的个数
            perItemWidth = this.getWidth() / 15;
            // 计算导联显示区域的宽度
            leaderWidth = this.getWidth() - 14 * perItemWidth;
            // 计算每一小格的宽度
            perLineWidth = perItemWidth / 5f;
        }
        // 初始化当前波形的位置
        curPos = new PointF[12];
        // 心电波形的位置
        ecgViewHeight = (int) (this.getHeight() - 2 * perItemWidth);
        for (int i = 0; i < curPos.length; i++) {
            curPos[i] = new PointF(0, 0);
            curPos[i].x = leaderWidth;
            curPos[i].y = 0;
        }
//        leaderRect.top = 0;
//        leaderRect.left = 0;
//        leaderRect.right = (int) leaderWidth - 1;
//        leaderRect.bottom = this.getHeight();
//        ecgBodyDisease.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ECGDialogManager.showBodyDiseaseSelect(getContext(), false);
//            }
//        });
    }

    public boolean isCurrentViewPattern() {
        return currentViewPattern;
    }

    public void setCurrentViewPattern(boolean currentViewPattern) {
        this.currentViewPattern = currentViewPattern;
    }

    public void changeView(int lines) {
        for (int i = 0; i < leaders.length; i++) {
//            if (lines == ECGStorageData.LINES_SINGLE) {
//                statusItemView.setLeaderLlVisibility(View.GONE);
//                leaders[i] = i == 0 ? true : false;
//            } else {
//                statusItemView.setLeaderLlVisibility(View.VISIBLE);
//            }
        }
        postInvalidate();
    }

    public void addBlock(ECGDataView data) {
        blockDataList.addObject(data);
    }

    public synchronized ECGDataView getBlock() throws Exception {
        return blockDataList.getObject();
    }

    public void clear() {
        blockDataList.clear();
    }

    // 心电历史记录的绘制
    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "ondraw方法");
        if (currentViewPattern == false) {
            Log.e(TAG, "ondraw方法" + currentViewPattern);
            if (this.perItemWidth < 1) {
                initalize(getContext());
                postInvalidateDelayed(100);
                return;
            }
            if (getContext().getResources().getConfiguration().orientation != orientation) {
                orientation = getContext().getResources()
                        .getConfiguration().orientation;
                int pos = orientation == Configuration.ORIENTATION_PORTRAIT ? 750
                        : 1250;
                if (posChangelistener != null) {
                    posChangelistener.posChange(pos);
                }
                baseline = null;
                initalize(getContext());
            }
            if (historyData != null) {
                //canvas.drawColor(Color.WHITE);
//                changeView(historyData.block.row());
                drawBack(canvas);
                drawWave(canvas, historyData.block, historyData.adunit, historyData.sampling, historyData.leadCount);
//                int i = (int) acq.Pos();
//                statusItemView.setRecordTimeStr(ECGLeaderUtils
//                        .getSampingTimeString(i, historyData.sampling));
//                statusItemView.setBeatStr(historyData.startMeasureTime);
//                drawLeader(canvas, leaderRect);
            }
            drawGain(canvas);
            drawBetween(canvas);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e(TAG, "surface改变");
        if (perItemWidth < 1)
            initalize(getContext());
        Canvas canvas = holder.lockCanvas(null);
        if (canvas != null) {
            // canvas.drawColor(Color.WHITE);
            drawBack(canvas);
            drawEcgGrid(
                    canvas,
                    new Rect(0, 0, this.getWidth(), this
                            .getHeight()));
            drawGain(canvas);
            drawBetween(canvas);
        }
        if (canvas != null) {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBack(Canvas canvas) {
        Paint p = new Paint();
//        LinearGradient gradient = new LinearGradient(getWidth() / 2, 0, getWidth() / 2, getHeight(), new int[]{
//                ThreeInOneApplication.getColorRes(R.color.bg_end),
//                ThreeInOneApplication.getColorRes(R.color.bg_start),
//        }, new float[]{0, 1.0f}, Shader.TileMode.MIRROR);
//        p.setShader(gradient);
        p.setColor(ThreeInOneApplication.getColorRes(R.color.google_white));
        canvas.drawPaint(p);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surface创建");
        if (perItemWidth < 1)
            initalize(getContext());
        Canvas canvas = holder.lockCanvas(null);
        if (canvas != null) {
            // canvas.drawColor(Color.WHITE);
            drawBack(canvas);
            drawEcgGrid(
                    canvas,
                    new Rect(0, 0, this.getWidth(), this
                            .getHeight()));
            drawGain(canvas);
            drawBetween(canvas);
        }
        if (canvas != null) {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 修改时间2013年12月31日17:17:35 修改原因:由于采样点的固定个数不是50个 对心电数据进行抽样，以符合心电显示的要求
     *
     * @param block
     * @return
     */
    private BinMatrix<Short> Sampling2DrawData(BinMatrix<Short> block, int i) {
        int block_size = 50;
        ratio = 1;
        BinMatrix<Short> data = new BinMatrix<Short>(block.row(),
                block_size);
        for (int r = 0; r < data.row(); r++) {
            for (int c = 0; c < data.col(); c++) {
                int index1 = (int) (c * ratio) + i * block_size;
//                data.set_element(r, c, (Short) block.get_element(r, index1));
            }
        }
        return data;
    }

    private synchronized void drawWave(Canvas canvas,
                                       short[] data, double adunit, float sampling, int leadCount) {
        if (data == null)
            throw new NullPointerException("不是波形数据");
        drawBlockData(canvas, data, adunit, sampling, leadCount);
    }

    private void leaderChange(boolean status) {
        if (status) {
            for (int i = 0; i < leaders.length; i++) {
                if (i / 3 == currentDisplayLeader)
                    leaders[i] = true;
                else
                    leaders[i] = false;
            }
            reset();
        }
    }

    /**
     * 显示某一个数据块
     *
     * @param canvas
     * @param cur_block
     * @param adunit
     */
    private synchronized void drawBlockData(Canvas canvas,
                                            short[] cur_block, double adunit, float sampling, int leadCount) {
        int row = leadCount;
        int col = cur_block.length / row;
        if (baseline == null) {
            baseline = new float[row];
            //firstPoint = new short[row];
            int visible_count = 0;
            float step_y = 0;
            if (leadCount == 12) {
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
                short v = cur_block[i];
                curPos[i] = new PointF(leaderWidth, v);
                // firstPoint[i] = (Short) cur_block.get_element(i, 0);
                // firstPoint[i] = 0;
                if (leaders[i]) {
                    baseline[i] = 2 * perItemWidth + step_y
                            * (2 * visible_index + 1);
                    visible_index++;
                    curPos[i].y = baseline[i];
//                    Log.e(TAG, "baseLine = " + baseline[i]);
                }
            }
        }

        Paint paint = new Paint();
        paint.setStrokeWidth(2.5f);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        //canvas.drawColor(Color.WHITE);
        //修改历史心电背景
        drawBack(canvas);
        Rect dirt = new Rect();
        if (currentViewPattern == true) {
            dirt.left = (int) (curPos[0].x);
            dirt.right = (int) (curPos[0].x + perItemWidth * zoomX) + 4 + 4;
            dirt.top = 0;
            dirt.bottom = this.getHeight();
            canvas.drawRect(dirt, paint);
            dirt.left = (int) curPos[0].x;
            dirt.right = (int) (curPos[0].x + perItemWidth * zoomX);
        } else {
            dirt.top = 0;
            dirt.bottom = this.getHeight();
            dirt.left = 0;
            dirt.right = this.getWidth();
        }
        drawEcgGrid(canvas, dirt);
        paint.setColor(ThreeInOneApplication.getColorRes(R.color.google_black));
        float step = perItemWidth / (sampling / 5);
        paint.setStyle(Paint.Style.STROKE);
        Path ps[] = new Path[row];
        for (int i = 0; i < row; i++) {
            ps[i] = new Path();
            ps[i].reset();
            for (int j = 0; j < col; j++) {
                float x = curPos[i].x + step * zoomX;
                short val = cur_block[i + j * row];
                float y = (float) (-((val) / adunit) * perLineWidth
                        * zoomY * 10);
                y += baseline[i];
                if (j == 0) {
                    ps[i].moveTo(x, y);
                } else {
                    ps[i].lineTo(x, y);
                }
//                    if (leaders[i] == true) {
                // canvas.drawLine(curPos[i].x, curPos[i].y, x, y, paint);
//                    }
                if (leaders[i] == true) {
                    curPos[i].x = x;
                    curPos[i].y = y;
                    if (Math.abs(curPos[i].x) >= (lineLeft[lineLeft.length - 1] - 1)) {
                        curPos[i].x = leaderWidth;
                        break;
                    }
                }
            }
            canvas.drawPath(ps[i], paint);
        }
    }

//    /**
//     * 画波形
//     *
//     * @param data
//     */
//    private void drawWave(ECGData data) {
//        synchronized (data) {
//            final BinMatrix<Short> cur_block = data.getData();
//            if (cur_block == null)
//                throw new NullPointerException("不是波形数据");
//            int size = cur_block.col() / 50;
//            for (int i = 0; i < size; i++) {
//                data.setProcessTime(System.currentTimeMillis());
//                ECGDataView ecg = new ECGDataView();
//                ecg.block = this.Sampling2DrawData(cur_block, i);
//                ecg.hr = data.getNonZeroHr();
//                ecg.adunit = data.getWaveData().get_adunit();
//                ecg.startMeasureTime = data.getMeasureTime();
//                ecg.processTime = System.currentTimeMillis();
//                addBlock(ecg);
//            }
//        }
//    }

    // 绘制指定区域的心电网格
    public synchronized void drawEcgGrid(Canvas canvas, Rect rect) {
        try {
            Paint paint = new Paint();
            boolean status = false;// 判断是否是第一次绘制网格，为了网格能够重叠
            int number = (int) (rect.width() / perItemWidth * 5);// 计算需要绘制网格线的数量
//                Log.e(getClass().getSimpleName(), "perItemWidth = " + perItemWidth);
            int leftNumber = 0;// 从第几根线开始绘制
            if (lineLeft == null) {
//                    Log.e(getClass().getSimpleName(), "lineLeft1 = " + lineLeft);
                lineLeft = new float[number + 1];
                status = true;
            } else {// 找出当前为第几根线
//                    Log.e(getClass().getSimpleName(), "lineLeft2 = " + lineLeft);
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
            int i = 0;
            for (i = leftNumber; i < number + leftNumber; i++) {
                if (status == true)
                    lineLeft[i] = left;
                else
                    left = lineLeft[i];
                if (i % 5 == 0) {
//                        paint.setStrokeWidth(2.5f);
                    paint.setColor(Color.RED);
//                        paint.setColor(Color.argb(64, 255, 255, 255));
//                    paint.setColor(Color.rgb(163, 163, 188));
                    canvas.drawLine(left, rect.top, left, rect.bottom,
                            paint);
                } else {
//                        paint.setColor(Color.argb(38, 255, 255, 255));
                    paint.setColor(Color.rgb(247, 200, 200));
//                        paint.setStrokeWidth(1.0f);
//                    paint.setColor(Color.rgb(66, 66, 92));
                    canvas.drawLine(left, rect.top, left, rect.bottom, paint);
                }
                left += perLineWidth;
            }
            if (status == true) {
                lineLeft[i] = left;
            }
            if (i % 5 == 0) {
//                    paint.setColor(Color.argb(64, 255, 255, 255));
//                    paint.setStrokeWidth(2.5f);
                paint.setColor(Color.RED);
//                paint.setColor(Color.rgb(163, 163, 188));
                canvas.drawLine(left, rect.top, left, rect.bottom, paint);
            } else {
//                    paint.setColor(Color.argb(38, 255, 255, 255));
//                    paint.setStrokeWidth(1.0f);
                paint.setColor(Color.rgb(247, 200, 200));
//                paint.setColor(Color.rgb(66, 66, 92));
                canvas.drawLine(left, rect.top, left, rect.bottom, paint);
            }
            number = (int) (rect.height() / (float) perItemWidth * 5);
            if (lineTop == null) {
//                    Log.e(getClass().getSimpleName(), "lineTop1 = " + lineTop);
                lineTop = new float[number + 1];
                status = true;
            } else {
                float value = 7.2f;
//                    Log.e(getClass().getSimpleName(), "lineTop2 = " + lineTop);
                for (i = 0; i < lineTop.length; i++) {// 找出最小的number
                    float value1 = rect.top - lineTop[i];
                    if (Math.abs(value1) < Math.abs(value)) {
                        value = value1;
                        leftNumber = i;
                    }
                }
            }
            left = rect.top;
            for (i = leftNumber; i < number + leftNumber; i++) {
                if (status == true) {
                    lineTop[i] = left;
                } else {
                    left = lineTop[i];
                }
                if (i % 5 == 0) {
//                        paint.setColor(Color.argb(64, 255, 255, 255));
//                        paint.setStrokeWidth(2.5f);
                    paint.setColor(Color.RED);
//                    paint.setColor(Color.rgb(163, 163, 188));
                    canvas.drawLine(rect.left, left, rect.right, left, paint);
                } else {
//                        paint.setColor(Color.argb(38, 255, 255, 255));
//                        paint.setStrokeWidth(1.0f);
                    paint.setColor(Color.rgb(247, 200, 200));
//                    paint.setColor(Color.rgb(66, 66, 92));
                    canvas.drawLine(rect.left, left, rect.right, left, paint);
                }
                left += perLineWidth;
            }
            if (status == true) {
                lineTop[i] = left;
            }
            if (i % 5 == 0) {
//                    paint.setColor(Color.argb(64, 255, 255, 255));
//                    paint.setStrokeWidth(2.5f);
                paint.setColor(Color.RED);
//                paint.setColor(Color.rgb(163, 163, 188));
                canvas.drawLine(rect.left, left, rect.right, left, paint);
            } else {
//                    paint.setColor(Color.argb(38, 255, 255, 255));
//                    paint.setStrokeWidth(1.0f);
                paint.setColor(Color.rgb(247, 200, 200));
//                paint.setColor(Color.rgb(66, 66, 92));
                canvas.drawLine(rect.left, left, rect.right, left, paint);
            }
        } catch (Exception e) {
//            Logger.e(e, null);
        }
    }

    public void drawLeaderOut() {
        if (ECGUtils.judgeBooleanIs(leaderOut, false) == false) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < leaderOut.length; i++) {
                if (leaderOut[i] == true) {
                    builder.append(leaderNames1[i]).append(",");
                }
            }
            builder.deleteCharAt(builder.length() - 1);
//            builder.append(DfthMonitorApplication
//                    .getStringRes(R.string.ecg_leader_out));
            handler.obtainMessage(0, builder.toString()).sendToTarget();
        } else {
            handler.obtainMessage(0, "").sendToTarget();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case UPDATE_LEADER: {
                    String message = (String) msg.obj;
//                    leaderView.setText(message);
                }
                break;
                case UPDATE_BEAT: {
                    int hr = (Integer) ((Object[]) msg.obj)[0];
                    long time = (Long) ((Object[]) msg.obj)[1];
//                    statusItemView.setBeat(hr);
//                    statusItemView.setRecordTime(time);
                }
                break;
            }

        }

        ;
    };

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    public void setLeaderOut(boolean[] leaderOut) {
        for (int i = 0; i < leaderOut.length; i++) {
            this.leaderOut[i] = leaderOut[i];
        }
    }

    private void drawLeader(Canvas canvas, Rect dirt) {
        if (canvas == null || baseline == null)
            return;
        Paint paint = new Paint();
        paint.setTextSize(perItemWidth / 2.2f);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        if (dirt != null) {
            canvas.drawRect(dirt, paint);
        }
        paint.setColor(Color.WHITE);
        for (int i = 0; i < leaders.length; i++) {
            if (leaders[i] == true) {
                if (historyData.leadCount == 12) {
                    String text = leaderNames[i];
                    if (baseline != null && baseline.length > i) {
                        float width = paint.measureText(text);
                        canvas.drawText(text, dirt.width() / 2, baseline[i],
                                paint);
                    }
                }
            }
        }
    }

    private void drawBeat(int hr, long startTime, long processTime) {
        handler.obtainMessage(UPDATE_BEAT, 0, 0,
                new Object[]{hr, processTime - startTime})
                .sendToTarget();
    }

    GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            isInter = true;
            // getDialog();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mInner = true;
            if (mWaveRectChangeListener != null) {
                perItemWidth = 0;
                if (!mCurrentSreen) {
                    screenIn();
                } else {
                    screenOut();
                }
                mCurrentSreen = !mCurrentSreen;
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            isInter = true;
            if (currentViewPattern == true)
                return super.onScroll(e1, e2, distanceX, distanceY);
//            if (acq != null) {
//                int distance = (int) (e1.getX() - e2.getX());
//                int length = (int) acq.Length();
//                int pos = (int) acq.Pos();
//                float p = historyData.sampling / 5f * 15f;
//                if (pos >= length - p && distance > 0) {
//                } else if (pos <= 0 && distance < 0) {
//                } else {
//                    int new_pos = (int) (pos + distance
//                            * (historyData.sampling / 20f / perItemWidth));
//                    //distance = distance > 0 ? perItemWidth : -perItemWidth;
//                    //int new_pos = (int) (pos + (historyData.sampling / 5) * perItemWidth / distance);
//                    if (new_pos > length - p)
//                        new_pos = (int) (length - p);
//                    if (new_pos < 0)
//                        new_pos = 0;
//                    acq.seek_to(new_pos);
//                }
//            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            isInter = true;
            if (currentViewPattern == false)
                return super.onFling(e1, e2, velocityX, velocityY);
            if (velocityY > 2.0 || velocityY < -2.0) {
                currentDisplayLeader = velocityY > 0 ? currentDisplayLeader + 1
                        : currentDisplayLeader - 1;
                boolean status = currentDisplayLeader < 0 ? false
                        : currentDisplayLeader > 3 ? false : true;
                currentDisplayLeader = currentDisplayLeader < 0 ? 0
                        : currentDisplayLeader > 3 ? 3
                        : currentDisplayLeader;
                leaderChange(status);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    private void screenOut() {
        for (WaveRectChangeListener l : mWaveRectChangeListener) {
            l.screenOut();
        }
    }

    private void screenIn() {
        for (WaveRectChangeListener l : mWaveRectChangeListener) {
            l.screenIn();
        }
    }


    private void screenVScreen() {
        for (WaveRectChangeListener l : mWaveRectChangeListener) {
            l.screenVScreen();
        }
    }
    private void screenHScreen() {
        for (WaveRectChangeListener l : mWaveRectChangeListener) {
            l.screenHScreen();
        }
    }

    private void drawGain(Canvas canvas) {
        mDrawGainPaint.setColor(Color.BLACK);
        mDrawGainPaint.setTextSize(perItemWidth / 3f);
        mDrawGainPaint.setStyle(Paint.Style.FILL);
        int i;
        for (i = 0; i < getHeight() / perItemWidth; i++) {
            if (i * perItemWidth - (getHeight() / 2) > perItemWidth / 2f) {
                break;
            }
        }
        int l = leaderRect.right + 1;
        canvas.drawLine(l, (i + 2) * perItemWidth, l + perLineWidth, (i + 2) * perItemWidth, mDrawGainPaint);
        canvas.drawLine(l + perLineWidth, (i + 2) * perItemWidth, l + perLineWidth, i * perItemWidth, mDrawGainPaint);
        canvas.drawLine(l + perLineWidth, i * perItemWidth, l + 4 * perLineWidth, i * perItemWidth, mDrawGainPaint);
        canvas.drawLine(l + 4 * perLineWidth, i * perItemWidth, l + 4 * perLineWidth, (i + 2) * perItemWidth, mDrawGainPaint);
        canvas.drawLine(l + 4 * perLineWidth, (i + 2) * perItemWidth, l + 5 * perLineWidth, (i + 2) * perItemWidth, mDrawGainPaint);
        float left = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 21.5f : 11.5f;
        canvas.drawText(ThreeInOneApplication.getStringRes(R.string.ecg_gain), left * perItemWidth, DisplayUtils.getTextHeight(mDrawGainPaint, perItemWidth), mDrawGainPaint);
        canvas.drawText(ThreeInOneApplication.getStringRes(R.string.ecg_rate), left * perItemWidth, DisplayUtils.getTextHeight(mDrawGainPaint, 2 * perItemWidth), mDrawGainPaint);
    }

    private void drawBetween(Canvas canvas) {
        if (!currentViewPattern) {
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
    }

    @SuppressWarnings("unchecked")
//    public void updata(IModel model) {
//        if (model instanceof IRandomAcquisition) {
//            acq = (FileAcquisition) model;
//        }
//        ECGData data = (ECGData) model.getData();
//        historyData = new ECGDataView();
//        historyData.block = (BinMatrix<Short>) data.getWaveData()
//                .getLastData();
//        historyData.adunit = data.getWaveData().get_adunit();
//        historyData.startMeasureTime = data.getMeasureTime();
//        historyData.sampling = acq.getSampling();
//        historyData.hr = data.get_hr();
//        this.postInvalidate();
//    }


            // 心电波形绘制线程
    class UpdateThread extends Thread {
        boolean pause = false;

        public void pause() {
            pause = true;
        }

        public void startDraw() {
            reset();
            clear();
            pause = false;
        }

        public UpdateThread() {
            reset();
        }

        @Override
        public void run() {
            while (runner) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
//                    Logger.e(e, null);
                }
                if (check() == true)
                    continue;
                synchronized (holder) {
                    Canvas canvas = null;
                    try {
                        ECGDataView data = getBlock();
                        if (data != null && !pause) {
                            Rect dirt = new Rect();
//                                changImage();
//                                if (curPos[0].x <= leaderWidth + 1) {
//                                    curPos[0].x = leaderWidth;
//                                    if(firstPoint == null)
//                                    firstPoint = new short[data.block.row()];
//                                    for(int i = 0; i < firstPoint.length; i++){
//                                        firstPoint[i] = (short) data.block.get_element(i,0);
//                                    }
//                                }
                            dirt.left = (int) curPos[0].x;
                            dirt.right = (int) (curPos[0].x + perItemWidth
                                    * zoomX) + 8;
                            dirt.top = 0;
                            dirt.bottom = NewWaveView.this.getHeight();
                            canvas = holder.lockCanvas(dirt);
                            if (canvas != null) {// 绘制波形
                                drawWave(canvas, data.block, data.adunit, data.sampling, data.leadCount);
                                drawGain(canvas);
                                holder.unlockCanvasAndPost(canvas);
                            }
//                                drawBeat(data.hr, data.startMeasureTime,data.processTime);
                            canvas = holder.lockCanvas(leaderRect);
                            drawLeader(canvas, leaderRect);
                            drawBack(canvas);
                            leaderRect.top = 0;
                            leaderRect.left = 0;
                            leaderRect.right = (int) leaderWidth;
                            leaderRect.bottom = NewWaveView.this.getHeight();
                            drawEcgGrid(canvas, leaderRect);
                            drawLeader(canvas, leaderRect);
                            holder.unlockCanvasAndPost(canvas);
                            Rect dirt2 = new Rect();
                            dirt2.left = (int) curPos[0].x;
                            dirt2.right = (int) (curPos[0].x) + 8;
                            dirt2.top = 0;
                            dirt2.bottom = NewWaveView.this.getHeight();
                            canvas = holder.lockCanvas(dirt2);
//                            drawLineBack(canvas);
                        }
                    } catch (Exception e) {
//                        Logger.e(e, null);
                    } finally {
                        if (canvas != null) {
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
            reset();
        }

        public void cancel() {
            clear();
            runner = false;
            interrupt();
        }

        private boolean check() {
            return perItemWidth < 1 ? true : false;
        }
    }


    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {

        return super.onFilterTouchEventForSecurity(event);
    }


    public void onPause() {
        if (thread != null) {
            thread.pause();
        }
    }

    public void onResume() {
        reset();
        if (thread != null) {
            DelayPerformMethod.getMethod().performMethodDelayTime(200,
                    thread, "startDraw");
        }
    }


    public void startEcg() {
//        User user = UserManager.getUserManager().getDefaultUser();
//        if (user.getmId() != -1) {
//            ECGDialogManager.showBodyDiseaseSelect(getContext(), true);
//        } else {
//            HandlerEvent event = new HandlerEvent(
//                    EventNameMessage.ECG_MEASURE_START, 0, null);
//            HandlerManager.getManager().sendEvent(event);
//        }
    }

    public void startDraw() {
//        reset();
        if (thread == null) {
            runner = true;
            thread = new UpdateThread();
            thread.start();
        }
    }

    public void endDraw() {
        if (thread != null) {
            thread.cancel();
            thread = null;
        }
    }

    private void resetEnd() {
        runner = false;
        clear();
        thread = null;
//        if (bottomControlItem != null) {
//            bottomControlItem.setImageView(R.drawable.ecg_start);
//            statusItemView.reset();
//        }
        reset();
    }

    // 界面重置
    public void reset() {
        if (currentViewPattern == false) {
            if (curPos != null) {
                for (int i = 0; i < curPos.length; i++) {
                    curPos[i] = new PointF(0, 0);
                    curPos[i].x = leaderWidth;
                    curPos[i].y = 0;
                }
                baseline = null;
            }
            this.postInvalidate();
            return;
        }
        synchronized (holder) {
            Canvas canvas = holder.lockCanvas(null);
            if (canvas != null) {
                drawBack(canvas);
                drawEcgGrid(canvas,
                        new Rect(0, 0, this.getWidth(),
                                this.getHeight()));
                drawGain(canvas);
                drawBetween(canvas);
                handler.obtainMessage(0, "").sendToTarget();
            }
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
            if (curPos != null) {
                for (int i = 0; i < curPos.length; i++) {
                    curPos[i] = new PointF(0, 0);
                    curPos[i].x = (int) leaderWidth;
                    curPos[i].y = 0;
                }
                baseline = null;
            }
        }
    }

    // 波形数据的模型
    class ECGDataView {
        short[] block;// 数据块（心电数据）
        int hr;// 心律
        double adunit;// 采样率
        long startMeasureTime;// 开始测量时间
        long processTime;// 记录时间
        float sampling;
        int leadCount;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        gestureDetector.onTouchEvent(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                fingerXDistance = event.getX();
                fingerYDistance = event.getY();
                mInner = false;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (currentViewPattern == false)
                    return false;
                if (isInter == true) {
                    isInter = false;
                    return false;
                }
                if (mInner) {
                    return true;
                }
                fingerXDistance = event.getX() - fingerXDistance;
                fingerYDistance = event.getY() - fingerYDistance;
//                int status = statusItemView.getVisibility() == View.GONE ? View.VISIBLE
//                        : View.GONE;
//                statusItemView.setVisibility(status);
//                status = bottomControlItem.getVisibility() == View.GONE ? View.VISIBLE
//                        : View.GONE;
//                bottomControlItem.setVisibility(status);
//                status = fullScreenButton.getVisibility() == View.GONE ? View.VISIBLE
//                        : View.GONE;
//                if(status == View.VISIBLE && blueStatus.equals(DeviceStatus.START)){
//                    ecgBodyDisease.setVisibility(View.VISIBLE);
//                }else{
//                    ecgBodyDisease.setVisibility(View.GONE);
//                }
//                fullScreenButton.setVisibility(status);
                return false;
            }
        }
        return true;
    }


    public void setWaveRectChangeListener(
            WaveRectChangeListener waveRectChangeListener) {
        this.mWaveRectChangeListener.add(waveRectChangeListener);
    }

    public static interface WaveRectChangeListener {
        public void screenIn();//进入全屏

        public void screenOut();//退出全屏

        public void screenHScreen();//转屏

        public void screenVScreen();//恢复
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation != orientation) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setFullscreen(true);
                screenHScreen();
//                if (mFragment != null) {
//                    mFragment.getTitleView().setVisibility(GONE);
//                }
            } else {
                setFullscreen(false);
                screenVScreen();
//                if (mFragment != null) {
//                    mFragment.getTitleView().setVisibility(VISIBLE);
//                }
            }
            getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
            readyView();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void readyView() {
        if (currentViewPattern) {
            if (thread != null) {
                thread.pause();
            }
        }
    }

    public void quit() {
//        isPause = true;
        HomeActivity activity = (HomeActivity) getContext();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullscreen(false);
//            if (mFragment != null) {
//                mFragment.getTitleView().setVisibility(VISIBLE);
//            }
        }
    }

    public boolean back() {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            HomeActivity activity = (HomeActivity) getContext();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            return true;
        }
        return false;
    }

    public void jump() {
        HomeActivity activity = (HomeActivity) getContext();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setFullscreen(boolean on) {
        Window win = ((EcgMeasureActivity) getContext()).getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            winParams.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        win.setAttributes(winParams);
    }
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
//            if (isPause == false) {
                updataView();
//            }
        }
    };

    public void updataView() {
        if (getResources().getConfiguration().orientation != orientation) {
            if (currentViewPattern == true) {
                orientation = getContext().getResources().getConfiguration().orientation;
                initalize(getContext());
                reset();
                if (thread != null) {
                    DelayPerformMethod.getMethod().performMethodDelayTime(500, thread, "startDraw");
                }
            }
        }
    }
    public void updateWave(int progress) {

    }

    /**
     * 画波形
     *
     * @param data
     */

    public void drawWave(ECGMeasureData data) {
        synchronized (data) {
            if (data == null)
                throw new NullPointerException("不是波形数据");
            ECGDataView ecg = new ECGDataView();
            ecg.block = data.datas();
            ecg.adunit = data.adunit();
            ecg.sampling = data.sampling();
            ecg.leadCount = data.chan();
            addBlock(ecg);
        }
    }
}

