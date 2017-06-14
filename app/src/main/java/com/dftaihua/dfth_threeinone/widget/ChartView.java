package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ChartView extends LinearLayout {
    protected ChartBottomView bottomView;
    protected View chartView;

    public enum TYPE {
        SSY, SZY, BEAT
    }

    public ChartView(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(ThreeInOneApplication.getColorRes(R.color.google_white));
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(ThreeInOneApplication.getColorRes(R.color.google_white));
    }

    public interface BottomViewChangeListener {
        public void change(View view);
    }

    public final class ChartBottomView extends LinearLayout implements OnClickListener {
        private BottomViewChangeListener bottomViewChangeListener;
        List<TextView> textViews = new ArrayList<TextView>();
        private final ViewHolder[] viewHolders;
        final int color = ThreeInOneApplication.getColorRes(R.color.gray);

        public ChartBottomView(Context context, int[] backRes, int[] contents) {
            this(context, backRes, contents, new int[]{0, 1, 2, 3});
        }

        public ChartBottomView(Context context, int[] backRes, int[] contents, int[] ids) {
            super(context);
            this.setOrientation(HORIZONTAL);
            viewHolders = new ViewHolder[backRes.length];
            for (int i = 0; i < viewHolders.length; i++) {
                ViewHolder holder = new ViewHolder();
                holder.id = ids[i];
                holder.status = false;
                holder.colorRes = backRes[i];
                viewHolders[i] = holder;
            }
            for (int i = 0; i < backRes.length; i++) {
                InnerView view = new InnerView(context, contents[i], backRes[i]);
                view.setTag(viewHolders[i]);
                view.setOnClickListener(this);
                LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1;
                addView(view, layoutParams);
            }
        }

        public void setBottomViewChangeListener(
                BottomViewChangeListener bottomViewChangeListener) {
            this.bottomViewChangeListener = bottomViewChangeListener;
        }

        @Override
        public void onClick(View v) {
            if (bottomViewChangeListener != null) {
                ViewHolder holder = (ViewHolder) v.getTag();
                ((InnerView) v).setFocus(!holder.status);
                holder.status = !holder.status;
                bottomViewChangeListener.change(v);
            }
        }

        public class ViewHolder {
            public int id;
            public boolean status;
            public int colorRes;
        }

        private class InnerView extends View {
            private Paint paint = new Paint();
            private String content;
            private int color;
            private boolean isFocus = false;
            private Rect rect = new Rect();

            public InnerView(Context context, int contentRes, int color) {
                super(context);
                this.content = ThreeInOneApplication.getStringRes(contentRes);
                this.color = ThreeInOneApplication.getColorRes(color);
                paint.setAntiAlias(true);
                // TODO Auto-generated constructor stub
            }

            public boolean isFocus() {
                return isFocus;
            }

            public void setFocus(boolean isFocus) {
                this.isFocus = isFocus;
                invalidate();
            }

            @Override
            protected void onDraw(Canvas canvas) {
                rect.left = 0;
                rect.top = 0;
                rect.right = this.getWidth();
                rect.bottom = this.getHeight();
                paint.setColor(ThreeInOneApplication.getColorRes(R.color.standard_line_color));
                paint.setStrokeWidth(2);
                paint.setStyle(Style.STROKE);
                canvas.drawRect(rect, paint);
                paint.setStyle(Style.FILL);
                float width = this.getHeight() / 5;
                if (!isFocus) {
                    rect.left = (int) (this.getWidth() / 2.0f - width - DisplayUtils.sp2px(getContext(), 15) / 2f);
                    rect.right = (int) (this.getWidth() / 2.0f - DisplayUtils.sp2px(getContext(), 15) / 2f);
                    rect.top = (int) (this.getHeight() / 2.0f - width / 2);
                    rect.bottom = (int) (this.getHeight() / 2.0f + width / 2);
                    paint.setColor(color);
                    canvas.drawRect(rect, paint);
                    paint.setTextSize(DisplayUtils.sp2px(getContext(), 15));
                    paint.setTextAlign(Align.LEFT);
                    canvas.drawText(content, this.getWidth() / 2.0f - DisplayUtils.sp2px(getContext(), 15) / 2f + 5, getTextHeight(), paint);
                } else {
                    rect.left = (int) (this.getWidth() / 2.0f - width - DisplayUtils.sp2px(getContext(), 15) / 2f);
                    rect.right = (int) (this.getWidth() / 2.0f - DisplayUtils.sp2px(getContext(), 15) / 2f);
                    rect.top = (int) (this.getHeight() / 2.0f - width / 2);
                    rect.bottom = (int) (this.getHeight() / 2.0f + width / 2);
                    paint.setColor(ThreeInOneApplication.getColorRes(R.color.disable_color));
                    canvas.drawRect(rect, paint);
                    paint.setTextSize(DisplayUtils.sp2px(getContext(), 15));
                    paint.setTextAlign(Align.LEFT);
                    canvas.drawText(content, this.getWidth() / 2.0f - DisplayUtils.sp2px(getContext(), 15) / 2f + 5, getTextHeight(), paint);
                }
            }

            public float getTextHeight() {
                FontMetrics fontMetrics = paint.getFontMetrics();
                return this.getHeight() - (this.getHeight() - fontMetrics.bottom + fontMetrics.top) / 2
                        - fontMetrics.bottom;
            }
        }

    }

    /**
     * 获取字符串的宽度
     *
     * @param paint
     * @param str   字符串
     * @return 字符串的宽度
     */
    public static int getTextWidth(Paint paint, String str) {
        int iRect = 0;
        ;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < widths.length; j++) {
                iRect += widths[j];
            }
        }

        return iRect;
    }

    /**
     * 判断rect是否相同
     *
     * @param rect1
     * @param rect2
     * @return
     */
    public static boolean JudgeRect(RectF rect1, RectF rect2) {
        if (rect1 == null || rect2 == null)
            return false;
        boolean isSame = (rect1.bottom != rect2.bottom) ? false : (rect1.top != rect2.top) ? false : rect1.left != rect2.left ? false : rect1.right != rect2.right ? false : true;
        return isSame;
    }
}
