package com.dftaihua.dfth_threeinone.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.MeasureMediator;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.model.ecg.ECGResult;

/**
 * Created by leezhiqiang on 2017/3/23.
 */

public class ECGResultProcessDialog extends Dialog implements Component<MeasureMediator> {
    private MeasureMediator mMeasureMediator;
    private Animation mAnimation;
    private long mStartTime;
    private long mDisplayTime;
    private ECGResultProcessDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
        mAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.progress_anim);
        init();
    }
    private void init(){
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.progress_layout, null);
        TextView progress_text = (TextView) view
                .findViewById(R.id.progress_text);
        progress_text.setText(ThreeInOneApplication.getStringRes(R.string.calc_ecg_end));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        int height = ThreeInOneApplication.getScreenHeight();
//        int width = ThreeInOneApplication.getScreenWidth();
//        params.height = height / 2;
//        params.width = width/2;
        params.height = DisplayUtils.dip2px(getContext(),150);
        params.width = DisplayUtils.dip2px(getContext(),250);
        setContentView(view, params);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mAnimation != null) {
                    mAnimation.cancel();
                }
            }
        });
    }
    public static ECGResultProcessDialog create(Context context,MeasureMediator measureMediator,long displaySecond){
        ECGResultProcessDialog dialog = new ECGResultProcessDialog(context);
        dialog.mDisplayTime = displaySecond;
        dialog.bindMediator(measureMediator);
        return dialog;
    }

    public void startProcess(){
        mStartTime = System.currentTimeMillis();
        show();
    }

    public void endProcess(final ECGResult result){
        long time = mDisplayTime - (System.currentTimeMillis() - mStartTime);
        if(time <=0){
            dismiss();
            mMeasureMediator.doProcessResult(result);
        }else{
            DispatchUtils.performMainThreadDelay(new DispatchTask() {
                @Override
                public void accept(Object o) throws Exception {
                    if(getContext() instanceof Activity
                            && !((Activity) getContext()).isFinishing()){
                        dismiss();
                    }
                    mMeasureMediator.doProcessResult(result);
                }
            },time);
        }
    }

    @Override
    public void bindMediator(MeasureMediator measureMediator) {
        mMeasureMediator = measureMediator;
    }
}
