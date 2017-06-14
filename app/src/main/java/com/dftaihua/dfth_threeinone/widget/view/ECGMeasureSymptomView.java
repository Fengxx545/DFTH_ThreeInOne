package com.dftaihua.dfth_threeinone.widget.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.measure.listener.ECGScreenConduct;
import com.dftaihua.dfth_threeinone.measure.listener.ECGSymptomConduct;
import com.dftaihua.dfth_threeinone.mediator.ECGSymptomMediator;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.ECGDeviceUtils;

/**
 * Created by leezhiqiang on 2017/4/19.
 */

public class ECGMeasureSymptomView extends TextView implements ECGSymptomConduct, View.OnClickListener, ECGScreenConduct {
    private ECGSymptomMediator mECGSymptomMediator;
    private Dialog mDiseaseDialog;

    public ECGMeasureSymptomView(Context context) {
        super(context);
        setText(R.string.symptoms);
        setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
        setGravity(Gravity.CENTER);
        setId(R.id.symptom_text);
        setBackgroundResource(R.drawable.measure_symptom_bg);
        setOnClickListener(this);
    }

    @Override
    public void bindMediator(ECGSymptomMediator mediator) {
        mECGSymptomMediator = mediator;
    }

    @Override
    public void onClick(View v) {
        if (mDiseaseDialog == null) {
            mDiseaseDialog = DialogFactory.showBodyDiseaseSelect(getContext(), true);
        }
        mDiseaseDialog.show();
    }


    @Override
    public void orientationChange(int orientation) {
        if (mDiseaseDialog != null && mDiseaseDialog.isShowing()) {
            mDiseaseDialog.dismiss();
            mDiseaseDialog = null;
            mDiseaseDialog = DialogFactory.showBodyDiseaseSelect(getContext(), true);
            mDiseaseDialog.show();
        }
    }
}
