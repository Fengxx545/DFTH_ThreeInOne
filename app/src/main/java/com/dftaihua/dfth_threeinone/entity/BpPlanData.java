package com.dftaihua.dfth_threeinone.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/29 0029.
 */
public class BpPlanData implements Serializable{
    private long mBeginTime;
    private long mEndTime;
    private int mAverSSY;
    private int mAverSZY;
    private int mAverBeat;
    private int mEffectTimes;
    private long mSetPlanTime;
    private int mStandard;
    private int mPattern;

    public long getBeginTime() {
        return mBeginTime;
    }

    public void setBeginTime(long beginTime) {
        mBeginTime = beginTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public int getAverSSY() {
        return mAverSSY;
    }

    public void setAverSSY(int averSSY) {
        mAverSSY = averSSY;
    }

    public int getAverSZY() {
        return mAverSZY;
    }

    public void setAverSZY(int averSZY) {
        mAverSZY = averSZY;
    }

    public int getAverBeat() {
        return mAverBeat;
    }

    public void setAverBeat(int averBeat) {
        mAverBeat = averBeat;
    }

    public int getEffectTimes() {
        return mEffectTimes;
    }

    public void setEffectTimes(int effectTimes) {
        mEffectTimes = effectTimes;
    }

    public long getSetPlanTime() {
        return mSetPlanTime;
    }

    public void setSetPlanTime(long setPlanTime) {
        mSetPlanTime = setPlanTime;
    }

    public int getStandard() {
        return mStandard;
    }

    public void setStandard(int standard) {
        mStandard = standard;
    }

    public int getPattern() {
        return mPattern;
    }

    public void setPattern(int pattern) {
        mPattern = pattern;
    }
}
