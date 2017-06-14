package com.dftaihua.dfth_threeinone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dftaihua.dfth_threeinone.widget.FamilyDoctorView;

public class FamilyDoctorFragment extends BaseFragment{

    public FamilyDoctorFragment(){
        mStatus = TITLE_VISIBLE | BACK_VISBLE;
        mTitleNameStr = "医生展示";
    }

    @Override
    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FamilyDoctorView view = new FamilyDoctorView(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
