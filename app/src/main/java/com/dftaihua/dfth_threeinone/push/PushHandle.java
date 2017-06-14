package com.dftaihua.dfth_threeinone.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.manager.NotificationReceiver;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.push.DfthPushEvent;
import com.dfth.push.model.DoctorAnalysisPushMessage;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.DfthSdkCallBack;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.network.DfthService;
import com.dfth.sdk.network.RealDfthService;
import com.dfth.sdk.network.response.DfthServiceResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by tang on 2017/3/29.
 */

public class PushHandle {
    private static PushHandle sHandle;
    private static Context mContext;

    private PushHandle(){

    }
    private static PushHandle getHandle(){
        if(sHandle == null){
            sHandle = new PushHandle();
        }
        return sHandle;
    }
    public static void start(Context context){
        mContext = context;
        EventBus.getDefault().register(getHandle());
    }

    public static void destroy(){
        EventBus.getDefault().unregister(getHandle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DfthMessageEvent event){
        if(event.getEventName().equals(DfthPushEvent.MESSAGE_EVENT)){
            doMessage(event);
        }
    }

    private void doMessage(final DfthMessageEvent event){
        final DoctorAnalysisPushMessage message = (DoctorAnalysisPushMessage) event.getData();
        Logger.e("push eid :" + message.getEid());
        Logger.e("push userId :" + message.getUserId());
        if(message.isNotify()){
            ECGResult result = DfthSDKManager.getManager().getDatabase().getECGResultByEid(message.getEid());
            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.PUSH_UPDATE_DETAIL, result));
        } else{
            ECGResult result = DfthSDKManager.getManager().getDatabase().getECGResultByEid(message.getEid());
            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.PUSH_UPDATE_DETAIL, result));
        }
    }
}
