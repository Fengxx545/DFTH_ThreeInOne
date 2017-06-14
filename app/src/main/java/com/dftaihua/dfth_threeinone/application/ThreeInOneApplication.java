package com.dftaihua.dfth_threeinone.application;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.dftaihua.dfth_threeinone.BuildConfig;
import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.EcgDetailActivity;
import com.dftaihua.dfth_threeinone.manager.BpMeasurePlanManager;
import com.dftaihua.dfth_threeinone.push.PushHandle;
import com.dftaihua.dfth_threeinone.service.DfthKeepForegroundService;
import com.dftaihua.dfth_threeinone.utils.AndroidUtils;
import com.dftaihua.dfth_threeinone.widget.Toast;
import com.dfth.push.DfthPushConfig;
import com.dfth.push.DfthPushManager;
import com.dfth.sdk.DfthSDKConfig;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.DfthSdkCallBack;
import com.dfth.sdk.Others.Utils.CrashHandler;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.update.DfthUpdateSdk;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;
import java.util.Locale;


/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class ThreeInOneApplication extends MultiDexApplication{

    private static ThreeInOneApplication mApplication;
    private String mUserId;
    private List<String> mSingleEcgMacList;      //单道心电
    private List<String> mTwelvEcgMacList;       //12心电
    private List<String> mBpMacList;             //血压设备
    private List<String> mPrinterMacList;        //打印机

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initAll();
    }

    private void initAll(){
        String processName = AndroidUtils.getProcessName(this);
        if(getPackageName().equals(processName)){
            initSDK();
            showStartupLog();
            BpMeasurePlanManager.getManager(this);
            DfthUpdateSdk.init(this);
            initDfthPushManager();
            initImageManager();
            initKeepService();
            Toast.init(this);
            initCrash();
        }
    }


    private void initKeepService(){
        Intent intent = new Intent();
        intent.setClassName(this, DfthKeepForegroundService.class.getName());
        startService(intent);
    }

    public static ThreeInOneApplication getInstance(){
        if(mApplication == null){
            mApplication = new ThreeInOneApplication();
        }
        return mApplication;
    }

    //初始化图片管理
    private void initImageManager(){
        ImageLoaderConfiguration configuration =
                ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }

    private void initSDK(){
        DfthSDKConfig config = DfthSDKConfig.getFormalConfig();
        config.setClientId(BuildConfig.clientId);
        config.setClientSecret(BuildConfig.serectId);
        config.setbaseUrl(BuildConfig.baseUrl);
        config.setDebugEnable(BuildConfig.log_enable);
        config.setPoint(true).
                setPointAppId("c1969a2c222c4dcfa2ce0e4b26416a84").
                setPointChannelId("123");
        DfthSDKManager.initWithConfig(config);
        DfthSDKManager.getManager().onInit(this);
        DfthSDKManager.getManager().oauth(new DfthSdkCallBack() {
            @Override
            public void onInitResponse(boolean success, String accessToken) {
            }
        });
    }

    private void initCrash(){
        if(!isApkDebug(this,getPackageName())){
            CrashHandler.getInstance().init(this);
        }
    }
    public static boolean isApkDebug(Context context,String packageName) {
        try {
            PackageInfo pkginfo = context.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_ACTIVITIES);
            ApplicationInfo info= pkginfo.applicationInfo;
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {

        }
        return false;
    }

    private void initDfthPushManager(){
        DfthPushConfig config = new DfthPushConfig().setAppIcon(R.drawable.logo_icon)
                .setMainActivity(EcgDetailActivity.class).setPackageName(getPackageName()).setForeGroundNotify(true);
        config.setIsUpdateServer(true);
        DfthPushManager.initPush(this, config);
        PushHandle.start(this);
    }

    private void showStartupLog(){
        String appName = getResources().getString(R.string.app_name);
        Logger.e(appName + " started...");
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            Logger.e("SDK version = " + DfthSDKManager.getManager().getSDKVersion());
            Logger.e("Application version code = " + info.versionCode);
            Logger.e("Application version name = " + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
//            Logger.e(e, null);
        }
    }
    public static int getColorRes(int res){
        return mApplication.getResources().getColor(res);
    }

    public static String getStringRes(int id, Object... obj) {
        return mApplication.getResources().getString(id, obj);
    }

    public static String getStringRes(int res){
        return mApplication.getResources().getString(res);
    }
    public static String[] getStrings(int id) {
        return mApplication.getResources().getStringArray(id);
    }

    public static int getIntRes(int id){
        return mApplication.getResources().getInteger(id);
    }

    public static float getDimen(int res){
        return mApplication.getResources().getDimension(res);
    }

    public static String[] getStringArrayRes(int resId) {
        return mApplication.getResources().getStringArray(resId);
    }

    public static Drawable getDrawableRes(int res){
        return mApplication.getResources().getDrawable(res);
    }

    public List<String> getSingleEcgMacList() {
        return mSingleEcgMacList;
    }

    public void setSingleEcgMacList(List<String> mSingleEcgMacList) {
        this.mSingleEcgMacList = mSingleEcgMacList;
    }

    public List<String> getTwelvEcgMacList() {
        return mTwelvEcgMacList;
    }

    public void setTwelvEcgMacList(List<String> mTwelvEcgMacList) {
        this.mTwelvEcgMacList = mTwelvEcgMacList;
    }

    public List<String> getBpMacList() {
        return mBpMacList;
    }

    public void setBpMacList(List<String> mBpMacList) {
        this.mBpMacList = mBpMacList;
    }

    public List<String> getPrinterMacList() {
        return mPrinterMacList;
    }

    public void setPrinterMacList(List<String> mPrinterMacList) {
        this.mPrinterMacList = mPrinterMacList;
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) mApplication.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) mApplication.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static boolean isLunarSetting() {
        Locale locale =mApplication.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language != null && language.trim().contains("zh"))
            return true;
        else
            return false;
    }

    public static String getAppVersionName() {
        PackageManager manager = mApplication.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(mApplication.getPackageName(), PackageManager.GET_ACTIVITIES);
            return packageInfo.versionName == null ? "" : packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent intent = new Intent();
        intent.setClassName(this, DfthKeepForegroundService.class.getName());
        stopService(intent);
        PushHandle.destroy();
    }
}
