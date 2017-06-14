package com.dftaihua.dfth_threeinone.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.EcgDetailActivity;
import com.dftaihua.dfth_threeinone.activity.EcgMeasureActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.broadcastreceiver.AlarmBroadcastReceiver;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.device.BindedDevice;
import com.dftaihua.dfth_threeinone.dialog.ECGLessThanDoctorTimeDialog;
import com.dftaihua.dfth_threeinone.dialog.ECGResultProcessDialog;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.measure.listener.ECGLeaderConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGMeasureConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGMeasureControllerConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGRhythmConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGSymptomConduct;
import com.dftaihua.dfth_threeinone.mediator.MeasureMediator;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.ECGDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.ScreenUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.BottomControlView;
import com.dftaihua.dfth_threeinone.widget.BpmLinearLayout;
import com.dftaihua.dfth_threeinone.widget.view.ECGMeasureBottomView;
import com.dftaihua.dfth_threeinone.widget.view.ECGMeasureSymptomView;
import com.dftaihua.dfth_threeinone.widget.view.ECGMeasureWaveView;
import com.dftaihua.dfth_threeinone.widget.view.ECGTwelveLeaderView;
import com.dfth.sdk.Others.Utils.BluetoothUtils;
import com.dfth.sdk.Others.Utils.ECGUtils;
import com.dfth.sdk.config.DfthConfig;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.device.DfthECGDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.listener.DfthSingleDeviceDataListener;
import com.dfth.sdk.listener.DfthTwelveDeviceDataListener;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.model.ecg.EcgDataTransmitted;
import com.dfth.sdk.user.DfthUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public abstract class BaseBPHistoryFragment extends Fragment implements MeasureMediator{
    protected ECGLeaderConduct mLeader;
    protected ECGMeasureConduct mMeasure;
    protected ECGMeasureControllerConduct mBottom;
    protected ECGRhythmConduct mRhythm;
    protected ECGResultProcessDialog mDialog;
    protected ECGSymptomConduct mSymptom;
    protected AlarmManager mManager;
    protected Intent mIntent;
    protected PendingIntent mPendingIntent;
    private EcgDataTransmitted mLastestData;

    public BaseBPHistoryFragment() {
        super();
    }

    @Override
    public void onResume() {
        super.onResume();
        initAlarm();
        isOrientation();
    }

    @Override
    public void onStop() {
        super.onStop();
        ScreenUtils.setOrientation((Activity) getContext(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mMeasure.pauseDraw();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMeasure.endDraw();
        mMeasure.destroyDraw();
        ScreenUtils.disableOrientation(getActivity());
    }

    @SuppressWarnings("unchecked")
    private void initHandle() {
        DfthDevice currentDevice = getECGDevice();
        if (currentDevice != null && ((DfthECGDevice)currentDevice).isReconnect()) {
            if (mSymptom != null) {
                mSymptom.setVisibility(View.VISIBLE);
            }
            mMeasure.startDraw();
            mBottom.setStart(true);
        } else{
            if (currentDevice != null && currentDevice.getDeviceState() != DfthDevice.DISCONNECTED) {
                configByDevice(currentDevice);
            }
            if (currentDevice != null && currentDevice.getDeviceState() == DfthDevice.MEASURING) {
                if (mSymptom != null) {
                    mSymptom.setVisibility(View.VISIBLE);
                }
                mMeasure.startDraw();
                mBottom.setStart(true);
            } else{
//                if((boolean)SharePreferenceUtils.get(getActivity(),SharePreferenceConstant.ECG_MEASURE_IS_BEGIN,false)){
//                    if (mSymptom != null) {
//                        mSymptom.setVisibility(View.VISIBLE);
//                    }
//                    mMeasure.startDraw();
//                    mBottom.setStart(true);
//                }
            }
        }
        isOrientation();
    }

    private void isOrientation() {
        DfthDevice currentDevice = getECGDevice();
        if (currentDevice != null && currentDevice.getDeviceState() == DfthDevice.MEASURING) {
            ScreenUtils.setOrientationDelay((Activity) getContext(), ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            ScreenUtils.setOrientationDelay((Activity) getContext(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void configByDevice(DfthDevice device) {
        if (device.getDeviceType() == DfthDevice.SingleDevice) {
            mMeasure.setIsDisplayLeaders(false);
            selectLeader(ECGDeviceUtils.getSingleLeaders());
        } else {
            mMeasure.setIsDisplayLeaders(false);
            selectLeader(ECGDeviceUtils.getTwelveLeaders());
        }
    }

    /*下面是调停者模式的处理**/
    @Override
    public void longPressScreen() {
        if (mLeader != null) {
            mLeader.showLeader();
        }
    }

    @Override
    public void singlePressScreen() {

    }

    @Override
    public void selectLeader(boolean[] leaders) {
        mMeasure.leaderChange(leaders);
        if (leaders != null && mLeader!=null) {
            mLeader.setLeaders(leaders);
        }
    }

    @Override
    public void lessThan25Second(boolean confirm) {
        DfthDevice currentDevice = getECGDevice();
        if(currentDevice != null && currentDevice.getDeviceState() == DfthDevice.MEASURING){
            stopDevice(currentDevice);
        }
    }

    private void initAlarm() {
        mIntent = new Intent(getActivity(), AlarmBroadcastReceiver.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.setAction("Action.Alarm.30s");
        mPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
    }

    protected void startAlarm() {
        if(mManager != null){
            mManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30 * 1000, mPendingIntent);
        }
    }

    protected void stopAlarm() {
        if(mManager != null){
            mManager.cancel(mPendingIntent);
        }
    }

    /*测量按钮点击*/
    public void measureButtonClick() {
        DfthECGDevice currentDevice = getECGDevice();
        if ((currentDevice == null || currentDevice.getDeviceState() == DfthDevice.DISCONNECTED) &&
                !(currentDevice != null && currentDevice.isReconnect())) {
            connectDevice(getDeviceType(), getConnectMacAddress());
        } else if (currentDevice.getDeviceState() == DfthDevice.CONNECTED) {
            if(this instanceof SingleECGMeasureFragment){
                startAlarm();
            }
            startDevice(currentDevice);
        } else if (currentDevice.isReconnect() || currentDevice.getDeviceState() == DfthDevice.MEASURING) {
            if(this instanceof SingleECGMeasureFragment){
                stopAlarm();
            }
            if(mLastestData != null && System.currentTimeMillis() - mLastestData.getStartTime() < DfthConfig.getConfig().ecgConfig.ecgAnalysisConfig.doctorMinAnalysisTime){
//                ECGLessThanDoctorTimeDialog.create(getActivity(),this).show();
            }else {
//                stopDevice(currentDevice);
            }
        }
    }

    @Override
    public void doProcessResult(ECGResult result) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put(Constant.DFTH_RESULT_DATA, result);
        maps.put(Constant.DFTH_DEVICE_KEY, getDeviceType());
        ActivitySkipUtils.skipAnotherActivity(getActivity(), EcgDetailActivity.class, maps);
    }

    /*调停者模式 end*/
    private void connectDevice(int type, String macAddress) {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            BluetoothUtils.startActivityBluetooth(getActivity());
            return;
        }
        DeviceUtils.autoConnectOrMeasure(getActivity(), type, macAddress, 1, false, new DfthCallBack<Boolean>() {
            @Override
            public void onResponse(DfthResult<Boolean> dfthResult) {
                if(dfthResult.getReturnData()){
                    ToastUtils.showShort(getActivity(),R.string.device_connect_success);
                }
                initHandle();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void startDevice(DfthDevice device) {
        DfthUser user = UserManager.getInstance().getDefaultUser();
        device.bindUserId(user.getUserId());
        if(((EcgMeasureActivity)getActivity()).getMeasureMark() == SharePreferenceConstant.LONG_TIME_MARK){
            ((EcgMeasureActivity)getActivity()).setAlarmManager();
        }
        ((DfthECGDevice) device).startMeasure(1).asyncExecute(new DfthCallBack<Boolean>() {
            @Override
            public void onResponse(DfthResult<Boolean> response) {
                if (response.getReturnData()) {
                    mMeasure.startDraw();
                    mBottom.setStart(true);
                    if (mSymptom != null) {
                        mSymptom.setVisibility(View.VISIBLE);
                    }
                }
                isOrientation();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void stopDevice(DfthDevice device) {
        device.stopMeasure().asyncExecute(new DfthCallBack<Boolean>() {
            @Override
            public void onResponse(DfthResult<Boolean> response) {
            }
        });
    }

    /*数据处理结束*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) onCreateView(inflater);
        mLeader = initializeLeaderConduct(v);
        mBottom = initializeMeasureControllerConduct(v);
        mMeasure = initializeMeasureConduct(v);
        mRhythm = initializeRhythmConduct(v);
        mSymptom = initializeSymptomConduct(v);
        ScreenUtils.enableOrientation(getActivity(), null);
        initHandle();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DfthECGDevice currentDevice = getECGDevice();
        if ((currentDevice == null || currentDevice.getDeviceState() == DfthDevice.DISCONNECTED) &&
                !(currentDevice != null && currentDevice.isReconnect())) {
            connectDevice(getDeviceType(), getConnectMacAddress());
        }
    }

    /**
     * 初始化测量界面
     *
     * @param group
     * @return
     */
    protected abstract ECGMeasureConduct initializeMeasureConduct(ViewGroup group);

    protected abstract ECGMeasureControllerConduct initializeMeasureControllerConduct(ViewGroup group);

    protected abstract ECGRhythmConduct initializeRhythmConduct(ViewGroup group);

    protected abstract ECGLeaderConduct initializeLeaderConduct(ViewGroup group);

    protected abstract ECGSymptomConduct initializeSymptomConduct(ViewGroup group);

    protected abstract View onCreateView(LayoutInflater inflater);

    protected abstract DfthECGDevice getECGDevice();

    protected abstract String getConnectMacAddress();

    protected abstract int getDeviceType();

    public static final class SingleECGMeasureFragment extends BaseBPHistoryFragment {
        private BpmLinearLayout mBpmView;
        private ECGMeasureBottomView mBottomView;
        private ECGMeasureWaveView mWaveView;

        @Override
        protected ECGMeasureConduct initializeMeasureConduct(ViewGroup group) {
            mWaveView.bindMediator(this);
            return mWaveView;
        }

        @Override
        protected ECGMeasureControllerConduct initializeMeasureControllerConduct(ViewGroup group) {
            mBottomView.bindMediator(this);
            return mBottomView;
        }

        @Override
        protected ECGSymptomConduct initializeSymptomConduct(ViewGroup group) {
            return null;
        }

        @Override
        protected ECGRhythmConduct initializeRhythmConduct(ViewGroup group) {
            return mBpmView;
        }

        @Override
        protected ECGLeaderConduct initializeLeaderConduct(ViewGroup group) {
            return null;
        }

        @Override
        protected View onCreateView(LayoutInflater inflater) {
            View v = inflater.inflate(R.layout.fragment_single_ecg_measure, null);
            mBpmView = (BpmLinearLayout) v.findViewById(R.id.ecg_bpm);
            mBottomView = (ECGMeasureBottomView) v.findViewById(R.id.single_measure_bottom_horizontal);
            mBottomView.orientationChange(Configuration.ORIENTATION_PORTRAIT);
            mWaveView = (ECGMeasureWaveView) v.findViewById(R.id.measure_view);
            mWaveView.setLine(1);
            EventBus.getDefault().register(this);
            return v;
        }

        @Override
        protected DfthECGDevice getECGDevice() {
            return (DfthECGDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.SingleDevice);
        }

        @Override
        protected String getConnectMacAddress() {
            String mac = BindedDevice.getBindedDevice(DfthDevice.SingleDevice).getMacAddresss();
            return mac;
        }

        @Override
        protected int getDeviceType() {
            return DfthDevice.SingleDevice;
        }

        @Override
        public void singlePressScreen() {
            if(getECGDevice().getDeviceState() == DfthDevice.MEASURING){
                if(mBottomView.getVisibility() == View.VISIBLE){
                    stopAlarm();
                } else{
                    startAlarm();
                }
            }
            mBottomView.setViewVisibility(mBottomView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }

        @Override
        public void doublePressScreen() {

        }

        @Override
        public void waveChange(int deltaPos) {

        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMessage(DfthMessageEvent event){
            if(event.getEventName().equals(EventNameMessage.NO_OPERATION_30S)){
                mBottomView.setViewVisibility(View.GONE);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            EventBus.getDefault().unregister(this);
        }
    }

    public static final class TwelveECGMeasureFragment extends BaseBPHistoryFragment {
        private BottomControlView mBottomView;
        private ECGMeasureWaveView mWaveView;
        private ECGTwelveLeaderView mLeaderView;
        private ECGMeasureSymptomView mSymptomView;

        @Override
        protected ECGMeasureConduct initializeMeasureConduct(ViewGroup group) {
            mWaveView.bindMediator(this);
            return mWaveView;
        }

        @Override
        protected ECGMeasureControllerConduct initializeMeasureControllerConduct(ViewGroup group) {
            mBottomView.bindMediator(this);
            return mBottomView;
        }

        @Override
        protected ECGRhythmConduct initializeRhythmConduct(ViewGroup group) {
            mBottomView.bindMediator(this);
            return mBottomView;
        }

        @Override
        protected ECGLeaderConduct initializeLeaderConduct(ViewGroup group) {
            mLeaderView.bindMediator(this);
            return mLeaderView;
        }


        @Override
        protected ECGSymptomConduct initializeSymptomConduct(ViewGroup group) {
            mSymptomView.bindMediator(this);
            return mSymptomView;
        }

        @Override
        protected View onCreateView(LayoutInflater inflater) {
            RelativeLayout parent = new RelativeLayout(getActivity());
            initWaveView(parent);
            initMeasureLeaderView(parent);
            initSymptomView(parent);
            initMeasureBottomView(parent);
            return parent;
        }

        private void initSymptomView(RelativeLayout parent) {
            mSymptomView = new ECGMeasureSymptomView(getActivity());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.bottomMargin = DisplayUtils.dip2px(getActivity(), 100);
            params.rightMargin = DisplayUtils.dip2px(getActivity(), 20);
            params.width = DisplayUtils.dip2px(getActivity(), 80);
            params.height = DisplayUtils.dip2px(getActivity(), 80);
            parent.addView(mSymptomView, params);
        }

        private void initMeasureLeaderView(RelativeLayout parent) {
            mLeaderView = new ECGTwelveLeaderView(getActivity());
            mLeaderView.bindMediator(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.bottomMargin = DisplayUtils.dip2px(getContext(), 80);
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.width = ThreeInOneApplication.getScreenWidth() / 5;
            parent.addView(mLeaderView, params);
        }

        private void initMeasureBottomView(RelativeLayout parent) {
            mBottomView = new BottomControlView(getActivity());
            mBottomView.setBackgroundResource(R.drawable.twelve_un_measure_bottom_bg);
            mBottomView.setId(R.id.measure_bottom);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = ThreeInOneApplication.getScreenWidth() * 221 / 749;
            parent.addView(mBottomView, params);
        }

        private void initWaveView(RelativeLayout parent) {
            mWaveView = new ECGMeasureWaveView(getActivity(), null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.bottomMargin = DisplayUtils.dip2px(getActivity(), 80);
            parent.addView(mWaveView, params);
            mWaveView.setLine(12);
        }

        @Override
        protected DfthECGDevice getECGDevice() {
            return (DfthECGDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.EcgDevice);
        }

        @Override
        protected String getConnectMacAddress() {
            String mac = BindedDevice.getBindedDevice(DfthDevice.EcgDevice).getMacAddresss();
            return mac;
        }

        @Override
        protected int getDeviceType() {
            return DfthDevice.EcgDevice;
        }

        @Override
        public void doublePressScreen() {

        }

        @Override
        public void waveChange(int deltaPos) {

        }
    }


    public static BaseBPHistoryFragment createFragment(int deviceType) {
        if (deviceType == DfthDevice.EcgDevice) {
            return new TwelveECGMeasureFragment();
        } else {
            return new SingleECGMeasureFragment();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && DisplayUtils.getOrientation() == Configuration.ORIENTATION_LANDSCAPE){
            ScreenUtils.setOrientation(getActivity(),ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DispatchUtils.performMainThreadDelay(new DispatchTask() {
                @Override
                public void accept(Object o) throws Exception {
                    if(getActivity() != null && mMeasure.isDrawing()){
                        ScreenUtils.setOrientation(getActivity(),ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                }
            },500);
            return false;
        }
        return true;
    }
}