package com.dftaihua.dfth_threeinone.activity;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.network.DfthNetworkService;
import com.dftaihua.dfth_threeinone.network.UserIconDownloadListener;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.utils.UserUtils;
import com.dftaihua.dfth_threeinone.widget.CheckedItemView;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.permission.DfthPermissionManager;
import com.dfth.sdk.user.DfthUser;

import java.util.HashMap;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2016/12/12 14:54
*/
public class MemberInfoActivity extends BaseActivity implements View.OnClickListener, UserIconDownloadListener {
//    private CheckedItemView mMyServiceItem;
//    private CheckedItemView mConsumeRecordItem;
    private CheckedItemView mEmergencyContactItem;
    private CheckedItemView mLifeHabitItem;
    private CheckedItemView mDiseaseHistoryItem;
    private ImageView mHeadIv;
    private DfthUser mUser;
    private TextView mUserName;
    private TextView mUserPhone;
    private TextView mUserAge;
    private TextView mUserHeight;
    private TextView mUserSex;
    private TextView mUserWeight;
    private RelativeLayout mMemberInfoRl;
    private HashMap<String, Object> mHashMap;

    public MemberInfoActivity() {
        mTitleNameRes = R.string.title_activity_self_info;
        mTitleColorRes = R.color.google_white;
        mTitleNameColorRes  = R.color.google_black;
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
        mUser = UserManager.getInstance().getDefaultUser();
    }

    @Override
    public View initView() {
        mHashMap = new HashMap<>();
        View view = LayoutInflater.from(this).inflate(R.layout.activity_member_info, null);
//        mMyServiceItem = (CheckedItemView) view.findViewById(R.id.activity_member_info_my_service_item);
//        mConsumeRecordItem = (CheckedItemView) view.findViewById(R.id.activity_member_info_consume_record_item);
        mEmergencyContactItem = (CheckedItemView) view.findViewById(R.id.activity_member_info_emergency_contact_item);
        mLifeHabitItem = (CheckedItemView) view.findViewById(R.id.activity_member_info_life_habit_item);
        mDiseaseHistoryItem = (CheckedItemView) view.findViewById(R.id.activity_member_info_disease_history_item);
        mHeadIv = (ImageView) view.findViewById(R.id.user_icon);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mUserPhone = (TextView) view.findViewById(R.id.user_phone);
        mUserAge = (TextView) view.findViewById(R.id.user_age);
        mUserHeight = (TextView) view.findViewById(R.id.user_height);
        mUserSex = (TextView) view.findViewById(R.id.user_sex);
        mUserWeight = (TextView) view.findViewById(R.id.user_weight);
        mMemberInfoRl = (RelativeLayout) view.findViewById(R.id.activity_member_info_rl);
//        mMyServiceItem.setOnClickListener(this);
//        mConsumeRecordItem.setOnClickListener(this);
        mEmergencyContactItem.setOnClickListener(this);
        mLifeHabitItem.setOnClickListener(this);
        mDiseaseHistoryItem.setOnClickListener(this);
//        mHeadIv.setOnClickListener(this);
        mMemberInfoRl.setOnClickListener(this);
//        mMyServiceItem.createItem(Constant.MemberInfoItem.MY_SERVICE);
//        mConsumeRecordItem.createItem(Constant.MemberInfoItem.CONSUME_RECORD);
        mEmergencyContactItem.createItem(Constant.MemberInfoItem.EMERGENCY_CONTACT);
        mLifeHabitItem.createItem(Constant.MemberInfoItem.LIFE_HABIT);
        mDiseaseHistoryItem.createItem(Constant.MemberInfoItem.DISEASE_HISTORY);
        return view;
    }

    @Override
    public void initData() {
        if (DfthPermissionManager.checkStoragePermission()){
            downLoadIcon();
        }else{
            DfthPermissionManager.assertSdkPermission(this,100);
//            ToastUtils.showShort(this,getString(R.string.storage_permission_tip));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mUser != null){
            mUserName.setText(mUser.getName() == null ? mUser.getTelNum() : mUser.getName());
            mUserPhone.setText(mUser.getTelNum());
            mUserWeight.setText(mUser.getWeight() + ThreeInOneApplication.getStringRes(R.string.member_info_weight_kg));
            mUserHeight.setText(mUser.getHeight() + ThreeInOneApplication.getStringRes(R.string.member_info_height_cm));
            mUserSex.setText(mUser.getGender()==1 ? ThreeInOneApplication.getStringRes(R.string.member_info_gender_man)
                    : ThreeInOneApplication.getStringRes(R.string.member_info_gender_woman));
            mUserAge.setText(TimeUtils.getAge(mUser.getBirthday()) + ThreeInOneApplication.getStringRes(R.string.member_info_birth_age));
            mEmergencyContactItem.setContentText(mUser.getKindredNum());
        }
        getIcon();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.activity_member_info_my_service_item:
//                break;
//            case R.id.activity_member_info_consume_record_item:
//                break;
            case R.id.activity_member_info_emergency_contact_item:
                mHashMap.put(Constant.MEMBER_ITEM_TYPE, Constant.MemberInfoItem.EMERGENCY_CONTACT);
                ActivitySkipUtils.skipAnotherActivity(MemberInfoActivity.this, MemberInfoItemActivity.class, mHashMap);
//                ToastUtils.showShort(this, R.string.slide_menu_add_home_doctor);
                break;
            case R.id.activity_member_info_life_habit_item:
                ToastUtils.showShort(this, R.string.slide_menu_add_home_doctor);
                break;
            case R.id.activity_member_info_disease_history_item:
                ToastUtils.showShort(this, R.string.slide_menu_add_home_doctor);
                break;
            case R.id.activity_member_info_rl:
                ActivitySkipUtils.skipAnotherActivity(this, MemberInfoModifyActivity.class);
                break;
        }
    }

    public void getIcon() {
        Bitmap b = UserUtils.getIcon(mUser);
        if (b != null){
            mHeadIv.setImageBitmap(b);
        }
    }

    private void downLoadIcon() {
        DfthNetworkService.downloadUserIcon(mUser.getUserId(),this);
    }

    @Override
    public void onComplate(boolean isOk) {
        Logger.e("UserIconDownload "+isOk);
        DispatchUtils.performMainThread(new DispatchTask<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                getIcon();
            }
        });
    }
}
