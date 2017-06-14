package com.dftaihua.dfth_threeinone.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.widget.ECGResultOther;
import com.dftaihua.dfth_threeinone.widget.LeaderRect;
import com.dfth.sdk.Others.Constant.FactorConstants;
import com.dfth.sdk.file.ecg.ECGResultFile;
import com.dfth.sdk.model.ecg.ECGResult;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2016/12/12 15:12
*/
@SuppressLint("ValidFragment")
public class EcgDataFragment extends Fragment implements ECGResultOther.OtherItemClickListener {

    private static final String TAG = "EcgDataFragment";
    private int mDeviceType;
    private LinearLayout mChangLeadLL;
    private View mHomeView;

    private LinearLayout mLead1;
    private LeaderRect[] mLeaderRects = new LeaderRect[12];
    private LinearLayout mLead2;
    private TextView mStartTime;
    private TextView mMeasureTime;
    private TextView mBeatCountsTv;
    private TextView mPVCTv;
    private TextView mSPTv;
    private TextView mAvgRateTv;
    private TextView mHighRateTv;
    private TextView mLowRateTv;
    private ECGResultOther mEcgResultView;
    private ECGResultFile mFile;
    private ECGResult mECGResult;
    private DiseaseChange mDiseaseChange;
    private String currentDisplayDisease = "";
    private DiseaseCountListener mDiseaseCountlistener;

    public interface DiseaseChange {
        public void diseaseChange(int pos);
    }

    public static interface DiseaseCountListener {
        public void onCount(int current, int max, short rhytm);
    }

    public EcgDataFragment(int deviceType, ECGResultFile file, ECGResult ecgResult) {
        mDeviceType = deviceType;
        mFile = file;
        mECGResult = ecgResult;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mHomeView = LayoutInflater.from(getActivity()).inflate(R.layout.base_history_data, null);
        mStartTime = (TextView) mHomeView.findViewById(R.id.start_time);
        mMeasureTime = (TextView) mHomeView.findViewById(R.id.text_time_all);
        mAvgRateTv = (TextView) mHomeView.findViewById(R.id.ecg_data_aver);
        mBeatCountsTv = (TextView) mHomeView.findViewById(R.id.ecg_data_beat_count);
        mEcgResultView = (ECGResultOther) mHomeView.findViewById(R.id.ecg_data_other);

        mStartTime.setText(TimeUtils.getString(mECGResult.getMeasureStartTime(), "yyyy.MM.dd HH:mm:ss"));
        mMeasureTime.setText(TimeUtils.getMAShms(mFile.Length()));
        mEcgResultView.setOtherItemClickListener(this);
        setData(mDeviceType,mFile,mECGResult);
        return mHomeView;
    }

    public void setData(int deviceType, ECGResultFile file, ECGResult ecgResult){
        mDeviceType = deviceType;
        mFile = file;
        mECGResult = ecgResult;
        mEcgResultView.setECGResultFile(mFile);
        mEcgResultView.setData(mECGResult);
        mAvgRateTv.setText(ThreeInOneApplication.getStringRes(R.string.avg_rate, mECGResult.getAverHr()) + "bpm");
        mBeatCountsTv.setText(ThreeInOneApplication.getStringRes(R.string.beat_count, mECGResult.getBeatCount()) + "次");
    }


    public void setmDiseaseChange(DiseaseChange mDiseaseChange) {
        this.mDiseaseChange = mDiseaseChange;
    }
    public void setmDiseaseCountlistener(DiseaseCountListener mDiseaseCountlistener) {
        this.mDiseaseCountlistener = mDiseaseCountlistener;
    }

    @Override
    public void clickPvc() {
        waveJump(Constant.ECG_ANALYSIS.PVC_NAME);
    }

    @Override
    public void clickSp() {
        waveJump(Constant.ECG_ANALYSIS.SP_NAME);
    }

    @Override
    public void clickMaxHr() {
        waveJump(ThreeInOneApplication.getStringRes(R.string.ecg_max_hr));
    }

    @Override
    public void clickMinHr() {
        waveJump(ThreeInOneApplication.getStringRes(R.string.ecg_min_hr));
    }

    private void waveJump(String str) {
        if (str.contains(ThreeInOneApplication.getStringRes(R.string.ecg_min_hr))) {
            currentDisplayDisease = str;
            if (mDiseaseChange != null) {
                mDiseaseChange.diseaseChange((int) mFile.getMinHrPeak());
            }
            mDiseaseCountlistener.onCount(0, 0, (short) -1);
        } else if (str.contains(ThreeInOneApplication.getStringRes(R.string.ecg_max_hr))) {
            currentDisplayDisease = str;
            if (mDiseaseChange != null) {
                mDiseaseChange.diseaseChange((int) mFile.getMaxHrPeak());
            }
            mDiseaseCountlistener.onCount(0, 0, (short) -1);
        } else if (!currentDisplayDisease.equals(str) && !str.equals("")) {
            currentDisplayDisease = str;
            if (mDiseaseChange != null) {
                short rhythm = str.equals(Constant.ECG_ANALYSIS.PVC_NAME) ? FactorConstants.PREMATURE_VENTRICULAR_CONTRACTION :
                        FactorConstants.SUPRAVENTRICULAR_PREMATURE_BEAT;
                int pos = (int) mFile.getRhythmData(rhythm, 0, 750);
                int count = mFile.getRhythmSize(rhythm);
                mDiseaseChange.diseaseChange(pos);
                boolean s = rhythm == FactorConstants.SUPRAVENTRICULAR_PREMATURE_BEAT
                        || rhythm == FactorConstants.PREMATURE_VENTRICULAR_CONTRACTION;
                mDiseaseCountlistener.onCount(0, s ? count : 0, rhythm);
            }
        } else {
            currentDisplayDisease = "";
            mDiseaseCountlistener.onCount(0, 0, (short) -1);
        }
        mEcgResultView.checkStatus(currentDisplayDisease,mDeviceType);
    }

}
