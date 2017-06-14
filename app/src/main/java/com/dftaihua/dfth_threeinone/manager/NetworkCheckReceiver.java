package com.dftaihua.dfth_threeinone.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.update.DfthUpdateCode;
import com.dfth.update.UpdateListener;
import com.dfth.update.UpdateManager;

/**
 * 检测网络类
 *
 * @author tangmingjie
 */
public class NetworkCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                initUpdate();
            }
        }
    }

    private void initUpdate() {
        Activity activity = ActivityCollector.getActivity();
        if(activity != null && !activity.isFinishing()){
            UpdateManager.handleUpdate(activity, new UpdateListener() {
                @Override
                public void onUpdateResponse(int code, String message) {
                    if (code == DfthUpdateCode.UPDATE_QUIT_APP) {
                        ActivityCollector.finishAll();
                    } else if (code == DfthUpdateCode.UPDATE_FAILED) {
                        ToastUtils.showLong(ThreeInOneApplication.getInstance(), R.string.update_failed);
                    }
                }
            });
        }
    }

    public static boolean getNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ThreeInOneApplication.
                getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else { //当前没有网络
            return false;
        }
    }

    public static int getCurrentNetworkType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ThreeInOneApplication.
                getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo.getType();
    }

}
