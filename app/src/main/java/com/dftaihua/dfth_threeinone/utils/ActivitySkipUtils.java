package com.dftaihua.dfth_threeinone.utils;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/17 9:53
*/
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dfth.sdk.model.bp.BpResult;
import com.dfth.sdk.model.ecg.ECGResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ActivitySkipUtils {

    public ActivitySkipUtils() {
        throw new UnsupportedOperationException("ActivitySkipUtil不能实例化");
    }



    public static void skipAnOtherActivityForResult(Context context,
                                                    Class<? extends Activity> cls,int requstCode){
        Intent intent = new Intent(context,cls);
        ((Activity)context).startActivityForResult(intent,requstCode);

    }

    /**
     * 功能描述:简单地Activity的跳转(不携带任何数据)
     *
     * @Time 2017/1/17
     * @param context
     *            发起跳转的Activity实例
     *            目标Activity实例
     */
    public static void skipAnotherActivity(Context context,
                                           Class<? extends Activity> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    /**
     * 功能描述：带数据的Activity之间的跳转
     *
     * @Time 2017/1/17
     * @param context
     * @param cls
     * @param hashMap
     */
    public static void skipAnotherActivity(Context context,
                                           Class<? extends Activity> cls,
                                           HashMap<String, ? extends Object> hashMap) {
        Intent intent = new Intent(context, cls);
        Iterator<?> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, Object> entry = (Entry<String, Object>) iterator
                    .next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                intent.putExtra(key, (String) value);
            }
            if (value instanceof Boolean) {
                intent.putExtra(key, (boolean) value);
            }
            if (value instanceof Integer) {
                intent.putExtra(key, (int) value);
            }
            if (value instanceof Float) {
                intent.putExtra(key, (float) value);
            }
            if (value instanceof Double) {
                intent.putExtra(key, (double) value);
            }
            if (value instanceof ECGResult){
                intent.putExtra(key, (Serializable) value);
            }
            if (value instanceof List){
                intent.putExtra(key, (Serializable) value);
            }
        }
        context.startActivity(intent);
    }


    /**
     * 功能描述：带数据的Activity之间的跳转
     *
     * @Time 2017/1/17
     * @param context
     * @param cls
     * @param hashMap
     */
    public static void skipAnotherActivityForResult(Context context,
                                           Class<? extends Activity> cls,
                                           HashMap<String, ? extends Object> hashMap,int resultCode) {
        Intent intent = new Intent(context, cls);
        Iterator<?> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, Object> entry = (Entry<String, Object>) iterator
                    .next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                intent.putExtra(key, (String) value);
            }
            if (value instanceof Boolean) {
                intent.putExtra(key, (boolean) value);
            }
            if (value instanceof Integer) {
                intent.putExtra(key, (int) value);
            }
            if (value instanceof Float) {
                intent.putExtra(key, (float) value);
            }
            if (value instanceof Double) {
                intent.putExtra(key, (double) value);
            }
            if (value instanceof ECGResult){
                intent.putExtra(key, (Serializable) value);
            }
        }
        ((Activity)context).startActivityForResult(intent,resultCode);
    }
}