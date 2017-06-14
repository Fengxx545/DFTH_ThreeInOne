package com.dftaihua.dfth_threeinone.activity;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.broadcastreceiver.AlarmBroadcastReceiver;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.fragment.BaseECGFragment;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.permission.DfthPermissionManager;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/7 16:15
*/
public class EcgMeasureActivity extends BaseActivity {
    private int mDeviceType;
    private int mMeasureMark;
    private BaseECGFragment mFragment;
    private Intent mIntent;
    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;

    @Override
    public View initView() {
        initializeBasicParams();
        initializeTitle(mDeviceType);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_ecg_measure, null);
        requestPermission();
        initializeFragment();
        initAlarm();
        getSlidMenu().setVisibility(View.GONE);
        return view;
    }

    private void initializeBasicParams(){
        Intent intent = getIntent();
        mDeviceType = intent.getIntExtra(Constant.DFTH_DEVICE_KEY, DfthDevice.SingleDevice);
        mMeasureMark = intent.getIntExtra(SharePreferenceConstant.LONG_TIME_MEASURE, -1);
    }

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= 23 && !DfthPermissionManager.checkBluetoothPermission()) {
            DfthPermissionManager.requestPermission(this, DfthPermissionManager.BLUETOOTH_PERMISSION, 100);
        }
    }

    private void initializeTitle(int deviceType){
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
        mTitleBackRes = R.drawable.arrow_back;
        if (deviceType == DfthDevice.SingleDevice) {
            mTitleNameRes = mMeasureMark == SharePreferenceConstant.LONG_TIME_MARK ? R.string.long_time_measure_text : R.string.single_ecg_btn_measure;
            mTitleNameColorRes = R.color.google_white;
            mTitleView.setBackgroundResource(R.color.single_color);
        } else {
            mTitleNameRes = R.string.twelve_ecg_btn_measure;
            mTitleNameColorRes = R.color.google_white;
            mTitleView.setBackgroundResource(R.color.twelve_color);
        }
    }

    private void initializeFragment() {
        mFragment = BaseECGFragment.createFragment(mDeviceType);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.activity_contant,mFragment);
        transaction.replace(R.id.activity_contant, mFragment);
        transaction.commitAllowingStateLoss();
    }

    private void initAlarm() {
        mIntent = new Intent(this, AlarmBroadcastReceiver.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.setAction("Action.Alarm");
        mPendingIntent = PendingIntent.getBroadcast(this, 0, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
    }

    public void setAlarmManager(){
        if(Build.VERSION.SDK_INT > 18){
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 24 * 60 * 60 * 1000, mPendingIntent);
        } else{
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 24 * 60 * 60 * 1000, mPendingIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(1);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(mFragment != null && !mFragment.onKeyUp(keyCode,event)){
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    public int getMeasureMark() {
        return mMeasureMark;
    }
}
