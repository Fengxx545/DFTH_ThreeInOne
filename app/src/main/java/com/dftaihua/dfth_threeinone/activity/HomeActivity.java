package com.dftaihua.dfth_threeinone.activity;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.service.TimeService;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.BpDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.BpPlanUtils;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.DevicePopup;
import com.dftaihua.dfth_threeinone.widget.TaskCardView;
import com.dfth.pay.PayManager;
import com.dfth.pay.model.DfthClaimGoods;
import com.dfth.pay.network.RealPayService;
import com.dfth.push.DfthPushManager;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.permission.DfthPermissionManager;
import com.dfth.update.DfthUpdateCode;
import com.dfth.update.UpdateListener;
import com.dfth.update.UpdateManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class HomeActivity extends BaseActivity implements OnClickListener,
        DevicePopup.OnItemClickListener, DevicePopup.OnItemLongClickListener {
    public static boolean IS_ALIVE = false;
    private final RealPayService mService;
    private DevicePopup mPopup;
    private ImageView mDevicePlusIv;
    private ImageView mLoginIv;
    private ImageView mGiftIv;
    private RelativeLayout mTaskCardHelpRl;
    private RelativeLayout mTaskCardBpRl;
    private RelativeLayout mTaskCardSingleRl;
    private RelativeLayout mTaskCardEcgRl;
    private ScrollView mTaskCardSl;

    private TaskCardView mHelperView;
    private TaskCardView mBloodView;
    private TaskCardView mSingleEcgView;
    private TaskCardView mTwelveEcgView;

    private int mDeviceType;
    private boolean isFromOut;
    private Animation mOperatingAnim;
    private RelativeLayout mMaskedRl;

    private List<DfthClaimGoods> mGoods;

    public HomeActivity() {
        mTitleNameRes = R.string.title_activity_home;
        mTitleColorRes = R.color.title_home_back;
        mTitleNameColorRes = R.color.title_home_name_text;
        mStatus = TITLE_VISIBLE | SLIDE_VISIBLE;
        EventBus.getDefault().register(this);
        mService = (RealPayService) PayManager.getPayManager().getPayService();
    }

    @Override
    public void initData() {
        TimeService.start(this);
        initPush();
        initUpdate();
    }

    private void initUpdate() {
        UpdateManager.handleUpdate(this, new UpdateListener() {
            @Override
            public void onUpdateResponse(int code, String message) {
                if (code == DfthUpdateCode.UPDATE_QUIT_APP) {
                    HomeActivity.this.finish();
                } else if (code == DfthUpdateCode.UPDATE_FAILED) {
                    if (NetworkCheckReceiver.getNetwork()) {
                        ToastUtils.showLong(HomeActivity.this, R.string.update_failed);
                    }
                }
            }
        });
    }

    private void initPush() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        if (!TextUtils.isEmpty(userId)) {
            DfthPushManager.register(userId);
        }
    }

    @Override
    public View initView() {
        IS_ALIVE = true;
        View view = LayoutInflater.from(this).inflate(R.layout.activity_home, null);
        mDevicePlusIv = (ImageView) view.findViewById(R.id.activity_home_device_plus_iv);
        mLoginIv = (ImageView) view.findViewById(R.id.activity_home_head_login_iv);
        mGiftIv = (ImageView) view.findViewById(R.id.activity_home_press_gift_iv);
        mTaskCardHelpRl = (RelativeLayout) view.findViewById(R.id.activity_home_task_card_helper_rl);
        mTaskCardBpRl = (RelativeLayout) view.findViewById(R.id.activity_home_task_card_blood_pressure_rl);
        mTaskCardSingleRl = (RelativeLayout) view.findViewById(R.id.activity_home_task_card_single_ecg_rl);
        mTaskCardEcgRl = (RelativeLayout) view.findViewById(R.id.activity_home_task_card_ecg_rl);
        mMaskedRl = (RelativeLayout) view.findViewById(R.id.cloth_layer_mask);
        mTaskCardSl = (ScrollView) view.findViewById(R.id.activity_home_task_card_sl);
        SharePreferenceUtils.put(HomeActivity.this,SharePreferenceConstant.FREE_SERVICE_STATUS,true);
        mService.getClaimGoods(UserManager.getInstance().getDefaultUser().getUserId()).asyncExecute(new DfthServiceCallBack<List<DfthClaimGoods>>() {
            @Override
            public void onResponse(DfthServiceResult<List<DfthClaimGoods>> dfthServiceResult) {
                mGoods = dfthServiceResult.mData;
                if (mGoods != null) {
                    if(mGoods.get(0).isExists == 0){
                        mGiftIv.setVisibility(View.VISIBLE);
                        SharePreferenceUtils.put(HomeActivity.this,SharePreferenceConstant.FREE_SERVICE_STATUS,false);
                    } else{
                        SharePreferenceUtils.put(HomeActivity.this,SharePreferenceConstant.FREE_SERVICE_STATUS,true);
                        mGiftIv.setVisibility(View.GONE);
                    }
                }
            }
        });
        mDevicePlusIv.setOnClickListener(this);
        mLoginIv.setOnClickListener(this);
        mGiftIv.setOnClickListener(this);
        mPopup = new DevicePopup(this, ThreeInOneApplication.getScreenWidth()/2, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopup.setListener(this);
//        mPopup.setLongListener(this);
        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                quitAnim();
                mMaskedRl.setVisibility(View.GONE);
            }
        });
        isFromOut = true;
        addDeviceCard(Constant.DeviceConstant.DFTH_HELPER);
        if ((Boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.DEVICE_BLOOD, false)) {
            addDeviceCard(DfthDevice.BpDevice);
        }
        if ((Boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.DEVICE_SINGLE_ECG, false)) {
            addDeviceCard(DfthDevice.SingleDevice);
        }
        if ((Boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.DEVICE_TWELVE_ECG, false)) {
            addDeviceCard(DfthDevice.EcgDevice);
        }
        DfthPermissionManager.assertSdkPermission(this, 100);
        return view;
    }

    private void openAnim(){
        final RotateAnimation animation =new RotateAnimation(0f,315f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        mDevicePlusIv.startAnimation(animation);
    }


    private void quitAnim(){
        final RotateAnimation animation =new RotateAnimation(360f,0f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        mDevicePlusIv.startAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_home_device_plus_iv:
                mPopup.show(mDevicePlusIv);
                mMaskedRl.setVisibility(View.VISIBLE);
                openAnim();
                break;
            case R.id.activity_home_head_login_iv:
                ActivitySkipUtils.skipAnotherActivity(this, MemberInfoActivity.class);
                break;
            case R.id.activity_home_press_gift_iv:
                ActivitySkipUtils.skipAnotherActivity(this, FreeServiceActivity.class);
                break;
        }
    }

    @Override
    public void onItemClick(int deviceType) {
        mDeviceType = deviceType;
//        BindedDevice bindedDevice = BindedDevice.getBindedDevice(deviceType);
//        if (bindedDevice == null){
            connectDevice(deviceType,"");
//        }else {
//            connectDevice(deviceType, bindedDevice.getMacAddresss());
//        }
    }


    @Override
    public void onItemLongClick(int deviceType) {
        mDeviceType = deviceType;
        connectDevice(deviceType,"");
    }

    private void connectDevice(final int deviceType,String macAddress) {
        DeviceUtils.autoConnectOrMeasure(HomeActivity.this, deviceType, macAddress, 0, false, new DfthCallBack<Boolean>() {
            @Override
            public void onResponse(DfthResult<Boolean> dfthResult) {
                if (dfthResult.getReturnData()) {
                    ToastUtils.showLong(HomeActivity.this, R.string.activity_home_helper_bind_device_success);
                    mPopup.dismiss();
                    isFromOut = false;
                    switch (deviceType) {
                        case DfthDevice.BpDevice:
                            if (mBloodView == null) {
                                addDeviceCard(deviceType);
                            }
                            break;
                        case DfthDevice.SingleDevice:
                            if (mSingleEcgView == null) {
                                addDeviceCard(deviceType);
                            }
                            break;
                        case DfthDevice.EcgDevice:
                            if (mTwelveEcgView == null) {
                                addDeviceCard(deviceType);
                            }
                            break;
                    }
                }
            }
        });
    }

    private void addDeviceCard(final int deviceType) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 20;
        params.bottomMargin = 20;
        switch (deviceType) {
            case Constant.DeviceConstant.DFTH_HELPER:
                mHelperView = new TaskCardView(this, deviceType);
                mHelperView.setId(R.id.task_card_helper);
                mTaskCardHelpRl.addView(mHelperView, params);
                break;
            case DfthDevice.BpDevice:
                mBloodView = new TaskCardView(this, deviceType);
                mBloodView.setId(R.id.task_card_blood_pressure);
                mTaskCardBpRl.addView(mBloodView, params);
                setBpData();
                break;
            case DfthDevice.SingleDevice:
                mSingleEcgView = new TaskCardView(this, deviceType);
                mSingleEcgView.setId(R.id.task_card_single_ecg);
                mTaskCardSingleRl.addView(mSingleEcgView, params);
                setEcgData(1);
                break;
            case DfthDevice.EcgDevice:
                mTwelveEcgView = new TaskCardView(this, deviceType);
                mTwelveEcgView.setId(R.id.task_card_ecg);
                mTaskCardEcgRl.addView(mTwelveEcgView, params);
                setEcgData(12);
                break;
        }
        mPopup.refreshView();
        if (!isFromOut) {
            mTaskCardSl.post(new Runnable() {
                public void run() {
                    mTaskCardSl.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    public void removeDeviceCard(int deviceType) {
        if (deviceType == DfthDevice.BpDevice) {
            if (mBloodView != null) {
                mTaskCardBpRl.removeView(mBloodView);
                mBloodView = null;
            }
        }
        if (deviceType == DfthDevice.SingleDevice) {
            if (mSingleEcgView != null) {
                mTaskCardSingleRl.removeView(mSingleEcgView);
                mSingleEcgView = null;
            }
        }
        if (deviceType == DfthDevice.EcgDevice) {
            if (mTwelveEcgView != null) {
                mTaskCardEcgRl.removeView(mTwelveEcgView);
                mTwelveEcgView = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IS_ALIVE = true;
        if (mBloodView != null) {
            setBpData();
        }
        if (mSingleEcgView != null) {
            mSingleEcgView.onResume(getEcgData(1));
        }
        if (mTwelveEcgView != null) {
            mTwelveEcgView.onResume(getEcgData(12));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_ALIVE = false;
        EventBus.getDefault().unregister(this);
        if(mSingleEcgView != null){
            mSingleEcgView.onDestroy();
        }
        if(mTwelveEcgView != null){
            mTwelveEcgView.onDestroy();
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void setBpData() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        List<BpResult> results = DfthSDKManager.getManager().getDatabase().getBPResult(userId);
        BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
        if (results != null && results.size() > 0) {
            mBloodView.setBpData(results.get(0));
        } else {
            mBloodView.setBpData(null);
        }
        mBloodView.onBpResume(plan);
    }

    private ECGResult getEcgData(int leaders) {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        List<ECGResult> results;
        if (leaders == 1) {
            results = DfthSDKManager.getManager().getDatabase().getSingleResult(userId);
        } else {
            results = DfthSDKManager.getManager().getDatabase().getTwelveECGResult(userId);
        }
        if (results != null && results.size() > 0) {
            ECGResult result = results.get(0);
            return result;
        }
        return null;
    }

    private void setEcgData(int leaders) {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        List<ECGResult> results;
        if (leaders == 1) {
            results = DfthSDKManager.getManager().getDatabase().getSingleResult(userId);
        } else {
            results = DfthSDKManager.getManager().getDatabase().getTwelveECGResult(userId);
        }
        if (results != null && results.size() > 0) {
            ECGResult result = results.get(0);
            if (leaders == 1) {
                mSingleEcgView.setEcgData(result,1);
                String status = result.getDoctorAnalysisStatus();
                if(!TextUtils.isEmpty(status)){
                    if (status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_NORMAL.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_EXCEPTION.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_OAUTH_EXCEPTION.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_WARN.getCode())) {
                        mSingleEcgView.setCountTimeVisibility(View.VISIBLE);
                        mSingleEcgView.setCountTimeText(result.getDoctorResult());
                        mSingleEcgView.setCountTimeHeadText(ThreeInOneApplication.getStringRes(R.string.activity_home_ecg_analysis));
                    } else{
                        mSingleEcgView.setCountTimeVisibility(View.GONE);
                    }
                } else{
                    mSingleEcgView.setCountTimeVisibility(View.GONE);
                }
            } else {
                mTwelveEcgView.setEcgData(result,12);
                String status = result.getDoctorAnalysisStatus();
                if(!TextUtils.isEmpty(status)){
                    if (status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_NORMAL.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_EXCEPTION.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_OAUTH_EXCEPTION.getCode())
                            || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_WARN.getCode())) {
                        mTwelveEcgView.setCountTimeVisibility(View.VISIBLE);
                        mTwelveEcgView.setCountTimeText(result.getDoctorResult());
                        mTwelveEcgView.setCountTimeHeadText(ThreeInOneApplication.getStringRes(R.string.activity_home_ecg_analysis));
                    } else{
                        mTwelveEcgView.setCountTimeVisibility(View.GONE);
                    }
                } else{
                    mTwelveEcgView.setCountTimeVisibility(View.GONE);
                }
            }
        } else {
            if (leaders == 1) {
                mSingleEcgView.setEcgData(null,1);
                mSingleEcgView.setCountTimeVisibility(View.GONE);
            } else {
                mTwelveEcgView.setEcgData(null,12);
                mTwelveEcgView.setCountTimeVisibility(View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DfthMessageEvent event) {
        if (event.getEventName().equals(EventNameMessage.MEASURE_COUNT_DOWN)) {
            if (mBloodView != null) {
                long time = (long) event.getData();
                mBloodView.setCountTime(time);
            }
        } else if (event.getEventName().equals(EventNameMessage.DEVICE_FIRST_ADD_SUCCESS)) {
            int deviceType = (int) event.getData();
            isFromOut = false;
            addDeviceCard(deviceType);
            mPopup.refreshView();
        } else if (event.getEventName().equals(EventNameMessage.DEVICE_BP_DATA_EXIST)) {
            if (mBloodView != null) {
                setBpData();
            }
        } else if (event.getEventName().equals(EventNameMessage.DEVICE_RECONNECT)) {
            if(ActivityCollector.getActivity() instanceof HomeActivity){
//                BindedDevice bindedDevice = BindedDevice.getBindedDevice(mDeviceType);
//                if (bindedDevice == null){
                    connectDevice(mDeviceType,"");
//                }else {
//                    connectDevice(mDeviceType, bindedDevice.getMacAddresss());
//                }
            }
        } else if (event.getEventName().equals(EventNameMessage.MEASURE_PLAN_END)) {
            if (mBloodView != null) {
                mBloodView.setCountTimeVisibility(View.GONE);
            }
        } else if (event.getEventName().equals(EventNameMessage.CLEAR_DATA_SUCCESS)) {
            if (mSingleEcgView != null) {
                mSingleEcgView.setEcgData(null,1);
                mSingleEcgView.setCountTimeVisibility(View.GONE);
            }
            if (mTwelveEcgView != null) {
                mTwelveEcgView.setEcgData(null,12);
                mTwelveEcgView.setCountTimeVisibility(View.GONE);
            }
        } else if (event.getEventName().equals(EventNameMessage.FREE_SERVICE_GET_SUCCESS)) {
            mGiftIv.setVisibility(View.GONE);
        } else if (event.getEventName().equals(EventNameMessage.BP_PLAN_IS_EXIST)) {
            Activity activity = ActivityCollector.getActivity();
            if(activity instanceof HomeActivity){
                String userId = UserManager.getInstance().getDefaultUser().getUserId();
                BpPlan everPlan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
                BpPlan devicePlan = (BpPlan) event.getData();
                DfthBpDevice bpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
                if (everPlan != null && (everPlan.getStatus() != BpPlan.STATE_FINISHED && everPlan.getStatus() != BpPlan.STATE_UPLOADED)) {
                    if (devicePlan != null && !BpPlanUtils.checkIsPlanOver(devicePlan)) {
                        if (!BpDeviceUtils.isSamePlan(devicePlan, everPlan)) {
                            Dialog dialog = DialogFactory.getPlanDialog(this, Constant.BP_PLAN_EXIST_BOTH);
                            dialog.show();
                        } else {
                            BpDeviceUtils.getBpPlanDatas(bpDevice, everPlan);
                        }
                    } else {
                        Dialog dialog = DialogFactory.getPlanDialog(this, Constant.BP_PLAN_EXIST_PHONE);
                        dialog.show();
                    }
                } else if (everPlan != null && everPlan.getStatus() == BpPlan.STATE_FINISHED) {
                    if (devicePlan != null && BpPlanUtils.checkIsPlanOver(devicePlan)) {
                        if (BpDeviceUtils.isSamePlan(devicePlan, everPlan)) {
                            BpDeviceUtils.getBpPlanDatas(bpDevice, everPlan);
                        }
                    } else if (devicePlan != null && !BpPlanUtils.checkIsPlanOver(devicePlan)) {
                        Dialog dialog = DialogFactory.getPlanDialog(this, Constant.BP_PLAN_EXIST_DEVICE);
                        dialog.show();
                    }
                } else if (everPlan == null) {
                    if (devicePlan != null) {
                        if (!BpPlanUtils.checkIsPlanOver(devicePlan)) {
                            Dialog dialog = DialogFactory.getPlanDialog(this, Constant.BP_PLAN_EXIST_DEVICE);
                            dialog.show();
                        }
                    }
                }
            }
        }
    }
}

