package com.dftaihua.dfth_threeinone.constant;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dfth.sdk.Others.Utils.TimeUtils;

/**
 * Created by Administrator on 2017/1/18 0018.
 */
public class Constant {

    public static final String[] BODY_CODES = ThreeInOneApplication.getStringArrayRes(R.array.body_disease_code);
    public static final String[] BODY_DISEASE = ThreeInOneApplication.getStringArrayRes(R.array.body_disease);
    public static final String[] BODY_DISEASE_TIME_CODES = ThreeInOneApplication.getStringArrayRes(R.array.body_disease_time_code);

    public final static String DFTH_DEVICE_KEY = "dfthdevicekey";
    public final static String DFTH_RESULT_DATA = "resultdata";
    public static final String BODY_STATUS = "body_status";
    public final static String BP_MEASURE_TYPE = "bpMeasureType";
    public final static String MEMBER_ITEM_TYPE = "member_item_type";
    public final static String ECG_DETAIL_NAME_TYPE = "ecg_detail_name_type";

    public final static int BP_PLAN_EXIST_PHONE = 1;
    public final static int BP_PLAN_EXIST_DEVICE = 2;
    public final static int BP_PLAN_EXIST_BOTH = 3;

    public final static String BP_DATA_TYPE = "bpDataType";
    public final static int BP_PLAN_DATA = 1;
    public final static int BP_MANUAL_DATA = 2;

    public final static int BP_UNIT_MMHG = 1;
    public final static int BP_UNIT_KPA = 2;

    public final static int BP_SLIP_LEFT = 1;
    public final static int BP_SLIP_RIGHT = 2;

    /**
     * 重力传感器延时转屏设置
     */
    public static final long SENSOR_CHECK1_TIME = 500L;

    public final static class DeviceConstant {
        public final static int DFTH_HELPER = 0;
        public final static int DFTH_DEVICE_PRINTER = 1;
        public final static int DFTH_DEVICE_BLOOD_PRESSURE = 6;
        public final static int DFTH_DEVICE_TWELVE_ECG = 7;
        public final static int DFTH_DEVICE_SINGLE_ECG = 8;
        public final static int DFTH_DEVICE_UNKNOWE = -1;
    }

    public final static class MemberInfoItem {
        public final static int MY_SERVICE = 0;
        public final static int CONSUME_RECORD = 1;
        public final static int EMERGENCY_CONTACT = 2;
        public final static int LIFE_HABIT = 3;
        public final static int DISEASE_HISTORY = 4;
    }
    public static class ECG{
        public static final long LESS_THAN_DOCTOR_TIME = 25 * TimeUtils.ONE_SECOND;
        public static final long ECG_GET_REPORT_TIME = TimeUtils.ONE_SECOND;
        public static final String SETTINGS_ORIENTATION_KEY = "settings_orientation_key";
    }

    public static class ECG_ANALYSIS{
        public static final String PVC_NAME = "室性期前收缩";
        public static final String SP_NAME = "室上性期前收缩";
    }

    public static class Action{
        public static final String KEEP_SERVICE_ACTION = "keep_service_action";
        public static final String ACTIVITY_STOP = "activity_stop";
        public static final String ACTIVITY_RESUME = "activity_resume";

    }
}
