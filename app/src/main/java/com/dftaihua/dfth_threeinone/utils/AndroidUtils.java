package com.dftaihua.dfth_threeinone.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaActionSound;
import android.os.Build;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.HomeActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;

import java.util.List;

/**
 * Created by Administrator on 2017/4/12 0012.
 */
public class AndroidUtils {

    public static boolean judgeIsActive(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = activityManager.getRunningAppProcesses();
        if (!HomeActivity.IS_ALIVE) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : tasks) {
            if (processInfo.processName.equals("com.dftaihua.dfth_threeinone")) {
                return true;
            }
        }
        return false;
    }

    public static String getApplicationName() {
        try {
            PackageManager packageManager = ThreeInOneApplication.getInstance().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ThreeInOneApplication.getInstance().getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            return ThreeInOneApplication.getStringRes(R.string.app_name);
        }
    }

    public static String getTopActivity(){
        ActivityManager manager = (ActivityManager) ThreeInOneApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            List<ActivityManager.AppTask> tasks = manager.getAppTasks();
            for(ActivityManager.AppTask task : tasks){
                ActivityManager.RecentTaskInfo info = task.getTaskInfo();
                if(info != null){
                    ComponentName name = info.origActivity;
                    if(name != null && name.getPackageName().equals(ThreeInOneApplication.getInstance().getPackageName())){
                        return name.getClassName();
                    }
                }
            }
        }else{
            List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(100);
            for(ActivityManager.RunningTaskInfo task : tasks){
                ComponentName name = task.topActivity;
                if(name != null && name.getPackageName().equals(ThreeInOneApplication.getInstance().getPackageName())){
                    return name.getClassName();
                }
            }
        }
        return null;
    }
    public static String getProcessName(Context context){
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }
}
