package com.dftaihua.dfth_threeinone.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.listener.BpViewClickListener;
import com.dftaihua.dfth_threeinone.manager.BpMeasurePlanManager;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.service.TimeService;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.BpDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.BpPlanUtils;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.BpLastResultView;
import com.dftaihua.dfth_threeinone.widget.BpPlanView;
import com.dftaihua.dfth_threeinone.widget.ECGContentView;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Constant.BpConstant;
import com.dfth.sdk.Others.Constant.DfthDeviceState;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.listener.DfthBpDeviceDataListener;
import com.dfth.sdk.listener.DfthDeviceStateListener;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/2/10 0010.
 */
public class BpMeasurePreActivity extends BaseActivity implements View.OnClickListener,
        BpViewClickListener, DfthDeviceStateListener, DfthBpDeviceDataListener {
    private BpLastResultView mBpLastResultView;
    private BpPlanView mPlanView;
    private LinearLayout mBpMeasureLl;
    private LinearLayout mBpExperienceMeasureLl;
    private LinearLayout mMeasureLl;
    private LinearLayout mMeasuringLl;
    private TextView mMeasuringTv;
    private TextView mMeasuringTimeTv;
    private HashMap<String, Integer> mHashMap = new HashMap<>();
    private DfthDevice mDfthDevice;
    private List<BpResult> mResults;
    private int mDeviceType;
    private int mMeasureType;
    private int mMeasurePreType; //体验测量之前的类型
    private int mIndex = 0;
    private long mCountTime = 0;

    public BpMeasurePreActivity() {
        mStatus = TITLE_VISIBLE | BACK_VISIBLE | SAVE_VISIBLE;
        mTitleNameRes = R.string.title_activity_bp_measure_pre;
        mTitleColorRes = R.color.google_white;
        mTitleNameColorRes = R.color.google_black;
        EventBus.getDefault().register(this);
    }

    @Override
    public View initView() {
        Intent intent = getIntent();
        mDeviceType = intent.getIntExtra(Constant.DFTH_DEVICE_KEY, DfthDevice.SingleDevice);
        mHashMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_bp_measure_pre, null);
        mBpLastResultView = (BpLastResultView) view.findViewById(R.id.activity_bp_measure_pre_view);
        mPlanView = (BpPlanView) view.findViewById(R.id.activity_bp_measure_plan_view);
        mBpMeasureLl = (LinearLayout) view.findViewById(R.id.activity_bp_measure_start_ll);
        mBpExperienceMeasureLl = (LinearLayout) view.findViewById(R.id.activity_bp_measure_experience_start_ll);
        mMeasureLl = (LinearLayout) view.findViewById(R.id.activity_bp_measure_bottom_measure_ll);
        mMeasuringLl = (LinearLayout) view.findViewById(R.id.activity_bp_measure_bottom_measuring_ll);
        mMeasuringTv = (TextView) view.findViewById(R.id.activity_bp_measure_bottom_measuring_tv);
        mMeasuringTimeTv = (TextView) view.findViewById(R.id.activity_bp_measure_pre_last_time_tv);
        mPlanView.setListener(this);
        mBpMeasureLl.setOnClickListener(this);
        mBpExperienceMeasureLl.setOnClickListener(this);
        mMeasuringLl.setOnClickListener(this);
        String type = (String) SharePreferenceUtils.get(this,SharePreferenceConstant.BP_MEASURE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
        if(type.equals(SharePreferenceConstant.BP_MEASURE_EXPERIENCE)){
            mMeasureType = BpConstant.BP_MEASURE_EXPERIENCE;
//            mMeasurePreType = BpConstant.BP_MEASURE_EXPERIENCE;
        } else if(type.equals(SharePreferenceConstant.BP_MEASURE_NORMAL)){
            mMeasureType = BpConstant.BP_MEASURE_NORMAL;
//            mMeasurePreType = BpConstant.BP_MEASURE_NORMAL;
        } else{
            String preType = (String) SharePreferenceUtils.get(this,SharePreferenceConstant.BP_MEASURE_PRE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
            if(preType.equals(SharePreferenceConstant.BP_MEASURE_EXPERIENCE)){
                mMeasureType = BpConstant.BP_MEASURE_EXPERIENCE;
//                mMeasurePreType = BpConstant.BP_MEASURE_EXPERIENCE;
            } else{
                mMeasureType = BpConstant.BP_MEASURE_NORMAL;
//                mMeasurePreType = BpConstant.BP_MEASURE_NORMAL;
            }
        }
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BpDeviceUtils.isBpDeviceConnected()) {
            DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice).bindStateListener(BpMeasurePreActivity.this);
            mTitleView.setTitleSaveRes(R.string.bluetooth_connect);
            mTitleView.setTitleSaveImgRes(R.drawable.bluetooth_connect);
            mTitleView.setTitleSaveTvColorRes(R.color.bluetooth_connect_tv_color);
            if (BpDeviceUtils.isMeasuring()) {
                setMeasuring(true);
            }
        } else {
            mTitleView.setTitleSaveRes(R.string.bluetooth_disconnect);
            mTitleView.setTitleSaveImgRes(R.drawable.bluetooth_disconnect);
            mTitleView.setTitleSaveTvColorRes(R.color.bluetooth_disconnect_tv_color);
            setMeasuring(false);
        }
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        setBpResult(userId);
        BpPlan mCurrentPlan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
        if (mCurrentPlan == null || mCurrentPlan.getStatus() != BpPlan.STATE_RUNNING) {
            mPlanView.setPlanView(null);
        } else {
            mPlanView.setPlanView(mCurrentPlan);
            if (mCountTime == 0) {
                mCountTime = BpPlanUtils.getCountDownTime(mCurrentPlan);
                mPlanView.setCountTime(mCountTime);
            }
        }
    }

    private void setBpResult(String userId) {
        mResults = DfthSDKManager.getManager().getDatabase().getBPResult(userId);
        mBpLastResultView.setLastResult((mResults != null && mResults.size() > 0) ? mResults.get(0) : null);
        mMeasuringTimeTv.setVisibility(View.GONE);
        if (mResults != null && mResults.size() > 0) {
            BpResult result = mResults.get(0);
            mMeasuringTimeTv.setVisibility(View.VISIBLE);
            mMeasuringTimeTv.setText(TimeUtils.getTimeStr(result.getMeasureTime(), "yyyy.MM.dd HH:mm:ss"));
        }
    }

    public void setMeasuring(boolean isMeasuring) {
        if (isMeasuring) {
            mMeasureLl.setVisibility(View.GONE);
            mMeasuringLl.setVisibility(View.VISIBLE);
            String userId = UserManager.getInstance().getDefaultUser().getUserId();
            BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
            String type = (String) SharePreferenceUtils.get(this,SharePreferenceConstant.BP_MEASURE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
            if(type.equals(SharePreferenceConstant.BP_MEASURE_PLAN)){
                mMeasuringTv.setText(R.string.bp_measure_plan_measuring);
            } else{
//                if (mMeasureType == BpConstant.BP_MEASURE_PLAN) {
//                    mMeasuringTv.setText(R.string.bp_measure_plan_measuring);
//                } else {
//                    if (plan != null && plan.getStatus() == BpPlan.STATE_RUNNING) {
//                        if (mCountTime == 0) {
//                            mCountTime = getCountDownTime(plan);
//                            mPlanView.setCountTime(mCountTime);
//                        }
//                        if (BpPlanUtils.checkIsPlanMeasure(mCountTime)) {
//                            mMeasurePreType = mMeasureType;
//                            mMeasureType = BpConstant.BP_MEASURE_PLAN;
//                            mMeasuringTv.setText(R.string.bp_measure_plan_measuring);
//                        } else{
//                            if (mMeasureType == BpConstant.BP_MEASURE_NORMAL) {
//                                mMeasuringTv.setText(R.string.bp_measure_normal_measuring);
//                            } else {
//                                mMeasuringTv.setText(R.string.bp_measure_experience_measuring);
//                            }
//                        }
//                    } else {
                        if (mMeasureType == BpConstant.BP_MEASURE_NORMAL) {
                            mMeasuringTv.setText(R.string.bp_measure_normal_measuring);
                        } else {
                            mMeasuringTv.setText(R.string.bp_measure_experience_measuring);
                        }
//                    }
//                }
            }
        } else {
            mMeasureLl.setVisibility(View.VISIBLE);
            mMeasuringLl.setVisibility(View.GONE);
//            if (mMeasureType == BpConstant.BP_MEASURE_PLAN) {
//                mMeasureType = BpConstant.BP_MEASURE_NORMAL;
//            }
        }
    }

    @Override
    public void initData() {
        DfthDeviceManager.getInstance().bindBpDatalistener(DfthDevice.BpDevice, this);
        BpMeasurePlanManager.getManager(this).checkUploadBpResult();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_bp_measure_start_ll:
                String text = DeviceUtils.isExistDeviceMeasuring(mDeviceType);
                if (!TextUtils.isEmpty(text)) {
                    DialogFactory.getMeasuringDialog(this, text).show();
                } else {
                    measureByType(BpConstant.BP_MEASURE_NORMAL);
                }
                break;
            case R.id.activity_bp_measure_experience_start_ll:
                String text1 = DeviceUtils.isExistDeviceMeasuring(mDeviceType);
                if (!TextUtils.isEmpty(text1)) {
                    DialogFactory.getMeasuringDialog(this, text1).show();
                } else {
                    DialogFactory.getBpExperienceDialog(this, ThreeInOneApplication.getStringRes(R.string.bp_experience_measure_tip)).show();
                }
//                measureByType(BpConstant.BP_MEASURE_EXPERIENCE);
                break;
            case R.id.activity_bp_measure_bottom_measuring_ll:
//                if (mMeasureType == BpConstant.BP_MEASURE_PLAN) {
//                    toMeasure(mMeasurePreType);
//                } else {
                    toMeasure(mMeasureType);
//                }
                break;
        }
    }

    private void measureByType(final int measureType) {
        toMeasure(measureType);
    }

    @Override
    public void onBpViewClick(int type, int action) {
        if (type == 1) {
            if (BpDeviceUtils.isBpDeviceConnected()) {
                if(BpDeviceUtils.isMeasuring()){
                    if (action == 1) {
                        ToastUtils.showShort(BpMeasurePreActivity.this, R.string.bp_measure_pre_stop_measure_to_set_plan);
                    } else{
                        ToastUtils.showShort(BpMeasurePreActivity.this, R.string.bp_measure_pre_stop_measure_to_stop_plan);
                    }
                } else{
                    mDfthDevice = DfthDeviceManager.getInstance().getDevice(mDeviceType);
                    if (action == 1) {
                        setPlan();
                    } else {
                        Dialog dialog = DialogFactory.getCancelPlanDialog(this);
                        dialog.show();
                    }
                }
            } else {
                if (action == 1) {
                    ToastUtils.showShort(BpMeasurePreActivity.this, R.string.bp_measure_pre_connect_device_to_set_plan);
                } else {
                    ToastUtils.showShort(BpMeasurePreActivity.this, R.string.bp_measure_pre_connect_device_to_stop_plan);
                }
            }
        }
    }

    public void setPlan() {
        BpPlan plan = new BpPlan();
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        plan.setUserId(userId);
        plan.setMacAddress(mDfthDevice.getMacAddress());
        plan.setDayInterval(30 * 60);
        plan.setNightInterval(60 * 60);
        plan.setAlarmTime((short) 60);
        long startTime = System.currentTimeMillis();
        plan.setStartTime(startTime, true);
        plan.setPlanId((int) startTime);
        plan.setStatus(BpPlan.STATE_RUNNING);
        boolean isSuccess = BpDeviceUtils.createMeasurePlan(plan);
        String toastText = isSuccess ? ThreeInOneApplication.getStringRes(R.string.bp_measure_pre_set_plan_success)
                : ThreeInOneApplication.getStringRes(R.string.bp_measure_pre_set_plan_fail);
        ToastUtils.showShort(BpMeasurePreActivity.this, toastText);
        if (isSuccess) {
            mPlanView.setPlanView(plan);
            TimeService.start(this);
            DfthSDKManager.getManager().getDatabase().saveBPPlan(plan);
        } else {
            mPlanView.setPlanView(null);
        }
    }

    public void cancelPlan() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
        plan.setStatus(BpPlan.STATE_FINISHED);
        plan.setDayInterval(BpPlan.DAY_SPACE_TIME * 60 * 60);
        plan.setNightInterval(BpPlan.NIGHT_SPACE_TIME * 60 * 60);
        boolean isSuccess = BpDeviceUtils.createMeasurePlan(plan);
        String toastText = isSuccess ? ThreeInOneApplication.getStringRes(R.string.bp_measure_pre_stop_plan_success)
                : ThreeInOneApplication.getStringRes(R.string.bp_measure_pre_stop_plan_fail);
        ToastUtils.showShort(BpMeasurePreActivity.this, toastText);
        if (isSuccess) {
            SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_LOCK, false);
            TimeService.stopSelfService(this);
            plan.setDayInterval(30 * 60);
            plan.setNightInterval(60 * 60);
            plan.setAlarmTime((short) 60);
            plan.setStatus(BpPlan.STATE_FINISHED);
            plan.setEndTime(System.currentTimeMillis() / 1000);
            DfthSDKManager.getManager().getDatabase().updateBPPlan(plan);
            mPlanView.setPlanView(null);
            BpMeasurePlanManager.getManager(this).checkUploadBpResultDelay();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DfthMessageEvent event) {
        if (event.getEventName().equals(EventNameMessage.MEASURE_COUNT_DOWN)) {
            mCountTime = (long) event.getData();
            mPlanView.setCountTime(mCountTime);
        } else if (event.getEventName().equals(EventNameMessage.BP_PLAN_CANCEL_SUCCESS)) {
            mPlanView.setPlanView(null);
        } else if (event.getEventName().equals(EventNameMessage.MEASURE_PLAN_END)) {
            mPlanView.setPlanView(null);
        } else if (event.getEventName().equals(EventNameMessage.BP_MEASURE_EXCEPTION)) {
            setMeasuring(false);
        } else if (event.getEventName().equals(EventNameMessage.BP_MEASURE_STOP)) {
            setMeasuring(false);
        } else if (event.getEventName().equals(EventNameMessage.BP_MEASURE_START)) {
            setMeasuring(true);
        }
    }

    public void toMeasure(int measureType) {
        mMeasureType = measureType;
        mHashMap.put(Constant.BP_MEASURE_TYPE, measureType);
        if(measureType == BpConstant.BP_MEASURE_EXPERIENCE){
            SharePreferenceUtils.put(this,SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_EXPERIENCE);
        } else if(measureType == BpConstant.BP_MEASURE_NORMAL){
            SharePreferenceUtils.put(this,SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_NORMAL);
        }
        ActivitySkipUtils.skipAnotherActivity(BpMeasurePreActivity.this, BpMeasureActivity.class, mHashMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPlanView.onDestroy();
    }

    @Override
    public void onStateChange(int state) {
        Logger.e("state = " + state);
        if (state == DfthDeviceState.DISCONNECTED) {
            mIndex = 0;
            mTitleView.setTitleSaveRes(R.string.bluetooth_disconnect);
            mTitleView.setTitleSaveImgRes(R.drawable.bluetooth_disconnect);
            mTitleView.setTitleSaveTvColorRes(R.color.bluetooth_disconnect_tv_color);
            if (mMeasuringLl.getVisibility() == View.VISIBLE) {
                setMeasuring(false);
            }
        } else if (state == DfthDeviceState.CONNECTED) {
            mIndex = 0;
            mTitleView.setTitleSaveRes(R.string.bluetooth_connect);
            mTitleView.setTitleSaveImgRes(R.drawable.bluetooth_connect);
            mTitleView.setTitleSaveTvColorRes(R.color.bluetooth_connect_tv_color);
            setMeasuring(false);
        }
    }

    @Override
    public void onMeasureException(String message) {
        mIndex = 0;
        setMeasuring(false);
    }

    @Override
    public void onBpSleep() {

    }

    @Override
    public void onDataChanged(Short data) {
        if (mIndex == 0) {
            boolean isPressBegin = (Boolean)SharePreferenceUtils.get(this, SharePreferenceConstant.BP_MEASURE_IS_PRESS,false);
            if(!isPressBegin){
                SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE,SharePreferenceConstant.BP_MEASURE_PLAN);
                if(mMeasureType == BpConstant.BP_MEASURE_EXPERIENCE){
                    SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_PRE_TYPE,SharePreferenceConstant.BP_MEASURE_EXPERIENCE);
                } else{
                    SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_PRE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
                }
            }
//            String type = (String) SharePreferenceUtils.get(this,SharePreferenceConstant.BP_MEASURE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
//            if(type.equals(SharePreferenceConstant.BP_MEASURE_PLAN)) {
//                String preType = (String) SharePreferenceUtils.get(this,SharePreferenceConstant.BP_MEASURE_PRE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
//                SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE, preType);
//            } else{
//                String userId = UserManager.getInstance().getDefaultUser().getUserId();
//                BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
//                boolean isPlanMeasure = false;
//                if (plan != null && plan.getStatus() == BpPlan.STATE_RUNNING) {
//                    long mCountTime = BpPlanUtils.getCountDownTime(plan);
//                    isPlanMeasure = BpPlanUtils.checkIsPlanMeasure(mCountTime);
//                }
//                if (!isPressBegin) {
//                    if(isPlanMeasure){
//                        SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_PLAN);
//                        if (mMeasureType == BpConstant.BP_MEASURE_EXPERIENCE) {
//                            SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_PRE_TYPE, SharePreferenceConstant.BP_MEASURE_EXPERIENCE);
//                        } else {
//                            SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_PRE_TYPE, SharePreferenceConstant.BP_MEASURE_NORMAL);
//                        }
//                    }
//                }
//            }
            mIndex = 1;
            setMeasuring(true);
        }
    }

    @Override
    public void onBatteryChanged(float battery) {

    }

    @Override
    public void onResultData(BpResult result) {
        mIndex = 0;
        if (mMeasureType != BpConstant.BP_MEASURE_EXPERIENCE) {
            mBpLastResultView.setLastResult(result);
            mMeasuringTimeTv.setVisibility(View.VISIBLE);
            mMeasuringTimeTv.setText(TimeUtils.getTimeStr(result.getMeasureTime(), "yyyy.MM.dd HH:mm:ss"));
        }
        setMeasuring(false);
    }
}
