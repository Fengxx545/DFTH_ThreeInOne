package com.dftaihua.dfth_threeinone.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.fragment.BaseMemberItemFragment;
import com.dftaihua.dfth_threeinone.widget.TitleView;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2016/12/12 14:54
*/
public class MemberInfoItemActivity extends BaseActivity implements View.OnClickListener,TitleView.OnSaveClickListener {
    private BaseMemberItemFragment mFragment;
    private int mItemType;

    public MemberInfoItemActivity() {
        mTitleNameRes = R.string.title_activity_self_info;
        mTitleColorRes = R.color.google_white;
        mTitleNameColorRes  = R.color.google_black;
        mStatus = TITLE_VISIBLE | BACK_VISIBLE | SAVE_VISIBLE;
    }

    @Override
    public View initView() {
        initializeBasicParams();
        View view = LayoutInflater.from(this).inflate(R.layout.activity_member_item, null);
        initializeFragment();
        mTitleView.setSaveListener(this);
        return view;
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeBasicParams(){
        Intent intent = getIntent();
        mItemType = intent.getIntExtra(Constant.MEMBER_ITEM_TYPE,Constant.MemberInfoItem.EMERGENCY_CONTACT);
    }

    private void initializeFragment() {
        mFragment = BaseMemberItemFragment.createFragment(mItemType);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.activity_member_item,mFragment);
        transaction.replace(R.id.activity_member_item, mFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void onSaveClick() {
        mFragment.save();
    }
}
