package com.dftaihua.dfth_threeinone.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.widget.ConnectDialogView;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.event.DfthMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class ConnectDialog extends Dialog implements ConnectDialogView.OnCancelListener{

    private static ConnectDialog dialog = null;
    private ConnectDialogView mDialogView;
    private int mDeviceType = DfthDevice.Unknown;

    public ConnectDialog(Context context,int deviceType) {
        super(context,R.style.connect_dialog_style);
        mDeviceType = deviceType;
        EventBus.getDefault().register(this);
        initialize();
    }

    public static ConnectDialog getDialog(Context context,int deviceType){
        if(dialog == null){
            return new ConnectDialog(context,deviceType);
        }
        return dialog;
    }

    private void initialize() {
        LinearLayout connectLl = new LinearLayout(getContext());
        connectLl.setOrientation(LinearLayout.VERTICAL);
        connectLl.setBackgroundResource(R.color.connect_dialog_back);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mDialogView = new ConnectDialogView(getContext());
        mDialogView.setListener(this);
        connectLl.addView(mDialogView,params);

        int height = ThreeInOneApplication.getScreenHeight();
        int width = ThreeInOneApplication.getScreenWidth();
        setContentView(connectLl, new LinearLayout.LayoutParams(width, height));

        if(mDeviceType != DfthDevice.Unknown){
            changViewByDeviceType(mDeviceType);
        }
    }

    public void changViewByDeviceType(int deviceType){
        switch (deviceType){
            case DfthDevice.BpDevice:
                setDeviceImageRes(R.drawable.dialog_device_blood);
                break;
            case DfthDevice.SingleDevice:
                setDeviceImageRes(R.drawable.dialog_device_single_ecg);
                break;
            case DfthDevice.EcgDevice:
                setDeviceImageRes(R.drawable.dialog_device_twelve_ecg);
                break;
        }
    }

    public void findNoDevice(){
        mDialogView.findNoDevice();
    }

    public void setDeviceImageRes(int res){
        mDialogView.setImageResource(res);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DfthMessageEvent event){
        if (event.getEventName().equals(EventNameMessage.DEVICE_DISCOVER)) {
            changViewByDeviceType((Integer) event.getData());
        }
    }

    @Override
    public void onCancel() {
        dismiss();
    }

    @Override
    public void onReconnect() {
        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.DEVICE_RECONNECT));
    }

    @Override
    public void dismiss() {
        super.dismiss();
        DeviceUtils.cancel();
        EventBus.getDefault().unregister(this);
    }
}
