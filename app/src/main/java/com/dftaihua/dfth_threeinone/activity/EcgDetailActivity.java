package com.dftaihua.dfth_threeinone.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.adapter.MyFragmentPagerAdapter;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.dialog.DownloadDialog;
import com.dftaihua.dfth_threeinone.fragment.DoctorReviewFragment;
import com.dftaihua.dfth_threeinone.fragment.EcgDataFragment;
import com.dftaihua.dfth_threeinone.listener.PosListener;
import com.dftaihua.dfth_threeinone.listener.SimpleAnimation;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.NewUploadManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.mediator.ECGHistoryMediator;
import com.dftaihua.dfth_threeinone.utils.ECGDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.ECGLeaderUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.widget.ECGLookProgressBar;
import com.dftaihua.dfth_threeinone.widget.HeartChartView;
import com.dftaihua.dfth_threeinone.widget.LeaderRect;
import com.dftaihua.dfth_threeinone.widget.NewWaveView.WaveRectChangeListener;
import com.dftaihua.dfth_threeinone.widget.TabView;
import com.dftaihua.dfth_threeinone.widget.view.ECGHistoryWaveView;
import com.dftaihua.dfth_threeinone.widget.view.ECGTwelveLeaderView;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.ECGUtils;
import com.dfth.sdk.Protocol.Ecg.LocalSportAlgorithm;
import com.dfth.sdk.bluetooth.Action;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.file.ECGFileFormat;
import com.dfth.sdk.file.ECGFiles;
import com.dfth.sdk.file.ecg.ECGResultFile;
import com.dfth.sdk.listener.ECGFileDownloadListener;
import com.dfth.sdk.listener.ECGFileUploadListener;
import com.dfth.sdk.model.ecg.ECGMeasureData;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.network.DfthService;
import com.dfth.sdk.network.response.DfthServiceResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/8 14:35
*/
public class EcgDetailActivity extends BaseActivity implements View.OnClickListener, WaveRectChangeListener,
        PosListener, LeaderRect.OnCheckListener, ECGHistoryMediator, EcgDataFragment.DiseaseChange, EcgDataFragment.DiseaseCountListener
        ,ECGFileUploadListener{
    private final int MAX_SCREEN_LENGTH = 750;
    private ViewPager mHistoryViewPager;
    private ArrayList<Fragment> listFragments;
    private EcgDataFragment mEcgDataFragment;
    private DoctorReviewFragment mDoctorReviewFragment;
    private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
    private TabView mECGDataTab;
    private TabView mECGDoctorTab;
    private int mDeviceType;
    private ECGLookProgressBar mProgressBar;
    private ECGResultFile mECGResultFile;
    private ECGResult mECGResult;
    private HeartChartView mHeartChartView;
    private TextView mHeartText;
    private TextView mTimeText;
    private ImageView mHeartIcon;
    private TextView mSportStatus;
    private LinearLayout mChangeLeadTv;
    private ImageView mBackDisease;
    private ImageView mNextDisease;
    private int mNameType;
    private int mCurrentCount = -1;
    private int mMaxCount = -1;
    private short mRhytm = -1;
    private int mPrePos;
    private ECGHistoryWaveView mECGHistoryWaveView;
    private ECGTwelveLeaderView mLeaderView;
    private boolean isFullScreen = false;
    public EcgDetailActivity() {
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
        mTitleBackRes = R.drawable.back_black;
    }
    @Override
    public View initView() {
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        mDeviceType = intent.getIntExtra(Constant.DFTH_DEVICE_KEY, DfthDevice.SingleDevice);
        mNameType = intent.getIntExtra(Constant.ECG_DETAIL_NAME_TYPE, 0);
        mECGResult = (ECGResult) intent.getSerializableExtra(Constant.DFTH_RESULT_DATA);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_single_ecg_detail, null);
        mHistoryViewPager = (ViewPager) view.findViewById(R.id.ecg_history_viewpager);
        mECGDataTab = (TabView) view.findViewById(R.id.ecg_data_tab);
        mECGDoctorTab = (TabView) view.findViewById(R.id.ecg_doctor_tab);
        mProgressBar = (ECGLookProgressBar) view.findViewById(R.id.ecg_look_progress);
        mHeartChartView = (HeartChartView) view.findViewById(R.id.ecg_heart_chart);
        mHeartText = (TextView) view.findViewById(R.id.ecg_look_bpm);
        mTimeText = (TextView) view.findViewById(R.id.ecg_look_time);
        mHeartIcon = (ImageView) view.findViewById(R.id.history_heart);
        mSportStatus = (TextView) view.findViewById(R.id.ecg_sport_status);
        mChangeLeadTv = (LinearLayout) view.findViewById(R.id.change_lead_ll);
        mBackDisease = (ImageView) view.findViewById(R.id.wave_left);
        mNextDisease = (ImageView) view.findViewById(R.id.wave_right);
        if (mNameType == 1) {
            mTitleNameRes = R.string.title_activity_detail;
        } else {
            mTitleNameRes = R.string.title_activity_result;
        }
        changeStyleByDevicetype();
        mBackDisease.setOnClickListener(this);
        mNextDisease.setOnClickListener(this);
        check(View.GONE, View.GONE);
        mChangeLeadTv.setOnClickListener(this);
        mECGDataTab.setOnClickListener(this);
        mECGDoctorTab.setOnClickListener(this);
        mECGHistoryWaveView = (ECGHistoryWaveView) view.findViewById(R.id.ecg_history_wave_view);
        mECGHistoryWaveView.bindMediator(this);
        mLeaderView = (ECGTwelveLeaderView) view.findViewById(R.id.ecg_detail_leader_view);
        mLeaderView.setLeaders(ECGDeviceUtils.getTwelveLeaders());
        mLeaderView.bindMediator(this);
        if (mECGResult != null) {
            mECGHistoryWaveView.setPosTime(TimeUtils.getString(mECGResult.getMeasureStartTime(), "HH:mm:ss"));
        }
        mDownloadDialog = DownloadDialog.getDialog(this);
        NewUploadManager.getInstance().getStartTask(mECGResult,this);
        return view;
    }

    private void changeStyleByDevicetype(){
        if (mDeviceType == DfthDevice.SingleDevice) {
            mTitleNameColorRes = R.color.google_black;
            mProgressBar.setProgressDrawable(ThreeInOneApplication.getDrawableRes(R.drawable.single_po_seekbar));
            mProgressBar.setThumb(ThreeInOneApplication.getDrawableRes(R.drawable.single_thumb));
            mHeartIcon.setImageResource(R.drawable.single_measure_logo);
            mChangeLeadTv.setVisibility(View.GONE);
            mSportStatus.setVisibility(View.VISIBLE);
        } else {
            mTitleNameColorRes = R.color.google_black;
            mProgressBar.setProgressDrawable(ThreeInOneApplication.getDrawableRes(R.drawable.twelve_po_seekbar));
            mProgressBar.setThumb(ThreeInOneApplication.getDrawableRes(R.drawable.twelve_thumb));
            mHeartIcon.setImageResource(R.drawable.twelve_measure_logo);
            mChangeLeadTv.setVisibility(View.VISIBLE);
            mSportStatus.setVisibility(View.GONE);
        }
    }
    private void check(int status1, int status2) {
        mBackDisease.setVisibility(status1);
        mNextDisease.setVisibility(status2);
    }
    @Override
    public void initData() {
        showProgress();
        DispatchUtils.performAsnycAction(new Action(0) {
            @Override
            protected void perform() {
                openResultFile();
                if (mECGResultFile != null) {
                    mHeartChartView.setFile(mECGResultFile, mDeviceType);
                }
            }
        }, new DfthCallBack() {
            @Override
            public void onResponse(DfthResult response) {
                if (mECGResultFile != null) {
                    configWaveView();
                    configSeekBar();
                    seekBarChange(mPrePos);
                    listFragments = new ArrayList<>();
                    if(mEcgDataFragment == null){
                        mEcgDataFragment = new EcgDataFragment(mDeviceType, mECGResultFile, mECGResult);
                        listFragments.add(mEcgDataFragment);
                    }else{
                        mEcgDataFragment.setData(mDeviceType,mECGResultFile,mECGResult);
                    }
                    if(mDoctorReviewFragment == null){
                        mDoctorReviewFragment = new DoctorReviewFragment(mDeviceType, mECGResult);
                        listFragments.add(mDoctorReviewFragment);
                        mDoctorReviewFragment.bindMediator(EcgDetailActivity.this);
                    }else{
                        mDoctorReviewFragment.setDoctorResult(mECGResult);
                    }
                    if(mMyFragmentPagerAdapter == null){
                        mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), listFragments);
                        mHistoryViewPager.setAdapter(mMyFragmentPagerAdapter);
                        mECGDataTab.setViewFocus(true, mDeviceType);
                        mECGDoctorTab.setViewFocus(false, mDeviceType);
                        mEcgDataFragment.setmDiseaseChange(EcgDetailActivity.this);
                        mEcgDataFragment.setmDiseaseCountlistener(EcgDetailActivity.this);
                        if (mECGResult != null && mECGResult.getDoctorAnalysisStatus() != null) {
                            if (mECGResult.getDoctorAnalysisStatus().equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_NORMAL.getCode())
                                    || mECGResult.getDoctorAnalysisStatus().equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_ANALYSIS_EXCEPTION.getCode())
                                    || mECGResult.getDoctorAnalysisStatus().equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_OAUTH_EXCEPTION.getCode())
                                    || mECGResult.getDoctorAnalysisStatus().equals(ECGResult.DoctorResultStatus.ECG_DOCTOR_WARN.getCode())) {
                                mECGDataTab.setViewFocus(false, mDeviceType);
                                mECGDoctorTab.setViewFocus(true, mDeviceType);
                                mHistoryViewPager.setCurrentItem(1, true);
                            }
                        }
                    }
                }
                dismissProgress();
            }
        });
    }

    private void configWaveView() {
        if (mECGResult.getLeaders() == 1) {
            mECGHistoryWaveView.setIsDisplayLeaders(false);
            mECGHistoryWaveView.leaderChange(ECGLeaderUtils.getSingleLeaders());
        } else {
            mECGHistoryWaveView.setIsDisplayLeaders(true);
            mECGHistoryWaveView.leaderChange(ECGLeaderUtils.getTwelveLeaders());
        }
    }

    private void configSeekBar() {
        mProgressBar.setMax((int) (mECGResultFile.pts() - MAX_SCREEN_LENGTH));
        mProgressBar.bindMediator(this);
    }

    private void openResultFile() {
        try {
            mECGResultFile = ECGResultFile.create(mECGResult.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ecg_data_tab: {
                mECGDataTab.setViewFocus(true, mDeviceType);
                mECGDoctorTab.setViewFocus(false, mDeviceType);
                mHistoryViewPager.setCurrentItem(0, true);
            }
            break;
            case R.id.ecg_doctor_tab: {
                mECGDataTab.setViewFocus(false, mDeviceType);
                mECGDoctorTab.setViewFocus(true, mDeviceType);
                mHistoryViewPager.setCurrentItem(1, true);
            }
            break;
            case R.id.change_lead_ll: {
                if (mLeaderView.getVisibility() == View.VISIBLE) {
                    mLeaderView.setVisibility(View.GONE);
                } else {
                    mLeaderView.setVisibility(View.VISIBLE);
                }
            }
            break;
            case R.id.wave_left: {
                if (mProgressBar != null) {
                    mCurrentCount--;
                    int pos = (int) mECGResultFile.getRhythmData(mRhytm, mCurrentCount, 750);
                    diseaseChange(pos);
                    int status = mCurrentCount <= 0 ? View.GONE : View.VISIBLE;
                    check(status, View.VISIBLE);
                }
            }
            break;
            case R.id.wave_right: {
                if (mProgressBar != null) {
                    mCurrentCount++;
                    int pos = (int) mECGResultFile.getRhythmData(mRhytm, mCurrentCount, 750);
                    diseaseChange(pos);
                    int status = mCurrentCount >= mMaxCount - 1 ? View.GONE : View.VISIBLE;
                    check(View.VISIBLE, status);
                }
            }
            break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchEvent = event.getAction();
        switch (touchEvent) {
            case MotionEvent.ACTION_DOWN: {
                if (mLeaderView.getVisibility() == View.VISIBLE) {
                    mLeaderView.setVisibility(View.GONE);
                }
            }
            break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void screenIn() {
        final View detailsOther = findViewById(R.id.ecg_details_other);
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f);
        animation.setDuration(300);
        detailsOther.startAnimation(animation);
        animation.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) detailsOther.getLayoutParams();
                params.weight = (float) (3.0 - input * 3.0f <= 0 ? 0 : (3.0 - input * 3.0f));
                detailsOther.setLayoutParams(params);
                return input;
            }
        });
        animation.setAnimationListener(new SimpleAnimation() {
            @Override
            public void onAnimationEnd(Animation animation) {
                detailsOther.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void screenOut() {
        final View detailsOther = findViewById(R.id.ecg_details_other);
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(300);
        detailsOther.startAnimation(animation);
        detailsOther.setVisibility(View.VISIBLE);
        animation.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) detailsOther.getLayoutParams();
                params.weight = input * 3.0f;
                detailsOther.setLayoutParams(params);
                return input;
            }
        });
    }

    @Override
    public void screenHScreen() {

    }

    @Override
    public void screenVScreen() {

    }

    @SuppressLint("DefaultLocale")
    private void setHr(int value) {
        String text;
        if (value < 100 && value > 0) {
            text = String.format("0%d bpm", value);
        } else if (value <= 0) {
            text = " _ _" + " bpm";
        } else {
            text = String.format("%d bpm", value);
        }
        mHeartText.setText(text);
    }

    private void setRecordTime(String time) {
        mTimeText.setText(time);
    }

    @Override
    public void posChange(int pos, ECGResultFile file, int len, int position) {
        int hr;
        if (position >= file.getLocal().getResults().length) {
            hr = 0;
        } else {
            hr = file.getLocal().getResults()[position]._hr;
        }
        if (hr != 0 || pos < MAX_SCREEN_LENGTH) {
            setHr(hr);
        }
        if (position == 0 || position == 1) {
            setHr(0);
        }
        String time = ECGUtils.getSampingTimeString(pos, 250);
        setRecordTime(time);
        if (file.getSportStatusFile() != null) {
            int sportStatus = file.getSportStatusByPos(pos).status;
            int color = sportStatus == LocalSportAlgorithm.STATUS_PEACEFUL ? R.color.google_green
                    : sportStatus == LocalSportAlgorithm.STATUS_FAST_MOTION ? R.color.google_orange
                    : R.color.google_yellow;
            int stringRes = sportStatus == LocalSportAlgorithm.STATUS_PEACEFUL ? R.string.sport_peaceful
                    : sportStatus == LocalSportAlgorithm.STATUS_FAST_MOTION ? R.string.sport_fast_ful
                    : R.string.sport_slow_ful;
            mSportStatus.setTextColor(ThreeInOneApplication.getColorRes(color));
            mSportStatus.setText(ThreeInOneApplication.getStringRes(stringRes));
        }
    }

    @Override
    public boolean onCheckChange(int position, boolean checked) {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(DfthMessageEvent event) {
        if (event.getEventName().equals(EventNameMessage.PUSH_UPDATE_DETAIL)) {
            ECGResult result = (ECGResult) event.getData();
            if(mECGResult == null || mECGResult.getMeasureStartTime() == result.getMeasureStartTime()){
                mECGResult =result;
                mDeviceType = mECGResult.getLeaders() == 1 ? DfthDevice.SingleDevice : DfthDevice.EcgDevice;
                changeStyleByDevicetype();
                getDoctorAnalyse();
                initData();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NewUploadManager.getInstance().getStartTask(mECGResult,null);
        setResult(RESULT_OK,new Intent());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(1);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void longPressScreen() {

    }

    @Override
    public void singlePressScreen() {

    }

    @Override
    public void doublePressScreen() {
        if (!isFullScreen) {
            screenIn();
        } else {
            screenOut();
        }
        isFullScreen = !isFullScreen;
    }

    @Override
    public void waveChange(int deltaPos) {
        int pos = mECGResultFile.pos() + deltaPos;
        pos = pos <= 0 ? 0 : (int) (pos > mECGResultFile.pts() - MAX_SCREEN_LENGTH ? mECGResultFile.pts() - MAX_SCREEN_LENGTH : pos);
        mECGResultFile.seekTo(pos);
        mProgressBar.setProgress(pos);
        seekBarChange(pos);
    }

    @Override
    public void selectLeader(boolean[] leaders) {
        mECGHistoryWaveView.leaderChange(leaders);
        waveChange(0);
    }

    private void createECGTask(DfthService service){
        service.createServiceTask(UserManager.getInstance().getDefaultUser().getUserId(),
                mECGResult.getEid(), DfthService.ALLECG).asyncExecute(new DfthServiceCallBack<Void>() {
            @Override
            public void onResponse(DfthServiceResult<Void> dfthServiceResult) {
                if (dfthServiceResult.mResult == 0) {
                    getDoctorAnalyse();
                } else {
//                    ToastUtils.showShort(EcgDetailActivity.this, dfthServiceResult.mMessage);
                }
            }
        });
    }
    private void refreshView(ECGResult result){
        mECGResult = result;
        if(mDoctorReviewFragment != null){
            mDoctorReviewFragment.setDoctorResult(mECGResult);
        }
    }
    private DownloadDialog mDownloadDialog;
    private void checkDownload() {
        if(mECGResult.getPath() != null && ECGFiles.isExistFile(mECGResult)) {
            refreshView(mECGResult);
        }else{
            final DfthService service = DfthSDKManager.getManager().getDfthService();
            service.downloadECGFile(mECGResult.getUserId(), mECGResult, new ECGFileDownloadListener() {
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
                    initData();
                }
            });

        }
    }

    @Override
    public void getDoctorAnalyse() {
        if (!NetworkCheckReceiver.getNetwork()) {
            ToastUtils.showShort(this, R.string.network_not_connect);
            return;
        }
        mPrePos = mECGResultFile == null ? 0 : mECGResultFile.pos();
        final DfthService service = DfthSDKManager.getManager().getDfthService();
        if (mECGResult != null && !TextUtils.isEmpty(mECGResult.getEid())) {
            service.getECGData(mECGResult.getUserId(), mECGResult.getEid()).asyncExecute(new DfthServiceCallBack<ECGResult>() {
                @Override
                public void onResponse(DfthServiceResult<ECGResult> response) {
                    if (response.mResult == 0 && response.mData != null) {
                        ECGResult result = response.mData;
                        boolean hasService = ECGResult.DoctorResultStatus.create(result.getDoctorAnalysisStatus()).hasService();
                        int post = hasService ? ECGResult.CREATE_TASK_SUCCESS : ECGResult.DoctorResultStatus.ECG_DATA_NOT_YET_UPLOAD.getCode().equals(result.getDoctorAnalysisStatus())
                                ? 0 : ECGResult.UPLOAD_RESULT_SUCCESS;
                        if(mECGResult.getLastNodifyTime() != result.getLastNodifyTime()){
                            DfthSDKManager.getManager().getStorage().deleteECGFile(mECGResult.getPath(),
                                    ECGFileFormat.DAT,ECGFileFormat.INI).asyncExecute(new DfthCallBack<Boolean>() {
                                @Override
                                public void onResponse(DfthResult<Boolean> response) {

                                }
                            });
                        }
                        mECGResult.copyOf(result);
                        mECGResult.setProcessDone(1);
                        mECGResult.setPost(post);
                        DfthSDKManager.getManager().getDatabase().updateECGResult(mECGResult);
                        hasService = ECGResult.DoctorResultStatus.create(mECGResult.getDoctorAnalysisStatus()).hasService();
                        if(!hasService){
                            createECGTask(service);
                            if(mECGResult.getPath() != null && ECGFiles.isExistFile(mECGResult)) {
                                refreshView(mECGResult);
                            }
                        }else {
                            checkDownload();
                        }
                    }else{
                        ToastUtils.showShort(EcgDetailActivity.this,"获取医师分析失败");
                    }
                }
            });
        }
    }

    @Override
    public void seekBarChange(int pos) {
        int position = mECGResultFile.getRecentPeakPositionByPos(pos);
        mHeartChartView.posChange(pos, mECGResultFile, MAX_SCREEN_LENGTH, position);
        pos = mECGResultFile.pts() > MAX_SCREEN_LENGTH ? pos : 0;
        ECGMeasureData data = mECGResultFile.getData(pos, MAX_SCREEN_LENGTH);
        mECGHistoryWaveView.setEcgResultFile(mECGResultFile);
        mECGHistoryWaveView.drawWave(data);
        String time = ECGUtils.getSampingTimeString(pos, 250);
        setRecordTime(time);
        if (mECGResultFile.getSportStatusFile() != null) {
            int sportStatus = mECGResultFile.getSportStatusByPos(pos).status;
            int color = sportStatus == LocalSportAlgorithm.STATUS_PEACEFUL ? R.color.google_green
                    : sportStatus == LocalSportAlgorithm.STATUS_FAST_MOTION ? R.color.google_orange
                    : R.color.google_yellow;
            int stringRes = sportStatus == LocalSportAlgorithm.STATUS_PEACEFUL ? R.string.sport_peaceful
                    : sportStatus == LocalSportAlgorithm.STATUS_FAST_MOTION ? R.string.sport_fast_ful
                    : R.string.sport_slow_ful;
            mSportStatus.setTextColor(ThreeInOneApplication.getColorRes(color));
            mSportStatus.setText(ThreeInOneApplication.getStringRes(stringRes));
        }
        String hTime = TimeUtils.getString(mECGResult.getMeasureStartTime(), "HH");
        String mTime = TimeUtils.getString(mECGResult.getMeasureStartTime(), "mm");
        String sTime = TimeUtils.getString(mECGResult.getMeasureStartTime(), "ss");
        long posTime = (Integer.parseInt(hTime) * 60 * 60 + Integer.parseInt(mTime) * 60 + Integer.parseInt(sTime));
        mECGHistoryWaveView.setPosTime(TimeUtils.getSampingTimeString(posTime, pos, 250));
        int hr;
        if (position >= mECGResultFile.getLocal().getResults().length) {
            hr = 0;
        } else {
            hr = mECGResultFile.getLocal().getResults()[position]._hr;
        }
        if (hr != 0 || pos < MAX_SCREEN_LENGTH) {
            setHr(hr);
        }
        if (position == 0 || position == 1) {
            setHr(0);
        }
    }

    @Override
    public void diseaseChange(int pos) {
        seekBarChange(pos);
        mProgressBar.setProgress(pos);
    }

    @Override
    public void onCount(int current, int max, short rhytm) {
        mCurrentCount = current;
        mMaxCount = max;
        mRhytm = rhytm;
        int status = mMaxCount > 1 ? View.VISIBLE : View.GONE;
        check(View.GONE, status);
    }


    @Override
    public void onProgress(ECGResult result, int progress) {

    }

    @Override
    public void onComplete(ECGResult result, boolean success) {
        getDoctorAnalyse();
    }
}
