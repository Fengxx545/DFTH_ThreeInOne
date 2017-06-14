package com.dftaihua.dfth_threeinone.entity;

import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dfth.sdk.model.bp.BpResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class BpGroupData implements Serializable {
    private long mTime;
    private BpDataUtils.QUERY_RULE mRule;
    private List<BpPlanData> mPlanDatas = new ArrayList<>();

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public BpDataUtils.QUERY_RULE getRule() {
        return mRule;
    }

    public void setRule(BpDataUtils.QUERY_RULE rule) {
        mRule = rule;
    }

    public List<BpPlanData> getPlanDatas() {
        return mPlanDatas;
    }

    public void setPlanDatas(List<BpPlanData> planDatas) {
        mPlanDatas = planDatas;
    }

    public void add(BpPlanData data) {
        mPlanDatas.add(data);
    }

    public int size() {
        return mPlanDatas.size();
    }

    public void clear() {
        mPlanDatas.clear();
    }

    public String getTimeDisplay() {
        String str = "";
        switch (mRule) {
            case DAY: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mTime);
                str = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
            }
            break;
            case WEEK: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mTime);
                str = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH)
                        + "--";
                long time1 = TimeUtils.getZeroTime(mTime) + 7 * 24 * 60 * 60 * 1000 - 1;
                if (time1 >= System.currentTimeMillis()) {
                    time1 = System.currentTimeMillis();
                }
                calendar.setTimeInMillis(time1);
                str += calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
            }
            break;
            case MONTH: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mTime);
                str = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1);
            }
            break;
            case AMB: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mTime);
                str = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
            }
            break;
        }
        return str;
    }
}
