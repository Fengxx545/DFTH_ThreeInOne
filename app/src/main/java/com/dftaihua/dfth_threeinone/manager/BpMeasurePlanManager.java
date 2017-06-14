package com.dftaihua.dfth_threeinone.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.dftaihua.dfth_threeinone.activity.EcgDetailActivity;
import com.dftaihua.dfth_threeinone.activity.LoginActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.BpPlanUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.bluetooth.Action;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.network.response.DfthServiceResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/3/18 0018.
 */
public class BpMeasurePlanManager {
    private static BpMeasurePlanManager manager;
    private Context mContext;

    private BpMeasurePlanManager(Context context) {
        mContext = context;
        EventBus.getDefault().register(this);
    }

    public static BpMeasurePlanManager getManager(Context context) {
        if (manager == null) {
            manager = new BpMeasurePlanManager(context);
        }
        return manager;
    }

    public Context getContext() {
        return mContext;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlanStatus(DfthMessageEvent event) {
        if (event.getEventName().equals(EventNameMessage.MEASURE_PLAN_QUERY)) {
            //电源关闭代表测量计划结束
        } else if (event.getEventName().equals(EventNameMessage.BLUETOOTH_SHUT_DOWN)) {
        } else if (event.getEventName().equals(EventNameMessage.DOWNLOAD_PLAN_RESPONSE)) {
            String userId = UserManager.getInstance().getDefaultUser().getUserId();
            BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
            //查询计划无计划，发送停止命令
            if (plan == null || plan.getStatus() != BpPlan.STATE_RUNNING) {
                measurePlanEnd();
            }
            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.MEASURE_PLAN_UPDATE_EVENT));
        } else if (event.getEventName().equals(EventNameMessage.MEASURE_PRE_ALARM)) {
            SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_LOCK, true);
        } else if (event.getEventName().equals(EventNameMessage.MEASURE_END_ALARM)) {
            SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_LOCK, false);
        } else if (event.getEventName().equals(EventNameMessage.MEASURE_PLAN_END)) {//测量计划已经执行完毕
//            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.MEASURE_PLAN_END));
//            measurePlanEnd();
        } else if (event.getEventName().equals(EventNameMessage.BP_PLAN_UPLOAD_SUCCESS)) {//测量计划上传成功
            BpPlan plan = (BpPlan) event.getData();
            plan.setStatus(BpPlan.STATE_UPLOADED);
            DfthSDKManager.getManager().getDatabase().updateBPPlan(plan);
        } else if (event.getEventName().equals(EventNameMessage.BP_MEASURE_STOP)) {
            SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.BP_MEASURE_IS_PRESS, false);
        } else if (event.getEventName().equals(EventNameMessage.BP_PLAN_UPLOAD_FAIL)) {
            Logger.e("bp plan upload failed!");
        }
//        else if (event.getEventName().equals(EventNameMessage.ALARM_24_HOUR_END)) {//24小时结束
//            Activity activity = ActivityCollector.getActivity();
//            if(activity != null){
//                HashMap<String, Object> deviceMap = new HashMap<>();
//                deviceMap.put(Constant.DFTH_RESULT_DATA, result);
//                ActivitySkipUtils.skipAnotherActivity(activity, EcgDetailActivity.class,);
//            }
//        }
    }

    private void measurePlanEnd() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
        plan.setEndTime((int) (System.currentTimeMillis() / 1000L));
        plan.setStatus(BpPlan.STATE_FINISHED);
        DfthSDKManager.getManager().getDatabase().updateBPPlan(plan);
    }

    public void checkUploadBpPlan() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        List<BpPlan> plans = DfthSDKManager.getManager().getDatabase().getBPPlanFinished(userId);
        if (plans != null && plans.size() > 0) {
            for (int i = 0; i < plans.size(); i++) {
                BpPlan plan = plans.get(i);
                if(BpPlanUtils.checkIs24HourPlan(plan)){
                    List<BpResult> results = DfthSDKManager.getManager().getDatabase().getBPResultByPlan(userId, plan);
                    if(results.size() == plan.getTotalCount()){
                        BpPlanUtils.uploadBpPlan(plan);
                    }
                } else{
                    BpPlanUtils.uploadBpPlan(plan);
                }
            }
        }
    }

    private List<BpResult> mResults;

    public void checkUploadBpResultDelay() {
        DispatchUtils.performDelay(new DispatchTask() {
            @Override
            public void accept(Object o) throws Exception {
                checkUploadBpResult();
            }
        }, 2 * 60 * 1000);
    }

    public void checkUploadBpResult() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        mResults = BpDataUtils.getInvalidData(DfthSDKManager.getManager().getDatabase().getBPResultNotUpload(userId));
        if (mResults != null && mResults.size() > 0) {
            DfthSDKManager.getManager().getDfthService().batchUploadBpData(userId, mResults).asyncExecute(new DfthServiceCallBack<List<String>>() {
                @Override
                public void onResponse(DfthServiceResult<List<String>> response) {
                    if (response.mResult == 0) {
                        for (int i = 0; i < mResults.size(); i++) {
                            BpResult result = mResults.get(i);
                            result.setIsUpload(BpResult.BP_RESULT_UPLOADED);
                            DfthSDKManager.getManager().getDatabase().updateBPResult(result);
                        }
                        mResults = null;
                        checkUploadBpPlan();
                        ToastUtils.showShort(ThreeInOneApplication.getInstance(), "上传血压数据成功!");
                    } else {
                        ToastUtils.showShort(ThreeInOneApplication.getInstance(), "上传血压数据失败!" + response.mMessage);
                    }
                }
            });
        } else {
            checkUploadBpPlan();
        }
    }
}
