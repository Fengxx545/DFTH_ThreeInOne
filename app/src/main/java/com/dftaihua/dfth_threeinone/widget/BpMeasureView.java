package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.utils.BpDataUtils;
import com.dftaihua.dfth_threeinone.utils.TextViewUtils;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.model.bp.BpResult;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
public class BpMeasureView extends RelativeLayout implements View.OnClickListener {
    private BpWaveView mBpWaveView;
    private Button mControlBtn;
    private LinearLayout mValueLl;
    private RelativeLayout mResultRl;
    private TextView mResultTv;
    private TextView mValueTv;
    private TextView mHighPressureTv;
    private TextView mLowPressureTv;
    private TextView mHeartRateTv;
    private TextView mValueUnitTv;
    private HashMap<String, Object> mDeviceMap;

    private OnButtonClickListener mListener;

    public BpMeasureView(Context context) {
        this(context, null);
    }

    public BpMeasureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpMeasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_bp_measure, this, true);
        mBpWaveView = (BpWaveView) findViewById(R.id.view_bp_measure_bwv);
        mControlBtn = (Button) findViewById(R.id.view_bp_measure_control_btn);
        mValueLl = (LinearLayout) findViewById(R.id.view_bp_measure_value_ll);
        mResultRl = (RelativeLayout) findViewById(R.id.view_bp_measure_result_rl);
        mResultTv = (TextView) findViewById(R.id.view_bp_measure_result_tv);
        mValueTv = (TextView) findViewById(R.id.view_bp_measure_value_tv);
        mValueUnitTv = (TextView) findViewById(R.id.view_bp_measure_value_unit_tv);
        mHighPressureTv = (TextView) findViewById(R.id.view_bp_measure_high_tv);
        mLowPressureTv = (TextView) findViewById(R.id.view_bp_measure_low_tv);
        mHeartRateTv = (TextView) findViewById(R.id.view_bp_measure_rate_tv);
        String unit = BpDataUtils.getDefaultUnit(getContext());
        mValueUnitTv.setText(unit);
        resetView();
        mControlBtn.setText(R.string.bp_measure_view_start);
        mValueLl.setVisibility(VISIBLE);
        mValueTv.setText("- - -");
        mResultRl.setVisibility(GONE);
        mBpWaveView.setAnimDuration(3000);
        mControlBtn.setOnClickListener(this);
        mDeviceMap = new HashMap<>();
        mDeviceMap.put(Constant.DFTH_DEVICE_KEY, DfthDevice.BpDevice);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_bp_measure_control_btn:
                boolean isBegin = mControlBtn.getText().toString().equals(R.string.bp_measure_view_stop);
                mListener.onButtonClick(isBegin);
        }
    }

    public void setValueView(){
        mBpWaveView.setProgressValue(0);
        mControlBtn.setText(R.string.bp_measure_view_start);
        mValueLl.setVisibility(VISIBLE);
        mValueTv.setText("- - -");
        mResultRl.setVisibility(GONE);
    }

    public void resetView() {
        String unit = BpDataUtils.getDefaultUnit(getContext());
        int position = unit.equals(ThreeInOneApplication.getStringRes(R.string.setting_kPa)) ? 10 : 9;
        SpannableString high = TextViewUtils.getSizeSpanSpToPx(getContext(), ThreeInOneApplication.getStringRes(R.string.bp_measure_view_high_default)
                + unit, 6, position, 36);
        SpannableString low = TextViewUtils.getSizeSpanSpToPx(getContext(), ThreeInOneApplication.getStringRes(R.string.bp_measure_view_low_default)
                + unit, 6, position, 36);
        SpannableString rate = TextViewUtils.getSizeSpanSpToPx(getContext(), ThreeInOneApplication.getStringRes(R.string.bp_measure_view_rate_default)
                + "bpm", 6, 9, 36);
        mHighPressureTv.setText(high);
        mLowPressureTv.setText(low);
        mHeartRateTv.setText(rate);
        mResultTv.setVisibility(GONE);
    }

    public void changeView(boolean isBegin) {
        if (isBegin) {
            setValueView();
        } else {
            mControlBtn.setText(R.string.bp_measure_view_stop);
            mValueLl.setVisibility(VISIBLE);
            mResultRl.setVisibility(GONE);
            mValueTv.setText(0 + "");
            mBpWaveView.setProgressValue(0);
        }
    }

    public void onDataChanged(Short data) {
        mBpWaveView.setProgressValue(data);
        mValueTv.setText(BpDataUtils.getDefaultUnitValue(getContext(), data));
    }

    public void onResultData(BpResult bpResult) {
        resetView();
        if(bpResult.getSbp() != 0 && bpResult.getDbp() != 0 && bpResult.getPulseRate() != 0){
            String unit = BpDataUtils.getDefaultUnit(getContext());
            int position = unit.equals(ThreeInOneApplication.getStringRes(R.string.setting_kPa)) ? 10 : 9;
            SpannableString high = TextViewUtils.getSizeSpanSpToPx(getContext(), ThreeInOneApplication.getStringRes(R.string.bp_measure_view_high)
                    + BpDataUtils.getDefaultUnitValue(getContext(), bpResult.getSbp()) + " " + unit, 6, position, 36);
            SpannableString low = TextViewUtils.getSizeSpanSpToPx(getContext(), ThreeInOneApplication.getStringRes(R.string.bp_measure_view_low)
                    + BpDataUtils.getDefaultUnitValue(getContext(), bpResult.getDbp()) + " " + unit, 6, position, 36);
            SpannableString rate = TextViewUtils.getSizeSpanSpToPx(getContext(), ThreeInOneApplication.getStringRes(R.string.bp_measure_view_rate)
                    + bpResult.getPulseRate() + " bpm", 6, 9, 36);
            mHighPressureTv.setText(high);
            mLowPressureTv.setText(low);
            mHeartRateTv.setText(rate);
            mBpWaveView.setProgressValue(0);
            mControlBtn.setText(R.string.bp_measure_view_start);
            mValueLl.setVisibility(GONE);
            mResultRl.setVisibility(VISIBLE);
            mResultTv.setVisibility(VISIBLE);
            int[] avers = {bpResult.getSbp(), bpResult.getDbp(), bpResult.getPulseRate()};
            int res = !bpResult.isValidData() ? R.string.bp_result_invalid : BpDataUtils.getLevel(avers);
            String des = ThreeInOneApplication.getStringRes(res);
            mResultTv.setText(des);
        }
    }

    public void onMeasureException(String s) {
        mBpWaveView.setProgressValue(0);
        mControlBtn.setText(R.string.bp_measure_view_start);
        mValueLl.setVisibility(VISIBLE);
        mValueTv.setText("- - -");
        mResultRl.setVisibility(GONE);
    }

    public void setListener(OnButtonClickListener listener) {
        mListener = listener;
    }

    public interface OnButtonClickListener {
        void onButtonClick(boolean begin);
    }
}
