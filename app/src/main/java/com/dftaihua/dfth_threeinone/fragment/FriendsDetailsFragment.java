package com.dftaihua.dfth_threeinone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dftaihua.dfth_threeinone.R;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2016/12/16 13:56
*/
public class FriendsDetailsFragment extends BaseFragment {
    private View mView;

    @Override
    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friends_details,null);

        init();

        return mView;
    }

    private void init() {
    }
}
