package com.dftaihua.dfth_threeinone.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.OrientationEventListener;

import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
/**
 * Created by Administrator on 2015-05-20.
 */
public class ScreenUtils {
    public interface ScreenOrientationChangeListener{
        public void onOrientationChange(int degree);
    }
    private static ScreenOrientation sOrientation;
    private static ScreenOrientationChangeListener sListener;
    public static void setOrientation(Activity activity, int code) {
        boolean s = SharePreferenceUtils.getBoolean(Constant.ECG.SETTINGS_ORIENTATION_KEY, true);
        if (s) {
            if (activity != null) {
                activity.setRequestedOrientation(code);
            }
        }
    }
    public static void setOrientationDelay(final Activity activity, final int code) {
        boolean s = SharePreferenceUtils.getBoolean(Constant.ECG.SETTINGS_ORIENTATION_KEY, true);
        if (s) {
            DispatchUtils.performMainThreadDelay(new DispatchTask() {
                @Override
                public void accept(Object o) throws Exception {
                    setOrientation(activity,code);
                }
            },Constant.SENSOR_CHECK1_TIME);


            if (activity != null) {
                DelayPerformMethod.getMethod().performMedthDelayStaticTime(Constant.SENSOR_CHECK1_TIME, ScreenUtils.class, "setOrientation", new Class[]{Activity.class,int.class},new Object[]{activity,code});
            }
        }
    }


    public static boolean isScreenLandscape(Context context) {

        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向

        if(ori == mConfiguration.ORIENTATION_LANDSCAPE){

//横屏
            return true;
        }else if(ori == mConfiguration.ORIENTATION_PORTRAIT){

//竖屏
            return false;
        }
        return false;
    }

    public static int getOrientation(int degree) {
        int o = 0;
        if (degree > 80 && degree < 100) {
            o = 90;
        }
        if (degree > 260 && degree < 280) {
            o = 270;
        }
        return o;
    }

    public static void enableOrientation(Context context,ScreenOrientationChangeListener listener) {
        if (sOrientation == null) {
            sOrientation = new ScreenOrientation(context);
        }
        sListener = listener;
        sOrientation.enable();
    }

    public static void disableOrientation(Context context) {
        if (sOrientation != null) {
            sOrientation.disable();
        }
        sListener = null;
    }

    public static int getOrientation() {
        return sOrientation == null ? 0 : sOrientation.orientation;
    }


    private static class ScreenOrientation extends OrientationEventListener {
        int orientation;

        private ScreenOrientation(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            this.orientation = orientation;
            if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN){
                return;
            }
//            if(orientation > 350 && orientation < 10){
//                orientation = 0;
//            }else if(orientation > 80 && orientation < 100){
//                orientation = 90;
//            }else if(orientation )

            if(sListener != null){
                sListener.onOrientationChange(orientation);
            }
        }
    }


}
