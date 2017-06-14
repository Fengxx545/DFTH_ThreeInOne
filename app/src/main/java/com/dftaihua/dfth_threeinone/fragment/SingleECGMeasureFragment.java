//package com.dftaihua.dfth_threeinone.fragment;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.dftaihua.dfth_threeinone.R;
//import com.dftaihua.dfth_threeinone.activity.EcgDetailActivity;
//import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
//import com.dftaihua.dfth_threeinone.constant.Constant;
//import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
//import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
//import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
//import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
//import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
//import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
//import com.dftaihua.dfth_threeinone.utils.TimeUtils;
//import com.dftaihua.dfth_threeinone.utils.ToastUtils;
//import com.dftaihua.dfth_threeinone.widget.BpmLinearLayout;
//import com.dftaihua.dfth_threeinone.widget.NewWaveView;
//import com.dfth.sdk.Others.Utils.Logger.Logger;
//import com.dfth.sdk.device.DfthDevice;
//import com.dfth.sdk.dispatch.DfthCallBack;
//import com.dfth.sdk.dispatch.DfthResult;
//import com.dfth.sdk.listener.DfthSingleDeviceDataListener;
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
//@SuppressLint("ValidFragment")
//public class SingleECGMeasureFragment extends BaseECGFragment implements NewWaveView.WaveRectChangeListener, View.OnClickListener, DfthSingleDeviceDataListener {
//
//    private NewWaveView mWaveView;
//    private BpmLinearLayout mBpmView;
//    private LinearLayout mStartLL;
//    private DfthDevice mDevice;
//    private boolean mConnectted;
//    private Dialog mConnectDialog;
//    private Dialog mDataDialog;
//    private HashMap<String, Object> mDeviceMap;
//    private Context mContext;
//    private int mMeasureMark;
//    private LinearLayout mHorizontalStartLL;
//    private TextView mHorizontalRate;
//    private TextView mHorizontalMeasureTime;
//    private ImageView mStartImage;
//
//    public SingleECGMeasureFragment(int mark) {
//        mMeasureMark = mark;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mContext = getActivity();
//        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_single_ecg_measure, null);
//        mDevice = DfthDeviceManager.getInstance().getDevice(Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG);
//        mWaveView = (NewWaveView) view.findViewById(R.id.wave_view);
//        mBpmView = (BpmLinearLayout) view.findViewById(R.id.ecg_bpm);
//        mStartLL = (LinearLayout) view.findViewById(R.id.single_measure_bottom);
//        mHorizontalStartLL = (LinearLayout) view.findViewById(R.id.single_measure_bottom_horizontal);
//        mHorizontalRate = (TextView) view.findViewById(R.id.h_show_beat);
//        mHorizontalMeasureTime = (TextView) view.findViewById(R.id.h_show_measure_time);
//        mStartImage = (ImageView) view.findViewById(R.id.ecg_pause_image);
//
//
//        if (mMeasureMark == SharePreferenceConstant.LONG_TIME_MARK) {
////            mStartLL.setVisibility(View.GONE);
//            DialogFactory.getTipDialog((Activity) mContext,ThreeInOneApplication.getStringRes(R.string.long_time_tip)).show();
//        }else{
//            mStartLL.setVisibility(View.VISIBLE);
//        }
//        mWaveView.setCurrentViewPattern(true);
//        mStartLL.setOnClickListener(this);
//        mHorizontalStartLL.setOnClickListener(this);
//        mWaveView.setWaveRectChangeListener(this);
//        DfthDeviceManager.getInstance().bindSingleDatalistener(Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG, this);
//
//        if (mDevice != null && mDevice.getDeviceState() == DfthDevice.MEASURING) {
//            measureStart();
//        }
//        mConnectDialog = DialogFactory.getLoadingDialog(getActivity(), "正在连接...","");
//        mDeviceMap = new HashMap<>();
//        mDeviceMap.put(Constant.DFTH_DEVICE_KEY, Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG);
//
//        return view;
//    }
//
//    @Override
//    public void screenIn() {
//        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//            mBpmView.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void screenOut() {
//        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//            mBpmView.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void screenHScreen() {
//        mStartLL.setVisibility(View.GONE);
//        mBpmView.setVisibility(View.GONE);
//        mHorizontalStartLL.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void screenVScreen() {
//        mStartLL.setVisibility(View.VISIBLE);
//        mBpmView.setVisibility(View.VISIBLE);
//        mHorizontalStartLL.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (mWaveView != null) {
//            mWaveView.onHiddenChanged(hidden);
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        switch (id) {
//
//            case R.id.single_measure_bottom_horizontal: {
//                int beforeDeviceType = DfthDeviceManager.getInstance().isExistsHaveMeasuringDevice();
//                if (beforeDeviceType != Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG && beforeDeviceType != -1) {
//                    mDevice = DfthDeviceManager.getInstance().getDevice(beforeDeviceType);
//                    if (mDevice.getDeviceState() == DfthDevice.MEASURING) {
//                        if (beforeDeviceType == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
//                            ToastUtils.showShort(mContext, R.string.bp_measuring);
//                        } else {
//                            ToastUtils.showShort(mContext, R.string.twelve_ecg_measuring);
//                        }
//                    }
//                    break;
//                }
//
//                beforeDeviceType = DfthDeviceManager.getInstance().isExistsHaveConnecttedDevice();
//                if (beforeDeviceType != Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG && beforeDeviceType != -1) {
//                    mDevice = DfthDeviceManager.getInstance().getDevice(beforeDeviceType);
//                    if (mDevice.getDeviceState() == DfthDevice.CONNECTED) {
//                        mDevice.disconnect().asyncExecute(new DfthCallBack<Boolean>() {
//                            @Override
//                            public void onResponse(DfthResult<Boolean> dfthResult) {
//                                if (dfthResult.getReturnData()) {
//                                    autoMeasure();
//                                } else {
//                                    ToastUtils.showShort(mContext, dfthResult.getErrorMessage());
//                                }
//                            }
//                        });
//                    }
//                    break;
//                }
//
//
//                mDevice = DfthDeviceManager.getInstance().getDevice(Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG);
//                if (mDevice != null && mDevice.getDeviceState() == DfthDevice.MEASURING) {
//                    mDevice.stopMeasure().asyncExecute(new DfthCallBack<Boolean>() {
//                        @Override
//                        public void onResponse(DfthResult<Boolean> response) {
//                            ToastUtils.showShort(getActivity(), response.getReturnData() ? "结束测量成功" : "结束测量失败");
//                            if (response.getReturnData()) {
//                                mDataDialog = DialogFactory.getLoadingDialog((Activity) mContext,ThreeInOneApplication.getStringRes(R.string.upload_text),ThreeInOneApplication.getStringRes(R.string.upload_text_tip));
//                                mDataDialog.show();
//                                measureEnd();
//                            }
//                            Log.e("dfth_sdk", "deviceStop->" + response.getReturnData());
//                        }
//                    });
//                } else if (mDevice != null && mDevice.getDeviceState() == DfthDevice.CONNECTED) {
//                    mDevice.startMeasure().asyncExecute(new DfthCallBack<Boolean>() {
//                        @Override
//                        public void onResponse(DfthResult<Boolean> dfthResult) {
//                            if (dfthResult.getReturnData()) {
//                                Logger.e("有数据返回");
//                                measureStart();
//                            }
//                        }
//                    });
//                } else {
//                    autoMeasure();
//                }
//            }
//            break;
//            case R.id.single_measure_bottom: {
//
//                int beforeDeviceType = DfthDeviceManager.getInstance().isExistsHaveMeasuringDevice();
//                if (beforeDeviceType != Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG && beforeDeviceType != -1) {
//                    mDevice = DfthDeviceManager.getInstance().getDevice(beforeDeviceType);
//                    if (mDevice.getDeviceState() == DfthDevice.MEASURING) {
//                        if (beforeDeviceType == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
//                            ToastUtils.showShort(mContext, R.string.bp_measuring);
//                        } else {
//                            ToastUtils.showShort(mContext, R.string.twelve_ecg_measuring);
//                        }
//                    }
//                    break;
//                }
//
//                beforeDeviceType = DfthDeviceManager.getInstance().isExistsHaveConnecttedDevice();
//                if (beforeDeviceType != Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG && beforeDeviceType != -1) {
//                    mDevice = DfthDeviceManager.getInstance().getDevice(beforeDeviceType);
//                    if (mDevice.getDeviceState() == DfthDevice.CONNECTED) {
//                        mDevice.disconnect().asyncExecute(new DfthCallBack<Boolean>() {
//                            @Override
//                            public void onResponse(DfthResult<Boolean> dfthResult) {
//                                if (dfthResult.getReturnData()) {
//                                    autoMeasure();
//                                } else {
//                                    ToastUtils.showShort(mContext, dfthResult.getErrorMessage());
//                                }
//                            }
//                        });
//                    }
//                    break;
//                }
//
//
//                mDevice = DfthDeviceManager.getInstance().getDevice(Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG);
//                if (mDevice != null && mDevice.getDeviceState() == DfthDevice.MEASURING) {
//                    mDevice.stopMeasure().asyncExecute(new DfthCallBack<Boolean>() {
//                        @Override
//                        public void onResponse(DfthResult<Boolean> response) {
//                            ToastUtils.showShort(getActivity(), response.getReturnData() ? "结束测量成功" : "结束测量失败");
//                            if (response.getReturnData()) {
//                                mDataDialog = DialogFactory.getLoadingDialog((Activity) mContext,ThreeInOneApplication.getStringRes(R.string.upload_text),ThreeInOneApplication.getStringRes(R.string.upload_text_tip));
//                                mDataDialog.show();
//                                measureEnd();
//                            }
//                            Log.e("dfth_sdk", "deviceStop->" + response.getReturnData());
//                        }
//                    });
//                } else if (mDevice != null && mDevice.getDeviceState() == DfthDevice.CONNECTED) {
//                    mDevice.startMeasure().asyncExecute(new DfthCallBack<Boolean>() {
//                        @Override
//                        public void onResponse(DfthResult<Boolean> dfthResult) {
//                            if (dfthResult.getReturnData()) {
//                                Logger.e("有数据返回");
//                                measureStart();
//                            }
//                        }
//                    });
//                } else {
//                    autoMeasure();
//                }
//                break;
//            }
//        }
//    }
//
//
//    private void measureEnd() {
//        mWaveView.endDraw();
//        mWaveView.reset();
//        mStartImage.setImageResource(R.drawable.measure_start);
//        mBpmView.cancelAnimation();
//        mBpmView.reset();
//    }
//
//
//    private void measureStart() {
//        mWaveView.startDraw();
//        mStartImage.setImageResource(R.drawable.measure_stop);
//        mBpmView.startAnimation();
//    }
//
//
//    public void autoMeasure() {
//        if (Build.VERSION.SDK_INT >= 23 && !(DfthPermissionManager.checkBluetoothPermission() && DfthPermissionManager.checkStoragePermission())) {
//            DfthPermissionManager.assertSdkPermission(getActivity(), 100);
//            return;
//        }
//        mConnectDialog.show();
//        String mac = (String) SharePreferenceUtils.get(getActivity(), SharePreferenceConstant.DEVICE_MAC_SINGLE_ECG, "");
//        DeviceUtils.autoConnectOrMeasure(getActivity(), Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG, mac, 0, true, new DfthCallBack<Boolean>() {
//            @Override
//            public void onResponse(DfthResult<Boolean> dfthResult) {
//                if (dfthResult.getReturnData()) {
//                    Logger.e("有数据返回");
//                    mConnectDialog.dismiss();
//                    mWaveView.startDraw();
//                    mStartImage.setImageResource(R.drawable.measure_stop);
//                    mBpmView.startAnimation();
//                } else {
//                    mConnectDialog.dismiss();
//                    ToastUtils.showShort(mContext, dfthResult.getErrorMessage());
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onLeaderOut(boolean b) {
//
//    }
//
//    @Override
//    public void onDataChanged(EcgDataTransmitted ecgDataTransmitted) {
////        Log.e(SingleECGMeasureFragment.class.getSimpleName(), "有心电数据");
//        Logger.e(String.valueOf(ecgDataTransmitted.getHeartRate()));
//        mWaveView.drawWave(ecgDataTransmitted.getEcgData());
//        final String time = TimeUtils.getMeasureTime(System.currentTimeMillis() - ecgDataTransmitted.getStartTime());
////        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//        mBpmView.setRate(ecgDataTransmitted.getHeartRate());
//        mBpmView.setTime(time);
////        }else {
//        mHorizontalRate.setText(ecgDataTransmitted.getHeartRate() + ThreeInOneApplication.getStringRes(R.string.bpm_unit));
//        mHorizontalMeasureTime.setText(time);
////        }
//    }
//
//    @Override
//    public void onBatteryChanged(float v) {
//
//    }
//
//    @Override
//    public void onResultData(ECGResult ecgResult) {
//        Log.e(SingleECGMeasureFragment.class.getSimpleName(), "有心电结果:" + ecgResult.toString());
//        mWaveView.endDraw();
//        mWaveView.reset();
//        mBpmView.cancelAnimation();
//        mBpmView.reset();
//        mStartImage.setImageResource(R.drawable.measure_start);
//        mDeviceMap.put(Constant.DFTH_RESULT_DATA, ecgResult);
//        if (mDataDialog != null && mDataDialog.isShowing())
//            mDataDialog.dismiss();
//        ActivitySkipUtils.skipAnotherActivity(mContext, EcgDetailActivity.class, mDeviceMap);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mWaveView.onResume();
//    }
//
//    public Dialog getDataDialog() {
//        return mDataDialog;
//    }
//
//    @Override
//    public void onDestroy() {
//        DfthDeviceManager.getInstance().unBindSingleDatalistener(Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG, this);
//        super.onDestroy();
//    }
//
//    @Override
//    public void startProcessECGResult() {
//
//    }
//}
