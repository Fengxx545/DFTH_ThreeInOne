package com.dftaihua.dfth_threeinone.mediator;

/**
 * Created by leezhiqiang on 2017/3/30.
 */

public interface WaveViewMediator {

    /**
     * 长按屏幕
     */
    public void longPressScreen();

    /**
     * 点击屏幕
     */
    public void singlePressScreen();

    /**
     * 双击屏幕
     */
    public void doublePressScreen();

    /**
     * 波形改变
     */
    public void waveChange(int deltaPos);
}