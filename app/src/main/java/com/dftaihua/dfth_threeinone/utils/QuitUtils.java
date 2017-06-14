package com.dftaihua.dfth_threeinone.utils;

import android.os.Handler;
import android.os.Message;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/28 11:34
*/
public class QuitUtils {

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    public static void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtils.showShort(ThreeInOneApplication.getInstance(),ThreeInOneApplication.getStringRes(R.string.quit_toast));
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            ActivityCollector.finishAll();
            System.exit(0);
        }
    }
}
