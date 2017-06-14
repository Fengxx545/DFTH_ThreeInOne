package com.dftaihua.dfth_threeinone.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.UserConstant;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.DateUtils;
import com.dftaihua.dfth_threeinone.utils.LoginUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.DatePicker;
import com.dftaihua.dfth_threeinone.widget.Ruler;
import com.dftaihua.dfth_threeinone.widget.Sound;
import com.dftaihua.dfth_threeinone.widget.Switcher;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.user.DfthUser;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/17 15:10
*/
public class ComplateSelfInfoActivity extends BaseActivity implements View.OnClickListener {
    private TextView mWeight;
    private TextView mHeight;
    private Switcher mXSwitch;
    private Ruler mWeightRuler;
    private Ruler mHeightRuler;
    private DatePicker mDatePicker;
    private TextView mComplete;
    private String mSex = "女";
    private int mWeightNum = 50;
    private int mHeightNum = 175;
    private int mYear;
    private int mMonthOfYear;
    private int mDayOfMonth;
    private EditText mName;
    private DfthUser mUser;

    @Override
    public View initView() {
        mStatus = TITLE_VISIBLE ;
        mTitleNameRes = R.string.title_activity_complete_selfinfo;
        mTitleNameColorRes = R.color.remarks_font_color;

        View view = LayoutInflater.from(this).inflate(R.layout.activity_self_information, null);
        mXSwitch = (Switcher) view.findViewById(R.id.xswitch);
        mWeight = (TextView) view.findViewById(R.id.weiget);
        mHeight = (TextView) view.findViewById(R.id.height);
        mWeightRuler = (Ruler) view.findViewById(R.id.ruler1);
        mHeightRuler = (Ruler) view.findViewById(R.id.ruler2);
        mDatePicker = (DatePicker) view.findViewById(R.id.datepicker);
        mComplete = (TextView) view.findViewById(R.id.complate);
        mName = (EditText) view.findViewById(R.id.login_phone_text);
        mComplete.setOnClickListener(this);
        return view;
    }

    @Override
    public void initData() {
        mXSwitch.setOnSwitcherChangeListener(new Switcher.SwitcherChange() {
            @Override
            public void changed(boolean isRight) {
                if (isRight)
                    mSex = ThreeInOneApplication.getStringRes(R.string.member_info_gender_man);
                else
                    mSex = ThreeInOneApplication.getStringRes(R.string.member_info_gender_woman);
                ToastUtils.showShort(ComplateSelfInfoActivity.this, isRight
                        ? ThreeInOneApplication.getStringRes(R.string.member_info_gender_man)
                        : ThreeInOneApplication.getStringRes(R.string.member_info_gender_woman));
            }
        });

        /**
         * 设置选中的条目
         */
        mWeightRuler.setOnSelectItemListener(new Ruler.SelectItemListener() {
            @Override
            public int setSelectItemListener() {
                return 50;
            }

        });
        mHeightRuler.setOnSelectItemListener(new Ruler.SelectItemListener() {
            @Override
            public int setSelectItemListener() {
                return 175;
            }
        });

        /**
         * 监听拖动时值得变化
         */
        mWeightRuler.setOnRulerValueChangeListener(new Ruler.RulerValue() {
            @Override
            public void value(int value) {
                mWeight.setText("体重\n" + value + "\nkg");
                mWeightNum = value;
            }
        });
        mHeightRuler.setOnRulerValueChangeListener(new Ruler.RulerValue() {
            @Override
            public void value(int value) {
                mHeight.setText("身高\n" + value + "\ncm");
                mHeightNum = value;
            }
        });
        Sound sound2 = new Sound(this);
//        sound2.setCustomSound(R.raw.beep);
        mDatePicker.setSoundEffect(sound2)
                .setDate(DateUtils.stringToDate("1970-1-1"))
//                .setTextColor(getResources().getColor(R.color.colorRulerLine))
//                .setFlagTextColor(Color.RED)
//                .setTextSize(25)
//                .setFlagTextSize(15)
//                .setBackground(Color.BLACK)
                .setTextColor(ThreeInOneApplication.getColorRes(R.color.google_black))
                .setSoundEffectsEnabled(true);
        mDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonthOfYear = monthOfYear;
                mDayOfMonth = dayOfMonth;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.complate) {
            // TODO: 2017/1/18  保存用户

            mYear = mDatePicker.getYear();
            mMonthOfYear = mDatePicker.getMonth();
            mDayOfMonth = mDatePicker.getDayOfMonth();
            boolean isBeforeToday = TimeUtils.isBeforeToday(mYear + "-" + mMonthOfYear + "-" + mDayOfMonth);
            if (!isBeforeToday){
                ToastUtils.showShort(ComplateSelfInfoActivity.this, R.string.select_before_today);
                return;
            }
            final long time = TimeUtils.getTimeByStr(mYear + "-" + mMonthOfYear + "-" + mDayOfMonth, "yyyy-MM-dd");
            mUser = UserManager.getInstance().getDefaultUser();
            if (mUser != null && !LoginUtils.isUserNameOk(this,mName.getText().toString().trim())){
                mUser.setBirthday(time);
                //1男2女
                mUser.setGender(mSex.equals("男") ? 1 : 2);
                mUser.setHeight(mHeightNum);
                mUser.setWeight(mWeightNum);
                mUser.setName(mName.getText().toString().trim());
                DfthSDKManager.getManager().getDfthService().updateMember(mUser).asyncExecute(new DfthServiceCallBack<Void>() {
                    @Override
                    public void onResponse(DfthServiceResult<Void> dfthServiceResult) {
                        if (dfthServiceResult.mResult == 0){
                            DfthSDKManager.getManager().getDatabase().updateUser(mUser);
                            SharePreferenceUtils.put(ComplateSelfInfoActivity.this, UserConstant.COMPLATE_SELF_INFO, UserConstant.COMPLATE_SELF_INFO);
                            Logger.e("性别: " + mSex + "\n" + "年: " + mYear + "\n" + "月: " + mMonthOfYear + "\n" + "日: " + mDayOfMonth + "\n" + "身高: "
                                    + mHeightNum + "\n" + "体重: " + mWeightNum + "\n" + "生日: "
                                    + time + "\n" + "\n" + "生日日期: " + TimeUtils.getTimeStr(time, "yyyy-MM-dd") + "\n");
                            ActivitySkipUtils.skipAnotherActivity(ComplateSelfInfoActivity.this, HomeActivity.class);
                            ComplateSelfInfoActivity.this.finish();
//                            ActivityCollector.finishOthers();
                        }else{
                            ToastUtils.showShort(ComplateSelfInfoActivity.this,dfthServiceResult.mMessage);
                        }
                    }
                });

            }
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            SharePreferenceUtils.put(this, UserConstant.COMPLATE_SELF_INFO,UserConstant.UN_COMPLATE_SELF_INFO);
//            QuitUtils.exit();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
