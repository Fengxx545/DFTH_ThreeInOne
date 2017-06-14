package com.dftaihua.dfth_threeinone.debug;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/5/11 10:16
*/

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.BaseActivity;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.model.bp.BpResult;

import java.util.ArrayList;
import java.util.List;

public class DebugReadBpPlanActivity extends BaseActivity{
    private TextView mShowText;
    DfthBpDevice mBpDevice = null;

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_read_config,null);
        mShowText = (TextView) view.findViewById(R.id.show_config);
        mBpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
        getBpPlan();
        return view;
    }
    private void getBpPlan(){
        if (mBpDevice != null) {
            mBpDevice.getPlanResult().asyncExecute(new DfthCallBack<ArrayList<BpResult>>() {
                @Override
                public void onResponse(DfthResult<ArrayList<BpResult>> response) {
                    if (response.getReturnData() != null) {
                        ArrayList<BpResult> bpResults = response.getReturnData();
                        mShowText.setText(bpPlanString(bpResults));
                    } else {
                        Logger.e("results = NULL");
                        mShowText.setText("NULL");
                    }
                }
            });
        }else{
            mShowText.setText("请连接血压设备!");
        }
    }
    protected String bpPlanString(List<BpResult> results){
        StringBuilder builder = new StringBuilder();
        for(BpResult result: results){
            builder.append(result.toString()+"\n");
        }
        return builder.toString();
    }
}
