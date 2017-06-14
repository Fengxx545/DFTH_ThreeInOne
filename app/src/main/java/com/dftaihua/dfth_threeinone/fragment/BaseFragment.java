package com.dftaihua.dfth_threeinone.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.widget.TitleView;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public abstract class BaseFragment extends Fragment{
    public static final long TITLE_VISIBLE =  0x0000000000000001L;
    public static final long BACK_VISBLE =    0x0000000000000010L;   //返回按钮
    public static final long SAVE_VISBLE =    0x0000000000000100L;   //保存按钮
    protected long mStatus =                  0x0000000000000000L;
    protected View mRootView;
    protected TitleView mTitleView;

    protected int mTitleNameRes = R.string.empty;
    protected String mTitleNameStr = "";

    public BaseFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootView = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
        if((mStatus & TITLE_VISIBLE) > 0){
            mTitleView = new TitleView(getActivity());
            mTitleView.setId(R.id.main_title);
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = DisplayUtils.dip2px(getActivity(), 50);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rootView.addView(mTitleView, params);
            params = new RelativeLayout.LayoutParams(0,0);
        }

        mRootView = onChildCreateView(inflater, container, savedInstanceState);
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.addRule(RelativeLayout.BELOW, R.id.main_title);
        rootView.addView(mRootView, params);

        if (mTitleView != null) {
            mTitleView.select(this);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected abstract View onChildCreateView(LayoutInflater inflater,
                                              ViewGroup container, Bundle savedInstanceState);

    public long getStatus(){
        return mStatus;
    }

    public int getTitleNameRes() {
        return mTitleNameRes;
    }

    public String getTitleNameStr(){
        return mTitleNameStr;
    }
}
