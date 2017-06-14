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
* 创建时间：2016/12/12 15:12
*/
public class LoginFragment extends BaseFragment{

    public LoginFragment(){

    }


    @Override
    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_login,null);
    }
}
