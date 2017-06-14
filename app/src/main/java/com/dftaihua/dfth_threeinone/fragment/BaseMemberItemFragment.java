package com.dftaihua.dfth_threeinone.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.ComplateSelfInfoActivity;
import com.dftaihua.dfth_threeinone.activity.HomeActivity;
import com.dftaihua.dfth_threeinone.activity.MemberInfoModifyActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.UserConstant;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.LoginUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.BottomControlView;
import com.dftaihua.dfth_threeinone.widget.view.ECGMeasureSymptomView;
import com.dftaihua.dfth_threeinone.widget.view.ECGMeasureWaveView;
import com.dftaihua.dfth_threeinone.widget.view.ECGTwelveLeaderView;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.user.DfthUser;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public abstract class BaseMemberItemFragment extends Fragment{

    public BaseMemberItemFragment() {
        super();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = onCreateView(inflater);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected abstract View onCreateView(LayoutInflater inflater);

    public abstract void save();

    public static final class ContactFragment extends BaseMemberItemFragment implements View.OnClickListener{
        private EditText mNameEt;
        private EditText mPhoneEt;
        private ImageView mNameClearIv;
        private ImageView mPhoneClearIv;
        private DfthUser mUser;

        @Override
        protected View onCreateView(LayoutInflater inflater) {
            View view = inflater.inflate(R.layout.fragment_contact, null);
            mNameEt = (EditText) view.findViewById(R.id.fragment_contact_name_et);
            mPhoneEt = (EditText) view.findViewById(R.id.fragment_contact_phone_et);
            mNameClearIv = (ImageView) view.findViewById(R.id.fragment_contact_name_clear_iv);
            mPhoneClearIv = (ImageView) view.findViewById(R.id.fragment_contact_phone_clear_iv);
            mNameClearIv.setOnClickListener(this);
            mPhoneClearIv.setOnClickListener(this);
            return view;
        }

        @Override
        public void save() {
            if (!NetworkCheckReceiver.getNetwork()) {
                ToastUtils.showShort(getActivity(), R.string.network_not_connect);
                return;
            }
            String name = mNameEt.getText().toString().trim();
            String phone = mPhoneEt.getText().toString().trim();
            if(TextUtils.isEmpty(name)){
                ToastUtils.showShort(getActivity(), R.string.member_info_contact_name_not_null);
                return;
            }
            if(TextUtils.isEmpty(phone)){
                ToastUtils.showShort(getActivity(), R.string.member_info_contact_phone_not_null);
                return;
            }
            if(!LoginUtils.isTelNum(phone)){
                ToastUtils.showShort(getActivity(), R.string.tel_is_invalid);
                return;
            }
            mUser = UserManager.getInstance().getDefaultUser();
            if (mUser != null){
                mUser.setKindredName(name);
                mUser.setKindredNum(phone);
                DfthSDKManager.getManager().getDfthService().updateMember(mUser).asyncExecute(new DfthServiceCallBack<Void>() {
                    @Override
                    public void onResponse(DfthServiceResult<Void> dfthServiceResult) {
                        if (dfthServiceResult.mResult == 0){
                            DfthSDKManager.getManager().getDatabase().updateUser(mUser);
                            ToastUtils.showShort(ThreeInOneApplication.getInstance(), R.string.member_info_contact_modify_success);
                            getActivity().finish();
                        }else{
                            ToastUtils.showShort(ThreeInOneApplication.getInstance(),dfthServiceResult.mMessage);
                        }
                    }
                });
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fragment_contact_name_clear_iv:
                    mNameEt.setText("");
                    break;
                case R.id.fragment_contact_phone_clear_iv:
                    mPhoneEt.setText("");
                    break;
            }
        }
    }

    public static final class HabitFragment extends BaseMemberItemFragment {
        private BottomControlView mBottomView;
        private ECGMeasureWaveView mWaveView;
        private ECGTwelveLeaderView mLeaderView;
        private ECGMeasureSymptomView mSymptomView;
        private RelativeLayout mParentRl;

        @Override
        protected View onCreateView(LayoutInflater inflater) {
            mParentRl = new RelativeLayout(getActivity());
            return mParentRl;
        }

        @Override
        public void save() {

        }
    }

    public static final class DiseaseFragment extends BaseMemberItemFragment {

        @Override
        protected View onCreateView(LayoutInflater inflater) {
            View v = inflater.inflate(R.layout.fragment_single_ecg_measure, null);
            return v;
        }

        @Override
        public void save() {

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            EventBus.getDefault().unregister(this);
        }
    }

    public static BaseMemberItemFragment createFragment(int itemType) {
        if (itemType == Constant.MemberInfoItem.EMERGENCY_CONTACT) {
            return new ContactFragment();
        } else if (itemType == Constant.MemberInfoItem.LIFE_HABIT){
            return new HabitFragment();
        } else{
            return new DiseaseFragment();
        }
    }
}