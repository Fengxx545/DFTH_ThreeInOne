package com.dftaihua.dfth_threeinone.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.FreeServiceActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.ECGHistoryMediator;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.Toast;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.ECGUtils;
import com.dfth.sdk.config.DfthConfig;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.network.DfthService;
import com.dfth.sdk.network.RealDfthService;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.user.DfthUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2016/12/12 15:12
*/
@SuppressLint("ValidFragment")
public class DoctorReviewFragment extends Fragment implements View.OnClickListener, Component<ECGHistoryMediator> {
    private TextView mDoctorResultTv;
    private TextView mInfo1Tv;
    private TextView mInfo2Tv;
    private ECGResult mECGResult;
    private RelativeLayout mServiceRl;
    private RelativeLayout mNoServiceRl;
    private Button mGetServiceBtn;
    private int mDeviceType;
    private TextView mDoctorNameTv;
    private TextView mDoctorTimeTv;
    private DfthUser mUser;
    private ECGHistoryMediator mECGHistoryMediator;
    private int mStatus = 0;
    private boolean isPressBtn = false;

    public DoctorReviewFragment(int deviceType, ECGResult ecgResult) {
        mDeviceType = deviceType;
        mECGResult = ecgResult;
        mUser = UserManager.getInstance().getDefaultUser();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.base_history_doctor, null);
        mDoctorResultTv = (TextView) view.findViewById(R.id.doctor_service_result);
        mInfo1Tv = (TextView) view.findViewById(R.id.history_doctor_result_no_service_1);
        mInfo2Tv = (TextView) view.findViewById(R.id.history_doctor_result_no_service_2);
        mServiceRl = (RelativeLayout) view.findViewById(R.id.history_doctor_service_rl);
        mNoServiceRl = (RelativeLayout) view.findViewById(R.id.history_doctor_no_service_rl);
        mGetServiceBtn = (Button) view.findViewById(R.id.history_doctor_get_service_btn);
        mDoctorNameTv = (TextView) view.findViewById(R.id.doctor_name);
        mDoctorTimeTv = (TextView) view.findViewById(R.id.doctor_service_time);
        mGetServiceBtn.setOnClickListener(this);
        if (!(Boolean) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.FREE_SERVICE_STATUS, false)) {
            mNoServiceRl.setVisibility(View.VISIBLE);
            mServiceRl.setVisibility(View.GONE);
        } else {
            mNoServiceRl.setVisibility(View.GONE);
            mServiceRl.setVisibility(View.VISIBLE);
        }
        int res = mDeviceType == DfthDevice.SingleDevice ? R.drawable.single_btn_selector
                : R.drawable.twelve_btn_selector;
        int textRes = mDeviceType == DfthDevice.SingleDevice ? R.color.single_color : R.color.twelve_color;
        mGetServiceBtn.setBackgroundResource(res);
        mGetServiceBtn.setTextColor(ThreeInOneApplication.getColorRes(textRes));
        setDoctorResult(mECGResult);
        return view;
    }

    public void setDoctorResult(ECGResult result) {
        if (mStatus == 101 && isPressBtn && ECGResult.DoctorResultStatus.create(result.getDoctorAnalysisStatus())
                .equals(ECGResult.DoctorResultStatus.ECG_ORGANIZATION_OVER)) {
            ToastUtils.showShort(getActivity(), "服务已关闭!");
        }
        isPressBtn = false;
        mStatus = 0;
        mECGResult = result;
        if (mServiceRl == null) {
            return;
        }
        ECGUtils.checkFileValid(result);
        String status = result.getDoctorAnalysisStatus();
        if (status != null && (status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_NORMAL.getCode())
                || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_EXCEPTION.getCode())
                || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_OAUTH_EXCEPTION.getCode())
                || status.equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_WARN.getCode()))) {
            String builder = mUser.getName() + (mUser.getGender() == 1 ? "先生" : "女士") + ",您本次测量的结果 :\n"
                    + ECGResult.DoctorResultStatus.create(status).getDoctorAnalysis(result.getDoctorResult());
            mDoctorResultTv.setText(builder);
            mDoctorNameTv.setText("医师：" + mECGResult.getDoctorId());
            mDoctorTimeTv.setText(TimeUtils.getString(mECGResult.getServiceTime(), "yyyy-MM-dd HH:mm"));
        } else {
            if (result.getPost() < ECGResult.UPLOAD_RESULT_SUCCESS) {
                status = ECGResult.DoctorResultStatus.ECG_DATA_NOT_YET_UPLOAD.getCode();
            } else if (result.getMeasureEndTime() - result.getMeasureStartTime() < DfthConfig
                    .getConfig().ecgConfig.ecgAnalysisConfig.doctorMinAnalysisTime) {
                status = ECGResult.DoctorResultStatus.ECG_TIME_LESS_THAN_ANALYSIS_TIME.getCode();
            } else if (TextUtils.isEmpty(status)) {
                status = ECGResult.DoctorResultStatus.ECG_DATA_UPLOADED.getCode();
            }
            ECGResult.DoctorResultStatus doctorResultStatus = ECGResult.DoctorResultStatus.create(status);
            String content = doctorResultStatus.getDoctorAnalysis(result.getDoctorResult());
            if (doctorResultStatus.equals(ECGResult.DoctorResultStatus.ECG_DATA_UPLOADED) ||
                    doctorResultStatus.equals(ECGResult.DoctorResultStatus.ECG_ORGANIZATION_OVER) ||
                    doctorResultStatus.equals(ECGResult.DoctorResultStatus.ECG_SERVICE_OVER)) {
                mNoServiceRl.setVisibility(View.VISIBLE);
                mServiceRl.setVisibility(View.GONE);
                mInfo1Tv.setVisibility(View.GONE);
                mInfo2Tv.setVisibility(View.GONE);
                if (doctorResultStatus.equals(ECGResult.DoctorResultStatus.ECG_SERVICE_OVER)) {  // 102
                    if ((Boolean) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.FREE_SERVICE_STATUS, false)) {
                        mInfo1Tv.setVisibility(View.VISIBLE);
                        mInfo2Tv.setVisibility(View.VISIBLE);
                        mInfo1Tv.setText(ThreeInOneApplication.getStringRes(R.string.get_report_service_close_tip1));
                        mInfo2Tv.setText(ThreeInOneApplication.getStringRes(R.string.get_report_service_close_tip2));
                        mGetServiceBtn.setText(ThreeInOneApplication.getStringRes(R.string.get_report));
                    } else {
                        mInfo1Tv.setVisibility(View.VISIBLE);
                        mInfo2Tv.setVisibility(View.VISIBLE);
                        mInfo1Tv.setText(ThreeInOneApplication.getStringRes(R.string.get_report_service_close_tip3));
                        mInfo2Tv.setText(ThreeInOneApplication.getStringRes(R.string.get_report_service_close_tip4));
                        mGetServiceBtn.setText(ThreeInOneApplication.getStringRes(R.string.history_doctor_get_service));
                    }
                } else if (doctorResultStatus.equals(ECGResult.DoctorResultStatus.ECG_ORGANIZATION_OVER)) { // 101
                    mStatus = 101;
                    mInfo1Tv.setVisibility(View.VISIBLE);
                    mInfo2Tv.setVisibility(View.VISIBLE);
                    mInfo1Tv.setText(ThreeInOneApplication.getStringRes(R.string.get_report_service_close_tip1));
                    mInfo2Tv.setText(ThreeInOneApplication.getStringRes(R.string.get_report_service_close_tip2));
                    mGetServiceBtn.setText(ThreeInOneApplication.getStringRes(R.string.get_report));
                } else {   // 1
                    mInfo1Tv.setVisibility(View.VISIBLE);
                    mInfo1Tv.setText(ThreeInOneApplication.getStringRes(R.string.get_report_fail_tip));
                    mGetServiceBtn.setText(ThreeInOneApplication.getStringRes(R.string.get_report));
                }
            } else {
                mServiceRl.setVisibility(View.VISIBLE);
                mNoServiceRl.setVisibility(View.GONE);
                mDoctorResultTv.setText(content);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DfthMessageEvent event) {
        if (event.getEventName().equals(EventNameMessage.FREE_SERVICE_GET_SUCCESS)) {
            if (mECGHistoryMediator != null) {
                mECGHistoryMediator.getDoctorAnalyse();
            }
        } else if (event.getEventName().equals("UPDATE_SINGLE_DATE")) {
            setDoctorResult((ECGResult) event.getData());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_doctor_get_service_btn:
                if (!NetworkCheckReceiver.getNetwork()) {
                    ToastUtils.showShort(getActivity(), R.string.network_not_connect);
                    return;
                }
                if (!(Boolean) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.FREE_SERVICE_STATUS, false)) {
                    if (mStatus == 101) {
                        createECGTask();
                    } else {
                        ActivitySkipUtils.skipAnotherActivity(getActivity(), FreeServiceActivity.class);
                    }
                } else {
                    if (mStatus == 101) {
                        createECGTask();
                    } else {
                        if (mECGHistoryMediator != null) {
                            mECGHistoryMediator.getDoctorAnalyse();
                            isPressBtn = true;
                        }
                    }
                }
                break;
        }
    }

    private void createECGTask() {
        RealDfthService service = (RealDfthService) DfthSDKManager.getManager().getDfthService();
        service.createServiceTask(UserManager.getInstance().getDefaultUser().getUserId(),
                mECGResult.getEid(), DfthService.ALLECG).asyncExecute(new DfthServiceCallBack<Void>() {
            @Override
            public void onResponse(DfthServiceResult<Void> dfthServiceResult) {
                if (mECGHistoryMediator != null) {
                    mECGHistoryMediator.getDoctorAnalyse();
                    isPressBtn = true;
                }
            }
        });
    }

    @Override
    public void onResume() {
        if (mECGResult != null) {
            setDoctorResult(mECGResult);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void bindMediator(ECGHistoryMediator mediator) {
        mECGHistoryMediator = mediator;
    }
}
