package com.dftaihua.dfth_threeinone.utils;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dfth.mobliemonitor.measure.ecg.HeartBeatType;

public class HeartTypeUtils {
    public static int getHeartType(short beat){
        int color = ThreeInOneApplication.getColorRes(R.color.normal_heart_beat);
        switch (beat){
            case HeartBeatType.HEARTBEAT_Normal:
                color = ThreeInOneApplication.getColorRes(R.color.normal_heart_beat);
                break;
            case HeartBeatType.HEARTBEAT_Ventricular:
                color = ThreeInOneApplication.getColorRes(R.color.ventricular_heart_beat);
                break;
            case HeartBeatType.HEARTBEAT_Atrial:
                color = ThreeInOneApplication.getColorRes(R.color.atrial_heart_beat);
                break;
            case HeartBeatType.HEARTBEAT_Distrub:
                color = ThreeInOneApplication.getColorRes(R.color.disturb_heart_beat);
                break;
            default:
                break;
        }
        return color;
    }

    public static String getHeartTypeName(short beat){
        String name = ThreeInOneApplication.getStringRes(R.string.normal_heart_beat);
        switch (beat){
            case HeartBeatType.HEARTBEAT_Normal:
                name = ThreeInOneApplication.getStringRes(R.string.normal_heart_beat);
                break;
            case HeartBeatType.HEARTBEAT_Ventricular:
                name = ThreeInOneApplication.getStringRes(R.string.ventricular_heart_beat);
                break;
            case HeartBeatType.HEARTBEAT_Atrial:
                name = ThreeInOneApplication.getStringRes(R.string.atrial_heart_beat);
                break;
            case HeartBeatType.HEARTBEAT_Distrub:
                name = ThreeInOneApplication.getStringRes(R.string.disturb_heart_beat);
                break;
            default:
                break;
        }
        return name;
    }
}