package com.dftaihua.dfth_threeinone.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.manager.BpMeasurePlanManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.BpPlanUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpPlan;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/3/18 0018.
 */
public class TimeService extends Service{
    public static final long INTERVAL = 1000L;
    public static final String ALARM_TIME_KEY = "alarm_time_key";
    private boolean isStop = false;
    private CountTimer mCountTimer;
    private long mCurrentTime;
    private BpPlan mCurrentPlan;
    private boolean isSendMeasure = true;
    private boolean isSendOver = true;
    private int mTimeCount = 0;//累计发送时间的次数

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context, TimeService.class);
        context.startService(intent);
    }

    public static void stopSelfService(Context context){
        Intent intent = new Intent();
        intent.setClass(context, TimeService.class);
        context.stopService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isStop = false;
        initializeModel();
        return super.onStartCommand(intent, flags, startId);
    }

    public void initializeModel(){
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        mCurrentPlan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
        //判断当前是否有测量模式
        if(mCurrentPlan != null && mCurrentPlan.getStatus() == BpPlan.STATE_RUNNING ) {
            setNextTime();
            updataTime();
        }else{
            this.stopSelf();
        }
    }

    private void sendBroadCastEvent(final long time){
//        checkSleepTime(time);
        isMeasurePreTime(time);
        isMeasureOverTime(time);
        if(!isStop){
            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.MEASURE_COUNT_DOWN, time));
        }
    }

    private void isMeasurePreTime(long time) {
        if (!isStop) {
            int minute = (int) (time / 1000 / 60);
            if (minute < 1) {
                if (isSendMeasure) {
                    EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.MEASURE_PRE_ALARM, time));
                    isSendMeasure = false;
                }
            } else {
                isSendMeasure = true;
            }
        }
    }

    private void isMeasureOverTime(long time) {
        if (!isStop) {
            int minute = (int) (time / 1000 / 60);
            if (minute == 29 || minute == 59) {
                if (isSendOver) {
                    EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.MEASURE_END_ALARM, time));
                    isSendOver = false;
                }
            } else {
                isSendOver = true;
            }
        }
    }

//    private void checkSleepTime(final long time){
//        if(mTimeCount == 0){
//            Intent intent = new Intent();
//            intent.setAction(BluetoothSleepManager.MEASURE_PLAN_ALARM);
//            intent.putExtra(ALARM_TIME_KEY, time);
//            TimeService.this.sendBroadcast(intent);
//        }
//        mTimeCount = mTimeCount == 10 ? 0 : mTimeCount + 1;
//    }

    private void updataTime(){
        if(mCurrentTime >0 ){
            if(mCountTimer == null){
                mCountTimer = new CountTimer(mCurrentTime, INTERVAL);
            }
            mCountTimer.start();
        }
    }

    private void cancelAlarm(){
        if(mCountTimer != null){
            mCountTimer.cancel();
            mCountTimer = null;
        }
    }

    private void setNextTime(){
        int count = 0;
        long current = System.currentTimeMillis();
        if(mCurrentPlan == null){
            stopSelf();
            return;
        }
        long startTime = mCurrentPlan.getStartTime() * 1000L;
        //计算当前测量计划执行到第几次
        while(count != mCurrentPlan.getSpaceTime().length){
            mCurrentTime = mCurrentPlan.getSpaceTime()[count++] * 60 * 1000 + startTime- current;
            if(mCurrentTime > 1000)
                break;
        }
        //测量计划已经执行完毕
        if(mCurrentTime <=  1000){
            mCurrentPlan.setStatus(BpPlan.STATE_FINISHED);
            DfthSDKManager.getManager().getDatabase().updateBPPlan(mCurrentPlan);
            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.MEASURE_PLAN_END));
            SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_LOCK, false);
//            BpPlanUtils.uploadBpPlan(mCurrentPlan);
            BpMeasurePlanManager.getManager(this).checkUploadBpResultDelay();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        cancelAlarm();
        //unregisterReceiver(receiver);
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void stopCountDown(){
        stopSelf();
    }

    public boolean isStop() {
        return isStop;
    }

    final class CountTimer extends CountDownTimer {
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long time){
            sendBroadCastEvent(time);
        }

        @Override
        public void onFinish() {
            setNextTime();
            cancelAlarm();
            updataTime();
        }
    }
}
