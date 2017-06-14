package com.dftaihua.dfth_threeinone.utils;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Constant.BpConstant;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
public class BpDeviceUtils {

    public static boolean isBpDeviceConnected() {
        DfthBpDevice device = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
        if (device == null) {
            return false;
        }
        if ((device.getDeviceState() == DfthDevice.MEASURING
                || device.getDeviceState() == DfthDevice.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMeasuring() {
        DfthBpDevice device = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
        if (device == null) {
            return false;
        }
        if ((device.getDeviceState() == DfthDevice.MEASURING)) {
            return true;
        } else {
            return false;
        }
    }

    public static BpPlan queryPlanStatus() {
        DfthBpDevice device = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
        if (!isBpDeviceConnected()) {
            return null;
        }
        return device.queryPlanStatus().syncExecute().getReturnData();
    }

    public static boolean createMeasurePlan(BpPlan plan) {
        DfthBpDevice device = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
        if (!isBpDeviceConnected()) {
            return false;
        }
        return device.createMeasurePlan(plan).syncExecute().getReturnData();
    }

    public static boolean disConnectDevice() {
        DfthBpDevice device = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
        if (!isBpDeviceConnected()) {
            return true;
        }
        return device.disconnect().syncExecute().getReturnData();
    }

    public static void getVoiceStatus(DfthDevice dfthDevice) {
        ((DfthBpDevice) dfthDevice).deviceVoiceStatus().asyncExecute(new DfthCallBack<Integer>() {
            @Override
            public void onResponse(DfthResult<Integer> dfthResult) {
//                Logger.e("语音状态 = " + dfthResult.getReturnData());
                boolean isVoiceOpen = dfthResult.getReturnData() == 0;
                SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_VOICE_SWITCH, isVoiceOpen);
            }
        });
    }

    public static void queryPlanStatus(final DfthDevice dfthDevice) {
        ((DfthBpDevice) dfthDevice).queryPlanStatus().asyncExecute(new DfthCallBack<BpPlan>() {
            @Override
            public void onResponse(DfthResult<BpPlan> dfthResult) {
                EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_PLAN_IS_EXIST, dfthResult.getReturnData()));
                getBpPlanDatas(dfthDevice, dfthResult.getReturnData());
            }
        });
    }

    public static void getBpPlanDatas(final DfthDevice dfthDevice,final BpPlan bpPlan) {
        ((DfthBpDevice) dfthDevice).getPlanResult().asyncExecute(new DfthCallBack<ArrayList<BpResult>>() {
            @Override
            public void onResponse(DfthResult<ArrayList<BpResult>> dfthResult) {
                ArrayList<BpResult> results = dfthResult.getReturnData();
                if (results != null) {
//                    String userId = UserManager.getInstance().getDefaultUser().getUserId();
//                    BpPlan appPlan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
                    for (int i = 0; i < results.size(); i++) {
                        BpResult bpResult = results.get(i);
                        bpResult.setPlanId((int) bpPlan.getSetPlanTime());
                        bpResult.setUserId(UserManager.getInstance().getDefaultUser().getUserId());
                        bpResult.setMacAddress(dfthDevice.getMacAddress());
                        DfthSDKManager.getManager().getDatabase().saveBPResult(bpResult);
                    }
                    EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.DEVICE_BP_DATA_EXIST));
                }
                getBpManualDatas(dfthDevice);
            }
        });
    }

    public static void getBpManualDatas(final DfthDevice dfthDevice) {
        ((DfthBpDevice) dfthDevice).getManualResult().asyncExecute(new DfthCallBack<ArrayList<BpResult>>() {
            @Override
            public void onResponse(DfthResult<ArrayList<BpResult>> dfthResult) {
                ArrayList<BpResult> results = dfthResult.getReturnData();
                if (results != null) {
                    int sum = 0;
                    for (int i = 0; i < results.size(); i++) {
                        BpResult bpResult = results.get(i);
                        BpResult localResult = DfthSDKManager.getManager().getDatabase().getBPResult(bpResult.getMeasureStartTime());
                        if(localResult == null || localResult.getType() != BpConstant.TYPE_EXPERIENCE){
                            sum++;
                            bpResult.setUserId(UserManager.getInstance().getDefaultUser().getUserId());
                            bpResult.setMacAddress(dfthDevice.getMacAddress());
                            DfthSDKManager.getManager().getDatabase().updateBPResult(bpResult);
                            Logger.e(bpResult.toString());
                        }
                    }
                    if(sum > 0){
                        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.DEVICE_BP_DATA_EXIST));
                    }
                    ((DfthBpDevice) dfthDevice).deleteManualResult().asyncExecute(new DfthCallBack<Boolean>() {
                        @Override
                        public void onResponse(DfthResult<Boolean> response) {
                            if(response.getReturnData()){
                                Logger.e("delete manual data success!");
                            } else{
                                Logger.e("delete manual data fail!");
                            }
                        }
                    });
                }
            }
        });
    }

    public static boolean isSamePlan(BpPlan devicePlan, BpPlan localPlan) {
        if (devicePlan == null || devicePlan.getSpaceTime() == null || localPlan == null || localPlan.getSpaceTime() == null)
            return false;
        short[] deviceSpaceTime = devicePlan.getSpaceTime();
        short[] localSpaceTime = localPlan.getSpaceTime();
        if (deviceSpaceTime.length != localSpaceTime.length)
            return false;
        for (int i = 0; i < deviceSpaceTime.length; i++) {
            if (deviceSpaceTime[i] != localSpaceTime[i])
                return false;
        }
        return true;
    }
}
