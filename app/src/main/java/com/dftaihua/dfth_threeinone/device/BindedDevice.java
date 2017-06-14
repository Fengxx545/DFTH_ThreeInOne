package com.dftaihua.dfth_threeinone.device;

import android.text.TextUtils;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.MathUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dfth.sdk.device.DfthDevice;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by leezhiqiang on 2017/5/11.
 */

public class BindedDevice implements Serializable{
    private int mDeviceType;
    private String mMacAddresss;
    private String mDeviceName;
    private String mDeviceVersion;
    private boolean isVoiceDevice = false;

    private BindedDevice(){
    }

    public int getDeviceType() {
        return mDeviceType;
    }

    public String getMacAddresss() {
        return mMacAddresss;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public String getDeviceVersion() {
        return mDeviceVersion;
    }

    public boolean isVoiceDevice() {
        return isVoiceDevice;
    }

    public static BindedDevice create(DfthDevice device){
        BindedDevice bindedDevice = new BindedDevice();
        bindedDevice.mDeviceType = device.getDeviceType();
        bindedDevice.mDeviceName = device.getDeviceName();
        bindedDevice.mMacAddresss = device.getMacAddress();
        bindedDevice.mDeviceVersion = (String) device.queryDeviceVersion().syncExecute().getReturnData();
        if(bindedDevice.mDeviceType == DfthDevice.BpDevice){
            try {
                if(!TextUtils.isEmpty(bindedDevice.mDeviceVersion) && MathUtils.judgeStringVersionBig(bindedDevice.mDeviceVersion, "V1.9.9")){
                    bindedDevice.isVoiceDevice = true;
                } else{
                    bindedDevice.isVoiceDevice = false;
                }
            } catch (Exception e) {
                bindedDevice.isVoiceDevice = false;
                e.printStackTrace();
            }
        }
        return bindedDevice;
    }

    public static void save(DfthDevice device){
        String json = new Gson().toJson(create(device));
        SharePreferenceUtils.put(ThreeInOneApplication.getInstance(),
                String.valueOf(device.getDeviceType()),json);
    }

    public static BindedDevice getBindedDevice(int type){
        String json = (String) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(),
                String.valueOf(type),"");
        if(!TextUtils.isEmpty(json)){
            return new Gson().fromJson(json,BindedDevice.class);
        }
        return null;
    }


}
