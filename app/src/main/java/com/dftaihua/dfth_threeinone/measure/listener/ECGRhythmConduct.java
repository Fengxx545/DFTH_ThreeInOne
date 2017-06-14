package com.dftaihua.dfth_threeinone.measure.listener;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public interface ECGRhythmConduct {
    /**
     * 设置心率
     * @param rate
     */
    public void setHeartRate(int rate);
    /**
     * 设置测量时间
     * @param time
     */
    public void setMeasureTime(String time);

    /**
     * 重置设置
     */
    public void reset();
}
