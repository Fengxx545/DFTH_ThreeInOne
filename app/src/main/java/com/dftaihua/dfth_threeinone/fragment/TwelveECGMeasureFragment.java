//package com.dftaihua.dfth_threeinone.fragment;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import com.dftaihua.dfth_threeinone.R;
//import com.dftaihua.dfth_threeinone.activity.EcgDetailActivity;
//import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
//import com.dftaihua.dfth_threeinone.constant.Constant;
//import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
//import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
//import com.dftaihua.dfth_threeinone.listener.IntermediaryInterface;
//import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
//import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
//import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
//import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
//import com.dftaihua.dfth_threeinone.utils.MathUtils;
//import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
//import com.dftaihua.dfth_threeinone.utils.TimeUtils;
//import com.dftaihua.dfth_threeinone.utils.ToastUtils;
//import com.dftaihua.dfth_threeinone.widget.BottomControlView;
//import com.dftaihua.dfth_threeinone.widget.LeaderRect;
//import com.dftaihua.dfth_threeinone.widget.NewWaveView;
//import com.dfth.sdk.Others.Utils.Logger.Logger;
//import com.dfth.sdk.device.DfthDevice;
//import com.dfth.sdk.device.DfthTwelveECGDevice;
//import com.dfth.sdk.dispatch.DfthCallBack;
//import com.dfth.sdk.dispatch.DfthResult;
//import com.dfth.sdk.listener.DfthTwelveDeviceDataListener;
//import com.dfth.sdk.model.ecg.ECGResult;
//import com.dfth.sdk.model.ecg.EcgDataTransmitted;
//import com.dfth.sdk.permission.DfthPermissionManager;
//
//import java.util.HashMap;
//
///*    *****      *     *
//**    *    *      *   *
//**    *****         *
//**    *   *         *
//**    *    *        *
//**    *     *       *
//* 创建时间：2016/12/16 16:34
//*/
//public class TwelveECGMeasureFragment extends BaseECGFragment implements IntermediaryInterface, LeaderRect.OnCheckListener, DfthTwelveDeviceDataListener, View.OnClickListener, NewWaveView.WaveRectChangeListener {
//    private static final String TAG = "TwelveECGMeasureFragment";
//    private BottomControlView mBottomView;
//    private NewWaveView mWaveView;
//    private RelativeLayout mContentView;
//    private LinearLayout mStartLL;
//    private LinearLayout mChangeLeadLL;
//    private LeaderRect[] mLeaderRects = new LeaderRect[12];
//    private LinearLayout mLeaderRectLl;
//    private TextView mSymptomText;
//    private DfthDevice mDevice;
//    private boolean mConnected;
//    private long mTime;
//    private Dialog mConnectDialog;
//    private HashMap<String, Object> mDeviceMap;
//    private Context mContext;
//    private BottomControlView mHorizontalBottomView;
//    private ScrollView mScrollView;
//    private Dialog mDataDialog;
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_twelve_measure, null);
//        mContext = getActivity();
//        mContentView = (RelativeLayout) view.findViewById(R.id.rl_twelve_measure);
//        mDevice = DfthDeviceManager.getInstance().getDevice(Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG);
//        RelativeLayout.LayoutParams paramRl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        mWaveView = new NewWaveView(getActivity());
//        mWaveView.setCurrentViewPattern(true);
//        mContentView.addView(mWaveView);
//        paramRl.bottomMargin = DisplayUtils.dip2px(mContext, 80);
//
//        mConnectDialog = DialogFactory.getLoadingDialog((Activity) mContext, "正在连接...","");
//        //底部状态栏
//        mBottomView = new BottomControlView(mContext, this);
//        mBottomView.setId(R.id.measure_bottom);
//        mBottomView.setBackgroundResource(R.drawable.twelve_un_measure_bottom_bg);
//        paramRl = new RelativeLayout.LayoutParams(0, 0);
//        paramRl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        paramRl.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        paramRl.height = ThreeInOneApplication.getScreenWidth() * 221 / 749;
//        mContentView.addView(mBottomView, paramRl);
//
//        //横屏底部状态栏
//        mHorizontalBottomView = new BottomControlView(mContext, this);
//        mHorizontalBottomView.setId(R.id.horizontal_measure_bottom);
//        mHorizontalBottomView.setBackgroundResource(R.color.measure_start_bg);
//        mHorizontalBottomView.setVisibility(View.GONE);
//        paramRl = new RelativeLayout.LayoutParams(0, 0);
//        paramRl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        paramRl.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        paramRl.height = DisplayUtils.dip2px(getActivity(),80);
//        mContentView.addView(mHorizontalBottomView, paramRl);
//
//
//        //左侧选择导联
//
//        mScrollView = new ScrollView(mContext);
//        mScrollView.setVisibility(View.GONE);
//        paramRl = new RelativeLayout.LayoutParams(0, 0);
//        paramRl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        paramRl.addRule(RelativeLayout.ABOVE, R.id.measure_leader);
//        paramRl.width = ThreeInOneApplication.getScreenWidth() / 5;
//        paramRl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        mContentView.addView(mScrollView, paramRl);
//
//        mLeaderRectLl = new LinearLayout(mContext);
////        mLeaderRectLl.setVisibility(View.GONE);
//        mLeaderRectLl.setOrientation(LinearLayout.VERTICAL);
//        paramRl = new RelativeLayout.LayoutParams(0, 0);
//        paramRl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        paramRl.addRule(RelativeLayout.ABOVE, R.id.measure_leader);
//        paramRl.width = ThreeInOneApplication.getScreenWidth() / 5;
//        paramRl.height = ThreeInOneApplication.getScreenHeight();
//        mScrollView.addView(mLeaderRectLl, paramRl);
//        addLeaders();
//
//
//        //出现症状
//        mSymptomText = new TextView(mContext);
//        paramRl = new RelativeLayout.LayoutParams(0, 0);
//        paramRl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        paramRl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        paramRl.bottomMargin = DisplayUtils.dip2px(mContext, 100);
//        paramRl.rightMargin = DisplayUtils.dip2px(mContext, 20);
//        paramRl.addRule(RelativeLayout.ABOVE, R.id.measure_leader);
//        paramRl.width = DisplayUtils.dip2px(mContext, 80);
//        paramRl.height = DisplayUtils.dip2px(mContext, 80);
//        mSymptomText.setText(R.string.symptoms);
//        mSymptomText.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
//        mSymptomText.setVisibility(View.GONE);
//        mSymptomText.setGravity(Gravity.CENTER);
//        mSymptomText.setId(R.id.symptom_text);
//        mSymptomText.setBackgroundResource(R.drawable.measure_symptom_bg);
//        mContentView.addView(mSymptomText, paramRl);
//
//        mSymptomText.setOnClickListener(this);
//
//        mDeviceMap = new HashMap<>();
//        mDeviceMap.put(Constant.DFTH_DEVICE_KEY, Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG);
//        DfthDeviceManager.getInstance().bindTwelveDatalistener(Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG, this);
//        if (mDevice != null && mDevice.getDeviceState() == DfthDevice.MEASURING) {
//            mWaveView.startDraw();
//        }
//
//        mWaveView.setWaveRectChangeListener(this);
//        return view;
//    }
//
//
//    private void addLeaders() {
//        for (int i = 0; i < 12; i++) {
//            mLeaderRects[i] = new LeaderRect(getActivity(), i);
//            mLeaderRects[i].setText(mWaveView.leaderNames[i]);
//            mLeaderRects[i].setChecked(mWaveView.leaders[i]);
//            mLeaderRects[i].setCheckChangeListener(this);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
//            params.width = ThreeInOneApplication.getScreenWidth() / 5;
//            // TODO: 2016/6/16
//            params.height = (ThreeInOneApplication.getScreenHeight() - (ThreeInOneApplication.getScreenWidth() * 152 * 221 / 749 / 212 + DisplayUtils.dip2px(getContext(), 40))) / 13 - 1;
//            params.bottomMargin = 2;
//            mLeaderRectLl.addView(mLeaderRects[i], params);
//        }
//    }
//
//    @Override
//    public void showLead() {
//        if (mScrollView.getVisibility() == View.VISIBLE && mLeaderRectLl.getVisibility() == View.VISIBLE) {
//            mScrollView.setVisibility(View.GONE);
//            mLeaderRectLl.setVisibility(View.GONE);
//        } else {
//            mScrollView.setVisibility(View.VISIBLE);
//            mLeaderRectLl.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void startDraw() {
//
//        int beforeDeviceType = DfthDeviceManager.getInstance().isExistsHaveMeasuringDevice();
//
//        if (beforeDeviceType != Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG && beforeDeviceType != -1) {
//            mDevice = DfthDeviceManager.getInstance().getDevice(beforeDeviceType);
//
//            if (beforeDeviceType == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
//                ToastUtils.showShort(mContext, R.string.bp_measuring);
//            } else {
//                ToastUtils.showShort(mContext, R.string.single_ecg_measuring);
//            }
//
//            return;
//        }
//
//        beforeDeviceType = DfthDeviceManager.getInstance().isExistsHaveConnecttedDevice();
//        if (beforeDeviceType != Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG && beforeDeviceType != -1) {
//            mDevice = DfthDeviceManager.getInstance().getDevice(beforeDeviceType);
//            mDevice.disconnect().asyncExecute(new DfthCallBack<Boolean>() {
//                @Override
//                public void onResponse(DfthResult<Boolean> dfthResult) {
//                    if (dfthResult.getReturnData()){
//                        autoMeasure();
//                    }
//                }
//            });
//            return;
//        }
//
//
//        mDevice = (DfthTwelveECGDevice) DfthDeviceManager.getInstance().getDevice(Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG);
//        if (mDevice != null && mDevice.getDeviceState() == DfthDevice.MEASURING) {
//            mDevice.stopMeasure().asyncExecute(new DfthCallBack<Boolean>() {
//                @Override
//                public void onResponse(DfthResult<Boolean> response) {
//                    ToastUtils.showShort(getActivity(), response.getReturnData() ? "结束测量成功" : "结束测量失败");
//                    if (response.getReturnData()) {
//                        mDataDialog = DialogFactory.getLoadingDialog((Activity) mContext,ThreeInOneApplication.getStringRes(R.string.upload_text),ThreeInOneApplication.getStringRes(R.string.upload_text_tip));
//                        mDataDialog.show();
//                        measureEnd();
//                    } else {
//                        ToastUtils.showShort(getActivity(), response.getErrorMessage());
//                    }
//                    Log.e("dfth_sdk", "deviceStop->" + response.getReturnData());
//                }
//            });
//        } else if (mDevice != null && mDevice.getDeviceState() == DfthDevice.CONNECTED) {
//            mDevice.startMeasure().asyncExecute(new DfthCallBack<Boolean>() {
//                @Override
//                public void onResponse(DfthResult<Boolean> dfthResult) {
//                    if (dfthResult.getReturnData()) {
////                        Logger.e("有数据返回");
//                        measureStart();
//                    } else {
//                        ToastUtils.showShort(getActivity(), dfthResult.getErrorMessage());
//                    }
//                }
//            });
//        } else {
//            autoMeasure();
//        }
//    }
//
//
//    private void measureEnd(){
//        mWaveView.endDraw();
//        mWaveView.reset();
//        mBottomView.setBackgroundResource(R.drawable.twelve_un_measure_bottom_bg);
//        mBottomView.setImageView(R.drawable.measure_start);
//        mBottomView.setTextView("开始");
//    }
//
//    private void measureStart(){
//        mWaveView.startDraw();
//        mBottomView.setBackgroundResource(R.drawable.twelve_measure_bottom_bg);
//        mSymptomText.setVisibility(View.VISIBLE);
//        mBottomView.setImageView(R.drawable.measure_stop);
//        mBottomView.setTextView("结束");
//    }
//
//    private void autoMeasure() {
//        if (Build.VERSION.SDK_INT >= 23 && !(DfthPermissionManager.checkBluetoothPermission() && DfthPermissionManager.checkStoragePermission())) {
//            DfthPermissionManager.assertSdkPermission(getActivity(), 100);
//            return;
//        }
//        mConnectDialog.show();
//        String mac = (String) SharePreferenceUtils.get(getActivity(), SharePreferenceConstant.DEVICE_MAC_TWELVE_ECG, "");
//        DeviceUtils.autoConnectOrMeasure(getActivity(), Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG, mac, 0, true, new DfthCallBack<Boolean>() {
//            @Override
//            public void onResponse(DfthResult<Boolean> dfthResult) {
//                if (dfthResult.getReturnData()) {
//                    Logger.e("有数据返回");
//                    mConnectDialog.dismiss();
//                    measureStart();
//                } else {
//                    mConnectDialog.dismiss();
//                    ToastUtils.showShort(getActivity(), dfthResult.getErrorMessage());
//                }
//            }
//        });
//    }
//
//    @Override
//    public boolean onCheckChange(int position, boolean checked) {
//        if (MathUtils.search(mWaveView.leaders, true, 1) && mWaveView.leaders[position]) {
//            return false;
//        }
//        mWaveView.leaders[position] = checked;
//        mWaveView.reset();
//        return true;
//    }
//
//    @Override
//    public void onLeaderStatusChanged(boolean[] booleen) {
//
//    }
//
//    @Override
//    public void onSosStatus(boolean b) {
//
//    }
//
//    @Override
//    public void onDataChanged(EcgDataTransmitted ecgDataTransmitted) {
////        Log.e(TwelveECGMeasureFragment.class.getSimpleName(), "有心电数据");
//        mWaveView.drawWave(ecgDataTransmitted.getEcgData());
//        final String time = TimeUtils.getMeasureTime(System.currentTimeMillis() - ecgDataTransmitted.getStartTime());
//        mBottomView.setRate(ecgDataTransmitted.getHeartRate());
//        mHorizontalBottomView.setRate(ecgDataTransmitted.getHeartRate());
//        mBottomView.setTime(time);
//        mHorizontalBottomView.setTime(time);
//    }
//
//    @Override
//    public void onBatteryChanged(float v) {
//
//    }
//
//    @Override
//    public void onResultData(ECGResult ecgResult) {
//
//        mSymptomText.setVisibility(View.GONE);
//        mBottomView.reset();
//        mDeviceMap.put(Constant.DFTH_RESULT_DATA, ecgResult);
//        if (mDataDialog != null && mDataDialog.isShowing())
//            mDataDialog.dismiss();
//        ActivitySkipUtils.skipAnotherActivity(mContext, EcgDetailActivity.class, mDeviceMap);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.symptom_text: {
//                DialogFactory.showBodyDiseaseSelect(mContext, true);
//            }
//            break;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        DfthDeviceManager.getInstance().unBindTwelveDatalistener(Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG, this);
//        super.onDestroy();
//    }
//
//    @Override
//    public void screenIn() {
//
//    }
//
//    @Override
//    public void screenOut() {
//
//    }
//
//    @Override
//    public void screenHScreen() {
//        mHorizontalBottomView.setVisibility(View.VISIBLE);
//        mBottomView.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void screenVScreen() {
//        mHorizontalBottomView.setVisibility(View.GONE);
//        mBottomView.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void startProcessECGResult() {
//
//    }
//}
