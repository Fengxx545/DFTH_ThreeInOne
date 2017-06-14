package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/14 17:03
*/
public class LeaderRect extends LinearLayout implements View.OnClickListener{
    public interface OnCheckListener{
        public boolean onCheckChange(int position,boolean checked);
    }
    private OnCheckListener mCheckListener;
    private static final String TAG = "LeaderRect";
    private TextView mLeaderTv;
    private int mPosition;
    private boolean mCheck;
    public LeaderRect(Context context, int position) {
        super(context);
        mPosition = position;
        mLeaderTv = new TextView(context);
        mLeaderTv.setGravity(Gravity.CENTER);
        mLeaderTv.setTextColor(Color.RED);
        mLeaderTv.setTextSize(15);
        mLeaderTv.setSingleLine(true);
        LinearLayout.LayoutParams params = new LayoutParams(0, 0);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        setOnClickListener(this);
        this.addView(mLeaderTv, params);
    }
    public void setChecked(boolean checked){
        mCheck = checked;
        changeBackground();
    }

    private void changeBackground(){
        int color = mCheck ? R.color.twelve_color : R.color.uncheck_lead;
        int textColor = mCheck ? R.color.google_white : R.color.google_black;
        setBackgroundResource(color);
        mLeaderTv.setTextColor(ThreeInOneApplication.getColorRes(textColor));
    }


    @Override
    public void onClick(View view) {

        if(mCheckListener != null){
            if (mCheckListener.onCheckChange(mPosition,!mCheck)){
                setChecked(!mCheck);
            }
        }
    }

    public void setCheckChangeListener(OnCheckListener listener){
        mCheckListener = listener;
    }

    public void setText(String text) {
        this.mLeaderTv.setText(text);
    }

}
