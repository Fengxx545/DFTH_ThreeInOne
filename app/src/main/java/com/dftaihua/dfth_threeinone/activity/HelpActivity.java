package com.dftaihua.dfth_threeinone.activity;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/5/27 13:12
*/

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;

import static com.dftaihua.dfth_threeinone.controler.ActivityCollector.getActivity;

public class HelpActivity extends BaseActivity {
    private ViewPager mViewPager;
    private final int[] mRes = new int[]{
            R.drawable.help_1,R.drawable.help_2,R.drawable.help_3
    };

    public HelpActivity() {
        mTitleNameRes = R.string.setting_help;
        mTitleColorRes = R.color.google_white;
        mTitleNameColorRes  = R.color.google_black;
        mStatus = TITLE_VISIBLE | BACK_VISIBLE;
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_help,null);
        mViewPager = (ViewPager)view.findViewById(R.id.help_list);
        mViewPager.setAdapter(mAdapter);
        return view;
    }
    private PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mRes.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(getActivity() == null){
                return null;
            }
            RelativeLayout layout = new RelativeLayout(getActivity());
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            DisplayUtils.displayHaveCacheResImage(imageView, mRes[position]);
            imageView.setId(10 + position);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0,0);
            params.height = ThreeInOneApplication.getScreenHeight() - DisplayUtils.dip2px(getActivity(),50);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            params.width = (int) (params.height * 1080f / 1920);
            layout.addView(imageView,params);
            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = container.findViewById(10 + position);
            if(v != null && v.getParent() != null){
                container.removeView(v);
            }
        }
    };
}
