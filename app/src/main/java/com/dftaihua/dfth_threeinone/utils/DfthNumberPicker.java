package com.dftaihua.dfth_threeinone.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/4/6 0006.
 */
public class DfthNumberPicker extends NumberPicker {

    public DfthNumberPicker(Context context) {
        super(context);
    }

    public DfthNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DfthNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        this.addView(child, null);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        this.addView(child, -1, params);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setNumberPicker(child);
    }

    /**
     * 设置CustomNumberPicker的属性 颜色 大小
     *
     * @param view
     */
    public void setNumberPicker(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextColor(Color.BLACK);
            ((EditText) view).setTextSize(DisplayUtils.sp2px(getContext(), 8));
//            int padding = DisplayUtils.dip2px(getContext(), 20);
//            view.setPadding(padding, padding, padding, padding);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,0);
            params.height = DisplayUtils.dip2px(getContext(), 200);
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int margin = DisplayUtils.dip2px(getContext(), 40);
//            params.leftMargin = DisplayUtils.dip2px(getContext(), 30);
//            params.rightMargin = DisplayUtils.dip2px(getContext(), 30);
            params.setMargins(margin,margin,margin,margin);
            view.setLayoutParams(params);
        }
    }

    /**
     * 设置分割线的颜色值
     *
     * @param numberPicker
     */
    @SuppressWarnings("unused")
    public void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, new ColorDrawable(ThreeInOneApplication.getColorRes(R.color.bp_result_dialog_line_color)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
