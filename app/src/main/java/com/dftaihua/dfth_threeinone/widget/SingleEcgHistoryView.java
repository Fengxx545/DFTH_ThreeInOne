package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.entity.EcgData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/3 0003.
 */
public class SingleEcgHistoryView extends LinearLayout {

    private ListView mHistoryLv;
    private HistoryAdapter mHistoryAdapter;
    private List<EcgData> mEcgdatas;

    public SingleEcgHistoryView(Context context) {
        super(context);
        initView(context);
    }

    public SingleEcgHistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SingleEcgHistoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        addHistory();
        LayoutInflater.from(context).inflate(R.layout.fragment_single_ecg_history, this, true);
        mHistoryLv = (ListView) findViewById(R.id.fragment_single_ecg_history_lv);
        mHistoryAdapter = new HistoryAdapter(context);
        mHistoryLv.setAdapter(mHistoryAdapter);
//        mHistoryAdapter.notifyDataSetChanged();
    }

    class HistoryAdapter extends BaseAdapter {

        private Context mmContext;

        public HistoryAdapter(Context context) {
            mmContext = context;
        }

        @Override
        public int getCount() {
            return mEcgdatas == null ? 0 : mEcgdatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mEcgdatas == null ? null : mEcgdatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mmContext).inflate(R.layout.item_single_ecg_history, null);
                holder.mmHistoryTimeTv = (TextView) convertView.findViewById(R.id.item_single_ecg_history_time_tv);
                holder.mmHistoryRateTv = (TextView) convertView.findViewById(R.id.item_single_ecg_aver_rate_tv);
                holder.mmHistoryPrematureBeatTv = (TextView) convertView.findViewById(R.id.item_single_ecg_history_premature_beat_tv);
                holder.mmHistoryAnalysisTv = (TextView) convertView.findViewById(R.id.item_single_ecg_history_analysis_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            EcgData data = (EcgData) getItem(position);
            if (data != null) {
                holder.mmHistoryTimeTv.setText(data.getEcgDataBeginTime() + data.getEcgDataTotalTime());
                holder.mmHistoryRateTv.setText(data.getEcgDataAverRate() + data.getEcgDataHighest() + data.getEcgDataLowest());
                holder.mmHistoryPrematureBeatTv.setText(data.getEcgDataPrematureBeat1() + data.getEcgDataPrematureBeat2());
                holder.mmHistoryAnalysisTv.setText(data.getEcgDataAnalysis());
            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView mmHistoryTimeTv;
        TextView mmHistoryRateTv;
        TextView mmHistoryPrematureBeatTv;
        TextView mmHistoryAnalysisTv;
    }

    public void setHistorys(List<EcgData> datas) {
        mEcgdatas = datas;
        mHistoryAdapter.notifyDataSetChanged();
    }

    private void addHistory(){
        mEcgdatas = new ArrayList<>();

        EcgData data1 = new EcgData();
        data1.setEcgDataBeginTime("2016.06.15 14:12:12 ");
        data1.setEcgDataTotalTime("(1 h 5 m 2 s)");
        data1.setEcgDataAverRate("平均心率：65bpm");
        data1.setEcgDataHighest(" (最高 213 | ");
        data1.setEcgDataLowest("最低 111)");
        data1.setEcgDataPrematureBeat1("室性早搏：0 bpm | ");
        data1.setEcgDataPrematureBeat2("室上性早搏：0 bpm");
        data1.setEcgDataAnalysis("医师分析：已经病入膏肓，建议准备后事!");

        EcgData data2 = new EcgData();
        data2.setEcgDataBeginTime("2016.06.15 14:12:12 ");
        data2.setEcgDataTotalTime("(1 h 5 m 2 s)");
        data2.setEcgDataAverRate("平均心率：65bpm");
        data2.setEcgDataHighest(" (最高 213 | ");
        data2.setEcgDataLowest("最低 111)");
        data2.setEcgDataPrematureBeat1("室性早搏：0 bpm | ");
        data2.setEcgDataPrematureBeat2("室上性早搏：0 bpm");
        data2.setEcgDataAnalysis("医师分析：已经病入膏肓，建议准备后事!");

        EcgData data3 = new EcgData();
        data3.setEcgDataBeginTime("2016.06.15 14:12:12 ");
        data3.setEcgDataTotalTime("(1 h 5 m 2 s)");
        data3.setEcgDataAverRate("平均心率：65bpm");
        data3.setEcgDataHighest(" (最高 213 | ");
        data3.setEcgDataLowest("最低 111)");
        data3.setEcgDataPrematureBeat1("室性早搏：0 bpm | ");
        data3.setEcgDataPrematureBeat2("室上性早搏：0 bpm");
        data3.setEcgDataAnalysis("医师分析：已经病入膏肓，建议准备后事!");

        EcgData data4 = new EcgData();
        data4.setEcgDataBeginTime("2016.06.15 14:12:12 ");
        data4.setEcgDataTotalTime("(1 h 5 m 2 s)");
        data4.setEcgDataAverRate("平均心率：65bpm");
        data4.setEcgDataHighest(" (最高 213 | ");
        data4.setEcgDataLowest("最低 111)");
        data4.setEcgDataPrematureBeat1("室性早搏：0 bpm | ");
        data4.setEcgDataPrematureBeat2("室上性早搏：0 bpm");
        data4.setEcgDataAnalysis("医师分析：已经病入膏肓，建议准备后事!");

        EcgData data5 = new EcgData();
        data5.setEcgDataBeginTime("2016.06.15 14:12:12 ");
        data5.setEcgDataTotalTime("(1 h 5 m 2 s)");
        data5.setEcgDataAverRate("平均心率：65bpm");
        data5.setEcgDataHighest(" (最高 213 | ");
        data5.setEcgDataLowest("最低 111)");
        data5.setEcgDataPrematureBeat1("室性早搏：0 bpm | ");
        data5.setEcgDataPrematureBeat2("室上性早搏：0 bpm");
        data5.setEcgDataAnalysis("医师分析：已经病入膏肓，建议准备后事!");

        mEcgdatas.add(data1);
        mEcgdatas.add(data2);
        mEcgdatas.add(data3);
        mEcgdatas.add(data4);
        mEcgdatas.add(data5);
    }
}
