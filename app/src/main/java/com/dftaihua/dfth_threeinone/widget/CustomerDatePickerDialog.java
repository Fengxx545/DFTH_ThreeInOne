package com.dftaihua.dfth_threeinone.widget;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by Administrator on 2017/3/29 0029.
 */
public class CustomerDatePickerDialog extends DatePickerDialog {

    public CustomerDatePickerDialog(Context context,
                                    OnDateSetListener callBack, int year, int monthOfYear,
                                    int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }
}
