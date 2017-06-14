package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.BaseActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.fragment.BaseFragment;
import com.dftaihua.dfth_threeinone.utils.TextViewUtils;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public class TitleView extends RelativeLayout implements OnClickListener {
    private ImageView mTitleBackIv;
    private ImageView mTitleSlideMenuIv;
    private TextView mTitleNameTv;
    private TextView mTitleSaveTv;
    private LinearLayout mTwoTitleLl;
    private TextView mTwoTitleFirstTv;
    private TextView mTwoTitleSecondTv;
    private ImageView mTitleSaveImg;
    private LinearLayout mTitleSaveLl;
    private LinearLayout mTitleBackLl;
    private View mV;
    private Context mContext;
    private BaseActivity mActivity;
    private String mTwoTitleFirstStr;
    private String mTwoTitleSecondStr;
    private OnTwoTitleClickListener mTwoTitleListener;
    private OnSaveClickListener mSaveListener;
    public final static int TITLE_PLAN_DATA = 1;
    public final static int TITLE_MANUAL_DATA = 2;

    public TitleView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        mV = LayoutInflater.from(getContext()).inflate(R.layout.view_title, this);
        mTitleNameTv = (TextView) mV.findViewById(R.id.title_name_tv);
        mTitleSaveLl = (LinearLayout) mV.findViewById(R.id.title_save_ll);
        mTitleBackLl = (LinearLayout) mV.findViewById(R.id.title_back_ll);
        mTitleBackIv = (ImageView) mV.findViewById(R.id.title_back_iv);
        mTitleSlideMenuIv = (ImageView) mV.findViewById(R.id.title_slide_menu_iv);
        mTwoTitleLl = (LinearLayout) mV.findViewById(R.id.title_two_name_ll);
        mTwoTitleFirstTv = (TextView) mV.findViewById(R.id.title_two_name_first_tv);
        mTwoTitleSecondTv = (TextView) mV.findViewById(R.id.title_two_name_second_tv);
        mTitleSaveTv = (TextView) mV.findViewById(R.id.title_save_text);
        mTitleSaveImg = (ImageView) mV.findViewById(R.id.title_save_img);
        mTitleNameTv.setOnClickListener(this);
        mTitleSaveLl.setOnClickListener(this);
        mTitleBackLl.setOnClickListener(this);
//        mTitleBackIv.setOnClickListener(this);
        mTitleSlideMenuIv.setOnClickListener(this);
        mTwoTitleFirstTv.setOnClickListener(this);
        mTwoTitleSecondTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
//            case R.id.title_back_iv:
//                if (mActivity != null) {
//                    mActivity.finish();
//                }
//                break;
            case R.id.title_back_ll:
                if (mActivity != null) {
                    mActivity.finish();
                }
                break;
            case R.id.title_slide_menu_iv:
                if (mActivity != null) {
                    mActivity.getSlidMenu().open();
                }
                break;
            case R.id.title_name_tv:

                break;
            case R.id.title_save_ll:
                if(mSaveListener != null){
                    mSaveListener.onSaveClick();
                }
                break;
            case R.id.title_two_name_first_tv:
                if(mTwoTitleListener != null){
                    mTwoTitleListener.onTitleClick(1);
                }
                break;
            case R.id.title_two_name_second_tv:
                if(mTwoTitleListener != null){
                    mTwoTitleListener.onTitleClick(2);
                }
                break;
        }
    }

    public void chooseTitle(int position){
        if(position == TITLE_PLAN_DATA){
            SpannableString spannableString = TextViewUtils.getUnderLineSpan(mActivity, mTwoTitleFirstStr, 0, mTwoTitleFirstStr.length());
            mTwoTitleFirstTv.setText(spannableString);
            mTwoTitleSecondTv.setText(mTwoTitleSecondStr);
        } else{
            SpannableString spannableString = TextViewUtils.getUnderLineSpan(mActivity, mTwoTitleSecondStr, 0, mTwoTitleSecondStr.length());
            mTwoTitleSecondTv.setText(spannableString);
            mTwoTitleFirstTv.setText(mTwoTitleFirstStr);
        }
    }

    public void select(BaseActivity activity) {
        mActivity = activity;
        long status = activity.getStatus();
        if ((status & BaseActivity.BACK_VISIBLE) > 0) {
            mTitleBackLl.setVisibility(VISIBLE);
        }
        if ((status & BaseActivity.SLIDE_VISIBLE) > 0) {
            mTitleSlideMenuIv.setVisibility(VISIBLE);
        }
        if ((status & BaseActivity.SAVE_VISIBLE) > 0) {
            mTitleSaveTv.setVisibility(VISIBLE);
            mTitleSaveLl.setVisibility(VISIBLE);
        }
        if ((status & BaseActivity.TWO_TITLE_VISIBLE) > 0) {
            mTwoTitleLl.setVisibility(VISIBLE);
            mTitleNameTv.setVisibility(GONE);
            if (TextUtils.isEmpty(activity.getTwoTitleFirstStr())) {
                mTwoTitleFirstStr = ThreeInOneApplication.getStringRes(activity.getTwoTitleFirstRes());
                SpannableString spannableString = TextViewUtils.getUnderLineSpan(activity, mTwoTitleFirstStr, 0, mTwoTitleFirstStr.length());
                mTwoTitleFirstTv.setText(spannableString);
            } else {
                mTwoTitleFirstStr = activity.getTwoTitleFirstStr();
                SpannableString spannableString = TextViewUtils.getUnderLineSpan(activity, mTwoTitleFirstStr, 0, mTwoTitleFirstStr.length());
                mTwoTitleFirstTv.setText(spannableString);
            }
            if (TextUtils.isEmpty(activity.getTwoTitleSecondStr())) {
                mTwoTitleSecondStr = ThreeInOneApplication.getStringRes(activity.getTwoTitleSecondRes());
                mTwoTitleSecondTv.setText(mTwoTitleSecondStr);
            } else {
                mTwoTitleSecondStr = activity.getTwoTitleSecondStr();
                mTwoTitleSecondTv.setText(mTwoTitleSecondStr);
            }
            if (activity.getTwoTitleFirstColorRes() != 0) {
                mTwoTitleFirstTv.setTextColor(ThreeInOneApplication.getColorRes(activity.getTwoTitleFirstColorRes()));
            }
            if (activity.getTwoTitleSecondColorRes() != 0) {
                mTwoTitleSecondTv.setTextColor(ThreeInOneApplication.getColorRes(activity.getTwoTitleSecondColorRes()));
            }
        }
        if (TextUtils.isEmpty(activity.getTitleNameStr())) {
            mTitleNameTv.setText(activity.getTitleNameRes());
        } else {
            mTitleNameTv.setText(activity.getTitleNameStr());
        }
        if (activity.getTitleColorRes() != 0) {
            setBackgroundColor(ThreeInOneApplication.getColorRes(activity.getTitleColorRes()));
        }
        if (activity.getTitleNameColorRes() != 0) {
            mTitleNameTv.setTextColor(ThreeInOneApplication.getColorRes(activity.getTitleNameColorRes()));
        }
        if(activity.getTitleBackRes() != 0){
            mTitleBackIv.setImageResource(activity.getTitleBackRes());
        }
    }

    public void select(BaseFragment fragment) {
        long status = fragment.getStatus();
        if ((status & BaseFragment.BACK_VISBLE) > 0) {
            mTitleBackLl.setVisibility(VISIBLE);
        }
        if ((status & BaseFragment.SAVE_VISBLE) > 0) {
            mTitleSaveLl.setVisibility(VISIBLE);
        }

        if (TextUtils.isEmpty(fragment.getTitleNameStr())) {
            mTitleNameTv.setText(fragment.getTitleNameRes());
        } else {
            mTitleNameTv.setText(fragment.getTitleNameStr());
        }
    }

    public void setListener(OnTwoTitleClickListener listener) {
        mTwoTitleListener = listener;
    }

    public void setSaveListener(OnSaveClickListener listener) {
        mSaveListener = listener;
    }

    public void setTitleName(String name) {
        mTitleNameTv.setText(name);
    }

    public void setTitleNameRes(int res) {
        mTitleNameTv.setText(res);
    }

    public void setTitleBackImg(int res) {
        mTitleBackIv.setImageResource(res);
    }

    public void setTitleSave(String save) {
        mTitleSaveTv.setText(save);
    }
    public void setTitleSaveTvColorRes(int res) {
        mTitleSaveTv.setTextColor(ThreeInOneApplication.getColorRes(res));
    }

    public void setTitleSaveRes(int res) {
        mTitleSaveTv.setText(res);
    }

    public void setTitleSaveImgRes(int res) {
        mTitleSaveImg.setImageResource(res);
    }

    public ImageView getTitleBackIv() {
        return mTitleBackIv;
    }

    public TextView getTwoTitleFirstTv() {
        return mTwoTitleFirstTv;
    }

    public TextView getTwoTitleSecondTv() {
        return mTwoTitleSecondTv;
    }

    public interface OnTwoTitleClickListener{
        public void onTitleClick(int type);
    }

    public interface OnSaveClickListener{
        public void onSaveClick();
    }
}
