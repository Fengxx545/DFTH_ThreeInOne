package com.dftaihua.dfth_threeinone.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.EcgMeasureActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.MeasureMediator;
import com.dfth.sdk.config.DfthConfig;

public class ECGLessThanDoctorTimeDialog extends Dialog implements Component<MeasureMediator> {
    private MeasureMediator mMeasureMediator;
    private Context mContext;
    private static boolean isLongMeasure = false;

    private ECGLessThanDoctorTimeDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
        mContext = context;
        init();
    }

    public static ECGLessThanDoctorTimeDialog create(Context context, MeasureMediator measureMediator, boolean isLong) {
        ECGLessThanDoctorTimeDialog dialog = new ECGLessThanDoctorTimeDialog(context);
        dialog.bindMediator(measureMediator);
        isLongMeasure = isLong;
        return dialog;
    }

    private void init() {
        View contentView = LayoutInflater.from(getContext()).inflate(
                R.layout.add_address, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
        if(isScreenChange()){
            params.width = ThreeInOneApplication.getScreenWidth() / 2;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }else{
            params.width = ThreeInOneApplication.getScreenWidth() * 4 / 5;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        setContentView(contentView, params);
        String text = ThreeInOneApplication.getStringRes(R.string.confirm_stop,
                DfthConfig.getConfig().ecgConfig.ecgAnalysisConfig.doctorMinAnalysisTime / 1000);
        SpannableStringBuilder span = new SpannableStringBuilder("缩进" + text);
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ((TextView) contentView.findViewById(R.id.add_address_text)).setText(span);
        TextView confirm = (TextView) contentView.findViewById(R.id.add_address_ok);
        TextView cancel = (TextView) contentView.findViewById(R.id.add_address_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
//                if(isLongMeasure){
//                    Dialog dialog = DialogFactory.getStopLongMeasureDialog((EcgMeasureActivity)mContext, mMeasureMediator);
//                    dialog.show();
//                } else{
                    if (mMeasureMediator != null) {
                        mMeasureMediator.lessThan25Second(true);
                    }
//                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void bindMediator(MeasureMediator measureMediator) {
        mMeasureMediator = measureMediator;
    }

    public boolean isScreenChange() {
        Configuration mConfiguration = mContext.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
//横屏
            return true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
//竖屏
            return false;
        }
        return false;
    }
}