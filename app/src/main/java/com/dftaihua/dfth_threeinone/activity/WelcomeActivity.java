package com.dftaihua.dfth_threeinone.activity;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.constant.UserConstant;
import com.dftaihua.dfth_threeinone.fragment.LoginFragment;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/17 10:11
*/
public class WelcomeActivity extends BaseActivity {

    @Override
    public View initView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_welcome, null);
    }

    @Override
    public void initData() {
        Handler handler = new Handler();
        //当计时结束,跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((int) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), SharePreferenceConstant.IS_FIRST_LOGIN, -1) == -1) {
                    SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), SharePreferenceConstant.IS_FIRST_LOGIN, 1);
                }
//                SharePreferenceUtils.put(WelcomeActivity.this, SharePreferenceConstant.ECG_MEASURE_IS_BEGIN, false);
                SharePreferenceUtils.put(WelcomeActivity.this, SharePreferenceConstant.BP_MEASURE_LOCK, false);

//                int guide = (int) SharePreferenceUtils.get(WelcomeActivity.this, SharePreferenceConstant.GUIDE_FIRST_KEY, 0);
//                if (guide == 0) {
//                    ActivitySkipUtils.skipAnotherActivity(WelcomeActivity.this, GuideActivity.class);
//                } else {
                    if (checkIsLogin()) {
                        if (!checkComplateSelfInfo()) {
                            ActivitySkipUtils.skipAnotherActivity(WelcomeActivity.this, ComplateSelfInfoActivity.class);
                        } else {
                            ActivitySkipUtils.skipAnotherActivity(WelcomeActivity.this, HomeActivity.class);
                        }
                    } else {
                        ActivitySkipUtils.skipAnotherActivity(WelcomeActivity.this, LoginActivity.class);
                    }
//                }
                WelcomeActivity.this.finish();
            }
        }, 500);
    }

    private boolean checkIsLogin() {
        return !SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), UserConstant.USERID, "-1").equals("-1");
    }

    private boolean checkComplateSelfInfo() {
        return SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), UserConstant.COMPLATE_SELF_INFO, UserConstant.UN_COMPLATE_SELF_INFO)
                .equals(UserConstant.COMPLATE_SELF_INFO);
    }
}
