package com.dftaihua.dfth_threeinone.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.caledar.CalendarAdapter;
import com.dftaihua.dfth_threeinone.caledar.CalendarBean;
import com.dftaihua.dfth_threeinone.caledar.CalendarDateView;
import com.dftaihua.dfth_threeinone.caledar.CalendarView;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.DateUtils;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.model.ecg.ECGResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/3/8 9:41
*/
public class CalendarDialog extends Dialog implements View.OnClickListener {
    private View mHomeView;
    private CalendarDateView mCalendarDateView;
    private TextView mTitle;
    private CalendarAdapter mAdapter;
    private int mDeviceType;
    private TextView mConfirm;
    private TextView mCancel;
    private CalendarClickListener mCalendarClickListener;
    private String mCurrentDate;
    private String mSelectDate;
    private String mHasBpDate = "";
    private String mHasSingleDate = "";
    private String mHasEcgDate = "";
    private List<String> mBpDates = new ArrayList<>();
    private List<String> mEcgDates = new ArrayList<>();
    private List<String> mSingleDates = new ArrayList<>();
    private int mBpSelectDayIndex;
    private int mSingleSelectDayIndex;
    private int mEcgSelectDayIndex;

    public interface CalendarClickListener {
        public void clickConfirm();

        public void clickCancel();
    }

    public CalendarDialog(Context context, int deviceType) {
        super(context,R.style.custom_dialog_style);
        setContentView(R.layout.dialog_calendar);
        mDeviceType = deviceType;
        init(context);
    }

    private void init(final Context context) {
        mCalendarDateView = (CalendarDateView) findViewById(R.id.calendarDateView);
        mTitle = (TextView) findViewById(R.id.title);
        mConfirm = (TextView) findViewById(R.id.confirm);
        mCancel = (TextView) findViewById(R.id.cancel);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mAdapter = new CalendarAdapter() {
            @Override
            public View getView(View convertView, ViewGroup parentView, CalendarBean bean) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_calendar, null);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DisplayUtils.dip2px(context, 48), DisplayUtils.dip2px(context, 48));
                    convertView.setLayoutParams(params);
                }
                TextView view = (TextView) convertView.findViewById(R.id.text);
                view.setText("" + bean.day);
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                if (mDeviceType == DfthDevice.BpDevice) {
//                    if (checkOneDayHasBpData(bean.year + "-" + bean.moth + "-" + bean.day)) {
//                        view1.setVisibility(View.VISIBLE);
//                        view1.setBackgroundResource(R.drawable.circle);
//                    } else {
//                        view1.setVisibility(View.GONE);
////                    view1.setBackgroundResource(R.drawable.circle);
//                    }
//                } else {
//                    if (checkOneDayHasData(bean.year + "-" + bean.moth + "-" + bean.day)) {
//                        view1.setVisibility(View.VISIBLE);
//                        view1.setBackgroundResource(R.drawable.circle);
//                    } else {
//                        view1.setVisibility(View.GONE);
////                    view1.setBackgroundResource(R.drawable.circle);
//                    }
//                }
                if (bean.mothFlag != 0) {
                    view.setTextColor(ThreeInOneApplication.getColorRes(R.color.remarks_font_color));
                } else {
                    view.setTextColor(ThreeInOneApplication.getColorRes(R.color.standard_font_color));
                }
                return convertView;
            }
        };
    }

    public void initData(){
        if (mDeviceType == DfthDevice.BpDevice) {
            checkHasBpDates();
        } else if (mDeviceType == DfthDevice.SingleDevice) {
            checkHasSingleDates();
        } else {
            checkHasEcgDates();
        }
        if(TextUtils.isEmpty(mSelectDate)){
            mSelectDate = DateUtils.dateToString(new Date());
            mTitle.setText(mSelectDate);
        }
        mCalendarDateView.setAdapter(mAdapter,mSelectDate);
        mCalendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CalendarBean bean) {
                mTitle.setText(bean.year + "-" + getDisPlayNumber(bean.moth) + "-" + getDisPlayNumber(bean.day));
                mSelectDate = bean.year + "-" + getDisPlayNumber(bean.moth) + "-" + getDisPlayNumber(bean.day);
            }
        });
//        int[] data = CalendarUtil.getYMD(new Date());
//        if (data[1] < 10) {
//            mCurrentDate = data[0] + "-0" + data[1] + "-" + getDisPlayNumber(data[2]);
//        } else {
//            mCurrentDate = data[0] + "-" + data[1] + "-" + getDisPlayNumber(data[2]);
//        }
//        mTitle.setText(data[0] + "年" + data[1] + "月" + data[2] + "日");
    }

    private void checkHasBpDates() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        List<BpResult> results = DfthSDKManager.getManager().getDatabase().getBPResult(userId);
        if (results == null || results.size() == 0) {
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            BpResult result = results.get(i);
            long startTime = result.getMeasureTime();
            String time = TimeUtils.getTimeStr(startTime, "yyyy-MM-dd");
            if (!time.equals(mHasBpDate)) {
                mHasBpDate = time;
                mBpDates.add(time);
                Logger.e("has bp data date : " + time);
            }
        }
        sortDates(mBpDates);
        mBpSelectDayIndex = mBpDates.size() - 1;
        mCurrentDate = mBpDates.get(mBpSelectDayIndex);
        mSelectDate = mCurrentDate;
        mTitle.setText(mCurrentDate);
    }

    private void checkHasSingleDates() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        List<ECGResult> results = DfthSDKManager.getManager().getDatabase().getSingleResult(userId);
        if (results == null || results.size() == 0) {
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            ECGResult result = results.get(i);
            long startTime = result.getMeasureStartTime();
            String time = TimeUtils.getTimeStr(startTime, "yyyy-MM-dd");
            if (!time.equals(mHasSingleDate)) {
                mHasSingleDate = time;
                mSingleDates.add(time);
                Logger.e("has single data date : " + time);
            }
        }
        sortDates(mSingleDates);
        mSingleSelectDayIndex = mSingleDates.size() - 1;
        mCurrentDate = mSingleDates.get(mSingleSelectDayIndex);
        mSelectDate = mCurrentDate;
        mTitle.setText(mCurrentDate);
    }

    private void checkHasEcgDates() {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        List<ECGResult> results = DfthSDKManager.getManager().getDatabase().getTwelveECGResult(userId);
        if (results == null || results.size() == 0) {
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            ECGResult result = results.get(i);
            long startTime = result.getMeasureStartTime();
            String time = TimeUtils.getTimeStr(startTime, "yyyy-MM-dd");
            if (!time.equals(mHasEcgDate)) {
                mHasEcgDate = time;
                mEcgDates.add(time);
//                Logger.e("has ecg data date : " + time);
            }
        }
        sortDates(mEcgDates);
        mEcgSelectDayIndex = mEcgDates.size() - 1;
        mCurrentDate = mEcgDates.get(mEcgSelectDayIndex);
        mSelectDate = mCurrentDate;
        mTitle.setText(mCurrentDate);
    }

    private void sortDates(List<String> dates){
        Collections.sort(dates, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if(TimeUtils.getTimeByStr(lhs,"yyyy-MM-dd") > TimeUtils.getTimeByStr(rhs,"yyyy-MM-dd")){
                    return 1;
                } else{
                    return -1;
                }
            }
        });
    }

    private String getDisPlayNumber(int num) {
        return num < 10 ? "0" + num : "" + num;
    }

    public boolean checkOneDayHasData(String data) {
        long start = TimeUtils.getOneDayStartTime(TimeUtils.stringToCalendar(data));
        long end = TimeUtils.getOneDayEndTime(TimeUtils.stringToCalendar(data));
        List list;
        if (mDeviceType == DfthDevice.SingleDevice) {
            list = DfthSDKManager.getManager().getDatabase().getSingleResult(UserManager.getInstance().getDefaultUser().getUserId(), start, end);
        } else {
            list = DfthSDKManager.getManager().getDatabase().getTwelveECGResult(UserManager.getInstance().getDefaultUser().getUserId(), start, end);
        }
        return list.size() != 0;
    }

    public boolean checkOneDayHasBpData(String data) {
        List list = DfthSDKManager.getManager().getDatabase().getBPResultByDay(data, UserManager.getInstance().getDefaultUser().getUserId());
        return list.size() != 0;
    }

    public void setCalendarClickListener(CalendarClickListener calendarClickListener) {
        mCalendarClickListener = calendarClickListener;
    }

    public String getCurrentDate() {
        return mCurrentDate;
    }

    public String getSelectDate() {
        return mSelectDate;
    }

    public void setSelectDate(String selectDate) {
        mSelectDate = selectDate;
        mCalendarDateView.setSelectDay(selectDate);
    }

    public String getProxDay() {
        List<String> dates = null;
        if (mDeviceType == DfthDevice.SingleDevice) {
            dates = mSingleDates;
        } else if (mDeviceType == DfthDevice.EcgDevice){
            dates = mEcgDates;
        }
        if (dates != null && dates.size() > 0) {
            for (int i = dates.size() - 1; i >= 0; i--) {
                String date = dates.get(i);
                long selectTime = TimeUtils.getTimeByStr(mSelectDate, "yyyy-MM-dd");
                long time = TimeUtils.getTimeByStr(date, "yyyy-MM-dd");
                if (selectTime - time < 0) {
                    continue;
                } else if (selectTime - time == 0) {
                    return mSelectDate;
                } else {
                    return dates.get(i);
                }
            }
        }
        return "";
    }

    public String getBeforeDay() {
//        Calendar calendar = Calendar.getInstance();
//        Date date = DateUtils.stringToDate(mCurrentDate);
//        calendar.setTime(date);
//        Date afterDay = TimeUtils.getAfterDay(calendar).getTime();
//        mCurrentDate = DateUtils.dateToString(afterDay);
//        return mCurrentDate;
        if (mBpSelectDayIndex >= 0 && (mBpSelectDayIndex + 1) < mBpDates.size()) {
            String day = mBpDates.get(mBpSelectDayIndex + 1);
            mBpSelectDayIndex = mBpSelectDayIndex + 1;
            return day;
        }
        return "";
    }

    public String getAfterDay() {
//        Calendar calendar = Calendar.getInstance();
//        Date date = DateUtils.stringToDate(mCurrentDate);
//        calendar.setTime(date);
//        Date beforeDay = TimeUtils.getBeforeDay(calendar).getTime();
//        mCurrentDate = DateUtils.dateToString(beforeDay);
//        return mCurrentDate;
        if (mBpSelectDayIndex >= 1) {
            String day = mBpDates.get(mBpSelectDayIndex - 1);
            mBpSelectDayIndex = mBpSelectDayIndex - 1;
            return day;
        }
        return "";
    }

    public String getLastDay() {
        if (mBpDates.size() - 1 >= 0) {
            String day = mBpDates.get(mBpDates.size() - 1);
            mBpSelectDayIndex = mBpDates.size() - 1;
            return day;
        }
        return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm: {
                mCalendarClickListener.clickConfirm();
            }
            break;
            case R.id.cancel: {
                mCalendarClickListener.clickCancel();
            }
            break;
        }
    }
}
