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
* 创建时间：2016/12/16 14:17
*/
public class ServiceRecordFragment extends BaseFragment implements View.OnClickListener {
    private View mView;
    private Button mHistorySession;

    @Override
    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_service_record,null);

        init();

        return mView;
    }

    private void init() {
        mHistorySession = (Button) mView.findViewById(R.id.history_session);
        mHistorySession.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.history_session:{


            }

        }
    }
}
