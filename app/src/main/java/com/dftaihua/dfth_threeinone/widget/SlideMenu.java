package com.dftaihua.dfth_threeinone.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.HomeActivity;
import com.dftaihua.dfth_threeinone.activity.LoginActivity;
import com.dftaihua.dfth_threeinone.activity.MemberInfoActivity;
import com.dftaihua.dfth_threeinone.activity.MyRelativeActivity;
import com.dftaihua.dfth_threeinone.activity.MyServiceActivity;
import com.dftaihua.dfth_threeinone.activity.ServiceRecordActivity;
import com.dftaihua.dfth_threeinone.activity.SettingActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.model.bp.BpPlan;


/**
 * Created by Administrator on 2015-06-19.
 */
public class SlideMenu extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "SlideMenu";
    private Scroller mScroller;
    private View mV;
    private boolean isOpen = false;
    private int mCurId = -1;
    private RelativeLayout mMemberInfoRl;
    private RelativeLayout mMyRelativeRl;
    private RelativeLayout mMyServiceRl;
    private RelativeLayout mServiceRecordRl;
    private RelativeLayout mSettingRl;
    private RelativeLayout mLogoutRl;
    private LinearLayout mNoDeviceLl;
    private LinearLayout mDeviceBloodLl;
    private LinearLayout mDeviceSingleEcgLl;
    private LinearLayout mDeviceTwelveEcgLl;
    private Dialog mDeviceDialog = null;

    public SlideMenu(Context context) {
        super(context);
        setWillNotDraw(false);
        mV = LayoutInflater.from(context).inflate(R.layout.view_slide_menu, null);
        LayoutParams p = new LayoutParams(0, 0);
        p.width = (int) (ThreeInOneApplication.getScreenWidth() * 0.55f);
        p.height = LayoutParams.MATCH_PARENT;
        addView(mV, p);
        setPadding(-p.width, 0, 0, 0);
//        mV.setBackgroundResource(R.color.setting_bg);
        mScroller = new Scroller(context);
        initView();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mV = LayoutInflater.from(context).inflate(R.layout.view_slide_menu, null);
        LayoutParams p = new LayoutParams(0, 0);
        p.width = (int) (ThreeInOneApplication.getScreenWidth() * 0.55f);
        p.height = LayoutParams.MATCH_PARENT;
        addView(mV, p);
        setPadding(-p.width, 0, 0, 0);
//        mV.setBackgroundResource(R.color.setting_bg);
        mScroller = new Scroller(context);
        initView();
    }

    private void initView() {
        mMemberInfoRl = (RelativeLayout) findViewById(R.id.slide_menu_member_info_rl);
        mMyRelativeRl = (RelativeLayout) findViewById(R.id.slide_menu_my_relative_rl);
        mMyServiceRl = (RelativeLayout) findViewById(R.id.slide_menu_my_service_rl);
        mServiceRecordRl = (RelativeLayout) findViewById(R.id.slide_menu_service_record_rl);
        mSettingRl = (RelativeLayout) findViewById(R.id.slide_menu_setting_rl);
        mLogoutRl = (RelativeLayout) findViewById(R.id.slide_menu_logout_rl);
        mNoDeviceLl = (LinearLayout) findViewById(R.id.slide_menu_no_device_ll);
        mDeviceBloodLl = (LinearLayout) findViewById(R.id.slide_menu_device_blood_ll);
        mDeviceSingleEcgLl = (LinearLayout) findViewById(R.id.slide_menu_device_single_ecg_ll);
        mDeviceTwelveEcgLl = (LinearLayout) findViewById(R.id.slide_menu_device_twelve_ecg_ll);
        mDeviceBloodLl.setOnClickListener(this);
        mDeviceSingleEcgLl.setOnClickListener(this);
        mDeviceTwelveEcgLl.setOnClickListener(this);
        mMemberInfoRl.setOnClickListener(this);
        mMyRelativeRl.setOnClickListener(this);
        mMyServiceRl.setOnClickListener(this);
        mServiceRecordRl.setOnClickListener(this);
        mSettingRl.setOnClickListener(this);
        mLogoutRl.setOnClickListener(this);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void open() {
        if (mScroller.isFinished()) {
            mScroller.startScroll(getPaddingLeft(), 0, mV.getWidth(), 0, 500);
            refreshView();
            invalidate();
        }
    }

    public void close() {
        if (mScroller.isFinished()) {
            mScroller.startScroll(getPaddingLeft(), 0, -mV.getWidth(), 0, 500);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            setPadding(mScroller.getCurrX(), 0, 0, 0);
            postInvalidate();
        } else {
            float xx = ThreeInOneApplication.getScreenWidth() * 0.5f;
            if (Math.abs(getPaddingLeft()) >= Math.abs(xx - 10)) {
                isOpen = false;
            } else {
                isOpen = true;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float xx = ThreeInOneApplication.getScreenWidth() * 0.5f;
        if (isOpen && x >= xx && event.getAction() == MotionEvent.ACTION_DOWN) {
            close();
            return true;
        }
        return !mScroller.isFinished() || isOpen || super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        mCurId = v.getId();
        switch (mCurId) {
            case R.id.slide_menu_member_info_rl:
                ActivitySkipUtils.skipAnotherActivity(getContext(), MemberInfoActivity.class);
                break;
            case R.id.slide_menu_my_relative_rl:
                ActivitySkipUtils.skipAnotherActivity(getContext(), MyRelativeActivity.class);
                break;
            case R.id.slide_menu_my_service_rl:
                ActivitySkipUtils.skipAnotherActivity(getContext(), MyServiceActivity.class);
                break;
            case R.id.slide_menu_service_record_rl:
                ActivitySkipUtils.skipAnotherActivity(getContext(), ServiceRecordActivity.class);
                break;
            case R.id.slide_menu_setting_rl:
                ActivitySkipUtils.skipAnotherActivity(getContext(), SettingActivity.class);
                break;
            case R.id.slide_menu_logout_rl:
                if(isExistMeasuring()) return;
                String userId = UserManager.getInstance().getDefaultUser().getUserId();
                BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
                if (plan != null && plan.getStatus() == BpPlan.STATE_RUNNING) {
                    ToastUtils.showLong(getContext(), R.string.slide_menu_logout_need_stop_plan);
                    return;
                }
                Dialog dialog = DialogFactory.getLogoutDialog((HomeActivity)getContext());
                dialog.show();
                break;
            case R.id.slide_menu_device_blood_ll:
                showDialog(DfthDevice.BpDevice);
                break;
            case R.id.slide_menu_device_single_ecg_ll:
                showDialog(DfthDevice.SingleDevice);
                break;
            case R.id.slide_menu_device_twelve_ecg_ll:
                showDialog(DfthDevice.EcgDevice);
                break;
        }
        close();
    }

    private void showDialog(int type){
        mDeviceDialog = null;
        mDeviceDialog = DialogFactory.getDeviceDialog((HomeActivity)getContext(),type);
        mDeviceDialog.show();
    }

    public void refreshView() {
        if ((Boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_BLOOD, false)
                || (Boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_SINGLE_ECG, false)
                || (Boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_TWELVE_ECG, false)) {
            mNoDeviceLl.setVisibility(GONE);
            mDeviceBloodLl.setVisibility(GONE);
            mDeviceSingleEcgLl.setVisibility(GONE);
            mDeviceTwelveEcgLl.setVisibility(GONE);
            if ((Boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_BLOOD, false)) {
                mDeviceBloodLl.setVisibility(VISIBLE);
            }
            if ((Boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_SINGLE_ECG, false)) {
                mDeviceSingleEcgLl.setVisibility(VISIBLE);
            }
            if ((Boolean) SharePreferenceUtils.get(getContext(), SharePreferenceConstant.DEVICE_TWELVE_ECG, false)) {
                mDeviceTwelveEcgLl.setVisibility(VISIBLE);
            }
        } else {
            mNoDeviceLl.setVisibility(VISIBLE);
            mDeviceBloodLl.setVisibility(GONE);
            mDeviceSingleEcgLl.setVisibility(GONE);
            mDeviceTwelveEcgLl.setVisibility(GONE);
        }
    }

    private boolean isExistMeasuring() {
        if (DeviceUtils.isDeviceMeasuring(DfthDevice.BpDevice)) {
            ToastUtils.showShort(getContext(),ThreeInOneApplication.getStringRes(R.string.slide_menu_bp_measuring));
            return true;
        } else if (DeviceUtils.isDeviceMeasuring(DfthDevice.SingleDevice)) {
            ToastUtils.showShort(getContext(),ThreeInOneApplication.getStringRes(R.string.slide_menu_single_measuring));
            return true;
        } else if (DeviceUtils.isDeviceMeasuring(DfthDevice.EcgDevice)) {
            ToastUtils.showShort(getContext(),ThreeInOneApplication.getStringRes(R.string.slide_menu_ecg_measuring));
            return true;
        }
        return false;
    }
}
