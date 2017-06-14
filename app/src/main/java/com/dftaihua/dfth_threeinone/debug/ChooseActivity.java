package com.dftaihua.dfth_threeinone.debug;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/5/11 11:22
*/

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.BaseActivity;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dfth.sdk.debug.BpPlanSimulateActivity;

import java.util.HashMap;

public class ChooseActivity extends BaseActivity implements View.OnClickListener {
    private TextView mReadText;
    private TextView mReadBPPlan;
    private TextView mSetBPPlan;

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_choose,null);
        mReadText = (TextView) view.findViewById(R.id.read_config);
        mReadBPPlan = (TextView) view.findViewById(R.id.get_bp_plan);
        mSetBPPlan = (TextView) view.findViewById(R.id.set_bp_plan);
        mReadBPPlan.setOnClickListener(this);
        mReadText.setOnClickListener(this);
        mSetBPPlan.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.read_config:{
                ActivitySkipUtils.skipAnotherActivity(this,DebugReadConfigActivity.class);
            }
            break;
            case R.id.get_bp_plan:{
                ActivitySkipUtils.skipAnotherActivity(this,DebugReadBpPlanActivity.class);
            }
            break;
            case R.id.set_bp_plan:{
                String userId = UserManager.getInstance().getDefaultUser().getUserId();
                HashMap<String,String> map = new HashMap<>();
                map.put("userId",userId);
                ActivitySkipUtils.skipAnotherActivity(this,BpPlanSimulateActivity.class,map);
            }
            break;
        }
    }
}
