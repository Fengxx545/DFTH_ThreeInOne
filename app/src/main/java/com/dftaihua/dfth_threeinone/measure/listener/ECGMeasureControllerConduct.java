package com.dftaihua.dfth_threeinone.measure.listener;

import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.MeasureMediator;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public interface ECGMeasureControllerConduct  extends Component<MeasureMediator> {
    /**
     * 设置当前的状态
     * @return
     */
    public int getVisibility();
    /**
     * 设置隐藏还是显示
     * @param visibility
     */
    public void setVisibility(int visibility);

    /**
     * 设置控制按键开始还是停止
     * @param start
     */
    public void setStart(boolean start);
}
