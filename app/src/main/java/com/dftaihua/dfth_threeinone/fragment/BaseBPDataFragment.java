package com.dftaihua.dfth_threeinone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.AmbChartActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.dialog.CalendarDialog;
import com.dftaihua.dfth_threeinone.entity.BpGroupData;
import com.dftaihua.dfth_threeinone.entity.BpPlanData;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.BpPlanUtils;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.AMBChartView;
import com.dftaihua.dfth_threeinone.widget.BpDataDialog;
import com.dftaihua.dfth_threeinone.widget.DividerGridItemDecoration;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public abstract class BaseBPDataFragment extends Fragment implements View.OnClickListener,
        AMBChartView.OnDoubleClickListener, AMBChartView.OnSlipListener,
        CalendarDialog.CalendarClickListener{
    protected RecyclerView mResultRv;
    protected ResultAdapter mAdapter;
    protected CalendarDialog mCalendarDialog;
    protected RelativeLayout mTimeSelectRl;
    protected TextView mTimeSelectTv;
    protected LinearLayout mTwiceTimeSelectLl;
    protected LinearLayout mTwiceTimeSelectCenterLl;
    protected TextView mTwiceTimeSelectTv;
    protected AMBChartView mAmbChartView;
    protected List<BpResult> mResults = new ArrayList<>();
    protected ImageView mBeforeDayTv;
    protected ImageView mAfterDayTv;
    protected TextView mHighAverTv;
    protected TextView mLowAverTv;
    protected TextView mRateAverTv;
    protected TextView mEffectTimesTv;
    protected List<BpPlan> mBpPlans;
    protected String mSelectDay;
    protected int mDataType;
    protected BpPlan mLastPlan;

    public BaseBPDataFragment() {
        super();
    }

    /*数据处理结束*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bp_result, null);
        mResultRv = (RecyclerView) view.findViewById(R.id.fragment_bp_result_rv);
        mTimeSelectRl = (RelativeLayout) view.findViewById(R.id.fragment_bp_result_time_rl);
        mTimeSelectTv = (TextView) view.findViewById(R.id.fragment_bp_result_time_tv);
        mTwiceTimeSelectLl = (LinearLayout) view.findViewById(R.id.fragment_bp_result_twice_time_ll);
        mTwiceTimeSelectCenterLl = (LinearLayout) view.findViewById(R.id.fragment_bp_result_twice_time_center_ll);
        mTwiceTimeSelectTv = (TextView) view.findViewById(R.id.fragment_bp_result_twice_time_tv);
        mBeforeDayTv = (ImageView) view.findViewById(R.id.fragment_bp_result_twice_before_day_iv);
        mAfterDayTv = (ImageView) view.findViewById(R.id.fragment_bp_result_twice_after_day_iv);
        mAmbChartView = (AMBChartView) view.findViewById(R.id.fragment_bp_result_acv);
        mHighAverTv = (TextView) view.findViewById(R.id.fragment_bp_result_high_aver_tv);
        mLowAverTv = (TextView) view.findViewById(R.id.fragment_bp_result_low_aver_tv);
        mRateAverTv = (TextView) view.findViewById(R.id.fragment_bp_result_rate_aver_tv);
        mEffectTimesTv = (TextView) view.findViewById(R.id.fragment_bp_result_effect_times_tv);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 6);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 3 == 0) {
                    return 3;
                } else if (position % 3 == 1) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mResultRv.setLayoutManager(manager);
        mAdapter = new ResultAdapter();
        mResultRv.setAdapter(mAdapter);
        mResultRv.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        mTimeSelectRl.setOnClickListener(this);
        mTwiceTimeSelectCenterLl.setOnClickListener(this);
        mBeforeDayTv.setOnClickListener(this);
        mAfterDayTv.setOnClickListener(this);
        mAmbChartView.setDoubleClickListener(this);
        mAmbChartView.setOnSlipListener(this);
        mCalendarDialog = new CalendarDialog(getActivity(), DfthDevice.BpDevice);
        mCalendarDialog.setCalendarClickListener(this);
        mCalendarDialog.initData();
        initView();
        return view;
    }

    protected String getUserId(){
        return UserManager.getInstance().getDefaultUser().getUserId();
    }

    protected void setAverText() {
        if (mResults != null && mResults.size() > 0) {
            int[] avers = BpDataUtils.getAverData(mResults);
            String unit = BpDataUtils.getDefaultUnit(getActivity());
            String high = avers[0] == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(getActivity(), avers[0]) + unit;
            String low = avers[1] == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(getActivity(), avers[1]) + unit;
            String rate = avers[2] == 0 ? "- -" : avers[2] + "bpm";
            String times = avers[3] == 0 ? "- -" : avers[3] + "次";
            mHighAverTv.setText("高压平均压:" + high);
            mLowAverTv.setText("低压平均压:" + low);
            mRateAverTv.setText("平均脉率:" + rate);
            mEffectTimesTv.setText("有效测量:" + times);
        } else {
            mHighAverTv.setText("高压平均压:- -");
            mLowAverTv.setText("低压平均压:- -");
            mRateAverTv.setText("平均脉率:- -");
            mEffectTimesTv.setText("有效测量:- -");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getBpResults();
        setData();
    }

    @Override
    public void onDoubleClick() {
        if (mResults == null || mResults.size() == 0) {
            ToastUtils.showLong(getActivity(), "没有血压数据！");
        } else {
            Intent intent = new Intent(getActivity(), AmbChartActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("BpResultList", (Serializable) mResults);
            bundle.putSerializable("BpPlan", mLastPlan);
            bundle.putString("day", mSelectDay);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }

    protected abstract List<BpResult> getBpResults();

    protected abstract void initView();

    protected abstract void setData();

    public static final class BpPlanDataFragment extends BaseBPDataFragment {

        public BpPlanDataFragment() {
            EventBus.getDefault().register(this);
        }

        @Override
        protected void initView() {
            mTwiceTimeSelectLl.setVisibility(View.GONE);
            mTimeSelectRl.setVisibility(View.VISIBLE);
        }

        @Override
        protected void setData() {
            if(mLastPlan != null){
                mTimeSelectTv.setText(TimeUtils.getTimeStr(mLastPlan.getStartTime() * 1000, "yyyy.MM.dd HH:mm")
                        + " - " + TimeUtils.getTimeStr(mLastPlan.getEndTime() * 1000, "yyyy.MM.dd HH:mm"));
                boolean isHighValue = BpDataUtils.isHighValue(mResults);
                mAmbChartView.setDatas(mResults, mSelectDay, mLastPlan, isHighValue, true);
            }
            setAverText();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected List<BpResult> getBpResults() {
            mBpPlans = BpPlanUtils.getAllPlans();
            if (mBpPlans != null && mBpPlans.size() > 0) {
                for(int i = 0; i < mBpPlans.size(); i++){
                    BpPlan plan = mBpPlans.get(i);
                    if(plan.getStatus() == BpPlan.STATE_RUNNING){
                        mLastPlan = mBpPlans.get(i);
                        break;
                    } else{
                        List<BpResult> results = BpPlanUtils.getBpResultByPlan(plan);
                        boolean isValid = false;
                        if(results != null && results.size() > 0){
                            for(int j = 0; j < results.size(); j++){
                                if(results.get(j).isValidData()){
                                    isValid = true;
                                    mLastPlan = mBpPlans.get(i);
                                    break;
                                }
                            }
                            if(isValid){
                                break;
                            }
                        }
                    }
                }
                if(mLastPlan == null){
                    mLastPlan = mBpPlans.get(0);
                }
                mSelectDay = TimeUtils.getString(mLastPlan.getSetPlanTime(),"yyyy-MM-dd");
                return mResults = BpPlanUtils.getBpResultByPlan(mLastPlan);
            }
            return mResults = null;
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMessage(DfthMessageEvent event){
            if(event.getEventName().equals(EventNameMessage.UPDATE_PLAN_DATA)){
                BpPlanData data = (BpPlanData) event.getData();
                if(data != null){
                    long planTime = data.getSetPlanTime();
                    mLastPlan = DfthSDKManager.getManager().getDatabase().getBPPlan(planTime);
                    mSelectDay = TimeUtils.getString(mLastPlan.getSetPlanTime(),"yyyy-MM-dd");
                    mResults = BpPlanUtils.getBpResultByPlan(mLastPlan);
                    setData();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            EventBus.getDefault().unregister(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fragment_bp_result_time_rl:
                    String userId = UserManager.getInstance().getDefaultUser().getUserId();
                    List<BpPlan> allPlans = BpPlanUtils.getAllPlans();
                    if (allPlans == null || allPlans.size() == 0) {
                        ToastUtils.showShort(getActivity(), "没有动态计划数据！");
                        return;
                    }
                    if(mLastPlan == null){
                        mLastPlan = allPlans.get(0);
                    }
                    String month = getHasValidPlanMonth(allPlans);
                    if(TextUtils.isEmpty(month)){
                        ToastUtils.showShort(getActivity(), "没有动态计划数据！");
                        return;
                    }
                    List<BpPlan> plans = DfthSDKManager.getManager().getDatabase().getBPPlanByMonth(month, userId);
                    BpGroupData groupData = new BpGroupData();
                    groupData.setTime(mLastPlan.getStartTime() * 1000);
                    BpDataUtils.getGroupData(groupData, plans);
                    BpDataDialog.Builder builder = new BpDataDialog.Builder(getActivity(), groupData);
                    BpDataDialog dialog = builder.create();
                    Window dialogWindow = dialog.getWindow();
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    dialogWindow.setGravity(Gravity.CENTER);
                    lp.width = ThreeInOneApplication.getScreenWidth() - DisplayUtils.dip2px(getActivity(), 20);
                    lp.height = ThreeInOneApplication.getScreenHeight() * 3 / 4;
                    dialogWindow.setAttributes(lp);
                    dialog.show();
                    break;
            }
        }

        private String getHasValidPlanMonth(List<BpPlan> plans){
            for(int i = 0; i < plans.size(); i++){
                BpPlan plan = plans.get(i);
                String month = TimeUtils.getTimeStr(plan.getStartTime() * 1000, "yyyy-MM-dd");
                if(plan.getStatus() == BpPlan.STATE_RUNNING){
                    return month;
                } else{
                    List<BpResult> results = BpPlanUtils.getBpResultByPlan(plan);
                    if (results != null && results.size() > 0) {
                        for(int j = 0; j < results.size(); j++){
                            BpResult result = results.get(j);
                            if(result.isValidData()){
                                return month;
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public void onSlip(int direction) {

        }

        @Override
        public void clickConfirm() {

        }

        @Override
        public void clickCancel() {

        }
    }

    public static final class BpManualDataFragment extends BaseBPDataFragment {

        @Override
        protected void initView() {
            mTwiceTimeSelectLl.setVisibility(View.VISIBLE);
            mTimeSelectRl.setVisibility(View.GONE);
        }

        @Override
        protected void setData() {
            setAverText();
            boolean isHighValue = BpDataUtils.isHighValue(mResults);
            mAmbChartView.setDatas(mResults, mSelectDay, null, isHighValue, true);
            mAdapter.notifyDataSetChanged();
            mTwiceTimeSelectTv.setText(mSelectDay);
        }

        @Override
        protected List<BpResult> getBpResults() {
            mSelectDay = mCalendarDialog.getLastDay();
            return mResults = DfthSDKManager.getManager().getDatabase().getBPResultByDay(mSelectDay, getUserId());
        }

        private void getSelectDayData(String day) {
            mResults = DfthSDKManager.getManager().getDatabase().getBPResultByDay(day, getUserId());
            mSelectDay = day;
            mCalendarDialog.setSelectDate(day);
            setData();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fragment_bp_result_twice_time_center_ll:
                    if (mCalendarDialog.isShowing()) {
                        mCalendarDialog.dismiss();
                    } else {
                        mCalendarDialog.show();
                    }
                    break;
                case R.id.fragment_bp_result_twice_before_day_iv:
                    String beforeDay = mCalendarDialog.getAfterDay();
                    if (!TextUtils.isEmpty(beforeDay)) {
                        getSelectDayData(beforeDay);
                    } else {
                        ToastUtils.showShort(getActivity(), "此日期之前无数据！");
                    }
                    break;
                case R.id.fragment_bp_result_twice_after_day_iv:
                    String afterDay = mCalendarDialog.getBeforeDay();
                    if (!TextUtils.isEmpty(afterDay)) {
                        getSelectDayData(afterDay);
                    } else {
                        ToastUtils.showShort(getActivity(), "此日期之后无数据！");
                    }
                    break;
            }
        }

        @Override
        public void onSlip(int direction) {
            if (direction == Constant.BP_SLIP_LEFT) {
                String day = mCalendarDialog.getBeforeDay();
                if (!TextUtils.isEmpty(day)) {
                    getSelectDayData(day);
                } else {
                    ToastUtils.showShort(getActivity(), "此日期之后无数据！");
                }
            } else {
                String day = mCalendarDialog.getAfterDay();
                if (!TextUtils.isEmpty(day)) {
                    getSelectDayData(day);
                } else {
                    ToastUtils.showShort(getActivity(), "此日期之前无数据！");
                }
            }
        }

        @Override
        public void clickConfirm() {
            if (mCalendarDialog.checkOneDayHasBpData(mCalendarDialog.getSelectDate())) {
                mCalendarDialog.dismiss();
                String day = mCalendarDialog.getSelectDate();
                mTwiceTimeSelectTv.setText(day);
                getSelectDayData(day);
            } else {
                ToastUtils.showShort(getActivity(), "选中日期没有数据!");
            }
        }

        @Override
        public void clickCancel() {
            mCalendarDialog.dismiss();
        }
    }

    class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {

        @Override
        public ResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ResultAdapter.MyViewHolder holder = new ResultAdapter.MyViewHolder(LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_bp_result, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(ResultAdapter.MyViewHolder holder, int position) {
            if (position == 0) {
                holder.tv.setText("时间");
            } else if (position == 1) {
                holder.tv.setText("高压/低压");
            } else if (position == 2) {
                holder.tv.setText("脉率");
            } else if (position % 3 == 0) {
                String measureTime = TimeUtils.getTimeStr(mResults.get((position - 3) / 3).getMeasureTime(),
                        "yyyy-MM-dd HH:mm:ss");
                holder.tv.setText(measureTime);
//                holder.tv.setWidth(100);
            } else if (position % 3 == 1) {
                int high = mResults.get((position - 3) / 3).getSbp();
                int low = mResults.get((position - 3) / 3).getDbp();
                StringBuffer builder = new StringBuffer();
                builder.append(high == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(getActivity(), high))
                        .append("/")
                        .append(low == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(getActivity(), low));
                holder.tv.setText(builder.toString());
            } else if (position % 3 == 2) {
                int rate = mResults.get((position - 3) / 3).getPulseRate();
                holder.tv.setText(rate == 0 ? "- -" : String.valueOf(rate));
            }
        }

        @Override
        public int getItemCount() {
            int count = (mResults == null || mResults.size() == 0) ? 0 : (mResults.size() * 3 + 3);
            return count;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.item_bp_result_num);
            }
        }
    }

    public static BaseBPDataFragment createFragment(int dataType) {
        if (dataType == Constant.BP_PLAN_DATA) {
            return new BpPlanDataFragment();
        } else {
            return new BpManualDataFragment();
        }
    }
}