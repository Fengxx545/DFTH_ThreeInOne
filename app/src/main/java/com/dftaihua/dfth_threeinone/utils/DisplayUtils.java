package com.dftaihua.dfth_threeinone.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DisplayUtils {
    public static final int DRAWABLE = 0;
    public static final int FILE = 1;
    public static final int ASSETS = 2;
    public static final int NETWORK = 3;

    /**
     * 将px值转换为dp值，保持尺寸大小不变
     *
     * @param context
     * @param pxValue （DisplayMetrics类中属性density值
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dp值装换为px值，保持尺寸不变
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转化为sp值
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        return new String(c);
    }

    /**
     * 测量文字的宽度
     */

    public static float measureTextWidth(String text, float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        return paint.measureText(text);
    }

    /**
     * 关闭键盘
     */
    public static void disappearKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static float getTextHeight(Paint paint, float height) {
        FontMetrics fontMetrics = paint.getFontMetrics();
        return height - (height - fontMetrics.bottom + fontMetrics.top) / 2
                - fontMetrics.bottom;
    }


    public static int getPhoneWidth(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getPhoneHeight(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getOrientation(){
        return ThreeInOneApplication.getInstance().getResources().getConfiguration().orientation;
    }

    /**
     * 没有缓冲 异步
     * @param view
     * @param type
     * @param uri
     */
    private static void displayImageNoCache(ImageView view, int type, String uri){
        uri = generateUri(type,uri);
        DisplayImageOptions imageOptions = ImageUtils.createDefaultNoCacheOptions(false);
        ImageLoader.getInstance().displayImage(uri, view, imageOptions);
    }

    /**
     * 有缓冲 异步
     * @param view
     * @param type
     * @param uri
     */
    private static void displayImageHaveCache(ImageView view, int type,String uri){
        uri = generateUri(type,uri);
        DisplayImageOptions imageOptions = ImageUtils.createDefaultOptions(false);
        ImageLoader.getInstance().displayImage(uri, view, imageOptions);
    }

    /**
     * 没有缓冲 同步
     * @param view
     * @param type
     * @param uri
     */
    private static void displayImageNoCacheSync(ImageView view, int type,String uri){
        uri = generateUri(type,uri);
        DisplayImageOptions imageOptions = ImageUtils.createDefaultNoCacheSyncOptions(false);
        ImageLoader.getInstance().displayImage(uri, view, imageOptions);
    }
    /**
     * 有缓冲 同步
     * @param view
     * @param type
     * @param uri
     */
    private static void displayImageHaveCacheSync(ImageView view, int type,String uri){
        uri = generateUri(type,uri);
        DisplayImageOptions imageOptions = ImageUtils.createDefaultOptionsSync(false);
        ImageLoader.getInstance().displayImage(uri, view, imageOptions);
    }
    public static void displayHaveCacheResImage(ImageView view,int res){
        displayImageHaveCache(view, DRAWABLE, String.valueOf(res));
    }

    public static void displayHaveCacheResImageSync(ImageView view,int res){
        displayImageHaveCacheSync(view, DRAWABLE, String.valueOf(res));
    }
    public static void displayNoCacheResImage(ImageView view,int res){
        displayImageNoCache(view, DRAWABLE, String.valueOf(res));
    }

    public static void displayNoCacheResImageSync(ImageView view,int res){
        displayImageNoCacheSync(view, DRAWABLE, String.valueOf(res));
    }
    public static void displayHaveCacheFileImage(ImageView view,String uri){
        displayImageHaveCache(view, FILE, uri);
    }
    public static void displayHaveCacheFileImageSync(ImageView view,String uri){
        displayImageHaveCacheSync(view, FILE, uri);
    }

    /**
     * 生成uri
     * @param type
     * @param uri
     * @return
     */
    public static String generateUri(int type, String uri){
        String imageUri = "";
        switch(type){
            case DRAWABLE:{
                imageUri = "drawable://" + uri;
            }
            break;
            case ASSETS:{
                imageUri = "assets://" + uri;
            }
            break;
            case FILE:{
                imageUri = "file://" + uri;
            }
            break;
            case NETWORK:{
                imageUri = uri;
            }
            break;
        }
        return imageUri;
    }
}
