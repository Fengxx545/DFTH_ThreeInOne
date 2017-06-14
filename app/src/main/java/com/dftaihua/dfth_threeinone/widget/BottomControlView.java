package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.measure.listener.ECGMeasureControllerConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGRhythmConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGScreenConduct;
import com.dftaihua.dfth_threeinone.mediator.MeasureMediator;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.ECGDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dfth.sdk.device.DfthDevice;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/13 14:08
*/
public class BottomControlView extends RelativeLayout implements View.OnClickListener, ECGMeasureControllerConduct, ECGRhythmConduct, ECGScreenConduct {
    private LinearLayout mBottomRl;
    private ImageView mControlIv;
    private ImageView mLeaderIv;
    private TextView mControlTv;
    private BpmMeasureLayout mBpmLayout;
    private TextView mLeaderTv;
    private MeasureMediator mMeasureMediator;
    private boolean isMeasuring = false;
    private int mOrientation = Configuration.ORIENTATION_PORTRAIT;

    public BottomControlView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mBottomRl = new LinearLayout(context);
        /**
         * 添加左边导联栏
         */
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
        params.weight = 1;
        params.width = 0;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        int imageWidth = DisplayUtils.dip2px(getContext(),40);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, 0);
        params1.width = imageWidth;
        params1.height = imageWidth;

        LinearLayout leaderLl = new LinearLayout(context);
        leaderLl.setOrientation(LinearLayout.VERTICAL);
        leaderLl.setId(R.id.measure_leader);
        leaderLl.setGravity(Gravity.CENTER);
        leaderLl.setOnClickListener(this);

        mLeaderIv = new ImageView(context);
        mLeaderIv.setImageResource(R.drawable.choose_lead);
        leaderLl.addView(mLeaderIv, params1);

        params1 = new LinearLayout.LayoutParams(0, 0);
        params1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params1.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        mLeaderTv = new TextView(context);
        mLeaderTv.setText(R.string.choose_leader);
        mLeaderTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mLeaderTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
        leaderLl.addView(mLeaderTv, params1);

        mBottomRl.addView(leaderLl, params);

        params = new LinearLayout.LayoutParams(0, 0);
        params.weight = 1;
        params.width = 0;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        View view = new View(getContext());
        mBottomRl.addView(view, params);
        /**
         * 添加中间测量栏
         */
        RelativeLayout.LayoutParams params2 = new LayoutParams(0, 0);
        params2.width = DisplayUtils.getPhoneWidth(ThreeInOneApplication.getInstance()) / 3;
        params2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params2.addRule(RelativeLayout.CENTER_IN_PARENT);

        params1 = new LinearLayout.LayoutParams(0, 0);
        params1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params1.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        LinearLayout bpmLl = new LinearLayout(context);
        bpmLl.setOrientation(LinearLayout.VERTICAL);
        bpmLl.setGravity(Gravity.CENTER);

        mBpmLayout = new BpmMeasureLayout(context);

        /**
         * 添加右侧控制栏
         */
        params = new LinearLayout.LayoutParams(0, 0);
        params.weight = 1;
        params.width = 0;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        params1 = new LinearLayout.LayoutParams(0, 0);
        params1.width = imageWidth;
        params1.height = imageWidth;

        LinearLayout controlLl = new LinearLayout(context);
        controlLl.setOrientation(LinearLayout.VERTICAL);
        controlLl.setId(R.id.measure_control_father);
        controlLl.setOnClickListener(this);
        controlLl.setGravity(Gravity.CENTER);
        mControlIv = new ImageView(context);
        mControlIv.setId(R.id.measure_control);
        mControlIv.setImageResource(R.drawable.measure_start);
        controlLl.addView(mControlIv, params1);

        params1 = new LinearLayout.LayoutParams(0, 0);
        params1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params1.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        mControlTv = new TextView(context);
        mControlTv.setText("开始");
        mControlTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mControlTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
        controlLl.addView(mControlTv, params1);

        mBottomRl.addView(controlLl, params);
        RelativeLayout.LayoutParams p = new LayoutParams(0, 0);
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = DisplayUtils.getPhoneWidth(ThreeInOneApplication.getInstance()) / 5;
        p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        this.addView(mBottomRl, p);
        this.addView(mBpmLayout, params2);
        setBackgroundResource(R.drawable.twelve_measure_bottom_bg);
    }

    public void reset() {
        mBpmLayout.reset();
    }

    public void setBack(){
        if(isMeasuring){
            setBackgroundResource(R.drawable.twelve_measure_bottom_bg);
        } else{
            setBackgroundResource(R.drawable.twelve_un_measure_bottom_bg);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.measure_control_father: {
                if (mMeasureMediator != null) {
                    mMeasureMediator.measureButtonClick();
//                    DfthDevice dfthDevice = DfthDeviceManager.getInstance().getDevice(DfthDevice.EcgDevice);
//                    isMeasuring = !(dfthDevice == null || dfthDevice.getDeviceState() == DfthDevice.DISCONNECTED);
//                    SharePreferenceUtils.put(getContext(), SharePreferenceConstant.ECG_MEASURE_IS_BEGIN,isMeasuring);
                }
            }
            break;
            case R.id.measure_leader: {
                if (mMeasureMediator != null) {
                    mMeasureMediator.longPressScreen();
                }
            }
            break;
        }
    }

    @Override
    public void bindMediator(MeasureMediator mediator) {
        mMeasureMediator = mediator;
    }

    @Override
    public void setStart(boolean start) {
        String text = start ? "结束" : "开始";
        int res = start ? R.drawable.measure_stop : R.drawable.measure_start;
        int measureRes = start ? R.drawable.twelve_measure_bottom_bg : R.drawable.twelve_un_measure_bottom_bg;
        setBackgroundResource(measureRes);
        mControlTv.setText(text);
        mControlIv.setImageResource(res);
    }

    @Override
    public void setHeartRate(int rate) {
        mBpmLayout.setRate(rate);
    }

    @Override
    public void setMeasureTime(String time) {
        mBpmLayout.setTime(time);
//        mIndex++;
//        if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
//            if(mIndex % 4 == 0){
//                setBackgroundResource(R.drawable.twelve_measure_bottom_bg);
//            } else if(mIndex % 4 == 3){
//                setBackgroundResource(R.drawable.twelve_un_measure_bottom_bg);
//            }
//        }
    }

    @Override
    public void orientationChange(int orientation) {
        mOrientation = orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setBackgroundResource(R.color.measure_start_bg);
            getLayoutParams().height = DisplayUtils.dip2px(getContext(), 80);
        } else {
            if(ECGDeviceUtils.isECGDeviceMeasuring()){
                setBackgroundResource(R.drawable.twelve_measure_bottom_bg);
            } else{
                setBackgroundResource(R.drawable.twelve_un_measure_bottom_bg);
            }
            getLayoutParams().height = ThreeInOneApplication.getScreenWidth() * 221 / 749;
        }
    }
}
