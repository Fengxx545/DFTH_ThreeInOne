package com.dftaihua.dfth_threeinone.manager;

import android.app.Activity;

import com.dftaihua.dfth_threeinone.activity.BpMeasureActivity;
import com.dftaihua.dfth_threeinone.activity.EcgHistoryActivity;
import com.dftaihua.dfth_threeinone.activity.EcgMeasureActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dfth.sdk.Others.Constant.BpConstant;
import com.dfth.sdk.Others.Constant.DfthDeviceState;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.device.DfthECGDevice;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.listener.DfthBpDeviceDataListener;
import com.dfth.sdk.listener.DfthDeviceDataListener;
import com.dfth.sdk.listener.DfthDeviceStateListener;
import com.dfth.sdk.listener.DfthSingleDeviceDataListener;
import com.dfth.sdk.listener.DfthTwelveDeviceDataListener;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.model.ecg.EcgDataTransmitted;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/20 17:45
*/
public class DfthDeviceManager {
    private HashMap<Integer, DfthDevice> mDevicesHashMap = new HashMap<>();
    private TwelveDfthEcgDevice mTwelveEcgListener;
    private SingleDfthEcgDevice mSingleEcgListener;
    private BpDfthDevice mBpDfthDeviceListener;
    private static DfthDeviceManager instance;
    private DfthDeviceManager() {
        if (mTwelveEcgListener == null) {
            mTwelveEcgListener = new TwelveDfthEcgDevice();
        }
        if (mSingleEcgListener == null) {
            mSingleEcgListener = new SingleDfthEcgDevice();
        }
        if (mBpDfthDeviceListener == null) {
            mBpDfthDeviceListener = new BpDfthDevice();
        }
    }

    public static synchronized DfthDeviceManager getInstance() {
        if (instance == null) {
            instance = new DfthDeviceManager();
        }
        return instance;
    }


    public void addDevice(int type, DfthDevice device) {
        mDevicesHashMap.put(type, device);
        if (device != null) {
            if (type == DfthDevice.EcgDevice) {
                device.bindDataListener(mTwelveEcgListener);
            } else if (type == DfthDevice.SingleDevice) {
                device.bindDataListener(mSingleEcgListener);
            } else {
                device.bindDataListener(mBpDfthDeviceListener);
            }
        }
    }


    public int isExistsHaveConnecttedDevice(){
        Set<Integer> devices = mDevicesHashMap.keySet();
        for(Integer type: devices){
            DfthDevice device = mDevicesHashMap.get(type);
            if(device.getDeviceState() == DfthDevice.CONNECTED){
                return type;
            }
        }
        return -1;
    }


    public int isExistsHaveMeasuringDevice(){
        Set<Integer> devices = mDevicesHashMap.keySet();
        for(Integer type: devices){
            DfthDevice device = mDevicesHashMap.get(type);
            if(device.getDeviceState() == DfthDevice.MEASURING){
                return type;
            }
        }
        return -1;
    }


    public DfthDevice existsHaveMeasuringDevice(){
        Set<Integer> devices = mDevicesHashMap.keySet();
        for(Integer type: devices){
            DfthDevice device = mDevicesHashMap.get(type);
            if(device.getDeviceState() == DfthDevice.MEASURING){
                return device;
            }
            if(device instanceof DfthECGDevice
                    && ((DfthECGDevice) device).isReconnect()){
                return device;
            }
        }
        return null;
    }

    public boolean isHaveMeasuringDevice(){
        Set<Integer> devices = mDevicesHashMap.keySet();
        for(Integer key: devices){
            DfthDevice device = mDevicesHashMap.get(key);
            if(device.getDeviceState() == DfthDevice.MEASURING){
                return true;
            }
            if((device.getDeviceType() == DfthDevice.EcgDevice || device.getDeviceType() == DfthDevice.SingleDevice)){
                if(((DfthECGDevice)device).isReconnect()){
                    return true;
                }
            }
        }
        return false;
    }

    public DfthDevice getDevice(int type) {
        return mDevicesHashMap.get(type);
    }


    public void removeDevice(DfthDevice<DfthDeviceDataListener> device) {
        mDevicesHashMap.remove(device);
    }

    public void bindDataListener(DfthDeviceDataListener listener){
        if(listener == null){
            return;
        }
        if(listener instanceof DfthSingleDeviceDataListener){
            bindSingleDataListener((DfthSingleDeviceDataListener) listener);
        }
        if(listener instanceof DfthTwelveDeviceDataListener){
            bindTwelveDataListener((DfthTwelveDeviceDataListener) listener);
        }
    }

    public void unBindDataListener(DfthDeviceDataListener listener){
        if(listener == null){
            return;
        }
        if(listener instanceof DfthSingleDeviceDataListener){
            unBindSingleDataListener((DfthSingleDeviceDataListener) listener);
        }
        if(listener instanceof DfthTwelveDeviceDataListener){
            unBindTwelveDataListener((DfthTwelveDeviceDataListener) listener);
        }
    }
    public void bindBpDatalistener(int type, DfthBpDeviceDataListener listener) {
        if (type == DfthDevice.BpDevice) {
            if (mBpDfthDeviceListener != null) {
                mBpDfthDeviceListener.mBpListeners.add(listener);
            }
        }
    }

    public void unBindBpDatalistener(int type, DfthBpDeviceDataListener listener) {
        if (type == DfthDevice.BpDevice) {
            if (mBpDfthDeviceListener != null) {
                mBpDfthDeviceListener.mBpListeners.remove(listener);
            }
        }
    }

    public void bindBpStateListener() {
        if (mBpDfthDeviceListener != null) {
            DfthBpDevice device = (DfthBpDevice) getDevice(DfthDevice.BpDevice);
            if(device != null){
                device.bindStateListener(mBpDfthDeviceListener);
            }
        }
    }

    private void bindTwelveDataListener(DfthTwelveDeviceDataListener listener) {
        if (mTwelveEcgListener != null && !mTwelveEcgListener.mTwelveListeners.contains(listener)) {
            mTwelveEcgListener.mTwelveListeners.add(listener);
        }
    }
    private void bindSingleDataListener(DfthSingleDeviceDataListener listener) {
        if (mSingleEcgListener != null && !mSingleEcgListener.mSingleListeners.contains(listener)) {
            mSingleEcgListener.mSingleListeners.add(listener);
        }
    }

    private void unBindTwelveDataListener(DfthTwelveDeviceDataListener listener) {
        if (mTwelveEcgListener != null) {
            mTwelveEcgListener.mTwelveListeners.remove(listener);
        }
    }

    private void unBindSingleDataListener(DfthSingleDeviceDataListener listener) {
        if (mSingleEcgListener != null) {
            mSingleEcgListener.mSingleListeners.remove(listener);
        }
    }

    private class SingleDfthEcgDevice implements DfthSingleDeviceDataListener {
        private List<DfthSingleDeviceDataListener> mSingleListeners = new ArrayList<>();

        @Override
        public void onLeaderOut(boolean b) {
            for (DfthSingleDeviceDataListener listener : mSingleListeners) {
                listener.onLeaderOut(b);
            }
        }

        @Override
        public void onDataChanged(EcgDataTransmitted ecgDataTransmitted) {
            for (DfthSingleDeviceDataListener listener : mSingleListeners) {
                listener.onDataChanged(ecgDataTransmitted);
            }
            String measureType = (String) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(),
                    SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.SINGLE_MEASURE);
            if(measureType.equals(SharePreferenceConstant.SINGLE_LONG_MEASURE)){
                if(System.currentTimeMillis() - ecgDataTransmitted.getStartTime() >= 24 * 60 * 60 * 1000){
                    Activity activity = ActivityCollector.getActivity();
                    if(activity != null && activity instanceof EcgMeasureActivity){
                        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.ALARM_24_HOUR_END));
                    } else if(activity != null && !(activity instanceof EcgMeasureActivity)){
                        HashMap<String, Object> deviceMap = new HashMap<>();
                        deviceMap.put(Constant.DFTH_DEVICE_KEY, DfthDevice.SingleDevice);
                        deviceMap.put(SharePreferenceConstant.LONG_TIME_MEASURE, SharePreferenceConstant.LONG_TIME_MARK);
                        ActivitySkipUtils.skipAnotherActivity(activity, EcgMeasureActivity.class, deviceMap);
                        DispatchUtils.performMainThreadDelay(new DispatchTask() {
                            @Override
                            public void accept(Object o) throws Exception {
                                EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.ALARM_24_HOUR_END));
                            }
                        }, 500);
                    }
                }
            }
        }

        @Override
        public void onBatteryChanged(float v) {
            for (DfthSingleDeviceDataListener listener : mSingleListeners) {
                listener.onBatteryChanged(v);
            }
        }

        @Override
        public void onResultData(ECGResult ecgResult) {
            for (DfthSingleDeviceDataListener listener : mSingleListeners) {
                listener.onResultData(ecgResult);
            }
        }

        @Override
        public void startProcessECGResult() {
            for (DfthSingleDeviceDataListener listener : mSingleListeners) {
                listener.startProcessECGResult();
            }
        }
    }


    private class TwelveDfthEcgDevice implements DfthTwelveDeviceDataListener {
        private List<DfthTwelveDeviceDataListener> mTwelveListeners = new ArrayList<>();

        @Override
        public void onLeaderStatusChanged(boolean[] booleen) {
            for (DfthTwelveDeviceDataListener listener : mTwelveListeners) {
                listener.onLeaderStatusChanged(booleen);
            }
        }

        @Override
        public void onSosStatus(boolean b) {
            for (DfthTwelveDeviceDataListener listener : mTwelveListeners) {
                listener.onSosStatus(b);
            }
        }

        @Override
        public void onDataChanged(EcgDataTransmitted ecgDataTransmitted) {
            for (DfthTwelveDeviceDataListener listener : mTwelveListeners) {
                listener.onDataChanged(ecgDataTransmitted);
            }
        }

        @Override
        public void onBatteryChanged(float v) {
            for (DfthTwelveDeviceDataListener listener : mTwelveListeners) {
                listener.onBatteryChanged(v);
            }
        }

        @Override
        public void onResultData(ECGResult ecgResult) {
            for (DfthTwelveDeviceDataListener listener : mTwelveListeners) {
                listener.onResultData(ecgResult);
            }
        }

        @Override
        public void startProcessECGResult() {
            for (DfthTwelveDeviceDataListener listener : mTwelveListeners) {
                listener.startProcessECGResult();
            }
        }
    }


    private class BpDfthDevice implements DfthBpDeviceDataListener,DfthDeviceStateListener {
        private List<DfthBpDeviceDataListener> mBpListeners = new ArrayList<>();

//        @Override
//        public void onResultData() {
//            for (DfthBpDeviceDataListener listener : mBpListeners) {
//                listener.onResultData();
//            }
//        }

        @Override
        public void onDataChanged(Short aShort) {
            for (DfthBpDeviceDataListener listener : mBpListeners) {
                listener.onDataChanged(aShort);
            }
        }

        @Override
        public void onBatteryChanged(float v) {
            for (DfthBpDeviceDataListener listener : mBpListeners) {
                listener.onBatteryChanged(v);
            }
        }

        @Override
        public void onResultData(BpResult bpResult) {
            SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_IS_PRESS,false);
            String type = (String) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
            if(type.equals(SharePreferenceConstant.BP_MEASURE_PLAN)){
                String preType = (String) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_PRE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
                SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_TYPE,preType);
            }
            for (DfthBpDeviceDataListener listener : mBpListeners) {
                listener.onResultData(bpResult);
            }
        }

        @Override
        public void onMeasureException(String s) {
            SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_IS_PRESS,false);
            String type = (String) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
            if(type.equals(SharePreferenceConstant.BP_MEASURE_PLAN)){
                String preType = (String) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_PRE_TYPE,SharePreferenceConstant.BP_MEASURE_NORMAL);
                SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_TYPE,preType);
            }
            for (DfthBpDeviceDataListener listener : mBpListeners) {
                listener.onMeasureException(s);
            }
        }

        @Override
        public void onBpSleep() {

        }

        @Override
        public void onStateChange(int state) {
            if(state == DfthDeviceState.DISCONNECTED){
                SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_IS_PRESS,false);
            } else if(state == DfthDeviceState.CONNECTED){
                SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_IS_PRESS,false);
            }
        }


//        @Override
//        public void onMeasureBegin() {
//            Activity activity = ActivityCollector.getActivity();
//            HashMap<String, Integer> hashMap = new HashMap();
//            hashMap.put(Constant.BP_MEASURE_TYPE, BpConstant.BP_MEASURE_NORMAL);
//            ActivitySkipUtils.skipAnotherActivity(activity, BpMeasureActivity.class,hashMap);
//        }
    }


}