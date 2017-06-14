package com.dftaihua.dfth_threeinone.controler;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/17 10:48
*/
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static Activity getActivity(){
        if(activities != null && activities.size() > 0){
            return activities.get(activities.size() -1);
        }
        return null;
    }


    public static void finishOthers() {
        for (int i = 0; i < activities.size(); i++){
            if (i != 0){
                activities.get(i).finish();
            }
        }
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}