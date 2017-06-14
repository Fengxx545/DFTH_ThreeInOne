package com.dftaihua.dfth_threeinone.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.device.BindedDevice;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.DataCleanManager;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.BpDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.CheckedItemView;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.event.DfthMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/21 10:59
*/
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private CheckedItemView mBpSwitch;
    private CheckedItemView mBpUnit;
    private CheckedItemView mWifiUpload;
    private CheckedItemView mHelp;
    private CheckedItemView mAboutUs;
    private CheckedItemView mClearData;
    private int mUnit = 1;
    private TextView mVersionTv;
    private long mDataSize;
    private View mVoiceLine;

    public SettingActivity() {
        mTitleNameRes = R.string.title_activity_setting;
        mTitleColorRes = R.color.google_white;
        mTitleNameColorRes  = R.color.google_black;
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
        EventBus.getDefault().register(this);
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_setting, null);
        mBpSwitch = (CheckedItemView) view.findViewById(R.id.setting_voice_switch);
        mBpUnit = (CheckedItemView) view.findViewById(R.id.setting_bp_unit);
        mWifiUpload = (CheckedItemView) view.findViewById(R.id.setting_wifi_upload);
        mHelp = (CheckedItemView) view.findViewById(R.id.setting_help);
        mAboutUs = (CheckedItemView) view.findViewById(R.id.setting_about_us);
        mClearData = (CheckedItemView) view.findViewById(R.id.setting_clear_data);
        mVersionTv = (TextView) view.findViewById(R.id.setting_version);
        mVoiceLine = view.findViewById(R.id.setting_voice_switch_line);
        if (!(Boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.BP_VOICE_SWITCH, false)) {
            mBpSwitch.getItemContentTv().setBackgroundResource(R.drawable.setting_default);
        } else {
            mBpSwitch.getItemContentTv().setBackgroundResource(R.drawable.setting_select);
        }
        if (!(Boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.WIFI_UPLOAD, false)) {
            mWifiUpload.getItemContentTv().setBackgroundResource(R.drawable.setting_default);
        } else {
            mWifiUpload.getItemContentTv().setBackgroundResource(R.drawable.setting_select);
        }
        mBpSwitch.setOnClickListener(this);
        mBpUnit.setOnClickListener(this);
        mWifiUpload.setOnClickListener(this);
        mWifiUpload.setVisibility(View.GONE);
        mHelp.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);
        mClearData.setOnClickListener(this);
        try {
            boolean isVoiceDevice = BindedDevice.getBindedDevice(DfthDevice.BpDevice).isVoiceDevice();
            if(isVoiceDevice){
                mBpSwitch.setVisibility(View.VISIBLE);
                mVoiceLine.setVisibility(View.VISIBLE);
            } else{
                mBpSwitch.setVisibility(View.GONE);
                mVoiceLine.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void initData() {
        StringBuilder builder = new StringBuilder();
        builder.append(ThreeInOneApplication.getStringRes(R.string.setting_version))
                .append("V").append(ThreeInOneApplication.getAppVersionName())
                .append("_").append(ThreeInOneApplication.getStringRes(R.string.app_version));
        mVersionTv.setText(builder.toString());
//        mVersionTv.setText(getString(R.string.setting_version, "V", ThreeInOneApplication.getAppVersionName(),
//                "_", R.string.app_version));
        mUnit = (int) SharePreferenceUtils.get(this, SharePreferenceConstant.BP_VALUE_UNIT, 1);
        setUnitText();
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        DfthSDKManager.getManager().getStorage().getECGFilesSize(userId).asyncExecute(new DfthCallBack<Long>() {
            @Override
            public void onResponse(DfthResult<Long> dfthResult) {
                if (dfthResult.getReturnData() != null) {
                    mDataSize = dfthResult.getReturnData();
                    mClearData.setDataSize(DataCleanManager.getFormatSize(mDataSize));
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DfthMessageEvent event){
        if(event.getEventName().equals(EventNameMessage.CLEAR_DATA_SUCCESS)){
            String userId = UserManager.getInstance().getDefaultUser().getUserId();
            DfthSDKManager.getManager().getStorage().getECGFilesSize(userId).asyncExecute(new DfthCallBack<Long>() {
                @Override
                public void onResponse(DfthResult<Long> dfthResult) {
                    if (dfthResult.getReturnData() != null) {
                        mDataSize = dfthResult.getReturnData();
                        mClearData.setDataSize(DataCleanManager.getFormatSize(mDataSize));
                    } else{
                        mClearData.setDataSize(ThreeInOneApplication.getStringRes(R.string.setting_0KB));
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_voice_switch: {
                DfthBpDevice bpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
                if (bpDevice != null) {
                    if(!BpDeviceUtils.isBpDeviceConnected()){
                        ToastUtils.showShort(this, R.string.setting_connect_device_first);
                        return;
                    }
                    if (SharePreferenceUtils.get(this, SharePreferenceConstant.BP_VOICE_SWITCH, false) != null) {
                        boolean isOpen = (Boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.BP_VOICE_SWITCH, false);
                        if (!isOpen) {
                            bpDevice.openVoice().asyncExecute(new DfthCallBack<Boolean>() {
                                @Override
                                public void onResponse(DfthResult<Boolean> response) {
                                    if (response.getReturnData()) {
                                        SharePreferenceUtils.put(SettingActivity.this, SharePreferenceConstant.BP_VOICE_SWITCH, true);
                                        mBpSwitch.getItemContentTv().setBackgroundResource(R.drawable.setting_select);
                                    }
                                }
                            });
                        } else {
                            bpDevice.closeVoice().asyncExecute(new DfthCallBack<Boolean>() {
                                @Override
                                public void onResponse(DfthResult<Boolean> response) {
                                    if (response.getReturnData()) {
                                        SharePreferenceUtils.put(SettingActivity.this, SharePreferenceConstant.BP_VOICE_SWITCH, false);
                                        mBpSwitch.getItemContentTv().setBackgroundResource(R.drawable.setting_default);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    ToastUtils.showShort(this, R.string.setting_no_bp_device);
                }
            }
            break;
            case R.id.setting_bp_unit: {
                showUnitDialog();
            }
            break;
            case R.id.setting_wifi_upload: {
                if (!(Boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.WIFI_UPLOAD, false)) {
                    SharePreferenceUtils.put(this, SharePreferenceConstant.WIFI_UPLOAD, true);
                    mWifiUpload.getItemContentTv().setBackgroundResource(R.drawable.setting_select);
                } else {
                    SharePreferenceUtils.put(this, SharePreferenceConstant.WIFI_UPLOAD, false);
                    mWifiUpload.getItemContentTv().setBackgroundResource(R.drawable.setting_default);
                }
            }
            break;
            case R.id.setting_help: {
                ActivitySkipUtils.skipAnotherActivity(this,HelpActivity.class);
            }
            break;
            case R.id.setting_about_us: {
                ActivitySkipUtils.skipAnotherActivity(this,AboutActivity.class);
            }
            break;
            case R.id.setting_clear_data: {
                if (!DfthDeviceManager.getInstance().isHaveMeasuringDevice()){
                    Dialog dialog = DialogFactory.getClearDataDialog(this);
                    dialog.show();
                }else{
                    ToastUtils.showShort(this,R.string.stop_measure_clean);
                }
            }
            break;
        }
    }

    public void showUnitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] genders = new String[2];
        genders[0] = ThreeInOneApplication.getStringRes(R.string.setting_mmHg);
        genders[1] = ThreeInOneApplication.getStringRes(R.string.setting_kPa);
        builder.setSingleChoiceItems(genders, mUnit - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUnit = which == 0 ? Constant.BP_UNIT_MMHG : Constant.BP_UNIT_KPA;
                SharePreferenceUtils.put(SettingActivity.this, SharePreferenceConstant.BP_VALUE_UNIT, mUnit);
                setUnitText();
                dialog.dismiss();
                EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.BP_UNIT_CHANGE));
            }
        });
        builder.create().show();
    }

    private void setUnitText(){
        mBpUnit.setContentText(mUnit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
