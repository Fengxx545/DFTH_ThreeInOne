package com.dftaihua.dfth_threeinone.broadcastreceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dftaihua.dfth_threeinone.activity.EcgDetailActivity;
import com.dftaihua.dfth_threeinone.activity.EcgMeasureActivity;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.event.DfthMessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/*    *****      *     *
**    *    *      *   *
**    *****         *
**    *   *         *
**    *    *        *
**    *     *       *
* 创建时间：2017/3/9 13:22
*/
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("Action.Alarm".equals(intent.getAction())) {
//            System.out.println("闹钟事件发生了！");
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


//            Toast.makeText(context, "闹钟事件发生了！", Toast.LENGTH_LONG).show();
        } else if("Action.Alarm.30s".equals(intent.getAction())){
            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.NO_OPERATION_30S,null));
        }
    }

}