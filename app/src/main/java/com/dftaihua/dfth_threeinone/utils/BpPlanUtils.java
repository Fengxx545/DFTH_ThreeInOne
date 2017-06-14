package com.dftaihua.dfth_threeinone.utils;

import android.app.ProgressDialog;
import android.text.TextUtils;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.entity.BpPlanData;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.network.RealDfthService;
import com.dfth.sdk.network.response.DfthServiceResult;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class BpPlanUtils {

    public static void cancelPhonePlan() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
        plan.setEndTime(System.currentTimeMillis() / 1000);
        plan.setStatus(BpPlan.STATE_FINISHED);
//        plan.setDayInterval(BpPlan.DAY_SPACE_TIME * 60 * 60);
//        plan.setNightInterval(BpPlan.NIGHT_SPACE_TIME * 60 * 60);
        DfthSDKManager.getManager().getDatabase().updateBPPlan(plan);
        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_PLAN_CANCEL_SUCCESS));
    }

    public static boolean cancelDevicePlan() {
        BpPlan plan = new BpPlan();
        plan.setEndTime(System.currentTimeMillis() / 1000);
        plan.setStatus(BpPlan.STATE_FINISHED);
        plan.setDayInterval(BpPlan.DAY_SPACE_TIME * 60 * 60);
        plan.setNightInterval(BpPlan.NIGHT_SPACE_TIME * 60 * 60);
        return BpDeviceUtils.createMeasurePlan(plan);
    }

    public static boolean checkIsPlanOver(BpPlan plan){
        if (plan.getDayInterval() == BpPlan.DAY_SPACE_TIME * 60 * 60 && plan.getNightInterval() == BpPlan.NIGHT_SPACE_TIME * 60 * 60) {
            return true;
        }
        int count = 0;
        long current = System.currentTimeMillis();
        if(plan == null){
            return false;
        }
        long currentTime = 0;
        long startTime = plan.getStartTime() * 1000L;
        while(count != plan.getSpaceTime().length){
            currentTime = plan.getSpaceTime()[count++] * 60 * 1000 + startTime - current;
            if(currentTime > 1000)
                break;
        }
        //测量计划已经执行完毕
        if(currentTime <=  1000){
            return true;
        }
        return false;
    }

    public static boolean checkIsPlanMeasure(long measureTime){
        int t = (int) (measureTime / 1000);
        int minute = t / 60;
        minute = minute % 60;
        int s = t % 60;
//        int minute = (int) (measureTime / 1000 / 60);
        if ((minute == 29 && s > 10) || (minute == 59 && s > 10)) {
            return true;
        }
        return false;
    }

    public static long getCountDownTime(BpPlan plan) {
        int count = 0;
        long current = System.currentTimeMillis();
        long startTime = plan.getStartTime() * 1000L;
        long countDownTime = 0;
        while (count != plan.getSpaceTime().length) {
            countDownTime = plan.getSpaceTime()[count++] * 60 * 1000 + startTime - current;
            if (countDownTime > 1000)
                break;
        }
        return countDownTime;
    }

    public static boolean checkIs24HourPlan(BpPlan plan){
        return plan.getEndTime() == plan.getStartTime() + 24 * 60 * 60;
    }

    public static boolean checkIsLock(BpPlan plan){
        if (plan.getDayInterval() == BpPlan.DAY_SPACE_TIME * 60 * 60 && plan.getNightInterval() == BpPlan.NIGHT_SPACE_TIME * 60 * 60) {
            return true;
        }
        int count = 0;
        long current = System.currentTimeMillis();
        if(plan == null){
            return false;
        }
        long currentTime = 0;
        long startTime = plan.getStartTime() * 1000L;
        while(count != plan.getSpaceTime().length){
            currentTime = plan.getSpaceTime()[count++] * 60 * 1000 + startTime - current;
            if(currentTime > 1000)
                break;
        }
        //测量计划已经执行完毕
        if(currentTime <=  1000){
            return true;
        }
        return false;
    }

    public static BpPlan getDefaultPlan() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        return DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
    }

    public static List<BpPlan> getAllPlans() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        List<BpPlan> planList = DfthSDKManager.getManager().getDatabase().getBPPlan(userId);
//        if(planList != null && planList.size() > 0){
//            Collections.sort(planList, new Comparator<BpPlan>() {
//                @Override
//                public int compare(BpPlan lhs, BpPlan rhs) {
//                    if(lhs.getSetPlanTime() > rhs.getSetPlanTime()){
//                        return 1;
//                    } else{
//                        return -1;
//                    }
//                }
//            });
//        }
        return planList;
    }

    public static List<BpResult> getBpResultByPlan(BpPlan plan) {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
//        return DfthSDKManager.getManager().getDatabase().getBPResultByPlan(userId, plan);
        return DfthSDKManager.getManager().getDatabase().getBPResultByPlanTime(userId, plan);
    }

    private static BpPlan mPlan;
    public static void uploadBpPlan(BpPlan plan){
        mPlan = plan;
        RealDfthService service = (RealDfthService) DfthSDKManager.getManager().getDfthService();
        final String userId = UserManager.getInstance().getDefaultUser().getUserId();
        service.uploadBpPlan(userId, plan).asyncExecute(new DfthServiceCallBack<String>() {
            @Override
            public void onResponse(DfthServiceResult<String> dfthServiceResult) {
                if (dfthServiceResult.mResult == 0) {
                    mPlan.setStatus(BpPlan.STATE_UPLOADED);
                    DfthSDKManager.getManager().getDatabase().updateBPPlan(mPlan);
                    getBpPlan(dfthServiceResult.mData);
                    createTask(userId,dfthServiceResult.mData);
                } else {
                    EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_PLAN_UPLOAD_FAIL));
                    ToastUtils.showShort(ThreeInOneApplication.getInstance(),"上传血压计划失败!" + dfthServiceResult.mMessage);
                }
            }
        });
    }

    public static void createTask(String userId, String eid){
        RealDfthService service = (RealDfthService) DfthSDKManager.getManager().getDfthService();
        service.createServiceTask(userId, eid, 2).asyncExecute(new DfthServiceCallBack<Void>() {
            @Override
            public void onResponse(DfthServiceResult<Void> response) {
                if(response.mResult == 0){
                    Logger.e("创建任务成功！");
                } else{
                    Logger.e("创建任务失败！");
                }
            }
        });
    }

    public static void getBpPlan(String id){
        if(!TextUtils.isEmpty(id)){
            RealDfthService service = (RealDfthService) DfthSDKManager.getManager().getDfthService();
            String userId = UserManager.getInstance().getDefaultUser().getUserId();
            service.downloadBpPlan(userId, id).asyncExecute(new DfthServiceCallBack<BpPlan>() {
                @Override
                public void onResponse(DfthServiceResult<BpPlan> dfthServiceResult) {
                    if (dfthServiceResult.mResult == 0) {
                        BpPlan plan = dfthServiceResult.mData;
                        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_PLAN_UPLOAD_SUCCESS, plan));
                        ToastUtils.showShort(ThreeInOneApplication.getInstance(),"上传血压计划成功!");
                    } else {
                        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_PLAN_UPLOAD_FAIL));
                        ToastUtils.showShort(ThreeInOneApplication.getInstance(),"上传血压计划失败!" + dfthServiceResult.mMessage);
                    }
                }
            });
        }
    }
}
