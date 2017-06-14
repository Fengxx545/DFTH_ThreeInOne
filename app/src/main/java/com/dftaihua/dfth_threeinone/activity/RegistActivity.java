package com.dftaihua.dfth_threeinone.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.MapConstant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.LoginUtils;
import com.dftaihua.dfth_threeinone.utils.MathUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.network.response.UserResponse;
import com.dfth.sdk.user.DfthUser;

import java.util.HashMap;

;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/17 14:06
*/
public class RegistActivity extends BaseActivity implements View.OnClickListener {

    private EditText mPhone;
    private EditText mVerifyCode;
    private TextView mNext;
    private Button mGetSMSCode;
    private TimeCount mTimeCount;
    private String mCode;
    private String mFromWhere;
    private EditText mPassword;
    private ImageView mShowPassword;
    private Dialog mLoadingDialog;
    private boolean[] mMark;

    @Override
    public View initView() {
        View view =  LayoutInflater.from(this).inflate(R.layout.activity_regist,null);
        mPhone = (EditText) view.findViewById(R.id.login_phone_text);
        mVerifyCode = (EditText) view.findViewById(R.id.verify_code);
        mNext = (TextView) view.findViewById(R.id.regist_next);
        mGetSMSCode = (Button) view.findViewById(R.id.btn_sms_code);
        mPassword = (EditText) view.findViewById(R.id.login_password);
        mShowPassword = (ImageView) view.findViewById(R.id.show_password);

        mNext.setOnClickListener(this);
        mGetSMSCode.setOnClickListener(this);
        mShowPassword.setOnClickListener(this);
        mMark = new boolean[3];
        mMark[0] = false;
        mMark[1] = false;
        mMark[2] = false;
        mNext.setClickable(false);
        mNext.setBackgroundResource(R.drawable.next_btn_forbid);
        mPhone.addTextChangedListener(new MyTextWatcher(0));
        mVerifyCode.addTextChangedListener(new MyTextWatcher(1));
        mPassword.addTextChangedListener(new MyTextWatcher(2));
        return view;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mFromWhere = intent.getStringExtra(MapConstant.LOGINJUMP);
        if (mFromWhere.equals(MapConstant.GOREGIST)){
            mLoadingDialog = DialogFactory.getLoadingDialog(this,
                    ThreeInOneApplication.getStringRes(R.string.registering),"");
        }else if (mFromWhere.equals(MapConstant.GOCHANGEPASSWORD)){
            mLoadingDialog = DialogFactory.getLoadingDialog(this,
                    ThreeInOneApplication.getStringRes(R.string.change_password_ing),"");
        }
        SharePreferenceUtils.put(this, SharePreferenceConstant.SHOW_PASSWORD,false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.regist_next:{
                String phoneNum = mPhone.getText().toString().trim();
                String passwordNum = mPassword.getText().toString().trim();
                String code = mVerifyCode.getText().toString().trim();
                if (LoginUtils.judgeRegistIsValid(this,phoneNum,passwordNum,code)) {
                    if (mFromWhere.equals(MapConstant.GOREGIST)){
                        if(NetworkCheckReceiver.getNetwork()){
                            registUser(phoneNum,passwordNum,code);
                        } else{
                            ToastUtils.showShort(this,R.string.network_not_connect);
                        }
                    }else{
                        // TODO: 2017/2/23 修改密码网络请求
                        if(NetworkCheckReceiver.getNetwork()){
                            if (!mLoadingDialog.isShowing()) {
                                mLoadingDialog.show();
                            }
                            resetPassword(phoneNum,passwordNum,code);
                        } else{
                            ToastUtils.showShort(this,R.string.network_not_connect);
                        }
                    }
                }
//                if (mFromWhere.equals(MapConstant.GOREGIST)) {
//                    ActivitySkipUtils.skipAnotherActivity(RegistActivity.this, ComplateSelfInfoActivity.class);
//                }else{
//                    ActivitySkipUtils.skipAnotherActivity(RegistActivity.this, HomeActivity.class);
//                    ActivityCollector.finishOthers();
//                }
            }
            break;
            case R.id.btn_sms_code:{
                String phoneNum = mPhone.getText().toString().trim();
                if(LoginUtils.isJudgeTelValid(this,phoneNum)){
                    if(NetworkCheckReceiver.getNetwork()){
                        getCodes(phoneNum);
                    } else{
                        ToastUtils.showShort(this,R.string.network_not_connect);
                    }
                }
            }
            break;
            case R.id.show_password:{
                boolean isShow = (boolean) SharePreferenceUtils.get(this, SharePreferenceConstant.SHOW_PASSWORD,false);
                if (isShow){
                    SharePreferenceUtils.put(this, SharePreferenceConstant.SHOW_PASSWORD,false);
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPassword.setSelection(mPassword.getText().length());
                }else{
                    //显示密码
                    SharePreferenceUtils.put(this, SharePreferenceConstant.SHOW_PASSWORD,true);
                    mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPassword.setSelection(mPassword.getText().length());
                }
            }
            break;
        }
    }

    private void resetPassword(String phoneNum, String passwordNum, String code) {
        DfthSDKManager.getManager().getDfthService().resetPassword(phoneNum,passwordNum,code).asyncExecute(new DfthServiceCallBack<Void>() {
            @Override
            public void onResponse(DfthServiceResult<Void> response) {
                if (mLoadingDialog.isShowing()){
                    mLoadingDialog.dismiss();
                }
                Log.e("dfth_sdk","reset_password->" + response.mResult);
                if (response.mResult == 0){
                    ToastUtils.showShort(RegistActivity.this, ThreeInOneApplication.getStringRes(R.string.change_password_ok));
                    ActivitySkipUtils.skipAnotherActivity(RegistActivity.this,LoginActivity.class);
                    ActivityCollector.finishOthers();
                }else{
                    if (mLoadingDialog.isShowing()){
                        mLoadingDialog.dismiss();
                    }
                    ToastUtils.showShort(RegistActivity.this,response.mMessage);
                }
            }
        });
    }

    /**
     * 注册用户
     * @param phoneNum 手机号
     * @param password 密码
     * @param vericode 验证码
     */
    private void registUser(String phoneNum,String password,String vericode) {
        // TODO: 2017/1/18 注册用户
        try {
            DfthSDKManager.getManager().getDfthService().registerUser(phoneNum,password,vericode).asyncExecute(new DfthServiceCallBack<UserResponse>() {
                @Override
                public void onResponse(DfthServiceResult<UserResponse> response) {
                    Log.e("dfth_sdk","register->" + response.mResult);
                    if (mLoadingDialog.isShowing()){
                        mLoadingDialog.dismiss();
                    }
                    if(response.mResult == 0 && response.mData != null){
                        DfthUser user = new DfthUser();
                        user.setTelNum(response.mData.telNumber);
                        user.setUserId(response.mData.id);
                        user.setGender(response.mData.gender);
                        user.setWeight(response.mData.weight);
                        user.setHeight(response.mData.height);
                        user.setName(response.mData.name);
                        user.setBirthday(response.mData.birthday);
                        user.setNation(String.valueOf(response.mData.nation));
                        user.setBlood(response.mData.blood);
                        user.setKindredName(response.mData.kindredName);
                        user.setCreateTime(response.mData.createTime);
                        user.setEmail(response.mData.email);
                        DfthSDKManager.getManager().getDatabase().saveUser(user);
                        UserManager.getInstance().setDefaultUser(response.mData.id);
                        if (mFromWhere.equals(MapConstant.GOREGIST)) {
                            ActivitySkipUtils.skipAnotherActivity(RegistActivity.this, ComplateSelfInfoActivity.class);
                        }else{
                            ActivitySkipUtils.skipAnotherActivity(RegistActivity.this, HomeActivity.class);
                            ActivityCollector.finishOthers();
                        }
                    }else{
                        if (mLoadingDialog.isShowing()){
                            mLoadingDialog.dismiss();
                        }
                        ToastUtils.showShort(RegistActivity.this,response.mMessage);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort(this,ThreeInOneApplication.getStringRes(R.string.connect_service_fail));
        }
    }

    /**
     * 获取验证码
     * @param phoneNum 手机号
     */
    private void getCodes(String phoneNum) {
        DfthSDKManager.getManager().getDfthService().registerSmsCode(phoneNum).asyncExecute(new DfthServiceCallBack<Void>() {
            @Override
            public void onResponse(DfthServiceResult<Void> response) {
                if(response.mResult == 0 && response.mData != null){
                    if(mTimeCount == null){
                        mTimeCount = new TimeCount(90000, 1000);
                    }
                    mTimeCount.start();
                    mCode = String.valueOf(response.mResult);
                }else{
                    ToastUtils.showShort(RegistActivity.this, response.mMessage);
                }
                Log.e("dfth_sdk","smsCode_code->" + response.mResult);
            }
        });
    }

    /**
     * 倒计时器类
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            mGetSMSCode.setText(R.string.code_re_get);
            mGetSMSCode.setTextSize(12);
            mGetSMSCode.setTextColor(Color.WHITE);
            mGetSMSCode.setBackgroundResource(R.drawable.login_btn_selector);
            mGetSMSCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            mGetSMSCode.setClickable(false);
            mGetSMSCode.setBackgroundResource(R.drawable.next_btn_forbid);
            mGetSMSCode.setTextSize(12);
            mGetSMSCode.setTextColor(Color.BLACK);
            mGetSMSCode.setText(Math.round(millisUntilFinished / 1000f) +" s");
        }
    }

    public class MyTextWatcher implements TextWatcher{
        private int mType;
        public MyTextWatcher(int type) {
            mType = type;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length()>0){
                mMark[mType] = true;
            } else{
                mMark[mType] = false;
            }
            if (MathUtils.search(mMark,true,3)){
                mNext.setClickable(true);
                mNext.setBackgroundResource(R.drawable.login_btn_n);
            } else{
                mNext.setClickable(false);
                mNext.setBackgroundResource(R.drawable.next_btn_forbid);
            }
        }
    }
}
