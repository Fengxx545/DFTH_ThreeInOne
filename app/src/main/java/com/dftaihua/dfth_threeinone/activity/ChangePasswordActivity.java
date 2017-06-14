package com.dftaihua.dfth_threeinone.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.constant.MapConstant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.LoginUtils;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/17 14:08
*/
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText mPassword;
    private TextView mNext;
    private String mFromWhere;

    @Override
    public View initView() {
        View view =  LayoutInflater.from(this).inflate(R.layout.activity_changepassword,null);
//        setContentView(R.layout.activity_change_password);
        mPassword = (EditText) view.findViewById(R.id.set_password_text);
        mNext = (TextView) view.findViewById(R.id.next);
        mNext.setOnClickListener(this);
        return view;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mFromWhere = intent.getStringExtra(MapConstant.LOGINJUMP);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()== R.id.next){
            String passWordNum = mPassword.getText().toString().trim();
            if (LoginUtils.isPasswordEmpty(this,passWordNum)) {
                if (mFromWhere.equals(MapConstant.GOREGIST)) {
                    ActivitySkipUtils.skipAnotherActivity(this, ComplateSelfInfoActivity.class);
                }else{
                    ActivitySkipUtils.skipAnotherActivity(this, HomeActivity.class);
                    ActivityCollector.finishOthers();
                }
            }
        }
    }
}
