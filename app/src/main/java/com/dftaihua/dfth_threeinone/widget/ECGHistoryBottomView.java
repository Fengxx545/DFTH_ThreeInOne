package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.BaseActivity;
import com.dftaihua.dfth_threeinone.fragment.AccountFragment;
import com.dftaihua.dfth_threeinone.fragment.FamilyDoctorFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/18.
 */
public class ECGHistoryBottomView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "ECGHistoryBottomView";
    private ECGDataView mEcgDataView;
    private DoctorReview mDoctorReview;
    private Context mContext;
    private AccountFragment mAccountFragment;
    private FamilyDoctorFragment mFamilyDoctorFragment;
    private PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                container.addView(mEcgDataView);
                return mEcgDataView;
            } else {
                container.addView(mDoctorReview);
                return mDoctorReview;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position == 0) {
                container.removeView(mEcgDataView);
            } else {
                container.removeView(mDoctorReview);
            }
        }
    };
    private DiseaseCountListener mDiseaseCountlistener;
//    private ECGProgramFragment.DiseaseChange mDiseaseChange;
    private int mType;
    private ViewPager mPager;
    private TabView mECGData;
    private TabView mECGDoctor;
    private ArrayList<Fragment> listFragments;
    private MyFragmentPagerAdapter mMyFragmentPagerAdapter;

    public ECGHistoryBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mContext = context;
        mAccountFragment = new AccountFragment();
        mFamilyDoctorFragment = new FamilyDoctorFragment();
        listFragments = new ArrayList<>();
        listFragments.add(mAccountFragment);
        listFragments.add(mFamilyDoctorFragment);
        LayoutInflater.from(context).inflate(R.layout.base_ecg_history, this, true);
        mPager = (ViewPager) findViewById(R.id.base_ecg_history_viewpager);
        mECGData = (TabView) findViewById(R.id.ecg_data_tab);
        mECGDoctor = (TabView) findViewById(R.id.ecg_doctor_tab);
        mECGData.setOnClickListener(this);
        mECGDoctor.setOnClickListener(this);
        mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(((BaseActivity)context).getSupportFragmentManager(),listFragments);
        mPager.setAdapter(mMyFragmentPagerAdapter);
        mEcgDataView = new ECGDataView(context);
        mDoctorReview = new DoctorReview(context);
        //默认
//        mECGData.setViewFocus(true);
//        mECGDoctor.setViewFocus(false);
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> listFragments;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> al) {
            super(fm);
            listFragments = al;
        }

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }
//    public void setContent(ECGStorageData data, ECGResultFile file) {
//        mDoctorReview.setContent(data, file);
//        mEcgDataView.setContent(data, file);
//        mReportView.setContent(data, file);
//        if (data.getmServiceType() == 1 && data.getmDoctorResult() == null || StringUtils.isEmpty(data.getmDoctorName())) {
//            mECGReport.setViewFocus(true);
//            mECGData.setViewFocus(false);
//            mECGDoctor.setViewFocus(false);
//            mPager.setCurrentItem(0, true);
//        } else {
//            mECGReport.setViewFocus(false);
//            mECGData.setViewFocus(false);
//            mECGDoctor.setViewFocus(true);
//            mPager.setCurrentItem(2, true);
//        }
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ecg_data_tab: {
//                mECGData.setViewFocus(true);
//                mECGDoctor.setViewFocus(false);
//                mPager.setCurrentItem(0, true);
            }
            break;
            case R.id.ecg_doctor_tab: {
//                mECGData.setViewFocus(false);
//                mECGDoctor.setViewFocus(true);
//                mPager.setCurrentItem(1, true);
                mDoctorReview.hide();
            }
            break;
        }
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

//    public void refreshDoctor(ECGStorageData data, ECGResultFile file) {
//        mDoctorReview.setContent(data, file);
//        mReportView.setContent(data, file);
//        if (mPager.getCurrentItem() == 2) {
//            data.setmUnRead(0);
//        } else {
//            mDoctorReview.show();
//        }
//    }

    public void setmDiseaseCountlistener(DiseaseCountListener mDiseaseCountlistener) {
        this.mDiseaseCountlistener = mDiseaseCountlistener;
    }

//    public void setmDiseaseChange(ECGProgramFragment.DiseaseChange mDiseaseChange) {
//        this.mDiseaseChange = mDiseaseChange;
//    }

    public static interface DiseaseCountListener {
        public void onCount(int current, int max, short rhytm);
    }

    private abstract class BaseECGHistoryView extends RelativeLayout {
        protected TextView mTimeView;
        protected TextView mContentView;
        protected View mTitleLayout;
        protected LinearLayout mOtherLayout;
//        protected ECGResultFile mFile;
//        protected ECGStorageData mData;

        private BaseECGHistoryView(Context context) {
            super(context);
            setBackgroundResource(R.color.google_white);
            LayoutInflater.from(context).inflate(R.layout.base_history_layout, this, true);
            mTimeView = (TextView) findViewById(R.id.base_history_time);
            mContentView = (TextView) findViewById(R.id.base_history_content);
            mTitleLayout = findViewById(R.id.base_history_title_layout);
            mOtherLayout = (LinearLayout) findViewById(R.id.base_history_other_layout);

        }

//        protected abstract void setContent(ECGStorageData data, ECGResultFile file);
    }


    private class ECGDataView extends BaseECGHistoryView implements OnClickListener {
        private TextView mTime;
        private TextView mbeatCount;
        private TextView mAver;
        private ECGResultOther mOther;

        private ECGDataView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.base_history_data, mOtherLayout);
//            mTime = (TextView) findViewById(R.id.ecg_data_time);
//            mAver = (TextView) findViewById(R.id.ecg_data_aver);
//            mbeatCount = (TextView) findViewById(R.id.ecg_data_beat_count);
//            mOther = (ECGResultOther) findViewById(R.id.ecg_data_other);
        }

//        @Override
//        protected void setContent(ECGStorageData data, ECGResultFile file) {
//            mData = data;
//            mFile = file;
//            mOther.setOnClickListener(this, file);
//            mTimeView.setText(TimeUtils.getString(data.getMeasuretime(),
//                    "yyyy-MM-dd HH:mm"));
//            mContentView.setText(R.string.soft_calc_result);
//            mTime.setText(TKECGApplication.getStringRes(R.string.a_ecg_time, ECGLeaderUtils.getECGRecordTime((int)((float)(mFile.Length()*1000/mFile.getSampling())))));
//            Log.e(TAG,"文件长度是: " + mFile.Length());
//            Log.e(TAG,"文件获取的Sampling是: " + mFile.getSampling());
//            Log.e(TAG, " mTime = " + mTime.getText());
//            Logger.e("测量时长: " + mTime.getText());
//            mAver.setText(TKECGApplication.getStringRes(R.string.ecg_aver_count, file.getLocal().getAver_hr()));
//            mbeatCount.setText(TKECGApplication.getStringRes(R.string.ecg_beat_count, file.getLocal().getBeatCount()));
//            mOther.setData(mFile);
//        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
//                case R.id.result_other_pvc_count_l: {
//                    mReportView.resetDiseaseStyle();
//                    mReportView.waveJump(ECGDiagnoticList.getInstance(getContext()).getDiseaseName(FactorConstants.PREMATURE_VENTRICULAR_CONTRACTION));
//                }
//                break;
//                case R.id.result_other_sp_count_l: {
//                    mReportView.resetDiseaseStyle();
//                    mReportView.waveJump(ECGDiagnoticList.getInstance(getContext()).getDiseaseName(FactorConstants.SUPRAVENTRICULAR_PREMATURE_BEAT));
//                }
//                break;
//                case R.id.result_other_max_hr_count_l: {
//                    mReportView.resetDiseaseStyle();
//                    mReportView.waveJump(TKECGApplication.getStringRes(R.string.ecg_max_hr));
//                }
//                break;
//                case R.id.result_other_min_hr_count_l: {
//                    mReportView.resetDiseaseStyle();
//                    mReportView.waveJump(TKECGApplication.getStringRes(R.string.ecg_min_hr));
//                }
//                break;
            }
        }
    }

    private class DoctorReview extends BaseECGHistoryView implements OnClickListener {
        private TextView mServiceResult;
//        private ECGStorageData mData;
        private TextView mDoctorName;
//        private BadgeView mBadgeView;

        private DoctorReview(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.base_history_doctor, mOtherLayout);
            mServiceResult = (TextView) findViewById(R.id.doctor_service_result);
            mServiceResult.setOnClickListener(this);
            mDoctorName = (TextView) findViewById(R.id.doctor_name);
//            mBadgeView = new BadgeView(context, mECGDoctor.mTabContent, 1);
        }

        void hide() {
//            if (mData != null) {
//                mBadgeView.hide();
//                mData.setmUnRead(0);
//            }
        }

        void show() {
//            if (mData != null) {
//                mBadgeView.show();
//            }
        }

//        @Override
//        protected void setContent(ECGStorageData data, ECGResultFile file) {
//            mData = data;
//            mContentView.setTextColor(TKECGApplication.getColor(R.color.main_font_color));
//            mServiceResult.setEnabled(false);
//            if (data.getmServiceType() >= 1) {
//                if (mType == 0 && data.getmDoctorResult() == null) {
//                    mServiceResult.setText(R.string.doctor_is_hard_to_work);
//                } else if (data.getmServiceTime() == -1) {
//                    mServiceResult.setText(R.string.network_error_click_get_service);
//                    mServiceResult.setEnabled(true);
//                } else {
//                    if (data.getmDoctorResult() == null) {
//                        mServiceResult.setText(R.string.doctor_is_hard_to_work);
//                    } else {
//                        BaiduTranslate.translate(mServiceResult,data.getmDoctorResult(),new BaiduTranslate.BaiduTranslateListener() {
//                            @Override
//                            public void translateResult(String sourceText, String dstText) {
//                                mServiceResult.setText(dstText);
//                            }
//                        });
//                        if (data.getmDoctorName() != null) {
//                            mDoctorName.setText(TKECGApplication.getStringRes(R.string.i_doctor, data.getmDoctorName()));
//                        }
//                        mContentView.setText(TimeUtils.getString(data.getmServiceTime(),
//                                "yyyy-MM-dd HH:mm"));
//                    }
//                }
//            } else {
//                mServiceResult.setText(R.string.this_data_have_no_service);
//            }
//        }

        @Override
        public void onClick(View v) {
//            SendTaskManager.getInstance(getContext()).sendTask(SendTaskManager.GETECGSERVICE, mData);
        }
    }


}
