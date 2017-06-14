package com.dftaihua.dfth_threeinone.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.adapter.EcgHistoryAdapter;
import com.dftaihua.dfth_threeinone.adapter.MyFragmentPagerAdapter;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.fragment.BaseBPDataFragment;
import com.dftaihua.dfth_threeinone.fragment.BaseBPDataFragment.BpManualDataFragment;
import com.dftaihua.dfth_threeinone.fragment.BaseBPDataFragment.BpPlanDataFragment;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.BpPlanUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.TitleView;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.bluetooth.Action;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/10 0010.
 */
public class BpResultActivity extends BaseActivity implements TitleView.OnTwoTitleClickListener {
    private BpPlanDataFragment mPlanFragment;
    private BpManualDataFragment mManualFragment;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
    private ArrayList<Fragment> mListFragments = new ArrayList<>();
    private int mDataType;
    protected List<BpPlan> mBpPlans;

    public BpResultActivity() {
        mStatus = TWO_TITLE_VISIBLE | BACK_VISIBLE;
        mTwoTitleFirstRes = R.string.title_activity_bp_result_1;
        mTwoTitleSecondRes = R.string.title_activity_bp_result_2;
        mTitleColorRes = R.color.google_white;
    }

    @Override
    public View initView() {
        Intent intent = getIntent();
        mDataType = intent.getIntExtra(Constant.BP_DATA_TYPE, 0);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_bp_result, null);
        mViewPager = (ViewPager) view.findViewById(R.id.activity_bp_result_viewpager);
        return view;
    }

    @Override
    public void initData() {
        showProgress();
        DispatchUtils.performAsnycAction(new Action(0) {
            @Override
            protected void perform() {
                mPlanFragment = (BpPlanDataFragment) BaseBPDataFragment.createFragment(Constant.BP_PLAN_DATA);
                mManualFragment = (BpManualDataFragment) BaseBPDataFragment.createFragment(Constant.BP_MANUAL_DATA);
                mListFragments.add(mPlanFragment);
                mListFragments.add(mManualFragment);
                mBpPlans = BpPlanUtils.getAllPlans();
            }
        }, new DfthCallBack() {
            @Override
            public void onResponse(DfthResult response) {
                mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mListFragments);
                mViewPager.setAdapter(mMyFragmentPagerAdapter);
                mTitleView.setListener(BpResultActivity.this);
                mTitleView.onClick(mDataType == Constant.BP_PLAN_DATA ? mTitleView.getTwoTitleFirstTv() : mTitleView.getTwoTitleSecondTv());
                dismissProgress();
            }
        });
    }


    @Override
    public void onTitleClick(int type) {
        mDataType = type;
        if (type == 1) {
            if (mBpPlans != null && mBpPlans.size() > 0) {
                boolean isValid = false;
                for (int i = 0; i < mBpPlans.size(); i++) {
                    BpPlan plan = mBpPlans.get(i);
                    List<BpResult> results;
                    if (plan.getStatus() == BpPlan.STATE_RUNNING) {
                        isValid = true;
                        break;
                    } else {
                        results = BpPlanUtils.getBpResultByPlan(plan);
                        if (results != null && results.size() > 0) {
                            for (int j = 0; j < results.size(); j++) {
                                if (results.get(j).isValidData()) {
                                    isValid = true;
                                    break;
                                }
                            }
                        }
                        if (isValid) {
                            break;
                        }
                    }
                }
                if (!isValid) {
                    ToastUtils.showShort(this, R.string.no_plan_data);
                    return;
                }
            } else {
                ToastUtils.showShort(this, R.string.no_plan_data);
                return;
            }
            mTitleView.chooseTitle(TitleView.TITLE_PLAN_DATA);
            mViewPager.setCurrentItem(0, true);
        } else {
            mTitleView.chooseTitle(TitleView.TITLE_MANUAL_DATA);
            mViewPager.setCurrentItem(1, true);
        }
    }

    public int getDataType() {
        return mDataType;
    }
}

