package com.dftaihua.dfth_threeinone.utils;

import android.graphics.Bitmap;

import com.dftaihua.dfth_threeinone.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class ImageUtils {
    public static DisplayImageOptions createDefaultOptions(boolean isBack) {
        return createDefaultOptions(isBack, R.drawable.libary_loading);
    }

    public static DisplayImageOptions createDefaultOptions(boolean isBack, int errorDrawable) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(errorDrawable)
                .showImageOnFail(errorDrawable)
                .showImageOnLoading(errorDrawable)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(0).setType(isBack))
                .build();
        return options;
    }


    public static DisplayImageOptions createDefaultOptionsSync() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.libary_loading)
                .showImageOnFail(R.drawable.libary_loading)
                .showImageOnLoading(R.drawable.libary_loading)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .syncLoading(true)
                .displayer(new FadeInBitmapDisplayer(0).setType(false))
                .build();
        return options;
    }

    public static DisplayImageOptions createDefaultOptionsSync(boolean b) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.libary_loading)
                .showImageOnFail(R.drawable.libary_loading)
                .showImageOnLoading(R.drawable.libary_loading)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .syncLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(0).setType(b))
                .build();
        return options;
    }

    public static DisplayImageOptions createDefaultNoCacheOptions(boolean isBack) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.libary_loading)
                .showImageOnFail(R.drawable.libary_loading)
                .showImageOnLoading(R.drawable.libary_loading)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(0).setType(isBack))
                .build();
        return options;
    }

    public static DisplayImageOptions createDefaultNoCacheSyncOptions(boolean isBack) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.libary_loading)
                .showImageOnFail(R.drawable.libary_loading)
                .showImageOnLoading(R.drawable.libary_loading)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .syncLoading(true)
                .displayer(new FadeInBitmapDisplayer(0).setType(isBack))
                .build();
        return options;
    }
}
