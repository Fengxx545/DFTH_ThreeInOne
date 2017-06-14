package com.dftaihua.dfth_threeinone.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.HomeActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.device.BindedDevice;
import com.dftaihua.dfth_threeinone.dialog.ConnectDialog;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.bluetooth.Action;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.device.DfthSingleECGDevice;
import com.dfth.sdk.device.DfthTwelveECGDevice;
import com.dfth.sdk.device.factory.DfthDeviceFactory;
import com.dfth.sdk.dispatch.DfthCall;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.permission.DfthPermissionException;
import com.dfth.sdk.permission.DfthPermissionManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/22 17:46
*/
public class DeviceUtils {
    private static DeviceUtils sDeviceUtils = new DeviceUtils();
    private static final String TAG = DeviceUtils.class.getSimpleName();
    private List<AutoConnectTask> sTasks = new ArrayList<>();
    private Action mTaskAction;
    private static ConnectDialog sConnectingDialog;
    private static Context mContext;
    private DeviceUtils(){
    }

    private AutoConnectTask runningTask(){
        Iterator<AutoConnectTask> iterator = sTasks.iterator();
        while(iterator.hasNext()){
            AutoConnectTask task = iterator.next();
            if(task.isStart){
                return task;
            }
        }
        return null;
    }

    private void addTask(AutoConnectTask task){
        AutoConnectTask runningTask = runningTask();
        if(runningTask != null){
            runningTask.isStart = false;
            runningTask.cancel();
        }
        sTasks.add(task);
        if(mTaskAction == null){
            mTaskAction = new RunningAction();
            DispatchUtils.performAsnycAction(mTaskAction, Schedulers.newThread());
        }
    }

    private void removeTask(AutoConnectTask task){
        Iterator<AutoConnectTask> iterator = sTasks.iterator();
        while(iterator.hasNext()){
            AutoConnectTask task1 = iterator.next();
            if(task1.equals(task)){
                iterator.remove();
                break;
            }
        }
    }


    public static void cancel(){
        AutoConnectTask task = sDeviceUtils.runningTask();
        if(task != null){
            task.cancel();
        }
        sConnectingDialog = null;
    }
    private final class RunningAction extends Action{
        private boolean mStart;
        public RunningAction() {
            super(0);
        }

        @Override
        protected void perform() {
            mStart = true;
            while(mStart){
                if(sTasks.size() == 0)
                    break;
                AutoConnectTask task = sTasks.get(0);
                task.execute();
                task.isStart = false;
                removeTask(task);
            }
            mStart = false;
            mTaskAction = null;
            callBackData(null,"");
        }
    }
    private static final class AutoConnectTask{
        String macAddress;//要连接的macAddress
        DfthCallBack<Boolean> callBack;
        DfthCall<? extends DfthDevice> deviceCall;
        DfthCall mCurrentCall;
        boolean mStartMeasure;
        int mMeasureType;
        boolean isStart;//是否开始执行
        AutoConnectTask(DfthCall<? extends DfthDevice> call){
            deviceCall = call;
            mCurrentCall = deviceCall;
        }
        void cancel(){
            isStart = false;
            mCurrentCall.cancel();
        }
        void callBackData(final boolean result, final String errorMessage){
            if(isStart){
                DispatchUtils.performMainThread(new DispatchTask() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(callBack != null){
                            callBack.onResponse(new DfthResult<>(result,errorMessage));
                        }
                        if(result){
                            sDeviceUtils.dismissConnectDialog();
                        }else{
                            sDeviceUtils.findNoDevice();
                        }
                    }
                });
            }
        }
        void execute(){
            isStart = true;
            final DfthResult<? extends DfthDevice> result = deviceCall.syncExecute();
            if (result.getReturnData() != null) {
                Log.e("dfth_sdk","扫描到设备-->" + result.getReturnData().getMacAddress());
                EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.DEVICE_DISCOVER, result.getReturnData().getDeviceType()));
                DfthDeviceManager.getInstance().addDevice(result.getReturnData().getDeviceType(), result.getReturnData());
                mCurrentCall = result.getReturnData().connect();
                final DfthDevice device = result.getReturnData();
                DfthResult<Boolean> result1 = mCurrentCall.syncExecute();
                boolean success = result1.getReturnData() == null ? false : result1.getReturnData();
                if (success) {
                    if ((int) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.IS_FIRST_LOGIN, -1) == 1) {
                        SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.IS_FIRST_LOGIN, 0);
                        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.DEVICE_FIRST_ADD_SUCCESS,
                                device.getDeviceType()));
                    }
                    //每次测量开始都要绑定userId
                    device.bindUserId(UserManager.getInstance().getDefaultUser().getUserId());
                    if (mStartMeasure) {
                        if (device.getDeviceType() == DfthDevice.SingleDevice) {
                            result1 = ((DfthSingleECGDevice) device).startMeasure(1).syncExecute();
                        } else if (device.getDeviceType() == DfthDevice.BpDevice) {
                            result1 = ((DfthBpDevice) device).startMeasure(mMeasureType).syncExecute();
                            if (result1.getReturnData()) {
                                EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_MEASURE_START));
                            }
                        } else {
                            result1 = ((DfthTwelveECGDevice)device).startMeasure(1).syncExecute();
                        }
                    }
                    BindedDevice.save(device);
                    if (device.getDeviceType() == DfthDevice.BpDevice) {
                        ((DfthBpDevice)device).queryPlanStatus().asyncExecute(new DfthCallBack<BpPlan>() {
                            @Override
                            public void onResponse(DfthResult<BpPlan> response) {
                                if(response.getReturnData() != null){
                                    EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_PLAN_IS_EXIST, response.getReturnData()));
                                } else{
                                    String userId = UserManager.getInstance().getDefaultUser().getUserId();
                                    BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
                                    if(plan != null && plan.getStatus() == BpPlan.STATE_RUNNING){
                                        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_PLAN_IS_EXIST, null));
                                    }
//                                    BpDeviceUtils.getBpPlanDatas(device, plan);
                                }
                            }
                        });
                        BpDeviceUtils.getVoiceStatus(device);
                    }
                }
                success = result1.getReturnData() == null ? false : result1.getReturnData();
                callBackData(success, result1.getErrorMessage());
            } else {
                callBackData(false, result.getErrorMessage());
                Log.e("dfth_sdk","没有扫描到设备");
            }
        }
    }

    private void findNoDevice(){
        DispatchUtils.performMainThread(new DispatchTask() {
            @Override
            public void accept(Object o) throws Exception {
                if(sConnectingDialog != null && (mContext != null || ((Activity)mContext).isFinishing())){
                    sConnectingDialog.findNoDevice();
                }
            }
        });
    }
    private void showConnectDialog(Context context,int type){
        if(context != null || ((Activity)context).isFinishing()){
            if(sConnectingDialog == null){
                sConnectingDialog = ConnectDialog.getDialog(context, type);
            }
            sConnectingDialog.show();
        }
    }

    private void dismissConnectDialog(){
        DispatchUtils.performMainThread(new DispatchTask() {
            @Override
            public void accept(Object o) throws Exception {
                if(mContext != null || ((Activity)mContext).isFinishing()){
                    if(sConnectingDialog != null){
                        sConnectingDialog.dismiss();
                    }
                    sConnectingDialog = null;
                }
            }
        });
    }


    public static void autoConnectOrMeasure(final Context context, final int byType, final String macAddress, final int measureType,
                                            final boolean isMeasure, final DfthCallBack<Boolean> callBack) {
        try {
            mContext = context;
            sDeviceUtils.showConnectDialog(context,byType);
            DfthDeviceFactory factory = DfthSDKManager.getManager().getDeviceFactory();
            final DfthCall<? extends DfthDevice> deviceCall;
            if (byType == DfthDevice.EcgDevice) {
                deviceCall = factory.getEcgDevice(macAddress);
            } else if (byType == DfthDevice.SingleDevice) {
                deviceCall = factory.getSingleEcgDevice(macAddress);
            } else if (byType ==DfthDevice.BpDevice) {
                deviceCall = factory.getBpDevice(macAddress);
            } else {
                deviceCall = factory.getDevice();
            }
            AutoConnectTask task = new AutoConnectTask(deviceCall);
            task.callBack = callBack;
            task.isStart = false;
            task.macAddress = macAddress;
            task.mStartMeasure = isMeasure;
            task.mMeasureType = measureType;
            sDeviceUtils.addTask(task);
        } catch (DfthPermissionException e) {
            DfthPermissionManager.requestPermission(context, e.getPermission(), 100);
        }
    }
    public static boolean isDeviceMeasuring(int deviceType) {
        DfthDevice device = DfthDeviceManager.getInstance().getDevice(deviceType);
        if (device == null) {
            return false;
        }
        if ((device.getDeviceState() == DfthDevice.MEASURING)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDeviceReconnect(int deviceType) {
        DfthDevice device = DfthDeviceManager.getInstance().getDevice(deviceType);
        if (device == null) {
            return false;
        }
        if(deviceType == DfthDevice.SingleDevice){
            if(((DfthSingleECGDevice)device).isReconnect()){
                return true;
            }
        } else{
            if(((DfthTwelveECGDevice)device).isReconnect()){
                return true;
            }
        }
        return false;
    }

    public static String isExistDeviceMeasuring(int deviceType) {
        if(DeviceUtils.isDeviceMeasuring(deviceType)){
            return "";
        }
        if (DeviceUtils.isDeviceMeasuring(DfthDevice.BpDevice)) {
            return ThreeInOneApplication.getStringRes(R.string.exist_bp_measuring);
        } else if (DeviceUtils.isDeviceMeasuring(DfthDevice.SingleDevice)) {
            return ThreeInOneApplication.getStringRes(R.string.exist_single_measuring);
        } else if (DeviceUtils.isDeviceMeasuring(DfthDevice.EcgDevice)) {
            return ThreeInOneApplication.getStringRes(R.string.exist_ecg_measuring);
        }
        if(DeviceUtils.isDeviceReconnect(DfthDevice.SingleDevice)){
            return "单道心电正在测量中，请结束测量后尝试连接其他设备。";
        }
        if(DeviceUtils.isDeviceReconnect(DfthDevice.EcgDevice)){
            return "12导心电正在测量中，请结束测量后尝试连接其他设备。";
        }
        return "";
    }

    public static String getDeviceName(DfthDevice device){
        if(device.getDeviceType() == DfthDevice.EcgDevice){
            return ThreeInOneApplication.getStringRes(R.string.measure_device_choose_ecg);
        }else if(device.getDeviceType() == DfthDevice.SingleDevice){
            return ThreeInOneApplication.getStringRes(R.string.measure_device_choose_single_ecg);
        }else if(device.getDeviceType() == DfthDevice.BpDevice){
            return ThreeInOneApplication.getStringRes(R.string.measure_device_choose_bp);
        }
        return null;
    }
}
