package com.dftaihua.dfth_threeinone.mediator;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/3/28 18:19
*/
public interface ECGHistoryMediator extends LeadersChangeMediator,WaveViewMediator{

    /**
     * 滚动条滑动
     * @param pos
     */
    public void seekBarChange(int pos);


    /**
     * 获取医师分析
     */
    public void getDoctorAnalyse();
}
