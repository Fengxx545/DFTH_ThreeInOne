package com.dftaihua.dfth_threeinone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dftaihua.dfth_threeinone.R;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private View mView;

    public HomeFragment(){
        mStatus = TITLE_VISIBLE | BACK_VISBLE;
        mTitleNameStr = "主页";
    }

    @Override
    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home,null);
//        mView.findViewById(R.id.single_ecg).setOnClickListener(this);
//        mView.findViewById(R.id.twelve_ecg).setOnClickListener(this);
//        mView.findViewById(R.id.bp).setOnClickListener(this);
//        mView.findViewById(R.id.assistant).setOnClickListener(this);
        mTitleView.setTitleBackImg(R.drawable.ic_launcher);
        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.single_ecg:{
//                FragmentCommand command = new FragmentCommand((HomeActivity)getContext(), SingleECGFragment.class, R.id.main_content);
//                CommandManager.getInstance().go(command);
//            }
//            break;
//            case R.id.twelve_ecg:{
//                FragmentCommand command = new FragmentCommand((HomeActivity)getContext(), TwelveECGFragment.class, R.id.main_content);
//                CommandManager.getInstance().go(command);
//            }
//            break;
//            case R.id.bp:{
//                FragmentCommand command = new FragmentCommand((HomeActivity)getContext(), BloodPressureFragment.class, R.id.main_content);
//                CommandManager.getInstance().go(command);
//            }
//            break;
//            case R.id.assistant:{
//                FragmentCommand command = new FragmentCommand((HomeActivity)getContext(), DFTHAssistantFragment.class, R.id.main_content);
//                CommandManager.getInstance().go(command);
//            }
//            break;
        }

    }
}
