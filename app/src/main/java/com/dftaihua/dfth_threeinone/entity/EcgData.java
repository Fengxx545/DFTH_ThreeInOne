package com.dftaihua.dfth_threeinone.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/3 0003.
 */
public class EcgData implements Serializable {
    private String mEcgDataId;                  //心电数据ID
    private String mEcgDataBeginTime;           //测量开始时间
    private String mEcgDataTotalTime;           //测量总时间
    private String mEcgDataAverRate;            //平均心率
    private String mEcgDataHighest;             //最高心率
    private String mEcgDataLowest;              //最低心率
    private String mEcgDataPrematureBeat1;      //室性早搏
    private String mEcgDataPrematureBeat2;      //室上性早搏
    private String mEcgDataAnalysis;            //医师分析

    public String getEcgDataId() {
        return mEcgDataId;
    }

    public void setEcgDataId(String mEcgDataId) {
        this.mEcgDataId = mEcgDataId;
    }

    public String getEcgDataBeginTime() {
        return mEcgDataBeginTime;
    }

    public void setEcgDataBeginTime(String mEcgDataBeginTime) {
        this.mEcgDataBeginTime = mEcgDataBeginTime;
    }

    public String getEcgDataTotalTime() {
        return mEcgDataTotalTime;
    }

    public void setEcgDataTotalTime(String mEcgDataTotalTime) {
        this.mEcgDataTotalTime = mEcgDataTotalTime;
    }

    public String getEcgDataAverRate() {
        return mEcgDataAverRate;
    }

    public void setEcgDataAverRate(String mEcgDataAverRate) {
        this.mEcgDataAverRate = mEcgDataAverRate;
    }

    public String getEcgDataHighest() {
        return mEcgDataHighest;
    }

    public void setEcgDataHighest(String mEcgDataHighest) {
        this.mEcgDataHighest = mEcgDataHighest;
    }

    public String getEcgDataLowest() {
        return mEcgDataLowest;
    }

    public void setEcgDataLowest(String mEcgDataLowest) {
        this.mEcgDataLowest = mEcgDataLowest;
    }

    public String getEcgDataPrematureBeat1() {
        return mEcgDataPrematureBeat1;
    }

    public void setEcgDataPrematureBeat1(String mEcgDataPrematureBeat1) {
        this.mEcgDataPrematureBeat1 = mEcgDataPrematureBeat1;
    }

    public String getEcgDataPrematureBeat2() {
        return mEcgDataPrematureBeat2;
    }

    public void setEcgDataPrematureBeat2(String mEcgDataPrematureBeat2) {
        this.mEcgDataPrematureBeat2 = mEcgDataPrematureBeat2;
    }

    public String getEcgDataAnalysis() {
        return mEcgDataAnalysis;
    }

    public void setEcgDataAnalysis(String mEcgDataAnalysis) {
        this.mEcgDataAnalysis = mEcgDataAnalysis;
    }


}
