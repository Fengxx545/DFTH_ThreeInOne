package com.dftaihua.dfth_threeinone.mediator;

import com.dfth.sdk.model.ecg.ECGResult;

/**
 * Created by leezhiqiang on 2017/3/21.
 */

public interface MeasureMediator extends LeadersChangeMediator,WaveViewMediator,ECGSymptomMediator{

    /**
     * 测量按钮点击
     */
    public void measureButtonClick();

    /**
     * 少于25秒测量
     */
    public void lessThan25Second(boolean confirm);

    /**
     * 数据处理完成
     * @param result
     */
    public void doProcessResult(ECGResult result);
}
