package com.dftaihua.dfth_threeinone.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.SlideMenu;
import com.dftaihua.dfth_threeinone.widget.TitleView;
import com.dfth.push.DfthPushManager;
import com.dfth.sdk.permission.DfthPermissionManager;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/17 9:22
*/
public abstract class BaseActivity extends FragmentActivity {
    public static final long TITLE_VISIBLE = 0x0000000000000001L;
    public static final long BACK_VISIBLE = 0X0000000000000010L;
    public static final long SLIDE_VISIBLE = 0X0000000000000100L;
    public static final long SAVE_VISIBLE = 0X0000000000001000L;
    public static final long TWO_TITLE_VISIBLE = 0X0000000000010000L;
    protected long mStatus = 0x0000000000000000L;
    protected SlideMenu mSlidMenu;
    protected TitleView mTitleView;
    protected int mTitleNameRes = R.string.empty;
    protected String mTitleNameStr = "";
    protected int mTitleColorRes = 0;
    protected int mTitleNameColorRes = 0;
    protected int mTitleBackRes = 0;
    protected int mTwoTitleFirstRes = R.string.empty;
    protected int mTwoTitleSecondRes = R.string.empty;
    protected String mTwoTitleFirstStr = "";
    protected String mTwoTitleSecondStr = "";
    protected int mTwoTitleFirstColorRes = 0;
    protected int mTwoTitleSecondColorRes = 0;
    private long lastPressBackTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long time = System.currentTimeMillis();
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_base, null);
        mSlidMenu = (SlideMenu) rootView.findViewById(R.id.activity_base_slide_menu);
        mTitleView = new TitleView(this);
        RelativeLayout mainView = (RelativeLayout) rootView.findViewById(R.id.main_content);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
        View contentView = initView();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        if (!(this instanceof HomeActivity)) {
            params.addRule(RelativeLayout.BELOW, R.id.main_title);
        }
        mainView.addView(contentView, params);
        if ((mStatus & TITLE_VISIBLE) > 0 || (mStatus & TWO_TITLE_VISIBLE) > 0) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
            mTitleView.setId(R.id.main_title);
            params.height = DisplayUtils.dip2px(this, ThreeInOneApplication.getIntRes(R.integer.title_height));
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mainView.addView(mTitleView, params);
        }
        if (mTitleView != null) {
            mTitleView.select(this);
        }
        setContentView(rootView);
        initData();
        ActivityCollector.addActivity(this);
        DfthPushManager.setNewIntent(getIntent());
        Log.e("dfth_sdk", "" + (System.currentTimeMillis() - time));
    }

    private ProgressDialog mProgressDialog;

    protected void showProgress() {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(ThreeInOneApplication.getStringRes(R.string.loading_data));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        if(!mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.setAction(Constant.Action.ACTIVITY_RESUME);
        sendBroadcast(intent);
        DfthPushManager.onActivityStart(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent();
        intent.setAction(Constant.Action.ACTIVITY_STOP);
        intent.putExtra("activity",getClass().getName());
        sendBroadcast(intent);
        DfthPushManager.onActivityStop(this);
    }

    public abstract View initView();

    public void initData() {

    }

    public long getStatus() {
        return mStatus;
    }

    public int getTitleNameRes() {
        return mTitleNameRes;
    }

    public String getTitleNameStr() {
        return mTitleNameStr;
    }

    public int getTitleColorRes() {
        return mTitleColorRes;
    }

    public int getTitleNameColorRes() {
        return mTitleNameColorRes;
    }

    public static long getTitleVisible() {
        return TITLE_VISIBLE;
    }

    public int getTwoTitleFirstRes() {
        return mTwoTitleFirstRes;
    }

    public int getTwoTitleSecondRes() {
        return mTwoTitleSecondRes;
    }

    public String getTwoTitleFirstStr() {
        return mTwoTitleFirstStr;
    }

    public String getTwoTitleSecondStr() {
        return mTwoTitleSecondStr;
    }

    public int getTwoTitleFirstColorRes() {
        return mTwoTitleFirstColorRes;
    }

    public int getTwoTitleSecondColorRes() {
        return mTwoTitleSecondColorRes;
    }

    public int getTitleBackRes() {
        return mTitleBackRes;
    }

    public SlideMenu getSlidMenu() {
        return mSlidMenu;
    }

    public TitleView getTitleView() {
        return mTitleView;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        return super.onKeyUp(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (this instanceof HomeActivity) {
                if (DfthDeviceManager.getInstance().isHaveMeasuringDevice()){
                    ToastUtils.showShort(this, R.string.prevent_quit_toast);
                    return false;
                }
                if (System.currentTimeMillis() - lastPressBackTime < 2000) {
                    this.finish();
                    ActivityCollector.finishAll();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    lastPressBackTime = System.currentTimeMillis();
                    ToastUtils.showShort(this, R.string.press_once_back);
                }
            } else if (this instanceof ComplateSelfInfoActivity) {
                if (System.currentTimeMillis() - lastPressBackTime < 2000) {
                    ActivityCollector.finishAll();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    lastPressBackTime = System.currentTimeMillis();
                    ToastUtils.showShort(this, R.string.press_once_back);
                }
            } else {
                this.finish();
            }
//            if (getSlidMenu().isOpen()) {
//                getSlidMenu().close();
//                return false;
//            }
////            return false;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        DfthPushManager.setNewIntent(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                String content = DfthPermissionManager.verifyPermission(this, permissions[0], requestCode);
                showPermissionDialog(content);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showPermissionDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(content).create().show();
    }
}
