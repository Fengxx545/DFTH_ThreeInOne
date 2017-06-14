package com.dftaihua.dfth_threeinone.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.HomeActivity;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.sdk.device.DfthDevice;

/**
 * Created by Administrator on 2017/1/17 0017.
 */
public class DevicePopup extends PopupWindow implements View.OnClickListener, View.OnLongClickListener {
    private Context mContext;
    private Rect mRect = new Rect();                 //实例化一个矩形
    private final int[] mLocation = new int[2];      //坐标的位置（x、y）
    private TextView mBloodTv;
    private TextView mSingleEcgTv;
    private TextView mTwelveEcgTv;
    private ImageView mBloodIv;
    private ImageView mSingleIv;
    private ImageView mTwelveIv;
    //    private ImageView mCloseIv;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;

    public DevicePopup(Context context, int width, int height) {
        mContext = context;
        setFocusable(true);     //设置可以获得焦点
        setTouchable(true);     //设置弹窗内可点击
        setOutsideTouchable(true); //设置弹窗外可点击
        setWidth(width);   //设置弹窗的宽度
        setHeight(height); //设置弹窗的高度
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.popup_anim_style);
        setContentView(LayoutInflater.from(mContext).inflate(R.layout.view_popup_plus, null));
        initView();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                hideTurc();
            }
        });
    }

    private void initView() {
        mBloodTv = (TextView) getContentView().findViewById(R.id.view_popup_plus_blood_tv);
        mSingleEcgTv = (TextView) getContentView().findViewById(R.id.view_popup_plus_single_ecg_tv);
        mTwelveEcgTv = (TextView) getContentView().findViewById(R.id.view_popup_plus_twelve_ecg_tv);
//        mCloseIv = (ImageView) getContentView().findViewById(R.id.view_popup_plus_close_iv);
        mBloodIv = (ImageView) getContentView().findViewById(R.id.view_popup_plus_blood_iv);
        mSingleIv = (ImageView) getContentView().findViewById(R.id.view_popup_plus_single_ecg_iv);
        mTwelveIv = (ImageView) getContentView().findViewById(R.id.view_popup_plus_twelve_ecg_iv);
        mBloodTv.setOnClickListener(this);
        mBloodTv.setOnLongClickListener(this);
        mSingleEcgTv.setOnClickListener(this);
        mSingleEcgTv.setOnLongClickListener(this);
        mTwelveEcgTv.setOnClickListener(this);
        mTwelveEcgTv.setOnLongClickListener(this);
//        mCloseIv.setOnClickListener(this);
        refreshView();
    }

    public void refreshView() {
        if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_BLOOD, false)) {
            mBloodIv.setImageResource(R.drawable.popup_blood_bind);
        }
        if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_SINGLE_ECG, false)) {
            mSingleIv.setImageResource(R.drawable.popup_single_ecg_bind);
        }
        if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_TWELVE_ECG, false)) {
            mTwelveIv.setImageResource(R.drawable.popup_twelve_ecg_bind);
        }
    }

    /**
     * 显示弹窗列表界面
     */
    public void show(View view) {
        view.getLocationOnScreen(mLocation);  //获得点击屏幕的位置坐标
        //设置矩形的大小
        mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(), mLocation[1] + view.getHeight());
        //显示弹窗的位置
//        showAtLocation(view, mPopupGravity, mScreenWidth - 10 - (getWidth() / 2), mRect.bottom / 3);
        int padding = DisplayUtils.dip2px(getContentView().getContext(), 110);
        showAtLocation(view, Gravity.BOTTOM, padding + 10, padding - 20);
        showTurc();
    }

    private void showTurc() {
        WindowManager.LayoutParams params = ((HomeActivity) mContext).getWindow().getAttributes();
        params.alpha = 1f;
        ((HomeActivity) mContext).getWindow().setAttributes(params);
    }

    private void hideTurc() {
        WindowManager.LayoutParams params = ((HomeActivity) mContext).getWindow().getAttributes();
        params.alpha = 1f;
        ((HomeActivity) mContext).getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.view_popup_plus_blood_tv:
                if (judgeDeviceMeasuring()) {
                    return;
                }
                if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_BLOOD, false)) {
                    Dialog dialog = DialogFactory.getBindDeviceDialog((HomeActivity) getContentView().getContext(), DfthDevice.BpDevice);
                    dialog.show();
                } else {
//                    if (mLongListener != null) {
//                        mLongListener.onItemLongClick(DfthDevice.BpDevice);
//                        dismiss();
//                    }
                    if(mListener != null){
                        mListener.onItemClick(DfthDevice.BpDevice);
                    }
                }
                break;
            case R.id.view_popup_plus_single_ecg_tv:
                if (judgeDeviceMeasuring()) {
                    return;
                }
                if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_SINGLE_ECG, false)) {
                    Dialog dialog = DialogFactory.getBindDeviceDialog((HomeActivity) getContentView().getContext(), DfthDevice.SingleDevice);
                    dialog.show();
                } else {
//                    if (mLongListener != null) {
//                        mLongListener.onItemLongClick(DfthDevice.SingleDevice);
//                        dismiss();
//                    }
                    if(mListener != null){
                        mListener.onItemClick(DfthDevice.SingleDevice);
                    }
                }
                break;
            case R.id.view_popup_plus_twelve_ecg_tv:
                if (judgeDeviceMeasuring()) {
                    return;
                }
                if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_TWELVE_ECG, false)) {
                    Dialog dialog = DialogFactory.getBindDeviceDialog((HomeActivity) getContentView().getContext(), DfthDevice.EcgDevice);
                    dialog.show();
                } else {
//                    if (mLongListener != null) {
//                        mLongListener.onItemLongClick(DfthDevice.EcgDevice);
//                        dismiss();
//                    }
                    if(mListener != null){
                        mListener.onItemClick(DfthDevice.EcgDevice);
                    }
                }
                break;
        }
    }

    private boolean judgeDeviceMeasuring() {
        if (DeviceUtils.isDeviceMeasuring(DfthDevice.BpDevice)) {
            ToastUtils.showLong(getContentView().getContext(), R.string.device_popup_bp_measuring);
            return true;
        } else if (DeviceUtils.isDeviceMeasuring(DfthDevice.SingleDevice)) {
            ToastUtils.showLong(getContentView().getContext(), R.string.device_popup_single_measuring);
            return true;
        } else if (DeviceUtils.isDeviceMeasuring(DfthDevice.EcgDevice)) {
            ToastUtils.showLong(getContentView().getContext(), R.string.device_popup_ecg_measuring);
            return true;
        }
        if (DeviceUtils.isDeviceReconnect(DfthDevice.SingleDevice)) {
            ToastUtils.showLong(getContentView().getContext(), R.string.device_popup_single_measuring);
            return true;
        }
        if (DeviceUtils.isDeviceReconnect(DfthDevice.EcgDevice)) {
            ToastUtils.showLong(getContentView().getContext(), R.string.device_popup_ecg_measuring);
            return true;
        }
        return false;
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setLongListener(OnItemLongClickListener listener) {
        mLongListener = listener;
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.view_popup_plus_blood_tv:
                if (judgeDeviceMeasuring()) {
                    return false;
                }
                if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_BLOOD, false)) {
                    Dialog dialog = DialogFactory.getBindDeviceDialog((HomeActivity) getContentView().getContext(), DfthDevice.BpDevice);
                    dialog.show();
                } else {
                    if (mLongListener != null) {
                        mLongListener.onItemLongClick(DfthDevice.BpDevice);
                        dismiss();
                    }
                }
                break;
            case R.id.view_popup_plus_single_ecg_tv:
                if (judgeDeviceMeasuring()) {
                    return false;
                }
                if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_SINGLE_ECG, false)) {
                    Dialog dialog = DialogFactory.getBindDeviceDialog((HomeActivity) getContentView().getContext(), DfthDevice.SingleDevice);
                    dialog.show();
                } else {
                    if (mLongListener != null) {
                        mLongListener.onItemLongClick(DfthDevice.SingleDevice);
                        dismiss();
                    }
                }
                break;
            case R.id.view_popup_plus_twelve_ecg_tv:
                if (judgeDeviceMeasuring()) {
                    return false;
                }
                if ((Boolean) SharePreferenceUtils.get(getContentView().getContext(), SharePreferenceConstant.DEVICE_TWELVE_ECG, false)) {
                    Dialog dialog = DialogFactory.getBindDeviceDialog((HomeActivity) getContentView().getContext(), DfthDevice.EcgDevice);
                    dialog.show();
                } else {
                    if (mLongListener != null) {
                        mLongListener.onItemLongClick(DfthDevice.EcgDevice);
                        dismiss();
                    }
                }
                break;
        }
        return false;
    }

    public interface OnItemClickListener {
        void onItemClick(int deviceType);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int deviceType);
    }

}
