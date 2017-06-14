package com.dftaihua.dfth_threeinone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.EcgDetailActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.listener.AdapterItemOnclickListener;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.NewUploadManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.ECGContentView;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.ECGUtils;
import com.dfth.sdk.config.DfthConfig;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.file.ECGFiles;
import com.dfth.sdk.listener.ECGFileUploadListener;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.model.ecg.ECGResultUtils;
import com.dfth.sdk.network.DfthService;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.upload.ECGFileUploadTask;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/17 13:46
*/
public class EcgHistoryAdapter extends RecyclerView.Adapter<EcgHistoryAdapter.MyViewHolder>{
    private Context mContext;
    private int mDeviceType;
    private List<ECGResult> mDataList;
    private List mItemCheckList;
    private boolean mIsShow = false;
    private OnItemClickListener mItemListener;
    private ECGFileUploadTask mUploadTask;
    public EcgHistoryAdapter(Context context, int deviceType) {
        mContext = context;
        mDeviceType = deviceType;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public EcgHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ECGContentView view = new ECGContentView(mContext, mDeviceType);
        return new MyViewHolder(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final EcgHistoryAdapter.MyViewHolder holder, int position) {
        ECGResult result = mDataList.get(position);
        holder.refreshView(result);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private void setDoctorResult(TextView doctorReport, ECGResult result){
        ECGUtils.checkFileValid(result);
        String status = result.getDoctorAnalysisStatus();
        if(result.getPost() < ECGResult.UPLOAD_RESULT_SUCCESS){
            status = ECGResult.DoctorResultStatus.ECG_DATA_NOT_YET_UPLOAD.getCode();
        }else if(result.getMeasureEndTime() - result.getMeasureStartTime() < DfthConfig.getConfig()
                .ecgConfig.ecgAnalysisConfig.doctorMinAnalysisTime){
            status = ECGResult.DoctorResultStatus.ECG_TIME_LESS_THAN_ANALYSIS_TIME.getCode();
        }else if(TextUtils.isEmpty(status)){
            status = ECGResult.DoctorResultStatus.ECG_DATA_UPLOADED.getCode();
        }
        ECGResult.DoctorResultStatus doctorResultStatus = ECGResult.DoctorResultStatus.create(status);
        String content = doctorResultStatus.getDoctorAnalysis(result.getDoctorResult());
        if (doctorResultStatus.equals(ECGResult.DoctorResultStatus.ECG_SERVICE_OVER)) {
            if((Boolean) SharePreferenceUtils.get(mContext, SharePreferenceConstant.FREE_SERVICE_STATUS, false)){
                content = ThreeInOneApplication.getStringRes(R.string.get_report_service_close_tip6);
            } else{
                content = ThreeInOneApplication.getStringRes(R.string.get_report_service_close_tip5);
            }
        }
        doctorReport.setText(content);
    }
    public boolean isShow() {
        return mIsShow;
    }

    public void setShow(boolean show) {
        mIsShow = show;
    }

    public List getDataList() {
        return mDataList;
    }

    public void setDataList(List dataList) {
        mDataList = dataList;
    }

    public List getItemCheckList() {
        return mItemCheckList;
    }

    public void setItemCheckList(List itemCheckList) {
        mItemCheckList = itemCheckList;
    }

    public void setItemListener(OnItemClickListener itemListener) {
        mItemListener = itemListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ECGResult result);
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    final class MyViewHolder extends RecyclerView.ViewHolder implements AdapterItemOnclickListener, ECGFileUploadListener {
        private TextView mStartTime;
        private TextView mMeasureTime;
        private TextView mAvgRate;
        private TextView mHighRate;
        private TextView mLowRate;
        private TextView mPvcRate;
        private TextView mSpRate;
        private TextView mDoctorReport;
        private TextView mUploadText;
        private TextView mItemCheck;
        private ImageView mUploadIcon;
        private ECGResult mResult;
        MyViewHolder(View view) {
            super(view);
            mStartTime = ((ECGContentView) view).getEcgTimeText();
            mMeasureTime = ((ECGContentView) view).getMeasureTimeText();
            mAvgRate = ((ECGContentView) view).getAvgRateText();
            mHighRate = ((ECGContentView) view).getHighRateText();
            mLowRate = ((ECGContentView) view).getLowRateText();
            mPvcRate = ((ECGContentView) view).getEcgPvcText();
            mSpRate = ((ECGContentView) view).getEcgSpText();
            mDoctorReport = ((ECGContentView) view).getEcgReportText();
            mItemCheck = ((ECGContentView) view).getItemCheck();
            mUploadIcon = ((ECGContentView) view).getEcgUploadIcon();
            mUploadText = ((ECGContentView) view).getEcgUploadText();
            ((ECGContentView) view).setListener(this);
        }

        private void refreshView(ECGResult result){
            mResult = result;
            mUploadTask = NewUploadManager.getInstance().getStartTask(result,this);
            mStartTime.setText(TimeUtils.getString(result.getMeasureStartTime(), "yyyy.MM.dd HH:mm:ss"));
            mMeasureTime.setText(" ( " + TimeUtils.getMAShms(result.getMeasureEndTime() - result.getMeasureStartTime()) + " )");
            mAvgRate.setText(result.getAverHr() + " bmp");
            mHighRate.setText(result.getMaxHr() + "");
            mLowRate.setText(result.getMinHr() + "");
            mPvcRate.setText(result.getPvcCount() + " 次");
            mSpRate.setText(result.getSpCount() + " 次");
            updateUploadView(mUploadTask);
            setDoctorResult(mDoctorReport,result);
            if (mIsShow) {
                mItemCheck.setVisibility(View.VISIBLE);
                if (!(Boolean) mItemCheckList.get(this.getAdapterPosition() - 1)) {
                    if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG) {
                        mItemCheck.setBackgroundResource(R.drawable.single_delete_unselector);
                    } else {
                        mItemCheck.setBackgroundResource(R.drawable.twelve_delete_unselector);
                    }
                } else {
                    if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG) {
                        mItemCheck.setBackgroundResource(R.drawable.single_delete_selector);
                    } else {
                        mItemCheck.setBackgroundResource(R.drawable.twelve_delete_selector);
                    }
                }
//                int res = mDeviceType == DfthDevice.SingleDevice ?
//                        R.drawable.single_delete_unselector : R.drawable.twelve_delete_unselector;
//                mItemCheck.setBackgroundResource(res);
            } else {
                mItemCheck.setVisibility(View.GONE);
            }
            if (!mIsShow && mUploadTask != null) {
                updateUploadView(mUploadTask);
            }
        }


        @Override
        public void itemOnClick() {
            if (mIsShow) {
                if (!(Boolean) mItemCheckList.get(this.getAdapterPosition() - 1)) {
//                    if (mDeviceType == DfthDevice.SingleDevice) {
//                        mItemCheck.setBackgroundResource(R.drawable.single_delete_selector);
//                    } else {
//                        mItemCheck.setBackgroundResource(R.drawable.twelve_delete_selector);
//                    }
                    mItemCheckList.set(this.getAdapterPosition() - 1, true);
                    notifyDataSetChanged();
                } else {
//                    if (mDeviceType == DfthDevice.SingleDevice) {
//                        mItemCheck.setBackgroundResource(R.drawable.single_delete_unselector);
//                    } else {
//                        mItemCheck.setBackgroundResource(R.drawable.twelve_delete_unselector);
//                    }
                    mItemCheckList.set(this.getAdapterPosition() - 1, false);
                    notifyDataSetChanged();
                }
            } else {
                if (mResult.getPath() != null && ECGFiles.isExistFile(mResult)) {
                    HashMap<String,Object> deviceMap = new HashMap<>();
                    deviceMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
                    deviceMap.put(Constant.DFTH_RESULT_DATA, mResult);
                    deviceMap.put(Constant.ECG_DETAIL_NAME_TYPE, 1);
                    deviceMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
                    ActivitySkipUtils.skipAnotherActivityForResult(mContext, EcgDetailActivity.class, deviceMap,100);
                } else {
                    mItemListener.onItemClick(mResult);
                }
            }
        }

        @Override
        public void uploadClick() {
            if (!NetworkCheckReceiver.getNetwork()) {
                ToastUtils.showShort(mContext, R.string.network_not_connect);
                return;
            }
            if (mResult.getPost() < ECGResult.UPLOAD_RESULT_SUCCESS) {
                ECGFileUploadTask task = NewUploadManager.getInstance().addOrRemoveTask(mResult, this);
                updateUploadView(task);
            }
        }

        @Override
        public void onProgress(ECGResult result, int i) {
            if(mResult.equals(result)){
                mUploadText.setText(i+"%");
                if (mDeviceType == DfthDevice.SingleDevice) {
                    mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.single_history_color));
                } else{
                    mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.twelve_history_color));
                }
            }
        }

        @Override
        public void onComplete(ECGResult result,boolean b) {
            if(result != null){
                ECGResult result1 = ECGResultUtils.findResultByMeasureTime(mDataList,result.getMeasureStartTime());
                result1.setPost(result.getPost());
            }
            if(!mResult.equals(result)){
                return;
            }
            if(mResult.getPost() < ECGResult.UPLOAD_RESULT_SUCCESS){
                if (mDeviceType == DfthDevice.SingleDevice) {
                    mUploadIcon.setImageResource(R.drawable.single_upload);
                } else{
                    mUploadIcon.setImageResource(R.drawable.twelve_upload);
                }
                mUploadText.setText(ThreeInOneApplication.getStringRes(R.string.upload_no));
                if (mDeviceType == DfthDevice.SingleDevice) {
                    mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.single_history_color));
                } else{
                    mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.twelve_history_color));
                }
            }else{
                updateSingleData(result);
            }
        }

        //更新单条数据
        private void updateSingleData(final ECGResult mResult){
            final DfthService service = DfthSDKManager.getManager().getDfthService();
            if(mResult != null && !TextUtils.isEmpty(mResult.getEid())){
                service.getECGData(mResult.getUserId(),mResult.getEid()).asyncExecute(new DfthServiceCallBack<ECGResult>() {
                    @Override
                    public void onResponse(DfthServiceResult<ECGResult> dfthServiceResult) {
                        if(dfthServiceResult.mResult == 0 && dfthServiceResult.mData != null){
                            String path = mResult.getPath();
                            int processDone = ECGResult.DoctorResultStatus.ECG_DATA_NOT_YET_UPLOAD.getCode().equals(dfthServiceResult.mData.getDoctorAnalysisStatus())
                                    ? mResult.getProcessDone() : 1;
                            boolean service = ECGResult.DoctorResultStatus.create(dfthServiceResult.mData.getDoctorAnalysisStatus()).hasService();
                            int post  = service ? ECGResult.CREATE_TASK_SUCCESS : ECGResult.DoctorResultStatus.ECG_DATA_NOT_YET_UPLOAD.getCode().equals(dfthServiceResult.mData.getDoctorAnalysisStatus())
                                    ? 0 : ECGResult.UPLOAD_RESULT_SUCCESS;
                            mResult.copyOf(dfthServiceResult.mData);
                            mResult.setPost(post);
                            mResult.setProcessDone(processDone);
                            mResult.setPath(path);
                        }
                        DfthSDKManager.getManager().getDatabase().updateECGResult(mResult);
                        EventBus.getDefault().post(DfthMessageEvent.create("UPDATE_SINGLE_DATE",mResult));
                        notifyDataSetChanged();
                    }
                });
            }
        }
        private void updateUploadView(ECGFileUploadTask task){
            if (task != null){
                String showText = NewUploadManager.getInstance().getUploadStatus(task);
                mUploadText.setText(showText);
                if(task.isRunner()){
                    if (mDeviceType == Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG) {
                        mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.single_history_color));
                        mUploadIcon.setImageResource(R.drawable.single_upload);
                    } else{
                        mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.twelve_history_color));
                        mUploadIcon.setImageResource(R.drawable.twelve_upload);
                    }
                }
            }else{
                if (mResult.getPost() >= ECGResult.UPLOAD_RESULT_SUCCESS){
                    mUploadText.setText(ThreeInOneApplication.getStringRes(R.string.upload_complete));
                    mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.ecg_data_uploaded));
                    mUploadIcon.setImageResource(R.drawable.upload_success);
                }else {
                    mUploadText.setText(ThreeInOneApplication.getStringRes(R.string.upload_no));
                    if (mDeviceType == DfthDevice.SingleDevice) {
                        mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.single_history_color));
                        mUploadIcon.setImageResource(R.drawable.single_upload);
                    } else{
                        mUploadText.setTextColor(ThreeInOneApplication.getColorRes(R.color.twelve_history_color));
                        mUploadIcon.setImageResource(R.drawable.twelve_upload);
                    }
                }
            }
        }
    }
}
