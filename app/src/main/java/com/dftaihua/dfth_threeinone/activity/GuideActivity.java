package com.dftaihua.dfth_threeinone.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dfth.sdk.Others.Utils.Logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4 0004.
 */

public class GuideActivity extends BaseActivity {
    private ViewPager viewPager;
    private GuideAdapter adapter;
    private TextView mExperienceTv;
    private final int[] images = new int[]{R.drawable.guide_1, R.drawable.guide_2,
            R.drawable.guide_3, R.drawable.guide_4};

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_guide, null);
        viewPager = (ViewPager) view.findViewById(R.id.guide_view_flow);
        adapter = new GuideAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        return view;
    }

    private final class GuideAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
        private List<View> views;

        public GuideAdapter() {
            views = new ArrayList<>();
            for (int i = 0; i < images.length; i++) {
                RelativeLayout layout = new RelativeLayout(GuideActivity.this);
                ImageView imageView = new ImageView(GuideActivity.this);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layout.addView(imageView, layoutParams);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                DisplayUtils.displayNoCacheResImage(imageView, images[i]);
                if (i == (images.length - 1)) {
                    mExperienceTv = new TextView(GuideActivity.this);
                    mExperienceTv.setBackgroundResource(R.drawable.shape_guide_button);
//                    experience.setAlpha((float) 0.3);
//                    experience.setBackgroundColor(Color.TRANSPARENT);
                    mExperienceTv.setText(ThreeInOneApplication.getStringRes(R.string.guide_button_experience));
                    mExperienceTv.setTextColor(ThreeInOneApplication.getColorRes(R.color.guide_button_text));
                    mExperienceTv.setTextSize(15);
                    int left = DisplayUtils.dip2px(GuideActivity.this, 30);
                    int top = DisplayUtils.dip2px(GuideActivity.this, 10);
                    mExperienceTv.setPadding(left, top, left, top);
//                    int padding = DisplayUtils.dip2px(GuideActivity.this, 5);
//                    mExperienceTv.setPadding(padding, padding, padding, padding);
                    mExperienceTv.setGravity(Gravity.CENTER);
                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                    int heightPixels = ThreeInOneApplication.getScreenHeight();
//                    layoutParams.bottomMargin = heightPixels / 6;
                    layoutParams.bottomMargin = DisplayUtils.dip2px(GuideActivity.this, 50);
                    layout.addView(mExperienceTv, layoutParams);
                    mExperienceTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharePreferenceUtils.put(GuideActivity.this, SharePreferenceConstant.GUIDE_FIRST_KEY, 1);
                            ActivitySkipUtils.skipAnotherActivity(GuideActivity.this, LoginActivity.class);
                            GuideActivity.this.finish();
                        }
                    });
                }
                views.add(layout);
            }
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = views.get(position);
            container.removeView(view);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = views.get(position);
            container.addView(view, 0);
            return view;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
        }
    }
}
