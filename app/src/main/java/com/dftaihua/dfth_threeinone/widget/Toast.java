package com.dftaihua.dfth_threeinone.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;

/**
 * Created by Administrator on 2017/5/23 0023.
 */

public class Toast {
    private static android.widget.Toast toast;
    private static Context _context;

    public static void init(Context context) {
        _context = context;
    }

    public static synchronized android.widget.Toast makeText(Context context, CharSequence text, int duration) {
        toast = new android.widget.Toast(_context);
        LayoutInflater inflate = (LayoutInflater)
                _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.toast_layout, null);
        TextView tv = (TextView) v.findViewById(R.id.toast_tv);
        tv.setText(text);
        tv.setBackgroundDrawable(new ToastDrawable());
        tv.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        v.setLayoutParams(params);
        toast.setView(v);
        toast.setDuration(duration);
        toast.show();
        return toast;
    }

    public static synchronized void makeText(Context context, int strId, int duration) {
        if (_context == null || !(_context instanceof Activity)) {
            _context = context;
        }
        if (_context != null && _context instanceof Activity) {
            String text = _context.getResources().getString(strId);
            makeText(context, text, duration);
        }

    }
}
