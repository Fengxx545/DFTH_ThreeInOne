package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.BpMeasureActivity;
import com.dftaihua.dfth_threeinone.activity.BpMeasurePreActivity;
import com.dftaihua.dfth_threeinone.activity.EcgHistoryActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Constant.BpConstant;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.device.DfthECGDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.listener.DfthSingleDeviceDataListener;
import com.dfth.sdk.listener.DfthTwelveDeviceDataListener;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.model.ecg.EcgDataTransmitted;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/1/17 0017.
 */
public class TaskCardView extends RelativeLayout implements View.OnClickListener,
        DfthSingleDeviceDataListener, DfthTwelveDeviceDataListener {
    private ImageView mDeviceIv;
    private TextView mMeasureTimeTv;
    private TextView mDeviceNameTv;
    private TextView mDataFirstValueTv;
    private TextView mDataSecondValueTv;
    private TextView mDataThirdValueTv;
    private TextView mDataFirstKeyTv;
    private TextView mDataSecondKeyTv;
    private TextView mDataThirdKeyTv;
    private TextView mDataDetailHeadTv;
    private TextView mDataDetailContentTv;
    private TextView mMeasureBtn;
    private int mDeviceType; //1.血压   2.单道   3.12导
    private LinearLayout mKeyLl;
    private LinearLayout mValueLL;
    private TextView mHelperInfoTv;
    private LinearLayout mDataDetailLl;
    private LinearLayout mDataValueLl;
    private Context mContext;
    private HashMap<String, Integer> mDeviceMap;
    private BpResult mBpResult;
    private ECGResult mEcgResult;
    private BpPlan mCurrentPlan;
    private DfthDevice mDfthDevice;
    private RelativeLayout mAllDataValueRl;

    public TaskCardView(Context context, int deviceType) {
        super(context);
        mDeviceType = deviceType;
        initView(context);
        mContext = context;
        mDeviceMap = new HashMap();
        EventBus.getDefault().register(this);
    }

    public TaskCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TaskCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_task_card, this, true);
        mDeviceIv = (ImageView) findViewById(R.id.view_task_card_device_iv);
        mMeasureTimeTv = (TextView) findViewById(R.id.view_task_card_measure_time_tv);
        mDeviceNameTv = (TextView) findViewById(R.id.view_task_card_device_name_tv);
        mDataFirstValueTv = (TextView) findViewById(R.id.view_task_card_data_first_value_tv);
        mDataSecondValueTv = (TextView) findViewById(R.id.view_task_card_data_second_value_tv);
        mDataThirdValueTv = (TextView) findViewById(R.id.view_task_card_data_third_value_tv);
        mDataFirstKeyTv = (TextView) findViewById(R.id.view_task_card_data_first_key_tv);
        mDataSecondKeyTv = (TextView) findViewById(R.id.view_task_card_data_second_key_tv);
        mDataThirdKeyTv = (TextView) findViewById(R.id.view_task_card_data_third_key_tv);
        mDataDetailHeadTv = (TextView) findViewById(R.id.view_task_card_data_detail_head_tv);
        mDataDetailContentTv = (TextView) findViewById(R.id.view_task_card_data_detail_content_tv);
        mKeyLl = (LinearLayout) findViewById(R.id.view_task_card_key_ll);
        mValueLL = (LinearLayout) findViewById(R.id.view_task_card_value_ll);
        mHelperInfoTv = (TextView) findViewById(R.id.view_task_card_helper_info_tv);
        mDataDetailLl = (LinearLayout) findViewById(R.id.view_task_card_data_detail_ll);
        mMeasureBtn = (TextView) findViewById(R.id.view_task_card_measure_btn);
        mDataValueLl = (LinearLayout) findViewById(R.id.view_task_card_data_value_ll);
        mAllDataValueRl = (RelativeLayout) findViewById(R.id.view_task_card_data_rl);
        mMeasureBtn.setOnClickListener(this);
//        mDataValueLl.setOnClickListener(this);
        mAllDataValueRl.setOnClickListener(this);
        if (mDeviceType == Constant.DeviceConstant.DFTH_HELPER) {
            if ((int) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.IS_FIRST_LOGIN, -1) == 1) {
                mHelperInfoTv.setText(R.string.activity_home_helper_first_see_you);
                mMeasureBtn.setText(R.string.activity_home_helper_search_device);
            } else {
                mHelperInfoTv.setText(R.string.activity_home_helper_has_bind_device);
                mMeasureBtn.setVisibility(GONE);
            }
            mDeviceIv.setImageResource(R.drawable.card_helper);
            mDataValueLl.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_card_helper));
            mDeviceNameTv.setText(R.string.activity_home_helper);
            mHelperInfoTv.setVisibility(VISIBLE);
            mKeyLl.setVisibility(GONE);
            mValueLL.setVisibility(GONE);
            mDataDetailLl.setVisibility(GONE);
            mMeasureTimeTv.setVisibility(INVISIBLE);
        } else if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
            mDeviceIv.setImageResource(R.drawable.card_blood);
            mDataValueLl.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_card_blood));
            mDataDetailHeadTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.card_bp_theme));
            mDeviceNameTv.setText(R.string.activity_home_device_blood);
            setBpUnit();
            mDataThirdKeyTv.setText(R.string.activity_home_blood_pulse_rate);
            String userId = UserManager.getInstance().getDefaultUser().getUserId();
            mCurrentPlan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
            //判断当前是否有测量模式
            if (mCurrentPlan == null || mCurrentPlan.getStatus() != BpPlan.STATE_RUNNING) {
                mDataDetailLl.setVisibility(GONE);
            } else {
                mDataDetailLl.setVisibility(VISIBLE);
            }
        } else if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG) {
            mDeviceIv.setImageResource(R.drawable.card_single_ecg);
            mDataValueLl.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_card_single_ecg));
            mDeviceNameTv.setText(R.string.activity_home_device_single_ecg);
            mDataFirstKeyTv.setText(R.string.activity_home_ecg_aver_rate);
            mDataSecondKeyTv.setText(R.string.activity_home_ecg_pvc);
            mDataThirdKeyTv.setText(R.string.activity_home_ecg_sves);
            mDataDetailHeadTv.setText(R.string.activity_home_ecg_analysis);
            mDataDetailHeadTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.card_single_theme));
        } else if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG) {
            mDeviceIv.setImageResource(R.drawable.card_twelve_ecg);
            mDataValueLl.setBackground(ThreeInOneApplication.getDrawableRes(R.drawable.shape_card_twelve_ecg));
            mDeviceNameTv.setText(R.string.activity_home_device_twelve_ecg);
            mDataFirstKeyTv.setText(R.string.activity_home_ecg_aver_rate);
            mDataSecondKeyTv.setText(R.string.activity_home_ecg_pvc);
            mDataThirdKeyTv.setText(R.string.activity_home_ecg_sves);
            mDataDetailHeadTv.setText(R.string.activity_home_ecg_analysis);
            mDataDetailHeadTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.card_twelve_theme));
        }
    }

    public void onResume(ECGResult result) {
        mEcgResult = result;
        setEcgData(result, mDeviceType);
        mDfthDevice = DfthDeviceManager.getInstance().getDevice(mDeviceType);
        if (mDfthDevice != null && (mDfthDevice.getDeviceState() == DfthDevice.MEASURING || mDfthDevice.getDeviceState() == DfthDevice.CONNECTED || ((DfthECGDevice) mDfthDevice).isReconnect())) {
            DfthDeviceManager.getInstance().bindDataListener(this);
        }
        if (mDfthDevice != null && (mDfthDevice.getDeviceState() == DfthDevice.MEASURING || ((DfthECGDevice) mDfthDevice).isReconnect())) {
            mDataDetailLl.setVisibility(VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = DisplayUtils.dip2px(getContext(),30);
            mDataDetailHeadTv.setLayoutParams(params);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mDataDetailContentTv.setLayoutParams(params);
            mMeasureBtn.setVisibility(GONE);
            String measureText;
            if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG) {
                measureText = (String) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.SINGLE_MEASURE);
            } else {
                measureText = (String) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.TWELVE_MEASURE);
            }
            mDataDetailHeadTv.setText(measureText);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            params.leftMargin = DisplayUtils.dip2px(mContext, 20);
            params.topMargin = DisplayUtils.dip2px(getContext(),30);
            mDataDetailHeadTv.setLayoutParams(params);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            params.leftMargin = DisplayUtils.dip2px(mContext, 20);
            params.rightMargin = DisplayUtils.dip2px(mContext, 20);
            mDataDetailContentTv.setLayoutParams(params);
            mMeasureBtn.setVisibility(GONE);
            if(mEcgResult != null){
                setEcgData(result,mDeviceType);
                String status = mEcgResult.getDoctorAnalysisStatus();
                if(!TextUtils.isEmpty(status)){
                    if (status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_NORMAL.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_EXCEPTION.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_OAUTH_EXCEPTION.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_WARN.getCode())) {
                        if (mEcgResult != null){
                            mDataDetailLl.setVisibility(VISIBLE);
                            mDataDetailHeadTv.setText(ThreeInOneApplication.getStringRes(R.string.activity_home_ecg_analysis));
                            mDataDetailContentTv.setText(mEcgResult.getDoctorResult());
                        } else {
                            mDataDetailLl.setVisibility(GONE);
                        }
                    } else{
                        mDataDetailLl.setVisibility(GONE);
                    }
                } else{
                    mDataDetailLl.setVisibility(GONE);
                }
            } else{
                setCountTimeVisibility(View.GONE);
                setEcgData(null,mDeviceType);
            }
        }
    }

    public void onBpResume(BpPlan plan){
        if (plan == null || plan.getStatus() != BpPlan.STATE_RUNNING) {
            setCountTimeVisibility(View.GONE);
        } else {
            setCountTimeVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mDataDetailContentTv.setLayoutParams(params);
        }
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mDfthDevice != null) {
            DfthDeviceManager.getInstance().unBindDataListener(this);
        }
    }

    public void setBpUnit() {
        int highRes = BpDataUtils.getDefaultUnit(getContext()).equals(ThreeInOneApplication.getStringRes(R.string.setting_mmHg))
                ? R.string.activity_home_blood_pressure_high_mmHg : R.string.activity_home_blood_pressure_high_kPa;
        int lowRes = BpDataUtils.getDefaultUnit(getContext()).equals(ThreeInOneApplication.getStringRes(R.string.setting_mmHg))
                ? R.string.activity_home_blood_pressure_low_mmHg : R.string.activity_home_blood_pressure_low_kPa;
        mDataFirstKeyTv.setText(highRes);
        mDataSecondKeyTv.setText(lowRes);
    }

    public void setBpData(BpResult result) {
        if (result != null) {
            mBpResult = result;
            mDataFirstValueTv.setText(BpDataUtils.getDefaultUnitValue(getContext(), mBpResult.getSbp()));
            mDataSecondValueTv.setText(BpDataUtils.getDefaultUnitValue(getContext(), mBpResult.getDbp()));
            mDataThirdValueTv.setText(mBpResult.getPulseRate() + "");
            mMeasureTimeTv.setText(TimeUtils.getTimeStr(mBpResult.getMeasureTime(), "yyyy-MM-dd HH:mm:ss"));
            mMeasureBtn.setVisibility(GONE);
        } else {
            if ((long)SharePreferenceUtils.get(getContext(),SharePreferenceConstant.BP_CARD_CREAT_TIME,0L) == (0L)
                    && !(boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_BLOOD, false)) {
                mMeasureTimeTv.setText(TimeUtils.getTimeStr(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                SharePreferenceUtils.put(getContext(), SharePreferenceConstant.DEVICE_BLOOD, true);
                SharePreferenceUtils.put(getContext(),SharePreferenceConstant.BP_CARD_CREAT_TIME,System.currentTimeMillis());
            }else{
                mMeasureTimeTv.setText(TimeUtils.getTimeStr((Long) SharePreferenceUtils.get(getContext(),SharePreferenceConstant.BP_CARD_CREAT_TIME,0L), "yyyy-MM-dd HH:mm:ss"));
            }
            mMeasureBtn.setVisibility(VISIBLE);
        }
    }

    public void setEcgData(ECGResult result,int deviceType) {
        mEcgResult = result;
        if (result != null) {
            mDataFirstValueTv.setText(result.getAverHr() + "");
            mDataSecondValueTv.setText(result.getPvcCount() + "");
            mDataThirdValueTv.setText(result.getSpCount() + "");
            mMeasureTimeTv.setText(TimeUtils.getTimeStr(result.getMeasureStartTime(), "yyyy-MM-dd HH:mm:ss"));
            mMeasureBtn.setVisibility(GONE);
        } else {
            if (deviceType == 1) {
                if ((long)SharePreferenceUtils.get(getContext(),SharePreferenceConstant.SINGLE_CARD_CREAT_TIME,0L) == (0L)
                        && !(boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_SINGLE_ECG, false)) {
                    mMeasureTimeTv.setText(TimeUtils.getTimeStr(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                    SharePreferenceUtils.put(getContext(), SharePreferenceConstant.DEVICE_SINGLE_ECG, true);
                    SharePreferenceUtils.put(getContext(),SharePreferenceConstant.SINGLE_CARD_CREAT_TIME,System.currentTimeMillis());
                }else{
                    mMeasureTimeTv.setText(TimeUtils.getTimeStr((Long) SharePreferenceUtils.get(getContext(),SharePreferenceConstant.SINGLE_CARD_CREAT_TIME,0L), "yyyy-MM-dd HH:mm:ss"));
                }
            }else{
                if ((long)SharePreferenceUtils.get(getContext(),SharePreferenceConstant.TWELVE_CARD_CREAT_TIME,0L) == (0L)
                        && !(boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_TWELVE_ECG, false)) {
                    mMeasureTimeTv.setText(TimeUtils.getTimeStr(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                    SharePreferenceUtils.put(getContext(), SharePreferenceConstant.DEVICE_TWELVE_ECG, true);
                    SharePreferenceUtils.put(getContext(),SharePreferenceConstant.TWELVE_CARD_CREAT_TIME,System.currentTimeMillis());
                }else{
                    mMeasureTimeTv.setText(TimeUtils.getTimeStr((Long) SharePreferenceUtils.get(getContext(),SharePreferenceConstant.TWELVE_CARD_CREAT_TIME,0L), "yyyy-MM-dd HH:mm:ss"));
                }
            }
            mMeasureBtn.setVisibility(VISIBLE);
        }
    }

    public void setCountTimeVisibility(int visibility) {
        mDataDetailLl.setVisibility(visibility);
    }

    public void setCountTimeText(String text) {
        mDataDetailContentTv.setText(text);
    }

    public void setCountTimeHeadText(String text) {
        mDataDetailHeadTv.setText(text);
    }

    public void setButtonVisiblity(int visiblity) {
        mMeasureBtn.setVisibility(visiblity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceStatus(DfthMessageEvent event) {
        if (event.getEventName().equals(EventNameMessage.DEVICE_FIRST_ADD_SUCCESS)) {
            mHelperInfoTv.setText(R.string.activity_home_helper_has_bind_device);
            mMeasureBtn.setVisibility(GONE);
        } else if (event.getEventName().equals(EventNameMessage.BP_PLAN_CANCEL_SUCCESS)) {
            if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
                mDataDetailLl.setVisibility(GONE);
            }
        } else if (event.getEventName().equals(EventNameMessage.BP_UNIT_CHANGE)) {
            if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
                setBpUnit();
                setBpData(mBpResult);
            }
        }
    }

    private void connectDevice() {
        DeviceUtils.autoConnectOrMeasure(getContext(), DfthDevice.Unknown, "", 0, false, new DfthCallBack<Boolean>() {
            @Override
            public void onResponse(DfthResult<Boolean> dfthResult) {
                if (dfthResult.getReturnData()) {
                    ToastUtils.showLong(getContext(), R.string.activity_home_helper_bind_device_success);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.view_task_card_measure_btn:
                if (mDeviceType == Constant.DeviceConstant.DFTH_HELPER) {
                    connectDevice();
                } else if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
//                    mDeviceMap.put(Constant.BP_MEASURE_TYPE, BpConstant.BP_MEASURE_NORMAL);
                    mDeviceMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
                    ActivitySkipUtils.skipAnotherActivity(getContext(), BpMeasurePreActivity.class, mDeviceMap);
                } else if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG) {
                    mDeviceMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
                    ActivitySkipUtils.skipAnotherActivity(mContext, EcgHistoryActivity.class, mDeviceMap);
                } else if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG) {
                    mDeviceMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
                    ActivitySkipUtils.skipAnotherActivity(mContext, EcgHistoryActivity.class, mDeviceMap);
                }
                break;
            case R.id.view_task_card_data_rl: {
                mDeviceMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
                if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG) {
                    ActivitySkipUtils.skipAnotherActivity(mContext, EcgHistoryActivity.class, mDeviceMap);
                } else if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG) {
                    ActivitySkipUtils.skipAnotherActivity(mContext, EcgHistoryActivity.class, mDeviceMap);
                } else if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
                    ActivitySkipUtils.skipAnotherActivity(mContext, BpMeasurePreActivity.class, mDeviceMap);
                }
            }
            break;
        }
    }

    public int getDeviceType() {
        return mDeviceType;
    }

    public void setCountTime(long time) {
        mDataDetailContentTv.setText(TimeUtils.getCountTime(time));
    }

    @Override
    public void onLeaderOut(boolean leadOff) {

    }

    @Override
    public void onLeaderStatusChanged(boolean[] leaderOut) {

    }

    @Override
    public void onSosStatus(boolean status) {

    }

    @Override
    public void startProcessECGResult() {

    }

    @Override
    public void onDataChanged(EcgDataTransmitted data) {
        if(!((mDeviceType == DfthDevice.SingleDevice && data.getEcgData().chan() == 1) || (mDeviceType == DfthDevice.EcgDevice && data.getEcgData().chan() == 12))){
            return;
        }
        String time = TimeUtils.getMeasureTime(System.currentTimeMillis() - data.getStartTime());
        mDataDetailContentTv.setText(time);
    }

    @Override
    public void onBatteryChanged(float battery) {

    }

    @Override
    public void onResultData(ECGResult result) {

    }
}
