package com.dftaihua.dfth_threeinone.constant;

/**
 * Created by Administrator on 2017/3/18 0018.
 */
public class EventNameMessage {
    public static final String BLUETOOTH_SHUT_DOWN = "bluetooth_shut_down";

    public static final String MEASURE_COUNT_DOWN = "measure_count_down";//测量计划倒计时
    public static final String MEASURE_PLAN_PAUSE = "measure_plan_pause";//测量计划停止事件
    public static final String MEASURE_PLAN_RESET = "measure_plan_reset";
    public static final String MEASURE_PLAN_END = "measure_plan_end";
    public static final String MEASURE_PLAN_OVERTIME = "measure_plan_overtime"; //测量计划超过24小时
    public static final String MEASURE_PRE_ALARM = "measure_pre_alarm";  //测量前1分钟提醒
    public static final String MEASURE_END_ALARM = "measure_end_alarm";  //测量结束1分钟后解锁
    public static final String MEASURE_PLAN_UPDATE_EVENT = "measure_plan_update_event";// 测量计划更新事件
    public static final String MEASURE_PLAN_QUERY = "measure_plan_query";

    public static final String DEVICE_DISCOVER = "device_discover";//发现设备
    public static final String DEVICE_BP_CONNECT = "device_bp_connect";//血压设备连接成功
    public static final String DEVICE_RECONNECT = "device_bp_plan_exist";//重新连接


    public static final String DEVICE_BP_PLAN_EXIST = "device_bp_plan_exist";   //存在血压计划
    public static final String DEVICE_BP_DATA_EXIST = "device_bp_data_exist";   //存在血压计划


    public static final String DOWNLOAD_PLAN_RESPONSE = "download_plan_response";          //下发测量计划
    public static final String DEVICE_FIRST_ADD_SUCCESS = "device_first_add_success";      //第一次添加设备成功
    public static final String BP_PLAN_CANCEL_SUCCESS = "bp_plan_cancel_success";          //第一次添加设备成功
    public static final String MEMBER_INFO_UPDATE_SUCCESS = "member_info_update_success";  //用户信息更新成功


    public static final String BP_MEASURE_STOP = "bp_measure_stop";                 //血压停止测量
    public static final String BP_MEASURE_START = "bp_measure_start";               //血压开始测量
    public static final String BP_MEASURE_EXCEPTION = "bp_measure_exception";       //血压测量异常
    public static final String BP_PLAN_UPLOAD_SUCCESS = "bp_plan_upload_success";   //血压计划上传成功
    public static final String BP_PLAN_UPLOAD_FAIL = "bp_plan_upload_fail";         //血压计划上传失败
    public static final String BP_PLAN_IS_EXIST = "bp_plan_is_exist";               //存在血压计划
    public static final String BP_UNIT_CHANGE = "bp_unit_change";                   //血压单位改变


    public static final String CLEAR_DATA_SUCCESS = "clear_data_success";         //清除缓存成功
    public static final String PUSH_GET_DATA_SUCCESS = "push_get_data_success";   //根据推送获取心电数据成功
    public static final String PUSH_UPDATE_DETAIL = "push_update_detail";         //更新详情界面


    public static final String FREE_SERVICE_GET_SUCCESS = "free_service_get_success";    //获取免费服务成功
    public static final String NO_OPERATION_30S = "no_operation_30s";    //30s无操作
    public static final String ALARM_24_HOUR_END = "alarm_24_hour_end";    //24小时长程计时
    public static final String UPDATE_PLAN_DATA = "update_plan_data";    //按计划更新数据

}
