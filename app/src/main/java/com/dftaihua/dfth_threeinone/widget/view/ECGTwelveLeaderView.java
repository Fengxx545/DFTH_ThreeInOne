package com.dftaihua.dfth_threeinone.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.measure.listener.ECGLeaderConduct;
import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.LeadersChangeMediator;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.MathUtils;
import com.dftaihua.dfth_threeinone.widget.LeaderRect;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public class ECGTwelveLeaderView extends ScrollView implements LeaderRect.OnCheckListener,Component<LeadersChangeMediator>,ECGLeaderConduct{
    private LeaderRect[] mLeaderRects = new LeaderRect[12];
    private boolean[] mLeaders;
    private LinearLayout mLayout;
    private LeadersChangeMediator mLeadersChangeMediator;
    private static String[] LEADERS_NAME = ThreeInOneApplication.getStrings(R.array.leader_names);
    public ECGTwelveLeaderView(Context context) {
        super(context);
        initView();
    }

    public ECGTwelveLeaderView(Context context, AttributeSet attrs) {
        super(context,attrs);
        initView();
    }

    @Override
    public void setLeaders(boolean[] leaders) {
        mLeaders = leaders;
        addLeaders(mLayout);
    }

    private void initView(){
        setVisibility(GONE);
        mLayout = new LinearLayout(getContext());
        mLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(0, 0);
        param.width = DisplayUtils.dip2px(getContext(),40);
        param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        addView(mLayout, param);
        addLeaders(mLayout);
        setVerticalScrollBarEnabled(false);
    }

    private void addLeaders(LinearLayout layout) {
        if(layout == null){
            return;
        }
        if(mLeaders == null){
            return;
        }
        if(mLayout.getChildCount() > 0){
            return;
        }
        for (int i = 0; i < 12; i++) {
            mLeaderRects[i] = new LeaderRect(getContext(), i);
            mLeaderRects[i].setText(LEADERS_NAME[i]);
            mLeaderRects[i].setChecked(mLeaders[i]);
            mLeaderRects[i].setCheckChangeListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
//            params.width = ThreeInOneApplication.getScreenWidth() / 5;
            params.width = DisplayUtils.dip2px(getContext(),40);
            // TODO: 2016/6/16
            params.height = (ThreeInOneApplication.getScreenHeight() - (ThreeInOneApplication.getScreenWidth() * 152 * 221 / 749 / 212 + DisplayUtils.dip2px(getContext(), 40))) / 13 - 1;
            params.bottomMargin = 2;
            layout.addView(mLeaderRects[i], params);
        }
    }


    @Override
    public boolean onCheckChange(int position, boolean checked) {
        if (MathUtils.search(mLeaders, true, 1) && mLeaders[position]) {
            return false;
        }
        mLeaders[position] = checked;
        if(mLeadersChangeMediator != null){
            mLeadersChangeMediator.selectLeader(mLeaders);
        }
        return true;
    }

    @Override
    public void showLeader() {
        if(getVisibility() == VISIBLE){
            setVisibility(GONE);
        }else{
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void bindMediator(LeadersChangeMediator mediator) {
        mLeadersChangeMediator = mediator;
    }
}
