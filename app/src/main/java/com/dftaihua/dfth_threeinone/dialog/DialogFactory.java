package com.dftaihua.dfth_threeinone.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.BpMeasurePreActivity;
import com.dftaihua.dfth_threeinone.activity.EcgHistoryActivity;
import com.dftaihua.dfth_threeinone.activity.HomeActivity;
import com.dftaihua.dfth_threeinone.activity.LoginActivity;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.UserConstant;
import com.dftaihua.dfth_threeinone.device.BindedDevice;
import com.dftaihua.dfth_threeinone.manager.DfthDeviceManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.mediator.MeasureMediator;
import com.dftaihua.dfth_threeinone.utils.ActivitySkipUtils;
import com.dftaihua.dfth_threeinone.utils.BpDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.BpPlanUtils;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.ECGDeviceUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.utils.UserUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Constant.BpConstant;
import com.dfth.sdk.device.DfthBpDevice;
import com.dfth.sdk.device.DfthDevice;
import com.dfth.sdk.device.DfthECGDevice;
import com.dfth.sdk.dispatch.DfthCallBack;
import com.dfth.sdk.dispatch.DfthResult;
import com.dfth.sdk.event.DfthMessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

/*    *****      *     *
**    *    *      *   *
**    *****         *
**    *   *         *
**    *    *        *
**    *     *       *
* 创建时间：2017/2/22 10:46
*/
public class DialogFactory {

    private static final String TAG = DialogFactory.class.getSimpleName();

    public static Dialog getFreeServiceDialog(Activity context) {
        Dialog dialog = new Dialog(context, R.style.loadingDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_free_service, null);
        TextView title = (TextView) view.findViewById(R.id.dialog_free_service_title_tv);
        TextView content = (TextView) view.findViewById(R.id.dialog_free_service_content_tv);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int height = context.getWindowManager().getDefaultDisplay().getHeight();
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = width * 2 / 3;
        dialog.setContentView(view, params);
        return dialog;
    }


    public static Dialog getLoadingDialog(Activity context, String showText, String tipText) {
        final Dialog dialog = new Dialog(context, R.style.loadingDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_login, null);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.progressBar1);
        TextView progress_text = (TextView) view.findViewById(R.id.show_text);
        TextView tip = (TextView) view.findViewById(R.id.tip);
        progress_text.setText(showText);
        if (!tipText.equals("")) {
            tip.setVisibility(View.VISIBLE);
            tip.setText(tipText);
        }
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int height = context.getWindowManager().getDefaultDisplay().getHeight();
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = height / 5;
        params.width = width * 2 / 3;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getDeviceDialog(final Activity context, final int type) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_device, null);
        TextView deviceNameTv = (TextView) view.findViewById(R.id.dialog_device_name_tv);
        TextView deviceStatusTv = (TextView) view.findViewById(R.id.dialog_device_connect_status_tv);
        TextView deviceModelTv = (TextView) view.findViewById(R.id.dialog_device_model_tv);
        TextView deviceVersionTv = (TextView) view.findViewById(R.id.dialog_device_version_tv);
        TextView deviceMacTv = (TextView) view.findViewById(R.id.dialog_device_mac_tv);
//        Button deleteBtn = (Button) view.findViewById(R.id.dialog_device_delete_btn);
        if (type == DfthDevice.BpDevice) {
            deviceNameTv.setText(R.string.slide_menu_device_blood);
            deviceStatusTv.setText(BpDeviceUtils.isBpDeviceConnected() ? R.string.dialog_device_status_connect
                    : R.string.dialog_device_status_not_connect);
            BindedDevice device = BindedDevice.getBindedDevice(DfthDevice.BpDevice);
            if(device != null){
                String name = BindedDevice.getBindedDevice(DfthDevice.BpDevice).getDeviceName();
                deviceModelTv.setText(TextUtils.isEmpty(name) ? "" : name);
                String version = BindedDevice.getBindedDevice(DfthDevice.BpDevice).getDeviceVersion();
                deviceVersionTv.setText(TextUtils.isEmpty(version) ? "" : version);
                String mac = BindedDevice.getBindedDevice(DfthDevice.BpDevice).getMacAddresss();
                deviceMacTv.setText(TextUtils.isEmpty(mac) ? "" : mac);
            }
        } else if (type == DfthDevice.SingleDevice) {
            deviceNameTv.setText(R.string.slide_menu_device_single_ecg);
            deviceStatusTv.setText(ECGDeviceUtils.isSingleDeviceConnected() ? R.string.dialog_device_status_connect
                    : R.string.dialog_device_status_not_connect);
            BindedDevice device = BindedDevice.getBindedDevice(DfthDevice.SingleDevice);
            if(device != null){
                String name = BindedDevice.getBindedDevice(DfthDevice.SingleDevice).getDeviceName();
                deviceModelTv.setText(TextUtils.isEmpty(name) ? "" : name);
                String version = BindedDevice.getBindedDevice(DfthDevice.SingleDevice).getDeviceVersion();
                deviceVersionTv.setText(TextUtils.isEmpty(version) ? "" : version);
                String mac = BindedDevice.getBindedDevice(DfthDevice.SingleDevice).getMacAddresss();
                deviceMacTv.setText(TextUtils.isEmpty(mac) ? "" : mac);
            }
        } else if (type == DfthDevice.EcgDevice) {
            deviceNameTv.setText(R.string.slide_menu_device_twelve_ecg);
            deviceStatusTv.setText(ECGDeviceUtils.isECGDeviceConnected() ? R.string.dialog_device_status_connect
                    : R.string.dialog_device_status_not_connect);
            BindedDevice device = BindedDevice.getBindedDevice(DfthDevice.EcgDevice);
            if(device != null){
                String name = BindedDevice.getBindedDevice(DfthDevice.EcgDevice).getDeviceName();
                deviceModelTv.setText(TextUtils.isEmpty(name) ? "" : name);
                String version = BindedDevice.getBindedDevice(DfthDevice.EcgDevice).getDeviceVersion();
                deviceVersionTv.setText(TextUtils.isEmpty(version) ? "" : version);
                String mac = BindedDevice.getBindedDevice(DfthDevice.EcgDevice).getMacAddresss();
                deviceMacTv.setText(TextUtils.isEmpty(mac) ? "" : mac);
            }
        }
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (type == Constant.DeviceConstant.DFTH_DEVICE_BLOOD_PRESSURE) {
//                    SharePreferenceUtils.put(context, SharePreferenceConstant.DEVICE_BLOOD, false);
//                    dialog.dismiss();
//                } else if (type == Constant.DeviceConstant.DFTH_DEVICE_SINGLE_ECG) {
//                    SharePreferenceUtils.put(context, SharePreferenceConstant.DEVICE_SINGLE_ECG, false);
//                    dialog.dismiss();
//                } else if (type == Constant.DeviceConstant.DFTH_DEVICE_TWELVE_ECG) {
//                    SharePreferenceUtils.put(context, SharePreferenceConstant.DEVICE_TWELVE_ECG, false);
//                    dialog.dismiss();
//                }
//                ((HomeActivity) context).removeDeviceCard(type);
//                ((HomeActivity) context).getSlidMenu().refreshView();
//            }
//        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        int height = context.getWindowManager().getDefaultDisplay().getHeight();
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 7 / 8;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getPlanDialog(final Activity context, final int type) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_plan, null);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_plan_name_tv);
        TextView confirmTv = (TextView) view.findViewById(R.id.dialog_plan_confirm_tv);
        final TextView cancelTv = (TextView) view.findViewById(R.id.dialog_plan_cancel_tv);
        nameTv.setText(R.string.bp_plan_not_same);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == Constant.BP_PLAN_EXIST_PHONE) {
                    dialog.dismiss();
                    BpPlanUtils.cancelPhonePlan();
                    ToastUtils.showShort(context, R.string.bp_plan_cancel_success);
                } else if (type == Constant.BP_PLAN_EXIST_DEVICE) {
                    dialog.dismiss();
                    boolean isSuccess = BpPlanUtils.cancelDevicePlan();
                    int toastRes = isSuccess ? R.string.bp_plan_cancel_success : R.string.bp_plan_cancel_fail;
                    ToastUtils.showShort(context, toastRes);
                } else {
                    dialog.dismiss();
                    boolean isSuccess = BpPlanUtils.cancelDevicePlan();
                    int toastRes = isSuccess ? R.string.bp_plan_cancel_success : R.string.bp_plan_cancel_fail;
                    ToastUtils.showShort(context, toastRes);
                    if (isSuccess) {
                        BpPlanUtils.cancelPhonePlan();
                    }
                }
                DfthBpDevice bpDevice = (DfthBpDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.BpDevice);
                BpDeviceUtils.getBpManualDatas(bpDevice);
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BpDeviceUtils.disConnectDevice();
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getBindDeviceDialog(final Activity context, final int deviceType) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bind_device, null);
        TextView confirmTv = (TextView) view.findViewById(R.id.dialog_bind_device_confirm_tv);
        final TextView cancelTv = (TextView) view.findViewById(R.id.dialog_bind_device_cancel_tv);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(deviceType == DfthDevice.BpDevice){
                    if(BpDeviceUtils.isBpDeviceConnected()){
                        BpDeviceUtils.disConnectDevice();
                    }
                } else if(deviceType == DfthDevice.SingleDevice){
                    if(ECGDeviceUtils.isSingleDeviceConnected()){
                        ECGDeviceUtils.disConnectDevice(deviceType);
                    }
                } else if(deviceType == DfthDevice.EcgDevice){
                    if(ECGDeviceUtils.isECGDeviceConnected()){
                        ECGDeviceUtils.disConnectDevice(deviceType);
                    }
                }
                ((HomeActivity) context).onItemClick(deviceType);
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getTipDialog(final Activity context, String text) {
        final Dialog dialog = new Dialog(context, R.style.loadingDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_single_tip, null);
        TextView showTip = (TextView) view.findViewById(R.id.single_show_tip_text);
        TextView confirm = (TextView) view.findViewById(R.id.dialog_single_tip_confirm_tv);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                ((EcgHistoryActivity) context).toMeasure();
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.dialog_single_tip_cancel_tv);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        SpannableStringBuilder span = new SpannableStringBuilder("缩进" + text);
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        showTip.setText(span);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int height = context.getWindowManager().getDefaultDisplay().getHeight();
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getBpExperienceDialog(final Activity context, String text) {
        final Dialog dialog = new Dialog(context, R.style.loadingDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_single_tip, null);
        TextView showTip = (TextView) view.findViewById(R.id.single_show_tip_text);
        TextView confirm = (TextView) view.findViewById(R.id.dialog_single_tip_confirm_tv);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                ((BpMeasurePreActivity)context).toMeasure(BpConstant.BP_MEASURE_EXPERIENCE);
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.dialog_single_tip_cancel_tv);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        SpannableStringBuilder span = new SpannableStringBuilder("缩进" + text);
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        showTip.setText(span);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int height = context.getWindowManager().getDefaultDisplay().getHeight();
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

//    public static Dialog getBpExperienceDialog(final Activity context, String text) {
//        final Dialog dialog = new Dialog(context, R.style.loadingDialogStyle);
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bp_experience, null);
//        TextView showTip = (TextView) view.findViewById(R.id.dialog_bp_experience_show_tip_text);
//        TextView confirm = (TextView) view.findViewById(R.id.dialog_bp_experience_dialog_tip_confirm);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//                ((BpMeasurePreActivity)context).toMeasure(BpConstant.BP_MEASURE_EXPERIENCE);
//            }
//        });
//        showTip.setText(text);
//        dialog.setCanceledOnTouchOutside(false);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        int height = context.getWindowManager().getDefaultDisplay().getHeight();
//        int width = context.getWindowManager().getDefaultDisplay().getWidth();
//        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        params.width = width * 2 / 3;
//        dialog.setContentView(view, params);
//        return dialog;
//    }

    public static Dialog getMeasuringDialog(Activity context, String text) {
        final Dialog dialog = new Dialog(context, R.style.loadingDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null);
        TextView showTip = (TextView) view.findViewById(R.id.show_tip_text);
        final TextView confirm = (TextView) view.findViewById(R.id.dialog_tip_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        showTip.setText(text);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int height = context.getWindowManager().getDefaultDisplay().getHeight();
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = width * 2 / 3;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getBpExperienceTipDialog(final Activity context, String text) {
        final Dialog dialog = new Dialog(context, R.style.loadingDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null);
        TextView showTip = (TextView) view.findViewById(R.id.show_tip_text);
        final TextView confirm = (TextView) view.findViewById(R.id.dialog_tip_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                ((BpMeasurePreActivity) context).toMeasure(BpConstant.BP_MEASURE_EXPERIENCE);
            }
        });
        showTip.setText(text);
        dialog.setCanceledOnTouchOutside(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int height = context.getWindowManager().getDefaultDisplay().getHeight();
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog showBodyDiseaseSelect(final Context context, final boolean isStart) {
        if (isStart) {
            SharePreferenceUtils.put(context, Constant.BODY_STATUS, "-1");
//            ThreeInOneApplication.getDefaultPreferences()
//                    .edit().putString(Constants.BODY_STATUS,"-1").apply();
        }
        final Dialog d = new Dialog(context, R.style.custom_dialog_style);
        View v = LayoutInflater.from(context).inflate(R.layout.body_disease_layout, null);
        final RadioGroup group = (RadioGroup) v.findViewById(R.id.body_time_radio_group);
        final RadioButton button1 = (RadioButton) v.findViewById(R.id.body_time_radio_1);
        final RadioButton button2 = (RadioButton) v.findViewById(R.id.body_time_radio_2);
        button1.setText(ThreeInOneApplication.getStringArrayRes(R.array.body_disease_time)[0]);
        SpannableString spannableString = new SpannableString(button1.getText());
        if (ThreeInOneApplication.isLunarSetting()) {
            spannableString.setSpan(new ForegroundColorSpan(ThreeInOneApplication.getColorRes(R.color.google_red)),
                    4, button1.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        button1.setText(spannableString);
        button2.setText(ThreeInOneApplication.getStringArrayRes(R.array.body_disease_time)[1]);
        spannableString = new SpannableString(button2.getText());
        if (ThreeInOneApplication.isLunarSetting()) {
            spannableString.setSpan(new ForegroundColorSpan(ThreeInOneApplication.getColorRes(R.color.google_red_1)),
                    4, button2.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        button2.setText(spannableString);
        button1.setChecked(true);
        button2.setChecked(false);
        final TextView confirm = (TextView) v.findViewById(R.id.body_d_confirm);
        final TextView cancel = (TextView) v.findViewById(R.id.body_d_cancel);
        TextView tips = (TextView) v.findViewById(R.id.body_d_tips);
        spannableString = new SpannableString(tips.getText());
        if (ThreeInOneApplication.isLunarSetting()) {
            spannableString.setSpan(new ForegroundColorSpan(ThreeInOneApplication.getColorRes(R.color.google_red_1)),
                    0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tips.setText(spannableString);
        ListView listView = (ListView) v.findViewById(R.id.body_disease_list_view);
        final boolean[] status = new boolean[Constant.BODY_CODES.length];
        final BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return Constant.BODY_CODES.length;
            }

            @Override
            public Object getItem(int position) {
                return Constant.BODY_CODES[position];
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.boay_disease_item, null);
                    holder = new ViewHolder();
                    holder.content = (TextView) convertView.findViewById(R.id.body_disease_item_content);
                    holder.box = (CheckBox) convertView.findViewById(R.id.body_disease_item_check);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                boolean s = status[position];
                String content = Constant.BODY_DISEASE[position];
                holder.box.setChecked(s);
                holder.content.setText(content);
                return convertView;
            }

        };
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isStart) {
//                    HandlerEvent event = new HandlerEvent(
//                            EventNameMessage.ECG_MEASURE_START, 0, null);
//                    HandlerManager.getManager().sendEvent(event);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    boolean s = status[position];
                    s = !s;
                    if (s) {
                        Arrays.fill(status, false);
                    }
                    status[position] = s;
                } else {
                    boolean s = status[0];
                    boolean s1 = status[position];
                    if (s && !s1) {
                        status[0] = false;
                    }
                    status[position] = !s1;
                }
                adapter.notifyDataSetChanged();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DfthECGDevice ecgDevice = (DfthECGDevice) DfthDeviceManager.getInstance().getDevice(DfthDevice.EcgDevice);

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < status.length; i++) {
                    if (status[i]) {
                        builder.append(Constant.BODY_CODES[i]).append(",");
                    }
                }
                if (builder.toString().length() > 0) {
                    int position = button1.isChecked() ? 0 : 1;

                    Log.e(TAG, Constant.BODY_DISEASE_TIME_CODES[position] + ".........." + builder.deleteCharAt(builder.length() - 1).toString());

                    ecgDevice.getParamsConfig().setSymptom(builder.toString(),Constant.BODY_DISEASE_TIME_CODES[position]);


//                    if(status[0]){
////                        builder.deleteCharAt(builder.length() -1);
//                        ecgDevice.getParamsConfig().setSymptom(Constant.BODY_DISEASE_TIME_CODES[position],builder.toString());
//                    }else{
////                        builder.append(Constant.BODY_DISEASE_TIME_CODES[position]);
//                        ecgDevice.getParamsConfig().setSymptom(Constant.BODY_DISEASE_TIME_CODES[position],builder.toString());
//                    }
//                    SharePreferenceUtils.put(context,Constant.BODY_STATUS,"-1");
//                    SharePreferenceUtils.put(context,Constant.BODY_STATUS,builder.toString());
//                    ThreeInOneApplication.getDefaultPreferences()
//                            .edit().putString(Constant.BODY_STATUS,"-1").apply();
//                    ThreeInOneApplication.getDefaultPreferences()
//                            .edit().putString(Constant.BODY_STATUS,builder.toString()).apply();
                } else {
//                    SharePreferenceUtils.put(context,Constant.BODY_STATUS,"-1");
//                    ThreeInOneApplication.getDefaultPreferences()
//                            .edit().putString(Constant.BODY_STATUS,"-1").apply();
                }
                d.dismiss();
            }
        });
        listView.setAdapter(adapter);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, 0);
        params.width = ThreeInOneApplication.getScreenWidth() - 2 * DisplayUtils.dip2px(context, 20);
        params.height = ThreeInOneApplication.getScreenHeight() - ThreeInOneApplication.getScreenHeight() / 10;
        d.setContentView(v, params);
        d.setCanceledOnTouchOutside(false);
        d.setCancelable(true);
        return d;
//        d.show();
    }

    static class ViewHolder {
        TextView content;
        CheckBox box;
    }

    public static Dialog getClearDataDialog(final Activity context) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_plan, null);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_plan_name_tv);
        TextView confirmTv = (TextView) view.findViewById(R.id.dialog_plan_confirm_tv);
        TextView cancelTv = (TextView) view.findViewById(R.id.dialog_plan_cancel_tv);
        nameTv.setText(R.string.setting_clear_data_dialog);
        final String userId = UserManager.getInstance().getDefaultUser().getUserId();
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DfthSDKManager.getManager().getStorage().deleteECGFileUploaded(userId).asyncExecute(new DfthCallBack<Boolean>() {
                    @Override
                    public void onResponse(DfthResult<Boolean> dfthResult) {
                        if (dfthResult.getReturnData()) {
//                            boolean isSuccess = DfthSDKManager.getManager().getDatabase().deleteAllECGResult();
//                            ToastUtils.showLong(context, isSuccess ? R.string.setting_clear_data_success
//                                    : R.string.setting_clear_data_fail);
//                            SharePreferenceUtils.put(context, SharePreferenceConstant.SINGLE_CARD_CREAT_TIME,0);
//                            SharePreferenceUtils.put(context, SharePreferenceConstant.TWELVE_CARD_CREAT_TIME,0);
//                            SharePreferenceUtils.put(context, SharePreferenceConstant.BP_CARD_CREAT_TIME,0);
                            EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.CLEAR_DATA_SUCCESS));
                        } else {
                            ToastUtils.showLong(context, R.string.setting_clear_data_fail);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getStopLongMeasureDialog(final Activity context, final MeasureMediator measureMediator) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_plan, null);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_plan_name_tv);
        TextView confirmTv = (TextView) view.findViewById(R.id.dialog_plan_confirm_tv);
        TextView cancelTv = (TextView) view.findViewById(R.id.dialog_plan_cancel_tv);
        nameTv.setText(R.string.stop_single_long_measure);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measureMediator.lessThan25Second(true);
                dialog.dismiss();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getCancelPlanDialog(final Activity context) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_plan, null);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_plan_name_tv);
        final TextView confirmTv = (TextView) view.findViewById(R.id.dialog_plan_confirm_tv);
        TextView cancelTv = (TextView) view.findViewById(R.id.dialog_plan_cancel_tv);
        nameTv.setText(R.string.bp_plan_cancel_dialog);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BpMeasurePreActivity)context).cancelPlan();
                dialog.dismiss();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

    public static Dialog getLogoutDialog(final Activity context) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_plan, null);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_plan_name_tv);
        TextView confirmTv = (TextView) view.findViewById(R.id.dialog_plan_confirm_tv);
        TextView cancelTv = (TextView) view.findViewById(R.id.dialog_plan_cancel_tv);
        nameTv.setText(R.string.slide_menu_is_logout);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String userId = UserManager.getInstance().getDefaultUser().getUserId();
//                BpPlan plan = DfthSDKManager.getManager().getDatabase().getDefaultBPPlan(userId);
//                if (plan != null && plan.getStatus() == BpPlan.STATE_RUNNING) {
//                    ToastUtils.showLong(context, R.string.slide_menu_logout_need_stop_plan);
//                    return;
//                }
                UserManager.getInstance().removeDefaultUser();
                SharePreferenceUtils.put(context, UserConstant.COMPLATE_SELF_INFO, UserConstant.UN_COMPLATE_SELF_INFO);
                ActivitySkipUtils.skipAnotherActivity(context, LoginActivity.class);
                context.finish();
//                ActivityCollector.finishOthers();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        dialog.setContentView(view, params);
        return dialog;
    }

    /**
     * 选择头像来源
     *
     * @param context
     * @param saveListener
     */
    public static Dialog photoImageSelectDialog(Context context, final DialogSaveListener saveListener) {
        final Dialog dialog = new Dialog(context, R.style.custom_dialog_style);
        View contentView = LayoutInflater.from(context).inflate(R.layout.select_photo_dialog, null);
        LinearLayout takePhoto = (LinearLayout) contentView.findViewById(R.id.user_takephoto);
        LinearLayout storagePhoto = (LinearLayout) contentView.findViewById(R.id.user_storageimage);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.width = ThreeInOneApplication.getScreenWidth() * 4 / 5;
        dialog.setContentView(contentView, layoutParams);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.user_takephoto:
                        if (saveListener != null) {
                            saveListener.onSave(dialog, UserUtils.TAKE_PICTURE);
                        }
                        break;
                    case R.id.user_storageimage:
                        if (saveListener != null) {
                            saveListener.onSave(dialog, UserUtils.LOCAL_ICON);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        takePhoto.setOnClickListener(listener);
        storagePhoto.setOnClickListener(listener);
        dialog.show();
        return dialog;
    }

    public static interface DialogSaveListener {
        void onSave(Dialog dialog, int type);
    }

}
