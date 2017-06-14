package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.listener.AdapterItemOnclickListener;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dfth.sdk.device.DfthDevice;

import java.util.HashMap;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/7 14:00
*/
public class ECGContentView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "ECGContentView";
    private View mHomeView;
    private TextView mEcgTimeText;
    private TextView mEcgReportText;
    private TextView mEcgPvcText;
    public LinearLayout mEcgUploadLL;
    public TextView mEcgServiceText;
    public TextView mTextAnalysisText;
    public ImageView rightArrows;
    private Context mContext;
    private int mDeviceType;
    private HashMap<String, Integer> mDeviceMap;
    private TextView mMeasureTimeText;
    private TextView mAvgRateText;
    private TextView mHighRateText;
    private TextView mLowRateText;
    private TextView mEcgSpText;
    private TextView mEcgUploadText;
    private ImageView mEcgUploadIcon;
    private TextView mItemCheck;
    private AdapterItemOnclickListener mListener;
    private LinearLayout mHomeViewLL;

    public ECGContentView(Context context, int deviceType) {
        super(context);
        mContext = context;
        mDeviceType = deviceType;
        this.setOrientation(VERTICAL);
        mHomeView = LayoutInflater.from(context).inflate(R.layout.ecg_history_item, this);
        initialize();
    }

    private void initialize() {
        mHomeViewLL = (LinearLayout) mHomeView.findViewById(R.id.ecg_history_item);
        mEcgTimeText = (TextView) mHomeView.findViewById(R.id.ecg_history_item_time);
        mItemCheck = (TextView) mHomeView.findViewById(R.id.item_check);
        mMeasureTimeText = (TextView) mHomeView.findViewById(R.id.show_measure_time);
        mTextAnalysisText = (TextView) mHomeView.findViewById(R.id.text_analysis);
        mAvgRateText = (TextView) mHomeView.findViewById(R.id.ecg_history_item_heart);
        mHighRateText = (TextView) mHomeView.findViewById(R.id.high_rate);
        mLowRateText = (TextView) mHomeView.findViewById(R.id.low_rate);
        mEcgPvcText = (TextView) mHomeView.findViewById(R.id.ecg_history_item_pvc_count);
        mEcgSpText = (TextView) mHomeView.findViewById(R.id.ecg_history_item_sp_count);
        mEcgUploadText = (TextView) mHomeView.findViewById(R.id.ecg_history_item_uploader);
        mEcgReportText = (TextView) mHomeView.findViewById(R.id.ecg_history_item_report);
        mEcgUploadIcon = (ImageView) mHomeView.findViewById(R.id.ecg_history_item_uploader_icon);
        mEcgUploadLL = (LinearLayout) mHomeView.findViewById(R.id.upload_rl);
        if (mDeviceType == DfthDevice.SingleDevice) {
            mEcgUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.single_history_color));
            mEcgUploadIcon.setImageResource(R.drawable.single_upload);
        } else {
            mEcgUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.twelve_history_color));
            mEcgUploadIcon.setImageResource(R.drawable.twelve_upload);
        }
        mHomeViewLL.setOnClickListener(this);
        mEcgUploadLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ecg_history_item: {
                mListener.itemOnClick();
            }
            break;
            case R.id.upload_rl: {
                mListener.uploadClick();
            }
            break;
        }
    }

    public void setMeasureTimeText(long startTime) {
        mEcgTimeText.setText(TimeUtils.getString(startTime, "yyyy/MM/dd HH:mm"));
    }

    public void setPulse(int pulse) {
//        mEcgPusleText.setText(ThreeInOneApplication.getStringRes(R.string.a_aver) + '\t' + pulse + " bpm");
    }

    public void setServiceReport(String report) {
        mEcgReportText.setText(report);
    }

    public void setTextAnalysisColor(int resource) {
        mTextAnalysisText.setTextColor(ThreeInOneApplication.getColorRes(resource));
    }

    public void setPvc(int pvc, int sp) {
//        mEcgPvcText.setText(ThreeInOneApplication.getStringRes(R.string.a_pvc_a_sp) + '\t' + pvc + "/" + sp);
    }

    public int getColor(int type) {
        return 0;
    }


    public ImageView getEcgUploadIcon() {
        return mEcgUploadIcon;
    }

    public TextView getEcgTimeText() {
        return mEcgTimeText;
    }

    public TextView getEcgReportText() {
        return mEcgReportText;
    }

    public TextView getEcgPvcText() {
        return mEcgPvcText;
    }

    public LinearLayout getEcgUploadLL() {
        return mEcgUploadLL;
    }

    public TextView getEcgServiceText() {
        return mEcgServiceText;
    }

    public TextView getTextAnalysisText() {
        return mTextAnalysisText;
    }

    public ImageView getRightArrows() {
        return rightArrows;
    }

    public TextView getMeasureTimeText() {
        return mMeasureTimeText;
    }

    public TextView getAvgRateText() {
        return mAvgRateText;
    }

    public TextView getHighRateText() {
        return mHighRateText;
    }

    public TextView getLowRateText() {
        return mLowRateText;
    }

    public TextView getEcgSpText() {
        return mEcgSpText;
    }

    public TextView getEcgUploadText() {
        return mEcgUploadText;
    }

    public TextView getItemCheck() {
        return mItemCheck;
    }

    public void setListener(AdapterItemOnclickListener listener) {
        mListener = listener;
    }
}
