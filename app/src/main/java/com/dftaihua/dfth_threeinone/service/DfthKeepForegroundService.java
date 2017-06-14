package com.dftaihua.dfth_threeinone.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.WelcomeActivity;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.utils.AndroidUtils;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dfth.sdk.device.DfthDevice;

/**
 * Created by leezhiqiang on 2017/5/3.
 */

public class DfthKeepForegroundService extends Service{
    private ServiceReceiver mServiceReceiver;
    private String mActivityName;
    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
        unregisterReceiver();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void registerReceiver(){
        if(mServiceReceiver == null){
            mServiceReceiver = new ServiceReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(getPackageName() + Constant.Action.KEEP_SERVICE_ACTION);
            filter.addAction(Constant.Action.ACTIVITY_STOP);
            filter.addAction(Constant.Action.ACTIVITY_RESUME);
            registerReceiver(mServiceReceiver,filter);
        }
    }

    private void unregisterReceiver(){
        if(mServiceReceiver != null){
            unregisterReceiver(mServiceReceiver);
            mServiceReceiver = null;
        }
    }
    private void startNotify(){
        DfthDevice device = DfthDeviceManager.getInstance().existsHaveMeasuringDevice();
        if(device != null){
            String content = String.format("正在测量%s", DeviceUtils.getDeviceName(device));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(AndroidUtils.getApplicationName());
            builder.setContentText(content);
            builder.setSmallIcon(R.drawable.logo_icon);
            builder.setShowWhen(true);
            Intent intent = new Intent(getPackageName() + Constant.Action.KEEP_SERVICE_ACTION);
            builder.setContentIntent(PendingIntent.getBroadcast(this,101,intent,PendingIntent.FLAG_UPDATE_CURRENT));
            startForeground(101,builder.build());
        }
    }

    private final class ServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Constant.Action.ACTIVITY_STOP)){
                startNotify();
                if(intent.hasExtra("activity")){
                    mActivityName = intent.getStringExtra("activity");
                }
            }else if(action.equals(Intent.ACTION_SCREEN_ON)
                    || action.equals(Constant.Action.ACTIVITY_RESUME)){
                stopForeground(true);
                mActivityName = null;
            }else if(action.equals(getPackageName() + Constant.Action.KEEP_SERVICE_ACTION)){
                Intent i = new Intent();
                if(TextUtils.isEmpty(mActivityName)){
                    i.setClass(DfthKeepForegroundService.this, WelcomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }else{
                    i.setClassName(getPackageName(),mActivityName);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                startActivity(i);
            }
        }
    }
}
