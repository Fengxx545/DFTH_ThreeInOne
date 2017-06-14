package com.dftaihua.dfth_threeinone.manager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.EcgDetailActivity;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.controler.ActivityCollector;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.AndroidUtils;
import com.dfth.sdk.model.ecg.ECGResult;

import java.util.HashMap;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_ACTION = "three_in_one_notification_action";
    public static final String PUSH_NOTIFICATION_RECEIVER = "three_in_one_push_notification_receiver";
    public static final String IMAGE_ID = "image_id";
    public static final String TEXT = "text";// 标题
    public static final String TEXT_DETAILS = "text_details"; // 描述
    public static final String TEXT_CONTENT = "text_content";// 内容
    public static final String PUSH_TEXT_CONTENT = "push_text_content";// 内容
    public static int notificationId = 10000;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NOTIFICATION_ACTION)) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int imageId = intent.getIntExtra(IMAGE_ID, 0);
            String title = intent.getStringExtra(TEXT);
            String details = intent.getStringExtra(TEXT_DETAILS);
            ECGResult content = (ECGResult) intent.getSerializableExtra(TEXT_CONTENT);
            Notification notification = new Notification(imageId, title, System.currentTimeMillis());
            notification.defaults |= Notification.DEFAULT_ALL;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.noyify_layout);
            contentView.setImageViewResource(R.id.notify_image, imageId);
            notification.contentView = contentView;
            Intent intentActivity = new Intent(PUSH_NOTIFICATION_RECEIVER);
            intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentActivity.putExtra(PUSH_TEXT_CONTENT, content);
            notification.contentIntent = PendingIntent.getBroadcast(context, notificationId, intentActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            contentView.setTextViewText(R.id.notify_text, title);
            String detail = details.length() < 10 ? details : details + "...";
            contentView.setTextViewText(R.id.notify_text_details, detail);
            manager.notify(0, notification);
        } else if (intent.getAction().equals(PUSH_NOTIFICATION_RECEIVER)) {
            ECGResult result = (ECGResult) intent.getExtras().getSerializable(PUSH_TEXT_CONTENT);
            if (AndroidUtils.judgeIsActive(context)) {
                Activity activity = ActivityCollector.getActivity();
                if(activity != null){
                    HashMap<String, Object> deviceMap = new HashMap<>();
                    deviceMap.put(Constant.DFTH_RESULT_DATA, result);
                    ActivitySkipUtils.skipAnotherActivity(activity, EcgDetailActivity.class, deviceMap);
                }
            } else {
                Intent intent1 = new Intent();
                ComponentName comp = new ComponentName("com.dftaihua.dfth_threeinone", "com.dftaihua.dfth_threeinone.activity.EcgDetailActivity");
                intent.setComponent(comp);
                intent.setAction("android.intent.action.VIEW");
                context.getApplicationContext().startActivity(intent1);
            }
        }
    }

    private Intent[] makeIntentStack(Context context) {
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, com.dftaihua.dfth_threeinone.activity.EcgHistoryActivity.class));
        intents[1] = new Intent(context,  com.dftaihua.dfth_threeinone.activity.EcgDetailActivity.class);
        return intents;
    }

    private void doMessageNotifyClick(Context context, ECGResult result){
        if (AndroidUtils.judgeIsActive(context)) {
            Activity activity = ActivityCollector.getActivity();
            if(activity != null){
                HashMap<String, Object> deviceMap = new HashMap<>();
                deviceMap.put(Constant.DFTH_RESULT_DATA, result);
                ActivitySkipUtils.skipAnotherActivity(activity, EcgDetailActivity.class, deviceMap);
            }
        } else {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.dftaihua.dfth_threeinone", "com.dftaihua.dfth_threeinone.activity.EcgDetailActivity");
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
            context.getApplicationContext().startActivity(intent);
        }
    }

    public static Intent getNotificationIntent(String action, int imageId, String title,
                                               String description, ECGResult data) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(IMAGE_ID, imageId);
        intent.putExtra(TEXT, title);
        intent.putExtra(TEXT_DETAILS, description);
        intent.putExtra(TEXT_CONTENT, data);
        return intent;
    }
}
