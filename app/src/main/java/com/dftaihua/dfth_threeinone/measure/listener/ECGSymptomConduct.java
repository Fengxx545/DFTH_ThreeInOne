package com.dftaihua.dfth_threeinone.measure.listener;

import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.ECGSymptomMediator;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public interface ECGSymptomConduct extends Component<ECGSymptomMediator>{

    public void setVisibility(int visibility);

    /**
     * 设置当前的状态
     * @return
     */
    public int getVisibility();



}
