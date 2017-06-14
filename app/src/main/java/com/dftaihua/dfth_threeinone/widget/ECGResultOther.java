package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.file.ecg.ECGResultFile;
import com.dfth.sdk.model.ecg.ECGLocal;
import com.dfth.sdk.model.ecg.ECGResult;


/**
 * Created by Administrator on 2015-06-08.
 */
public class ECGResultOther extends RelativeLayout implements View.OnClickListener {
    private TextView mPvcCount;
    private TextView mSpCount;
    private TextView mMaxHr;
    private TextView mMinHr;
    private View mPvcCountView;
    private View mSpCountView;
    private View mMaxHrView;
    private View mMinHrView;
    private ECGResultFile mECGResultFile;
    private OtherItemClickListener mOtherItemClickListener;
    public ECGResultOther(Context context, AttributeSet attrs) {
        super(context, attrs);
        View v = LayoutInflater.from(context).inflate(R.layout.ecg_result_other,this,true);
        mPvcCount = (TextView) v.findViewById(R.id.result_other_pvc_count);
        mSpCount = (TextView) v.findViewById(R.id.result_other_sp_count);
        mMaxHr = (TextView) v.findViewById(R.id.result_other_max_hr_count);
        mMinHr = (TextView) v.findViewById(R.id.result_other_min_hr_count);

        mPvcCountView = v.findViewById(R.id.result_other_pvc_count_l);
        mSpCountView = v.findViewById(R.id.result_other_sp_count_l);
        mMaxHrView = v.findViewById(R.id.result_other_max_hr_count_l);
        mMinHrView =  v.findViewById(R.id.result_other_min_hr_count_l);

        mPvcCountView.setOnClickListener(this);
        mSpCountView.setOnClickListener(this);
        mMaxHrView.setOnClickListener(this);
        mMinHrView.setOnClickListener(this);
        initalize();
    }

    private void initalize(){
        mPvcCountView.setBackgroundResource(R.drawable.ecg_result_other_n);
        mSpCountView.setBackgroundResource(R.drawable.ecg_result_other_n);
        mMaxHrView.setBackgroundResource(R.drawable.ecg_result_other_n);
        mMinHrView.setBackgroundResource(R.drawable.ecg_result_other_n);
    }

    public void setData(ECGResult result){
        mPvcCount.setText(ThreeInOneApplication.getStringRes(R.string.count,result.getPvcCount()));
        mSpCount.setText(ThreeInOneApplication.getStringRes(R.string.count,result.getSpCount()));
        mMaxHr.setText(ThreeInOneApplication.getStringRes(R.string.bpm,result.getMaxHr()));
        mMinHr.setText(ThreeInOneApplication.getStringRes(R.string.bpm,result.getMinHr()));
    }

    public interface OtherItemClickListener {
        public void clickPvc();
        public void clickSp();
        public void clickMaxHr();
        public void clickMinHr();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        ECGLocal local = mECGResultFile.getLocal();
        switch (id) {
            case R.id.result_other_pvc_count_l: {
                if(local.getPVCCount() > 0){
                    mOtherItemClickListener.clickPvc();
                }
            }
            break;
            case R.id.result_other_sp_count_l: {
                if(local.getSPCount() > 0){
                    mOtherItemClickListener.clickSp();
                }
            }
            break;
            case R.id.result_other_max_hr_count_l: {
                if(local.getMax_hr() > 0){
                    mOtherItemClickListener.clickMaxHr();
                }
            }
            break;
            case R.id.result_other_min_hr_count_l: {
                if(local.getMin_hr() > 0){
                    mOtherItemClickListener.clickMinHr();
                }
            }
            break;
        }
    }

    public void setOtherItemClickListener(OtherItemClickListener listener){
        mOtherItemClickListener = listener;
    }
    public void setECGResultFile(ECGResultFile file) {
        mECGResultFile = file;
    }

    public void checkStatus(String str, int deviceType){
        initalize();
        int res = deviceType == DfthDevice.SingleDevice ? R.drawable.ecg_result_other_single_p
                : R.drawable.ecg_result_other_twelve_p;
        if(str.equals(Constant.ECG_ANALYSIS.PVC_NAME)){
            mPvcCountView.setBackgroundResource(res);
        }
        if(str.equals(Constant.ECG_ANALYSIS.SP_NAME)){
            mSpCountView.setBackgroundResource(res);
        }
        if(str.equals(ThreeInOneApplication.getStringRes(R.string.ecg_max_hr))){
            mMaxHrView.setBackgroundResource(res);
        }
        if(str.equals(ThreeInOneApplication.getStringRes(R.string.ecg_min_hr))){
            mMinHrView.setBackgroundResource(res);
        }
    }

}
