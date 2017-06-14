package com.dftaihua.dfth_threeinone.utils;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.device.DfthECGDevice;
import com.dfth.sdk.device.DfthSingleECGDevice;
import com.dfth.sdk.device.DfthTwelveECGDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
public class ECGDeviceUtils {

    public static boolean isSingleDeviceConnected(){
        DfthSingleECGDevice device = (DfthSingleECGDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.SingleDevice);
        if(device == null){
            return false;
        }
        if ((device.getDeviceState() == DfthDevice.MEASURING
                || device.getDeviceState() == DfthDevice.CONNECTED)) {
            return true;
        } else{
            return false;
        }
    }

    public static boolean isECGDeviceConnected(){
        DfthTwelveECGDevice device = (DfthTwelveECGDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.EcgDevice);
        if(device == null){
            return false;
        }
        if ((device.getDeviceState() == DfthDevice.MEASURING
                || device.getDeviceState() == DfthDevice.CONNECTED)) {
            return true;
        } else{
            return false;
        }
    }

    public static boolean isECGDeviceMeasuring(){
        DfthTwelveECGDevice device = (DfthTwelveECGDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.EcgDevice);
        if(device == null){
            return false;
        }
        if ((device.getDeviceState() == DfthDevice.MEASURING)) {
            return true;
        } else{
            return false;
        }
    }

    public static boolean disConnectDevice(int deviceType) {
        DfthECGDevice device = (DfthECGDevice) DfthDeviceManager.getInstance().getDevice(deviceType);
        return (boolean) device.disconnect().syncExecute().getReturnData();
    }

    public static boolean[] getSingleLeaders(){
        boolean[] leaders = new boolean[12];
        Arrays.fill(leaders,false);
        leaders[0] = true;
        return leaders;
    }

    public static boolean[] getTwelveLeaders(){
        boolean[] leaders = new boolean[12];
        Arrays.fill(leaders,false);
        leaders[0] = true;
        leaders[1] = true;
        leaders[2] = true;
        return leaders;
    }
}
