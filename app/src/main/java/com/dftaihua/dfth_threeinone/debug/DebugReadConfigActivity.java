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

public class DebugReadConfigActivity extends BaseActivity{
    private TextView mShowText;


    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_read_config,null);
        mShowText = (TextView) view.findViewById(R.id.show_config);
        String showText = ConfigRead.getConfig().read();
        mShowText.setText(showText);
        return view;
    }
}
