package com.dftaihua.dfth_threeinone.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.adapter.EcgHistoryAdapter;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.entity.BpGroupData;
import com.dftaihua.dfth_threeinone.entity.BpPlanData;
import com.dftaihua.dfth_threeinone.listener.AdapterItemOnclickListener;
import com.dftaihua.dfth_threeinone.listener.BpDataItemClickListener;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.model.bp.BpPlan;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2017/2/17 0017.
 */
public class BpDataDialog extends Dialog {
    public static DataAdapter mAdapter;
    private BpGroupData mGroupData;
    private List<BpPlanData> mPlanDatas;
    private Context mContext;
    private BpDataView mSelectView;
    private static BpPlanData mSelectPlanData;

    public BpDataDialog(Context context, BpGroupData groupData) {
        super(context);
        mContext = context;
        mAdapter = new DataAdapter();
        mGroupData = groupData;
        mPlanDatas = groupData.getPlanDatas();
    }

    public BpDataDialog(Context context, int themeResId, BpGroupData groupData) {
        super(context, themeResId);
        mContext = context;
        mAdapter = new DataAdapter();
        mGroupData = groupData;
        mPlanDatas = groupData.getPlanDatas();
    }

    public static class Builder {
        private BpGroupData mGroupData;
        private String mCurrentMonth;
        private Context mmContxt;

        public Builder(Context context, BpGroupData groupData) {
            mmContxt = context;
            mGroupData = groupData;
            mCurrentMonth = TimeUtils.getTimeStr(groupData.getTime(), "yyyy-MM-dd");
        }

        public BpDataDialog create() {
            LayoutInflater inflater = (LayoutInflater) mmContxt
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final BpDataDialog dialog = new BpDataDialog(mmContxt, R.style.Dialog, mGroupData);
            View layout = inflater.inflate(R.layout.dialog_bp_data, null);
            String month = TimeUtils.getString(mGroupData.getTime(), "yyyy-MM");
            final TextView monthTv = ((TextView) layout.findViewById(R.id.dialog_bp_data_date_tv));
            monthTv.setText(month);
            ((ImageView) layout.findViewById(R.id.dialog_bp_data_after_month_iv)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = UserManager.getInstance().getDefaultUser().getUserId();
                    mCurrentMonth = TimeUtils.getAfterMonth(mCurrentMonth);
                    monthTv.setText(mCurrentMonth.substring(0, 7));
                    List<BpPlan> plans = DfthSDKManager.getManager().getDatabase().getBPPlanByMonth(mCurrentMonth, userId);
                    mGroupData.clear();
                    BpDataUtils.getGroupData(mGroupData, plans);
                    mAdapter.notifyDataSetChanged();
                }
            });
            ((ImageView) layout.findViewById(R.id.dialog_bp_data_before_month_iv)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = UserManager.getInstance().getDefaultUser().getUserId();
                    mCurrentMonth = TimeUtils.getBeforeMonth(mCurrentMonth);
                    monthTv.setText(mCurrentMonth.substring(0, 7));
                    List<BpPlan> plans = DfthSDKManager.getManager().getDatabase().getBPPlanByMonth(mCurrentMonth, userId);
                    mGroupData.clear();
                    BpDataUtils.getGroupData(mGroupData, plans);
                    mAdapter.notifyDataSetChanged();
                }
            });

            (layout.findViewById(R.id.dialog_bp_data_confirm_tv)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.UPDATE_PLAN_DATA, mSelectPlanData));
                    dialog.dismiss();
                }
            });
            (layout.findViewById(R.id.dialog_bp_data_cancel_tv)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ((RecyclerView) layout.findViewById(R.id.dialog_bp_data_rv)).setLayoutManager(new LinearLayoutManager(mmContxt));
            ((RecyclerView) layout.findViewById(R.id.dialog_bp_data_rv)).setAdapter(mAdapter);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, 0);
//            params.width = ThreeInOneApplication.getScreenWidth() * 3 / 4;
//            params.height = ThreeInOneApplication.getScreenHeight() * 2 / 3;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.addContentView(layout, params);
            dialog.setContentView(layout);
            return dialog;
        }
    }

    class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.item_bp_data_parent, parent, false));
//            BpDataView view = new BpDataView(mContext);
//            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            BpPlanData data = mPlanDatas.get(position);
            if (data != null) {
                holder.mTimeTv.setText(TimeUtils.getTimeStr(data.getBeginTime() * 1000, "yyyy-MM-dd HH:mm:ss")
                        + " - " + TimeUtils.getTimeStr(data.getEndTime() * 1000, "yyyy-MM-dd HH:mm:ss"));
                String unit = BpDataUtils.getDefaultUnit(mContext);
                String highAver = data.getAverSSY() == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(mContext, data.getAverSSY()) + unit;
                String lowAver = data.getAverSZY() == 0 ? "- -" : BpDataUtils.getDefaultUnitValue(mContext, data.getAverSZY()) + unit;
                String rateAver = data.getAverBeat() == 0 ? "- -" : data.getAverBeat() + "bpm";
                String times = data.getEffectTimes() == 0 ? "- -" : data.getEffectTimes() + "次";
                holder.mHighTv.setText("高压平均压:" + highAver);
                holder.mLowTv.setText("低压平均压:" + lowAver);
                holder.mRateTv.setText("平均脉率:" + rateAver);
                holder.mMeasureTimesTv.setText("有效测量:" + times);
                holder.position = position;
                holder.mPatternTv.setVisibility(View.GONE);
                if(data.getStandard() == 1){
                    holder.mPatternTv.setVisibility(View.VISIBLE);
                    if(data.getPattern() == 1){
                        holder.mPatternTv.setText("杓形");
                        holder.mPatternTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.bp_data_dipper));
                    } else if(data.getPattern() == 2){
                        holder.mPatternTv.setText("反杓型");
                        holder.mPatternTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.bp_data_opposite_dipper));
                    } else if(data.getPattern() == 3){
                        holder.mPatternTv.setText("非杓型");
                        holder.mPatternTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.bp_data_dipper));
                    } else if(data.getPattern() == 4){
                        holder.mPatternTv.setText("深杓型");
                        holder.mPatternTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.bp_data_opposite_dipper));
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mPlanDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements BpDataItemClickListener {
            TextView mTimeTv;
            TextView mHighTv;
            TextView mLowTv;
            TextView mRateTv;
            TextView mPatternTv;
            TextView mMeasureTimesTv;
            int position;

            public MyViewHolder(View view) {
                super(view);
                BpDataView dataView = (BpDataView) view.findViewById(R.id.item_bp_data_parent_view);
//                mTimeTv = (TextView) view.findViewById(R.id.item_bp_data_time_tv);
//                mHighTv = (TextView) view.findViewById(R.id.item_bp_data_high_tv);
//                mLowTv = (TextView) view.findViewById(R.id.item_bp_data_low_tv);
//                mRateTv = (TextView) view.findViewById(R.id.item_bp_data_rate_tv);
//                mPatternTv = (TextView) view.findViewById(R.id.item_bp_data_pattern_tv);
//                mMeasureTimesTv = (TextView) view.findViewById(R.id.item_bp_data_measure_times_tv);

                mTimeTv = dataView.getTimeTv();
                mHighTv = dataView.getHighTv();
                mLowTv = dataView.getLowTv();
                mRateTv = dataView.getRateTv();
                mPatternTv = dataView.getPatternTv();
                mMeasureTimesTv = dataView.getMeasureTimeTv();
                dataView.setListener(this);
            }

            @Override
            public void onItemClick(View view) {
                if (mSelectView != null) {
                    mSelectView.resetBackground();
                }
                mSelectView = (BpDataView) view;
                if(mSelectView != null){
                    mSelectPlanData = mPlanDatas.get(position);
                } else{
                    mSelectView = null;
                }
            }
        }
    }

}
