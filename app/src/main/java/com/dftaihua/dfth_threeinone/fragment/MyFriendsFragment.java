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
* 创建时间：2016/12/15 11:36
*/
public class MyFriendsFragment extends BaseFragment implements View.OnClickListener {
    private View mView;
    private Button mFriendDetails;

    @Override
    protected View onChildCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friends,null);

        init();

        return mView;
    }

    private void init() {

        mFriendDetails = (Button) mView.findViewById(R.id.friends_details);

        mFriendDetails.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.friends_details:{

            }
            break;

        }
    }
}
