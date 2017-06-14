package com.dftaihua.dfth_threeinone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dftaihua.dfth_threeinone.R;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2016/12/15 11:08
*/
public class AccountFragment extends BaseFragment implements View.OnClickListener {

    private View mView;
    private Button mRecharge;

    @Override
    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account,null);

        init();

        return mView;
    }

    private void init() {
        mRecharge = (Button) mView.findViewById(R.id.bt_recharge);

        mRecharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.bt_recharge:{

            }
            break;
        }

    }
}
