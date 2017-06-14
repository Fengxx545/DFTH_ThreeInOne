package com.dftaihua.dfth_threeinone.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.device.BindedDevice;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.BpDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.BpPlanUtils;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.BpMeasureView;
import com.dftaihua.dfth_threeinone.widget.BpMeasureView.OnButtonClickListener;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Constant.BpConstant;
import com.dfth.sdk.Others.Constant.DfthDeviceState;
import com.dfth.sdk.Others.Utils.BluetoothUtils;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
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

/**
 * Created by Administrator on 2017/2/10 0010.
 */
public class BpMeasureActivity extends BaseActivity implements OnButtonClickListener,
        DfthBpDeviceDataListener, DfthDeviceStateListener {
    private BpMeasureView mMeasureView;
    private DfthBpDevice mBpDevice = null;
    private int mMeasureType;

    public BpMeasureActivity() {
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
        mTitleNameRes = R.string.title_activity_bp_measure;
        mTitleColorRes = R.color.title_bp_measure_back;
        mTitleNameColorRes = R.color.google_white;
        mTitleBackRes = R.drawable.arrow_back;
        EventBus.getDefault().register(this);
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_bp_measure, null);
        mMeasureView = (BpMeasureView) view.findViewById(R.id.activity_bp_measure_view);
        mMeasureView.setListener(this);
        mBpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
        return view;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mMeasureType = intent.getIntExtra(Constant.BP_MEASURE_TYPE, BpConstant.BP_MEASURE_NORMAL);
        DfthDeviceManager.getInstance().bindBpDatalistener(DfthDevice.BpDevice, this);
        if (mBpDevice == null || mBpDevice.getDeviceState() == DfthDevice.DISCONNECTED) {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                BluetoothUtils.startActivityBluetooth(this);
                return;
            }
            String mac = BindedDevice.getBindedDevice(DfthDevice.BpDevice).getMacAddresss();
            DeviceUtils.autoConnectOrMeasure(this, DfthDevice.BpDevice, mac, mMeasureType, false, new DfthCallBack<Boolean>() {
                @Override
                public void onResponse(DfthResult<Boolean> dfthResult) {
                    if (!dfthResult.getReturnData()) {
                        ToastUtils.showShort(BpMeasureActivity.this, R.string.bp_measure_device_connect_fail);
                    } else {
                        ToastUtils.showShort(BpMeasureActivity.this, R.string.device_connect_success);
                        mBpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
                        mBpDevice.bindStateListener(BpMeasureActivity.this);
                    }
                }
            });
        } else {
            mBpDevice.bindStateListener(BpMeasureActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onButtonClick(boolean begin) {
        boolean isMeasureLock = (boolean) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(),
                SharePreferenceConstant.BP_MEASURE_LOCK, false);
        if (isMeasureLock) {
            ToastUtils.showLong(this, R.string.bp_measure_plan_will_execute);
            return;
        }
        if (mBpDevice != null && mBpDevice.getDeviceState() == DfthDevice.MEASURING) {
//            mMeasureView.changeView(true);
            mMeasureView.resetView();
            mBpDevice.stopMeasure().asyncExecute(new DfthCallBack<Boolean>() {
                @Override
                public void onResponse(DfthResult<Boolean> response) {
                    if (!response.getReturnData()) {
                        ToastUtils.showShort(BpMeasureActivity.this, R.string.bp_measure_stop_fail);
                    } else {
                        SharePreferenceUtils.put(BpMeasureActivity.this, SharePreferenceConstant.BP_MEASURE_IS_PRESS, false);
                        DispatchTask task = new DispatchTask() {
                            @Override
                            public void accept(Object o) throws Exception {
                                if (mMeasureView != null) {
                                    mMeasureView.setValueView();
                                }
                                EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_MEASURE_STOP));
                            }
                        };
                        DispatchUtils.performMainThreadDelay(task, 400);
                    }
                }
            });
        } else if (mBpDevice != null && mBpDevice.getDeviceState() == DfthDevice.CONNECTED) {
            mMeasureView.changeView(false);
            SharePreferenceUtils.put(BpMeasureActivity.this, SharePreferenceConstant.BP_MEASURE_IS_PRESS, true);
            mBpDevice.startMeasure(mMeasureType).asyncExecute(new DfthCallBack<Boolean>() {
                @Override
                public void onResponse(DfthResult<Boolean> dfthResult) {
                    if (dfthResult.getReturnData()) {
                        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_MEASURE_START));
                    }
                }
            });
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                BluetoothUtils.startActivityBluetooth(this);
                return;
            }
            String mac = BindedDevice.getBindedDevice(DfthDevice.BpDevice).getMacAddresss();
            DeviceUtils.autoConnectOrMeasure(this, DfthDevice.BpDevice, mac, mMeasureType, false, new DfthCallBack<Boolean>() {
                @Override
                public void onResponse(DfthResult<Boolean> dfthResult) {
                    if (dfthResult.getReturnData()) {
                        mMeasureView.changeView(true);
                        mBpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
                        mBpDevice.bindStateListener(BpMeasureActivity.this);
                        ToastUtils.showShort(BpMeasureActivity.this, R.string.device_connect_success);
                    } else {
                        mMeasureView.changeView(true);
                        ToastUtils.showShort(BpMeasureActivity.this, R.string.bp_measure_device_connect_fail);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DfthMessageEvent event) {
        if (event.getEventName().equals(EventNameMessage.BP_PLAN_IS_EXIST)) {
            String userId = UserManager.getInstance().getDefaultUser().getUserId();
            BpPlan everPlan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
            BpPlan devicePlan = (BpPlan) event.getData();
            DfthBpDevice bpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
            if (everPlan != null && (everPlan.getStatus() != BpPlan.STATE_FINISHED && everPlan.getStatus() != BpPlan.STATE_UPLOADED)) {
                if (devicePlan != null && !BpPlanUtils.checkIsPlanOver(devicePlan)) {
                    if (!BpDeviceUtils.isSamePlan(devicePlan, everPlan)) {
                        Dialog dialog = DialogFactory.getPlanDialog(this, Constant.BP_PLAN_EXIST_BOTH);
                        dialog.show();
                    } else {
                        BpDeviceUtils.getBpPlanDatas(bpDevice, everPlan);
                    }
                } else {
                    Dialog dialog = DialogFactory.getPlanDialog(this, Constant.BP_PLAN_EXIST_PHONE);
                    dialog.show();
                }
            } else if (everPlan != null && everPlan.getStatus() == BpPlan.STATE_FINISHED) {
                if (devicePlan != null && BpPlanUtils.checkIsPlanOver(devicePlan)) {
                    if (BpDeviceUtils.isSamePlan(devicePlan, everPlan)) {
                        BpDeviceUtils.getBpPlanDatas(bpDevice, everPlan);
                    }
                } else if (devicePlan != null && !BpPlanUtils.checkIsPlanOver(devicePlan)) {
                    Dialog dialog = DialogFactory.getPlanDialog(this, Constant.BP_PLAN_EXIST_DEVICE);
                    dialog.show();
                }
            } else if (everPlan == null) {
                if (devicePlan != null) {
                    if (!BpPlanUtils.checkIsPlanOver(devicePlan)) {
                        Dialog dialog = DialogFactory.getPlanDialog(this, Constant.BP_PLAN_EXIST_DEVICE);
                        dialog.show();
                    }
                }
            }
        } else if (event.getEventName().equals(EventNameMessage.DEVICE_RECONNECT)) {
            if (ActivityCollector.getActivity() instanceof BpMeasureActivity) {
                String mac = BindedDevice.getBindedDevice(DfthDevice.BpDevice).getMacAddresss();
                DeviceUtils.autoConnectOrMeasure(this, DfthDevice.BpDevice, mac, mMeasureType, false, new DfthCallBack<Boolean>() {
                    @Override
                    public void onResponse(DfthResult<Boolean> dfthResult) {
                        if (dfthResult.getReturnData()) {
                            mMeasureView.changeView(true);
                            mBpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
                            mBpDevice.bindStateListener(BpMeasureActivity.this);
                            ToastUtils.showShort(BpMeasureActivity.this, R.string.device_connect_success);
                        } else {
                            mMeasureView.changeView(true);
                            ToastUtils.showShort(BpMeasureActivity.this, R.string.bp_measure_device_connect_fail);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onMeasureException(final String exception) {
        if (mMeasureType == BpConstant.BP_MEASURE_EXPERIENCE) {
            SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_EXPERIENCE);
        } else {
            SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_NORMAL);
        }
        mIndex = 0;
        DispatchTask task = new DispatchTask() {
            @Override
            public void accept(Object o) throws Exception {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMeasureView != null) {
                            mMeasureView.onMeasureException(exception);
                        }
                    }
                });
            }
        };
        DispatchUtils.performMainThreadDelay(task, 400);
        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_MEASURE_EXCEPTION));
        Logger.e("exception" + exception);

    }

    @Override
    public void onBpSleep() {

    }

    private DispatchTask mTask;
    private int mIndex = 0;

    @Override
    public void onDataChanged(final Short data) {
        if (mIndex == 0) {
            boolean isPressBegin = (Boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.BP_MEASURE_IS_PRESS, false);
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
            mMeasureView.changeView(false);
            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_MEASURE_START));
        }
        if (mTask == null) {
            mTask = new DispatchTask() {
                @Override
                public void accept(Object o) throws Exception {
                    if (mMeasureView != null) {
                        mMeasureView.onDataChanged(data);
                    }
                    mTask = null;
                }
            };
            DispatchUtils.performMainThreadDelay(mTask, 400);
        }

    }

    @Override
    public void onBatteryChanged(float v) {

    }

    @Override
    public void onResultData(final BpResult bpResult) {
//        if ((boolean) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_LOCK, false)) {
//            BpResult result = bpResult;
//            result.setPlanId((int) System.currentTimeMillis());
//            DfthSDKManager.getManager().getDatabase().updateBPResult(result);
//        }
        mIndex = 0;
        DispatchTask task = new DispatchTask() {
            @Override
            public void accept(Object o) throws Exception {
                if (mMeasureView != null) {
                    mMeasureView.onResultData(bpResult);
                }
            }
        };
        DispatchUtils.performMainThreadDelay(task, 400);
        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_MEASURE_STOP));
    }

    @Override
    public void onStateChange(int state) {
        if (state == DfthDeviceState.CONNECTED) {
            if (mMeasureType == BpConstant.BP_MEASURE_EXPERIENCE) {
                SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_EXPERIENCE);
            } else {
                SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_NORMAL);
            }
            DispatchTask task = new DispatchTask() {
                @Override
                public void accept(Object o) throws Exception {
                    if (mMeasureView != null) {
                        mMeasureView.changeView(true);
                    }
                    EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_MEASURE_STOP));
                }
            };
            DispatchUtils.performMainThreadDelay(task, 400);
        } else if (state == DfthDeviceState.MEASURING) {
            DispatchTask task = new DispatchTask() {
                @Override
                public void accept(Object o) throws Exception {
                    if (mMeasureView != null) {
                        mMeasureView.changeView(false);
                    }
                }
            };
            DispatchUtils.performMainThreadDelay(task, 400);
        } else if (state == DfthDeviceState.DISCONNECTED) {
            if (mMeasureType == BpConstant.BP_MEASURE_EXPERIENCE) {
                SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_EXPERIENCE);
            } else {
                SharePreferenceUtils.put(this, SharePreferenceConstant.BP_MEASURE_TYPE, SharePreferenceConstant.BP_MEASURE_NORMAL);
            }
            mIndex = 0;
            DispatchTask task = new DispatchTask() {
                @Override
                public void accept(Object o) throws Exception {
                    if (mMeasureView != null) {
                        mMeasureView.changeView(true);
                    }
                    EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_MEASURE_STOP));
                }
            };
            DispatchUtils.performMainThreadDelay(task, 400);
        }
    }
}
