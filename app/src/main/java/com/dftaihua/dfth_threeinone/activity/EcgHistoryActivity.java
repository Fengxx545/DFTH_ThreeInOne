package com.dftaihua.dfth_threeinone.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.adapter.EcgHistoryAdapter;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.dialog.CalendarDialog;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.dialog.DownloadDialog;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.DeviceUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.bluetooth.Action;
import com.dfth.sdk.database.DfthLocalDatabase;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.device.DfthECGDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.dispatch.DispatchTask;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.file.ECGFileFormat;
import com.dfth.sdk.file.ECGFiles;
import com.dfth.sdk.file.ecg.ECGResultFile;
import com.dfth.sdk.listener.DfthDeviceStateListener;
import com.dfth.sdk.listener.DfthSingleDeviceDataListener;
import com.dfth.sdk.listener.DfthTwelveDeviceDataListener;
import com.dfth.sdk.listener.ECGFileDownloadListener;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.model.ecg.ECGResultUtils;
import com.dfth.sdk.model.ecg.EcgDataTransmitted;
import com.dfth.sdk.network.DfthService;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/6 14:35
*/
public class EcgHistoryActivity extends BaseActivity implements View.OnClickListener,
        CalendarDialog.CalendarClickListener, DfthSingleDeviceDataListener,
        DfthTwelveDeviceDataListener, DfthDeviceStateListener, ECGFileDownloadListener,
        EcgHistoryAdapter.OnItemClickListener {
    private LinearLayout mCalendarView;
    private LinearLayout mStertMeasureLl;
    private XRecyclerView mHistoryListView;
    private List<ECGResult> mDataList;
    private ArrayList<Boolean> mItemCheckList;
    private int mDeviceType;
    private ImageView mImgCalender;
    private TextView mTextCalender;
    private LinearLayout mHistoryBottom;
    private HashMap<String, Object> mHashMap;
    private LinearLayoutManager mLayoutManager;
    private EcgHistoryAdapter mAdapter;
    private LinearLayout mEditHistoryBottom;
    private LinearLayout mSelectAllLL;
    private LinearLayout mDeleteLL;
    private boolean mSelectAll = false;
    private LinearLayout mNoDataLL;
    private LinearLayout mLongTimeMeasure;
    private DfthDevice mDfthDevice;
    private TextView mCancelEditText;
    private LinearLayout mFriendMeasureLl;
    private CalendarDialog mCalendarDialog;
    private LinearLayout mMeasureBottomLl;
    private TextView mMeasureNameTv;
    private TextView mMeasureTimeTv;
    private TextView mStartMeasureText;
    private String mUserId;
    private DfthService mService;
    private DfthLocalDatabase mDataBase;
    private DownloadDialog mDownloadDialog;
    private int mLine;
    private boolean isAlive = false;
    private ImageView mImgEdit;
    private TextView mTextEdit;
    private LinearLayout mEditLL;

    private void initConfig(){
        EventBus.getDefault().register(this);
        mUserId = UserManager.getInstance().getDefaultUser().getUserId();
        mService = DfthSDKManager.getManager().getDfthService();
        mDataBase = DfthSDKManager.getManager().getDatabase();
        mHashMap = new HashMap<>();
        Intent intent = getIntent();
        mDeviceType = intent.getIntExtra(Constant.DFTH_DEVICE_KEY, DfthDevice.SingleDevice);
        mLine = mDeviceType == DfthDevice.SingleDevice ? 1 : 12;
        mHashMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
        if (mDeviceType == DfthDevice.SingleDevice) {
            mTitleNameRes = R.string.title_activity_single_ecg_history;
            mTitleNameColorRes = R.color.standard_font_color;
        } else {
            mTitleNameRes = R.string.title_activity_twelve_ecg_history;
            mTitleNameColorRes = R.color.standard_font_color;
        }
        mDfthDevice = DfthDeviceManager.getInstance().getDevice(mDeviceType);
        mItemCheckList = new ArrayList<>();
    }

    private void setTitleByDfthDevice(){
        if (mDfthDevice != null && (mDfthDevice.getDeviceState() == DfthDevice.MEASURING || mDfthDevice.getDeviceState() == DfthDevice.CONNECTED || ((DfthECGDevice) mDfthDevice).isReconnect())) {
            mDfthDevice.bindStateListener(this);
            mTitleView.setTitleSaveRes(R.string.bluetooth_connect);
            mTitleView.setTitleSaveImgRes(R.drawable.bluetooth_connect);
            mTitleView.setTitleSaveTvColorRes(R.color.bluetooth_connect_tv_color);
            if (mDfthDevice.getDeviceState() == DfthDevice.MEASURING || ((DfthECGDevice) mDfthDevice).isReconnect()) {
                mHistoryBottom.setVisibility(View.GONE);
                mMeasureBottomLl.setVisibility(View.VISIBLE);
                mMeasureNameTv.setText((String) SharePreferenceUtils.get(EcgHistoryActivity.this, SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.SINGLE_MEASURE));
            }
        } else {
            mTitleView.setTitleSaveRes(R.string.bluetooth_disconnect);
            mTitleView.setTitleSaveImgRes(R.drawable.bluetooth_disconnect);
            mTitleView.setTitleSaveTvColorRes(R.color.bluetooth_disconnect_tv_color);
        }
    }

    private void initRecycleView(){
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mHistoryListView.setLayoutManager(mLayoutManager);
        //设置成横向滑动
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mHistoryListView.setHasFixedSize(true);
        mHistoryListView.setLoadingMoreEnabled(true);
        mCalendarView.setOnClickListener(this);
        mCancelEditText.setOnClickListener(this);
        mHistoryListView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (!mAdapter.isShow()) {
                    refresh(null, getStartTime(true), null);
                }else{
                    completeLoad(null);
                }
            }
            @Override
            public void onLoadMore() {
                if (!mAdapter.isShow()) {
                    refresh(null, getEndTime(), "-measuringBegin");
                }else{
                    completeLoad("-measuringBegin");
                }
            }
        });
    }

    private void initDialog(){
        mDownloadDialog = DownloadDialog.getDialog(this);
        mCalendarDialog = new CalendarDialog(EcgHistoryActivity.this, mDeviceType);
        mCalendarDialog.setCalendarClickListener(EcgHistoryActivity.this);
    }

    private View createView(){
        mStatus = TITLE_VISIBLE | BACK_VISIBLE | SAVE_VISIBLE;
        mTitleView.setTitleBackImg(R.drawable.back_black);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_single_ecg_history, null);
        mCalendarView = (LinearLayout) view.findViewById(R.id.calendar);
        mHistoryListView = (XRecyclerView) view.findViewById(R.id.single_history_list);
        mStertMeasureLl = (LinearLayout) view.findViewById(R.id.start_measure);
        mStartMeasureText = (TextView) view.findViewById(R.id.measure_start_text);
        mImgCalender = (ImageView) view.findViewById(R.id.img_calender);
        mTextCalender = (TextView) view.findViewById(R.id.text_calender);
        mHistoryBottom = (LinearLayout) view.findViewById(R.id.history_bottom);
        mEditHistoryBottom = (LinearLayout) view.findViewById(R.id.edit_bottom);
        mSelectAllLL = (LinearLayout) view.findViewById(R.id.select_all);
        mDeleteLL = (LinearLayout) view.findViewById(R.id.delete_history);
        mNoDataLL = (LinearLayout) view.findViewById(R.id.no_data_ll);
        mLongTimeMeasure = (LinearLayout) view.findViewById(R.id.long_time_measure);
        mCancelEditText = (TextView) view.findViewById(R.id.cancel_edit);
        mFriendMeasureLl = (LinearLayout) view.findViewById(R.id.friend_measure_ll);
        mMeasureBottomLl = (LinearLayout) view.findViewById(R.id.measure_bottom);
        mMeasureNameTv = (TextView) view.findViewById(R.id.measure_bottom_name);
        mMeasureTimeTv = (TextView) view.findViewById(R.id.measure_bottom_time);
        mImgEdit = (ImageView) view.findViewById(R.id.img_edit);
        mTextEdit = (TextView) view.findViewById(R.id.text_edit);
        mEditLL = (LinearLayout) view.findViewById(R.id.editting);

        mEditLL.setOnClickListener(this);
        mMeasureBottomLl.setOnClickListener(this);
        mStertMeasureLl.setOnClickListener(this);
        mSelectAllLL.setOnClickListener(this);
        mDeleteLL.setOnClickListener(this);
        mLongTimeMeasure.setOnClickListener(this);
        mFriendMeasureLl.setOnClickListener(this);
        return view;
    }

    @Override
    public View initView() {
        View view = createView();
        initConfig();
        changeStyle();
        setTitleByDfthDevice();
        initRecycleView();
        initDialog();
        return view;
    }

    private void doNetworkResults(List<ECGResult> results){
        if(results == null || results.size() == 0){
            return;
        }
        Iterator<ECGResult> iterator = results.iterator();
        while(iterator.hasNext()){
            ECGResult result = iterator.next();
            if(!updateECGResult(result)){
                iterator.remove();
            }
        }
    }

    private boolean updateECGResult(ECGResult result){
        if(result.getMeasureEndTime() <= 0){
            return false;
        }
        ECGResult oldResult = ECGResultUtils.findResultByMeasureTime(mDataList,result.getMeasureStartTime());
        boolean service = ECGResult.DoctorResultStatus.create(result.getDoctorAnalysisStatus()).hasService();
        int post = service ? ECGResult.CREATE_TASK_SUCCESS : ECGResult.DoctorResultStatus.ECG_DATA_NOT_YET_UPLOAD.getCode().equals(result.getDoctorAnalysisStatus())
                ? 0 : ECGResult.UPLOAD_RESULT_SUCCESS;
        if(oldResult == null){
            result.setProcessDone(1);
            result.setPost(post);
            result.setPath(ECGFiles.getECGStorePath(result.getMeasureStartTime(),result.getUserId()));
            mDataBase.updateECGResult(result);
        }else{
            if(oldResult.getLastNodifyTime() != result.getLastNodifyTime()){
                DfthSDKManager.getManager().getStorage().deleteECGFile(oldResult.getPath(),
                        ECGFileFormat.DAT,ECGFileFormat.INI).asyncExecute(new DfthCallBack<Boolean>() {
                    @Override
                    public void onResponse(DfthResult<Boolean> response) {

                    }
                });
            }
            oldResult.copyOf(result);
            oldResult.setProcessDone(1);
            oldResult.setPost(post);
            if(oldResult.getPath() == null){
                oldResult.setPath(ECGFiles.getECGStorePath(result.getMeasureStartTime(),result.getUserId()));
            }
            mDataBase.updateECGResult(oldResult);
        }
        return oldResult == null;
    }



    private void refresh(Long beginTime, Long endTime, final String sort) {
        if (!NetworkCheckReceiver.getNetwork()) {
            ToastUtils.showShort(this, R.string.network_not_connect);
            completeLoad(sort);
            return;
        }
        mService.getECGGroupData(mUserId, beginTime, endTime, 1, 10, mLine, sort).asyncExecute(new DfthServiceCallBack<List<ECGResult>>() {
            @Override
            public void onResponse(DfthServiceResult<List<ECGResult>> dfthServiceResult) {
                if (dfthServiceResult.mResult == 0 && dfthServiceResult.mData != null) {
                    List<ECGResult> results = dfthServiceResult.mData;
                    doNetworkResults(results);
                    mDataList.addAll(results);
                    ECGResultUtils.sortResultsDescTime(mDataList);
                    mAdapter.setDataList(mDataList);
                    mAdapter.notifyDataSetChanged();
                }
                completeLoad(sort);
            }
        });
    }
    private void completeLoad(String sort) {
        if (TextUtils.isEmpty(sort)) {
            mHistoryListView.refreshComplete();
        } else {
            mHistoryListView.loadMoreComplete();
        }
    }

    private long getStartTime(boolean top) {
        long time = System.currentTimeMillis();
        if (top) {
            return time;
        }
        if (mDataList.size() > 0) {
            int size = mDataList.size();
            if (size > 0) {
                time = mDataList.get(size - 1).getMeasureStartTime();
            }
        }
        return time;
    }

    private long getEndTime() {
        long time = System.currentTimeMillis();
        if (mDataList.size() > 0) {
            time = mDataList.get(mDataList.size() - 1).getMeasureStartTime();
        }
        return time;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DfthDeviceManager.getInstance().bindDataListener(this);
        DfthDeviceManager.getInstance().bindDataListener(this);
        if (mDfthDevice != null && (mDfthDevice.getDeviceState() == DfthDevice.MEASURING || mDfthDevice.getDeviceState() == DfthDevice.CONNECTED || ((DfthECGDevice) mDfthDevice).isReconnect())) {
            mDfthDevice.bindStateListener(this);
        }
        if (!isAlive) {
            showProgress();
            DispatchUtils.performAsnycAction(new Action(0) {
                @Override
                protected void perform() {
                    mCalendarDialog.initData();
                    if (mDeviceType == DfthDevice.SingleDevice) {
                        mDataList = DfthSDKManager.getManager().getDatabase().getSingleResult(UserManager.getInstance().getDefaultUser().getUserId());
                    } else {
                        mDataList = DfthSDKManager.getManager().getDatabase().getTwelveECGResult(UserManager.getInstance().getDefaultUser().getUserId());
                    }
                    for (int i = 0; i < mDataList.size(); i++) {
                        mItemCheckList.add(false);
                    }
                }
            }, new DfthCallBack() {
                @Override
                public void onResponse(DfthResult response) {
                    initAdapter();
                    mAdapter.setDataList(mDataList);
                    mAdapter.setItemCheckList(mItemCheckList);
                    refresh(null, getStartTime(true), null);
                    DispatchUtils.performMainThreadDelay(new DispatchTask<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            if (mDataList.size() == 0) {
                                mNoDataLL.setVisibility(View.VISIBLE);
                                mHistoryListView.setVisibility(View.GONE);
                            }
                        }
                    },200);
                    dismissProgress();
                }
            });
        }else{
            mAdapter.setDataList(mDataList);
            mAdapter.setItemCheckList(mItemCheckList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initAdapter(){
        mAdapter = new EcgHistoryAdapter(EcgHistoryActivity.this, mDeviceType);
        mAdapter.setItemListener(EcgHistoryActivity.this);
        mHistoryListView.setAdapter(mAdapter);
    }
    private void changeStyle() {
        if (mDeviceType == DfthDevice.SingleDevice) {
            mCancelEditText.setTextColor(ThreeInOneApplication.getColorRes(R.color.single_color));
            mTextCalender.setTextColor(ThreeInOneApplication.getColorRes(R.color.single_color));
            mHistoryBottom.setBackgroundResource(R.drawable.shape_bottom_single_ecg);
            mEditHistoryBottom.setBackgroundResource(R.drawable.shape_bottom_single_ecg);
            mMeasureBottomLl.setBackgroundResource(R.drawable.shape_bottom_single_ecg);
            mLongTimeMeasure.setVisibility(View.VISIBLE);
            mStartMeasureText.setText(R.string.single_ecg_btn_measure);
            mImgCalender.setImageResource(R.drawable.single_calendar);
            mTextEdit.setTextColor(ThreeInOneApplication.getColorRes(R.color.single_color));
            mImgEdit.setImageResource(R.drawable.single_edit);
        } else {
            mLongTimeMeasure.setVisibility(View.GONE);
            mStartMeasureText.setText(R.string.twelve_ecg_btn_measure);
            mCancelEditText.setTextColor(ThreeInOneApplication.getColorRes(R.color.twelve_color));
            mTextCalender.setTextColor(ThreeInOneApplication.getColorRes(R.color.twelve_color));
            mHistoryBottom.setBackgroundResource(R.drawable.shape_bottom_twelve_ecg);
            mEditHistoryBottom.setBackgroundResource(R.drawable.shape_bottom_twelve_ecg);
            mMeasureBottomLl.setBackgroundResource(R.drawable.shape_bottom_twelve_ecg);
            mImgCalender.setImageResource(R.drawable.twelve_calendar);
            mTextEdit.setTextColor(ThreeInOneApplication.getColorRes(R.color.twelve_color));
            mImgEdit.setImageResource(R.drawable.twelve_edit);
        }
    }
    public void toMeasure() {
        mHashMap.put(SharePreferenceConstant.LONG_TIME_MEASURE, SharePreferenceConstant.LONG_TIME_MARK);
        SharePreferenceUtils.put(EcgHistoryActivity.this, SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.SINGLE_LONG_MEASURE);
        ActivitySkipUtils.skipAnotherActivityForResult(EcgHistoryActivity.this, EcgMeasureActivity.class, mHashMap, 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.long_time_measure: {
                String text = DeviceUtils.isExistDeviceMeasuring(mDeviceType);
                if (!TextUtils.isEmpty(text)) {
                    DialogFactory.getMeasuringDialog(this, text).show();
                } else {
                    DialogFactory.getTipDialog(this, ThreeInOneApplication.getStringRes(R.string.long_time_tip)).show();
                }
            }
            break;
            case R.id.friend_measure_ll: {
                ToastUtils.showShort(this, R.string.slide_menu_add_home_doctor);
            }
            break;
            case R.id.calendar: {
                if (mCalendarDialog.isShowing()) {
                    mCalendarDialog.dismiss();
                } else {
                    mCalendarDialog.show();
                }
            }
            break;
            case R.id.start_measure: {
                String text = DeviceUtils.isExistDeviceMeasuring(mDeviceType);
                if (!TextUtils.isEmpty(text)) {
                    DialogFactory.getMeasuringDialog(this, text).show();
                } else {
                    if (mDeviceType == DfthDevice.SingleDevice) {
                        SharePreferenceUtils.put(EcgHistoryActivity.this, SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.SINGLE_MEASURE);
                    } else {
                        SharePreferenceUtils.put(EcgHistoryActivity.this, SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.TWELVE_MEASURE);
                    }
                    mHashMap.put(SharePreferenceConstant.LONG_TIME_MEASURE, -1);
                    ActivitySkipUtils.skipAnotherActivityForResult(this, EcgMeasureActivity.class, mHashMap, 1);
                }
            }
            break;
            case R.id.cancel_edit: {
                mHistoryBottom.setVisibility(View.VISIBLE);
                mEditHistoryBottom.setVisibility(View.GONE);
                mCalendarView.setVisibility(View.VISIBLE);
                mCancelEditText.setVisibility(View.GONE);
                mAdapter.setShow(false);
                mEditLL.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.select_all: {
                if (!mSelectAll) {
                    mSelectAll = true;
                    for (int i = 0; i < mItemCheckList.size(); i++) {
                        mItemCheckList.set(i, true);
                    }
                } else {
                    mSelectAll = false;
                    for (int i = 0; i < mItemCheckList.size(); i++) {
                        mItemCheckList.set(i, false);
                    }
                }
                mAdapter.setItemCheckList(mItemCheckList);
                mAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.delete_history: {
                int length = mItemCheckList.size();
                for (int i = length - 1; i >= 0; i--) {
                    if (mItemCheckList.get(i)) {
                        ECGResult ecgResult = mDataList.get(i);
                        DfthSDKManager.getManager().getDatabase().deleteECGResult(ecgResult.getMeasureStartTime());
                        mDataList.remove(i);
                        mItemCheckList.remove(i);
                        mAdapter.notifyItemRemoved(i);
                        mAdapter.notifyItemRangeChanged(0, mDataList.size());
                    }
                }
                if (mDataList.size() == 0) {
                    mHistoryListView.setVisibility(View.GONE);
                    mNoDataLL.setVisibility(View.VISIBLE);
                    mHistoryBottom.setVisibility(View.VISIBLE);
                    mEditHistoryBottom.setVisibility(View.GONE);
                    mCalendarView.setVisibility(View.VISIBLE);
                    mEditLL.setVisibility(View.VISIBLE);
                    mCancelEditText.setVisibility(View.GONE);
                }
            }
            break;
            case R.id.editting: {
                if (mDfthDevice != null && mDfthDevice.getDeviceState() == DfthDevice.MEASURING) {
//                    ToastUtils.showShort(this, R.string.measureing_cant_edit);
                    break;
                }
                if (mDataList.size() == 0) {
                    ToastUtils.showShort(this, R.string.no_data);
                    break;
                }
                if (!mAdapter.isShow()) {
                    mHistoryBottom.setVisibility(View.GONE);
                    mCalendarView.setVisibility(View.GONE);
                    mEditLL.setVisibility(View.GONE);
                    mEditHistoryBottom.setVisibility(View.VISIBLE);
                    mCancelEditText.setVisibility(View.VISIBLE);
                    mAdapter.setShow(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
            break;
            case R.id.measure_bottom: {
                String measureType = (String) SharePreferenceUtils.get(EcgHistoryActivity.this, SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.SINGLE_MEASURE);
                switch (measureType) {
                    case SharePreferenceConstant.SINGLE_LONG_MEASURE:
                        mHashMap.put(SharePreferenceConstant.LONG_TIME_MEASURE, SharePreferenceConstant.LONG_TIME_MARK);
                        ActivitySkipUtils.skipAnotherActivityForResult(EcgHistoryActivity.this, EcgMeasureActivity.class, mHashMap, 1);
                        break;
                    case SharePreferenceConstant.SINGLE_FRIEND_MEASURE:

                        break;
                    case SharePreferenceConstant.TWELVE_MEASURE:
                        ActivitySkipUtils.skipAnotherActivityForResult(EcgHistoryActivity.this, EcgMeasureActivity.class, mHashMap, 1);
                        break;
                }
            }
            break;
        }
    }
    private void blueState(DfthDevice device) {
        if (device != null && (device.getDeviceState() == DfthDevice.MEASURING || device.getDeviceState() == DfthDevice.CONNECTED)) {
            mTitleView.setTitleSaveRes(R.string.bluetooth_connect);
            mTitleView.setTitleSaveImgRes(R.drawable.bluetooth_connect);
            mTitleView.setTitleSaveTvColorRes(R.color.bluetooth_connect_tv_color);
        } else {
            mTitleView.setTitleSaveRes(R.string.bluetooth_disconnect);
            mTitleView.setTitleSaveImgRes(R.drawable.bluetooth_disconnect);
            mTitleView.setTitleSaveTvColorRes(R.color.bluetooth_disconnect_tv_color);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            mDfthDevice = DfthDeviceManager.getInstance().getDevice(mDeviceType);
            blueState(mDfthDevice);
            if (mDfthDevice != null && (mDfthDevice.getDeviceState() == DfthDevice.MEASURING || ((DfthECGDevice) mDfthDevice).isReconnect())) {
                mHistoryBottom.setVisibility(View.GONE);
                mMeasureBottomLl.setVisibility(View.VISIBLE);
                mMeasureNameTv.setText((String) SharePreferenceUtils.get(EcgHistoryActivity.this, SharePreferenceConstant.MEASURE_TYPE, SharePreferenceConstant.SINGLE_MEASURE));
            } else {
                mHistoryBottom.setVisibility(View.VISIBLE);
                mMeasureBottomLl.setVisibility(View.GONE);
            }
            updateList();
        }
        if(requestCode == 100){
            updateList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateList() {
        final int size;
        if(mDataList == null || mDataList.size() < 10){
            size = 10;
        }else{
            size = mDataList.size();
        }
        if (mDeviceType == DfthDevice.SingleDevice) {
            mDataList = DfthSDKManager.getManager().getDatabase().getSingleResultNums(UserManager.getInstance().getDefaultUser().getUserId(),System.currentTimeMillis(),size);
        } else {
            mDataList = DfthSDKManager.getManager().getDatabase().getTwelveECGResultNums(UserManager.getInstance().getDefaultUser().getUserId(),System.currentTimeMillis(),size);
        }
        for (int i = 0; i < mDataList.size(); i++) {
            mItemCheckList.add(false);
        }
        if(mAdapter == null){
            initAdapter();
        }
        if (mDataList.size() != 0) {
            mNoDataLL.setVisibility(View.GONE);
            mHistoryListView.setVisibility(View.VISIBLE);
            mAdapter.setShow(false);
        }
        if (!(mDfthDevice != null && mDfthDevice.getDeviceState() == DfthDevice.MEASURING)) {
            mAdapter.setDataList(mDataList);
            mAdapter.setItemCheckList(mItemCheckList);
            mAdapter.notifyDataSetChanged();
        }
        refresh(null, getStartTime(true), null);
    }

    private void updateList(long startTime, long endTime) {
        if (mDeviceType == DfthDevice.SingleDevice) {
            mDataList = DfthSDKManager.getManager().getDatabase().getSingleResult(UserManager.getInstance().getDefaultUser().getUserId(), startTime, endTime);
        } else {
            mDataList = DfthSDKManager.getManager().getDatabase().getTwelveECGResult(UserManager.getInstance().getDefaultUser().getUserId(), startTime, endTime);
        }
        mCalendarDialog.dismiss();
        if (mDataList.size() == 0 || mDataList == null) {
            mNoDataLL.setVisibility(View.VISIBLE);
            mHistoryListView.setVisibility(View.GONE);
            ToastUtils.showShort(this, R.string.single_ecg_select_date_no_data);
            return;
        }
        for (int i = 0; i < mDataList.size(); i++) {
            mItemCheckList.add(false);
        }
        mNoDataLL.setVisibility(View.GONE);
        mHistoryListView.setVisibility(View.VISIBLE);
        mAdapter.setDataList(mDataList);
        mAdapter.setItemCheckList(mItemCheckList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void clickConfirm() {
        String date = mCalendarDialog.getSelectDate();
        if (!mCalendarDialog.checkOneDayHasData(date)) {
            ToastUtils.showShort(this, R.string.single_ecg_select_date_no_data);
        }
        long endTime = TimeUtils.getOneDayEndTime(TimeUtils.stringToCalendar(date));
        updateList(0, endTime);
    }

    @Override
    public void clickCancel() {
        mCalendarDialog.dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DfthMessageEvent event) {
        if (event.getEventName().equals(EventNameMessage.PUSH_GET_DATA_SUCCESS)) {
//            refreshView();
        }else if(event.getEventName().equals(EventNameMessage.PUSH_UPDATE_DETAIL)){
            ECGResult result = (ECGResult) event.getData();
            if(result != null){
                ECGResult result1 = ECGResultUtils.findResultByMeasureTime(mDataList,result.getMeasureStartTime());
                if(result1 != null){
                    result1.copyOf(result);
                }
                if(mAdapter != null){
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onLeaderOut(boolean b) {

    }

    @Override
    public void onDataChanged(EcgDataTransmitted ecgDataTransmitted) {
        String time = TimeUtils.getMeasureTime(System.currentTimeMillis() - ecgDataTransmitted.getStartTime());
        mMeasureTimeTv.setText(time);
    }

    @Override
    public void onBatteryChanged(float v) {

    }

    @Override
    public void onResultData(ECGResult ecgResult) {
        mHashMap.put(Constant.DFTH_RESULT_DATA, ecgResult);
        ActivitySkipUtils.skipAnotherActivityForResult(EcgHistoryActivity.this, EcgDetailActivity.class, mHashMap, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAlive = true;
        DfthDeviceManager.getInstance().unBindDataListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onLeaderStatusChanged(boolean[] booleen) {

    }

    @Override
    public void onSosStatus(boolean b) {

    }

    @Override
    public void onStateChange(int state) {
        blueState(mDfthDevice);
    }

    @Override
    public void startProcessECGResult() {

    }

    @Override
    public void onProgress(int progress) {
        if (mDownloadDialog != null) {
            mDownloadDialog.setProgress(progress);
        }
    }

    @Override
    public void onComplete(boolean success, ECGResult result) {
        if (mDownloadDialog != null) {
            mDownloadDialog.dismiss();
        }
        if (!success) {
            ToastUtils.showShort(this, R.string.single_ecg_download_data_fail);
        } else {
            HashMap<String, Object> deviceMap = new HashMap<>();
            ECGResultFile file = null;
            if (result != null) {
                mDataBase.updateECGResult(result);
                try {
                    file = ECGResultFile.create(result.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (file != null) {
                deviceMap.put(Constant.DFTH_RESULT_DATA, result);
                deviceMap.put(Constant.DFTH_DEVICE_KEY, mDeviceType);
                deviceMap.put(Constant.ECG_DETAIL_NAME_TYPE, 1);
                ActivitySkipUtils.skipAnotherActivityForResult(EcgHistoryActivity.this, EcgDetailActivity.class, deviceMap,100);
            } else {
                ToastUtils.showShort(this, R.string.single_ecg_no_file);
            }
        }
    }

    @Override
    public void onItemClick(ECGResult result) {
        if (!NetworkCheckReceiver.getNetwork()) {
            ToastUtils.showShort(this, R.string.network_not_connect);
            return;
        }
        mService.downloadECGFile(mUserId, result, this);
        mDownloadDialog.show();
    }
}
