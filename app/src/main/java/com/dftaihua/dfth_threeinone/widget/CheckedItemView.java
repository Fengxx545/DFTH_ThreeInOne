package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.SettingActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2016/12/13 10:10
*/
public class CheckedItemView extends LinearLayout {

    private TextView mItemNameTv;
    private TextView mItemContentTv;
    private ImageView mItemIconIv;
    private TextView mItemGoTv;
    private TextView mClearTv;

    public CheckedItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.check_item, this);
        mItemNameTv = (TextView) findViewById(R.id.check_item_name_tv);
        mItemContentTv = (TextView) findViewById(R.id.check_item_add_content_tv);
        mItemIconIv = (ImageView) findViewById(R.id.check_item_icon_iv);
        mItemGoTv = (TextView) findViewById(R.id.check_item_go_iv);
        mClearTv = (TextView) findViewById(R.id.check_item_clear_data_iv);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.checked_item);
        final int nameRes = a.getResourceId(R.styleable.checked_item_checked_name, -1);
        final int iconRes = a.getResourceId(R.styleable.checked_item_checked_icon, -1);
        final int contentBgRes = a.getResourceId(R.styleable.checked_item_checked_content_pg, -1);
        final int contentTextRes = a.getResourceId(R.styleable.checked_item_checked_content_text, -1);
        final int lastIconRes = a.getResourceId(R.styleable.checked_item_last_icon, -1);
        final int lastIconTextRes = a.getResourceId(R.styleable.checked_item_last_icon_text, -1);
        final int clearTextColorRes = a.getResourceId(R.styleable.checked_item_clear_text_color, -1);
        final int clearTextRes = a.getResourceId(R.styleable.checked_item_clear_text, -1);
        final boolean isCheckIconVisible = a.getBoolean(R.styleable.checked_item_checked_icon_visible, true);
        final boolean isCheckContentVisible = a.getBoolean(R.styleable.checked_item_checked_content_visible, true);
        final boolean isClearVisible = a.getBoolean(R.styleable.checked_item_checked_clear_visible, false);
        if (nameRes != -1) {
            mItemNameTv.setText(nameRes);
        }
        if (iconRes != -1) {
            mItemIconIv.setImageResource(iconRes);
        }
        if (contentBgRes != -1) {
            mItemContentTv.setBackgroundResource(contentBgRes);
        }
        if (contentTextRes != -1) {
            mItemContentTv.setText(contentTextRes);
        }
        if (lastIconTextRes != -1) {
            mItemGoTv.setText(lastIconTextRes);
        }
        if (lastIconRes != -1) {
            mItemGoTv.setBackgroundResource(lastIconRes);
        }
        if (clearTextColorRes != -1) {
            mClearTv.setBackgroundResource(clearTextColorRes);
        }
        if (clearTextRes != -1) {
            mClearTv.setText(clearTextRes);
        }
        if (isCheckIconVisible){
            mItemIconIv.setVisibility(VISIBLE);
        }else{
            mItemIconIv.setVisibility(GONE);
        }
        if (isCheckContentVisible){
            mItemContentTv.setVisibility(VISIBLE);
        }else{
            mItemContentTv.setVisibility(GONE);
        }
        if (isClearVisible){
            mItemGoTv.setVisibility(GONE);
            mClearTv.setVisibility(VISIBLE);
        }else{
            mItemGoTv.setVisibility(VISIBLE);
            mClearTv.setVisibility(GONE);
        }
        a.recycle();
    }

    public TextView getItemNameTv() {
        return mItemNameTv;
    }

    public TextView getItemContentTv() {
        return mItemContentTv;
    }

    public void setContentText(int unit){
        if(unit == Constant.BP_UNIT_MMHG){
            mItemContentTv.setText("mmHg");
        } else{
            mItemContentTv.setText("kPa");
        }
    }

    public void setContentText(CharSequence text){
        mItemContentTv.setText(text);
    }

    public void setDataSize(String dataSize){
        mClearTv.setText(dataSize);
    }

    public void setDataSizeText(String dataSize){
        mClearTv.setText(dataSize);
    }

    public ImageView getItemIconIv() {
        return mItemIconIv;
    }

    public TextView getItemGoTv() {
        return mItemGoTv;
    }

    public void createItem(int itemType) {
        switch (itemType) {
            case Constant.MemberInfoItem.MY_SERVICE:
                mItemNameTv.setText(R.string.member_info_item_my_service);
                mItemIconIv.setImageResource(R.drawable.member_item_my_service);
                mItemContentTv.setVisibility(GONE);
                break;
            case Constant.MemberInfoItem.CONSUME_RECORD:
                mItemNameTv.setText(R.string.member_info_item_consume_record);
                mItemIconIv.setImageResource(R.drawable.member_item_consume_record);
                mItemContentTv.setVisibility(GONE);
                break;
            case Constant.MemberInfoItem.EMERGENCY_CONTACT:
                mItemNameTv.setText(R.string.member_info_item_emergency_contact);
                mItemIconIv.setImageResource(R.drawable.member_item_emergency_contact);
//                mItemContentTv.setVisibility(GONE);
                mItemContentTv.setText(ThreeInOneApplication.getStringRes(R.string.member_info_item_not_add));
                break;
            case Constant.MemberInfoItem.LIFE_HABIT:
                mItemNameTv.setText(R.string.member_info_item_life_habit);
                mItemIconIv.setImageResource(R.drawable.member_item_life_habit);
                mItemContentTv.setText(ThreeInOneApplication.getStringRes(R.string.member_info_item_not_add));
                break;
            case Constant.MemberInfoItem.DISEASE_HISTORY:
                mItemNameTv.setText(R.string.member_info_item_disease_history);
                mItemIconIv.setImageResource(R.drawable.member_item_disease_history);
                mItemContentTv.setVisibility(VISIBLE);
                mItemContentTv.setText(ThreeInOneApplication.getStringRes(R.string.member_info_item_not_add));
                break;
        }
    }
}
