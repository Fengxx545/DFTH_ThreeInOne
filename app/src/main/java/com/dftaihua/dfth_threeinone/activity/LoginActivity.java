package com.dftaihua.dfth_threeinone.activity;

import android.app.Dialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.MapConstant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.constant.UserConstant;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.LoginUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.network.response.LoginResponse;
import com.dfth.sdk.network.response.UserInfoResponse;
import com.dfth.sdk.permission.DfthPermissionManager;
import com.dfth.sdk.user.DfthUser;
import com.dfth.update.DfthUpdateCode;
import com.dfth.update.UpdateListener;
import com.dfth.update.UpdateManager;

import java.util.HashMap;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/17 11:28
*/
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mPhoneNum;
    private EditText mPassword;
    private TextView mChangePassword;
    private TextView mLogin;
    private TextView mOldUserLoagin;
    private TextView mRegist;
    private ImageView mShowPassword;
    private HashMap<String, String> mHashMap;
    private Dialog mLoadingDialog;
    private int mMoveTimes = 0;
    private DfthUser mUser;

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_login, null);
        mPhoneNum = (EditText) view.findViewById(R.id.login_phone_text);
        mPassword = (EditText) view.findViewById(R.id.login_password);
        mChangePassword = (TextView) view.findViewById(R.id.text_change_password);
        mLogin = (TextView) view.findViewById(R.id.login_local);
        mOldUserLoagin = (TextView) view.findViewById(R.id.login_old_user);
        mRegist = (TextView) view.findViewById(R.id.regist);
        mShowPassword = (ImageView) view.findViewById(R.id.show_password);
        mPhoneNum.setSelection(mPhoneNum.getText().length());
        mPassword.setSelection(mPassword.getText().length());
        mChangePassword.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mRegist.setOnClickListener(this);
        mRegist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mRegist.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mRegist.setTextColor(ThreeInOneApplication.getColorRes(R.color.login_btn_default));
                    mMoveTimes = 0;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = motionEvent.getRawX();
                    float y = motionEvent.getRawY();
                    if (mMoveTimes == 0) {
                        mMoveTimes++;
                        if (checkDownPointerInView(view, x, y)) {
                            mRegist.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                        } else {
                            mRegist.setTextColor(ThreeInOneApplication.getColorRes(R.color.login_btn_default));
                        }
                    } else {
                        mRegist.setTextColor(ThreeInOneApplication.getColorRes(R.color.login_btn_default));
                    }

                }
                return false;
            }
        });
        mShowPassword.setOnClickListener(this);
        mLoadingDialog = DialogFactory.getLoadingDialog(this, ThreeInOneApplication.getStringRes(R.string.login_ing), "");
        DfthPermissionManager.assertSdkPermission(this, 100);
        return view;
    }

    public static boolean checkDownPointerInView(View view, float x, float y) {
        int[] location2 = new int[2];
        view.getLocationOnScreen(location2);
        if (x >= location2[0] && x <= location2[0] + view.getWidth() && y >= location2[1] && y <= location2[1] + view.getHeight()) {
            return true;
        }
        return false;
    }

    @Override
    public void initData() {
        mHashMap = new HashMap<>();
        SharePreferenceUtils.put(this, SharePreferenceConstant.SHOW_PASSWORD, false);
        initUpdate();
    }

    private void initUpdate() {
        UpdateManager.handleUpdate(this, new UpdateListener() {
            @Override
            public void onUpdateResponse(final int code, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == DfthUpdateCode.UPDATE_QUIT_APP) {
                            LoginActivity.this.finish();
                        } else if (code == DfthUpdateCode.UPDATE_FAILED) {
                            if (NetworkCheckReceiver.getNetwork()) {
                                ToastUtils.showLong(LoginActivity.this, R.string.update_failed);
                            }
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_local:
                String phoneNum = mPhoneNum.getText().toString().trim();
                String passwordNum = mPassword.getText().toString().trim();
                if (LoginUtils.judgeLoginIsValid(this, phoneNum, passwordNum)) {
                    if (!mLoadingDialog.isShowing()) {
                        mLoadingDialog.show();
                    }
                    login(phoneNum, passwordNum);

                }
//                ActivitySkipUtils.skipAnotherActivity(LoginActivity.this, HomeActivity.class);
//                ActivityCollector.finishOthers();
                break;
            case R.id.text_change_password:
                mHashMap.put(MapConstant.LOGINJUMP, MapConstant.GOCHANGEPASSWORD);
                ActivitySkipUtils.skipAnotherActivity(this, RegistActivity.class, mHashMap);
                break;
            case R.id.regist:
                mHashMap.put(MapConstant.LOGINJUMP, MapConstant.GOREGIST);
                ActivitySkipUtils.skipAnotherActivity(this, RegistActivity.class, mHashMap);
                break;
            case R.id.show_password:
                boolean isShow = (boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.SHOW_PASSWORD, false);
                if (isShow) {
                    SharePreferenceUtils.put(this, SharePreferenceConstant.SHOW_PASSWORD, false);
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPassword.setSelection(mPassword.getText().length());
                } else {
                    //显示密码
                    SharePreferenceUtils.put(this, SharePreferenceConstant.SHOW_PASSWORD, true);
                    mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPassword.setSelection(mPassword.getText().length());
                }
                break;
        }
    }

    private void login(String phoneNum, String passwordNum) {
        if (!NetworkCheckReceiver.getNetwork()) {
            mLoadingDialog.dismiss();
            ToastUtils.showShort(this, R.string.network_not_connect);
            return;
        }
        DfthSDKManager.getManager().getDfthService().login(phoneNum, passwordNum).asyncExecute(new DfthServiceCallBack<LoginResponse>() {
            @Override
            public void onResponse(final DfthServiceResult<LoginResponse> response) {
                Log.e("dfth_sdk", "login->" + response.mResult);
                if (response.mResult == 0 && response.mData != null) {
                    // TODO: 2017/2/21 记录登录状态
                    mUser = new DfthUser();
                    final String userId = response.mData.userId;
                    mUser.setUserId(userId);
                    DfthSDKManager.getManager().getDfthService().getUserInfo(userId).asyncExecute(new DfthServiceCallBack<UserInfoResponse>() {
                        @Override
                        public void onResponse(DfthServiceResult<UserInfoResponse> dfthServiceResult) {
                            if (mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                            if (dfthServiceResult.mResult == 0 && dfthServiceResult.mData != null) {
                                mUser.setTelNum(mPhoneNum.getText().toString().trim());
                                if (dfthServiceResult.mData.member != null) {
                                    mUser.setGender(dfthServiceResult.mData.member.gender);
                                    mUser.setWeight(dfthServiceResult.mData.member.weight);
                                    mUser.setHeight(dfthServiceResult.mData.member.height);
                                    mUser.setName(dfthServiceResult.mData.member.name);
                                    mUser.setBirthday(dfthServiceResult.mData.member.birthday);
                                    mUser.setNation(dfthServiceResult.mData.member.nation);
                                    mUser.setBlood(dfthServiceResult.mData.member.blood);
                                    mUser.setKindredName(dfthServiceResult.mData.member.kindredName);
                                    if (DfthSDKManager.getManager().getDatabase().getUser(userId) == null) {
                                        DfthSDKManager.getManager().getDatabase().saveUser(mUser);
                                    }
                                    UserManager.getInstance().setDefaultUser(userId);
                                    if (TextUtils.isEmpty(dfthServiceResult.mData.member.name)
                                            || dfthServiceResult.mData.member.gender == 0
                                            || dfthServiceResult.mData.member.weight == 0
                                            || dfthServiceResult.mData.member.height == 0
                                            || dfthServiceResult.mData.member.birthday == 0) {
                                        ActivitySkipUtils.skipAnotherActivity(LoginActivity.this, ComplateSelfInfoActivity.class);
                                        LoginActivity.this.finish();
                                    } else {
                                        SharePreferenceUtils.put(LoginActivity.this, UserConstant.COMPLATE_SELF_INFO, UserConstant.COMPLATE_SELF_INFO);
                                        ActivitySkipUtils.skipAnotherActivity(LoginActivity.this, HomeActivity.class);
                                        LoginActivity.this.finish();
//                                ActivityCollector.finishOthers();
                                    }
                                }
                            } else {
                                ToastUtils.showShort(LoginActivity.this, R.string.login_connect_server_fail);
                            }
                        }
                    });
                } else {
                    if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    if (response.mMessage.equals("登录失败，用户名或密码错误！")) {
                        ToastUtils.showShort(LoginActivity.this, response.mMessage);
                    } else {
                        ToastUtils.showShort(LoginActivity.this, R.string.login_connect_server_fail);
                    }
                }
            }
        });
    }
}
