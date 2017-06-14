package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dftaihua.dfth_threeinone.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class ConnectDialogView extends RelativeLayout implements View.OnClickListener{
    private ImageView mDeviceIv;
    private ImageView mHelpIv;
    private ConnectAnimation mAnimation;
    private Button mCancelBtn;
    private Button mReConnectBtn;
    private Button mNoDeviceCancelBtn;
    private LinearLayout mSearchLl;
    private LinearLayout mNoDeviceLl;
    private OnCancelListener mListener;

    public ConnectDialogView(Context context) {
        this(context, null);
    }

    public ConnectDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.dialog_connect, this, true);
        mAnimation = (ConnectAnimation) findViewById(R.id.dialog_connect_animation);
        mDeviceIv = (ImageView) findViewById(R.id.dialog_connect_device_iv);
        mHelpIv = (ImageView) findViewById(R.id.dialog_connect_help_iv);
        mSearchLl = (LinearLayout) findViewById(R.id.dialog_connect_search_ll);
        mNoDeviceLl = (LinearLayout) findViewById(R.id.dialog_connect_no_device_ll);
        mCancelBtn = (Button) findViewById(R.id.dialog_connect_cancel_btn);
        mReConnectBtn = (Button) findViewById(R.id.dialog_connect_no_device_reconnect_btn);
        mNoDeviceCancelBtn = (Button) findViewById(R.id.dialog_connect_no_device_cancel_btn);
        mCancelBtn.setOnClickListener(this);
        mReConnectBtn.setOnClickListener(this);
        mNoDeviceCancelBtn.setOnClickListener(this);
//        int res;
//        res = deviceName.equals(BluetoothCreateManager.SINGLEECG) ? R.drawable.measure_device_single : R.drawable.measure_device;
//        mDeviceIv.setImageResource(res);
        startAnimation();
    }

    public void setImageResource(int resId){
        mHelpIv.setVisibility(GONE);
        mDeviceIv.setVisibility(VISIBLE);
        mAnimation.setAnimationType(ConnectAnimation.ANIMATION_DEVICE);
        mDeviceIv.setImageResource(resId);
    }

    public void findNoDevice(){
        mSearchLl.setVisibility(GONE);
        mNoDeviceLl.setVisibility(VISIBLE);
    }

    public void startAnimation() {
        mAnimation.startAnimation();
    }

    public void setListener(OnCancelListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_connect_cancel_btn:
//                mAnimation.cancelAnimation();
                mListener.onCancel();
                break;
            case R.id.dialog_connect_no_device_reconnect_btn:
                mSearchLl.setVisibility(VISIBLE);
                mNoDeviceLl.setVisibility(GONE);
                mListener.onReconnect();
                break;
            case R.id.dialog_connect_no_device_cancel_btn:
                mListener.onCancel();
                break;
        }
    }

    public interface OnCancelListener{
        void onCancel();

        void onReconnect();
    }
}
