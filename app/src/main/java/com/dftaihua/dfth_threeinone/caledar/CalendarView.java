package com.dftaihua.dfth_threeinone.caledar;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by codbking on 2016/12/18.
 * email:codbking@gmail.com
 * github:https://github.com/codbking
 * blog:http://www.jianshu.com/users/49d47538a2dd/latest_articles
 */

public class CalendarView extends ViewGroup {
    private static final String TAG = "CalendarView";
    private int selectPosition = -1;
    private CalendarAdapter adapter;
    private List<CalendarBean> data;
    private OnItemClickListener onItemClickListener;

    private int row = 6;
    private int column = 7;
    private int itemWidth;
    private int itemHeight;

    private boolean isToday;
    private String mSelectDay;
    private View mSelectView;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, CalendarBean bean);
    }

    public CalendarView(Context context, int row, String selectDay) {
        super(context);
        this.row = row;
        mSelectDay = selectDay;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public void setAdapter(CalendarAdapter adapter) {
        this.adapter = adapter;
    }

    public void setData(List<CalendarBean> data, boolean isToday) {
        this.data = data;
        this.isToday = isToday;
        setItem();
        requestLayout();
    }

    private void setItem() {
        selectPosition = -1;
        if (adapter == null) {
            throw new RuntimeException("adapter is null,please setAdapter");
        }
        for (int i = 0; i < data.size(); i++) {
            CalendarBean bean = data.get(i);
            View view = getChildAt(i);
            View childView = adapter.getView(view, this, bean);
            if (view == null || view != childView) {
                addViewInLayout(childView, i, childView.getLayoutParams(), true);
            }
            if (isToday && selectPosition == -1) {
                Date selectDay = DateUtils.stringToDate(mSelectDay);
                int[] date = CalendarUtil.getYMD(selectDay);
//                int[]date=CalendarUtil.getYMD(new Date());
                if (bean.year == date[0] && bean.moth == date[1] && bean.day == date[2]) {
                    selectPosition = i;
                    TextView textView = (TextView) childView.findViewById(R.id.text);
                    textView.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                    mSelectView = childView;
                }
            } else {
                if (selectPosition == -1 && bean.day == 1) {
                    selectPosition = i;
                    TextView textView = (TextView) childView.findViewById(R.id.text);
                    textView.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                    mSelectView = childView;
                }
            }
            childView.setSelected(selectPosition == i);
            setItemClick(childView, i, bean);
        }
    }

    public Object[] getSelect() {
        return new Object[]{getChildAt(selectPosition), selectPosition, data.get(selectPosition)};
    }

    public void setItemClick(final View view, final int position, final CalendarBean bean) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectView != null) {
                    TextView textView = (TextView) mSelectView.findViewById(R.id.text);
                    textView.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_black));
                }
                mSelectView = view;
                TextView textView = (TextView) view.findViewById(R.id.text);
                textView.setTextColor(ThreeInOneApplication.getColorRes(R.color.google_white));
                if (selectPosition != -1) {
                    getChildAt(selectPosition).setSelected(false);
                    getChildAt(position).setSelected(true);
                }
                selectPosition = position;
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, position, bean);
                }
            }
        });
    }

    public int[] getSelectPosition() {
        Rect rect = new Rect();
        try {
            getChildAt(selectPosition).getHitRect(rect);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{rect.left, rect.top, rect.right, rect.top};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY));
        itemWidth = parentWidth / column;
        itemHeight = itemWidth;
        View view = getChildAt(0);
        if (view == null) {
            return;
        }
        LayoutParams params = view.getLayoutParams();
        if (params != null && params.height > 0) {
            itemHeight = params.height;
        }
        setMeasuredDimension(parentWidth, itemHeight * row);
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
        }
        Log.i(TAG, "onMeasure() called with: itemHeight = [" + itemHeight + "], itemWidth = [" + itemWidth + "]");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            layoutChild(getChildAt(i), i, l, t, r, b);
        }
    }

    private void layoutChild(View view, int postion, int l, int t, int r, int b) {
        int cc = postion % column;
        int cr = postion / column;
        int itemWidth = view.getMeasuredWidth();
        int itemHeight = view.getMeasuredHeight();
        l = cc * itemWidth;
        t = cr * itemHeight;
        r = l + itemWidth;
        b = t + itemHeight;
        view.layout(l, t, r, b);
    }
}
