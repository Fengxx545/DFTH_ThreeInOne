package com.dftaihua.dfth_threeinone.measure.listener;
import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.WaveViewMediator;
import com.dfth.sdk.model.ecg.ECGMeasureData;
import com.dfth.sdk.model.ecg.EcgDataTransmitted;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public interface ECGMeasureConduct extends Component<WaveViewMediator>{
    /**
     * 绘制图
     * @param ecgData
     */
    public void drawWave(ECGMeasureData ecgData);


    /**
     * 绘制导联脱落
     * @param leaderOut
     */
    public void drawLeaderOut(boolean[] leaderOut);

    /**
     * 开始绘制心电
     */
    public void startDraw();
    /**
     * 恢复绘制
     */
    public void resumeDraw();

    /**
     * 暂停绘制
     */
    public void pauseDraw();

    /**
     * 销毁绘制
     */
    public void destroyDraw();



    public void endDraw();


    public boolean isDrawing();

    /**
     * 设置是否显示导联
     * @param display
     */
    public void setIsDisplayLeaders(boolean display);


    public void leaderChange(boolean[] leaders);


    public void setLine(int line);

}
