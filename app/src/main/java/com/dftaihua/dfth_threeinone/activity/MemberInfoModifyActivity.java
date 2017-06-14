package com.dftaihua.dfth_threeinone.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.ThreeInOnePermissionManager;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.network.DfthNetworkService;
import com.dftaihua.dfth_threeinone.utils.BitmapUtils;
import com.dftaihua.dfth_threeinone.utils.DfthNumberPicker;
import com.dftaihua.dfth_threeinone.utils.LoginUtils;
import com.dftaihua.dfth_threeinone.utils.MIUIUtils;
import com.dftaihua.dfth_threeinone.utils.MathUtils;
import com.dftaihua.dfth_threeinone.utils.PhotoImageUtil;
import com.dftaihua.dfth_threeinone.utils.TimeUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dftaihua.dfth_threeinone.utils.UserUtils;
import com.dftaihua.dfth_threeinone.widget.TitleView;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.dfth.sdk.user.DfthUser;

import java.io.File;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/1/23 0023.
 */
public class MemberInfoModifyActivity extends BaseActivity implements View.OnClickListener,
        TitleView.OnSaveClickListener, DialogFactory.DialogSaveListener {
    private RelativeLayout mHeadRl;
    private RelativeLayout mNameRl;
    private RelativeLayout mGenderRl;
    private RelativeLayout mBirthRl;
    private RelativeLayout mHeightRl;
    private RelativeLayout mWeightRl;
    private TextView mNameTv;
    private TextView mGenderTv;
    private TextView mBirthTv;
    private TextView mHeightTv;
    private TextView mWeightTv;
    private DfthUser mUser;
    private int mUserGender = 0; //性别　1男 2女
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    private String mUserName = "";
    private String mUserBirth = "";
    private int mUserHeight;
    private int mUserWeight;
    private DfthNumberPicker mNumberPicker;
    private DatePickerDialog mDatePickerDialog;
    private EditText mEditText;
    private ImageView mPhotoImage;

    private File mFile;
    private File mTempFile;


    public MemberInfoModifyActivity() {
        mTitleNameRes = R.string.title_activity_modify_info;
        mTitleColorRes = R.color.google_white;
        mTitleNameColorRes = R.color.google_black;
        mStatus = TITLE_VISIBLE | BACK_VISIBLE | SAVE_VISIBLE;
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_member_info_modify, null);
        mHeadRl = (RelativeLayout) view.findViewById(R.id.member_info_modify_head_rl);
        mNameRl = (RelativeLayout) view.findViewById(R.id.member_info_modify_name_rl);
        mGenderRl = (RelativeLayout) view.findViewById(R.id.member_info_modify_gender_rl);
        mBirthRl = (RelativeLayout) view.findViewById(R.id.member_info_modify_birth_rl);
        mHeightRl = (RelativeLayout) view.findViewById(R.id.member_info_modify_height_rl);
        mWeightRl = (RelativeLayout) view.findViewById(R.id.member_info_modify_weight_rl);
        mNameTv = (TextView) view.findViewById(R.id.member_info_modify_name_tv);
        mGenderTv = (TextView) view.findViewById(R.id.member_info_modify_gender_tv);
        mBirthTv = (TextView) view.findViewById(R.id.member_info_modify_birth_tv);
        mHeightTv = (TextView) view.findViewById(R.id.member_info_modify_height_tv);
        mWeightTv = (TextView) view.findViewById(R.id.member_info_modify_weight_tv);
        mPhotoImage = (ImageView) view.findViewById(R.id.member_info_modify_head_iv);
        mHeadRl.setOnClickListener(this);
        mNameRl.setOnClickListener(this);
        mGenderRl.setOnClickListener(this);
        mBirthRl.setOnClickListener(this);
        mHeightRl.setOnClickListener(this);
        mWeightRl.setOnClickListener(this);
        mTitleView.setSaveListener(this);
        return view;
    }

    @Override
    public void initData() {
        mUser = UserManager.getInstance().getDefaultUser();
        mUserName = mUser.getName();
        mUserGender = mUser.getGender();
        mUserBirth = TimeUtils.getString(mUser.getBirthday(), "yyyy-MM-dd");
        mUserHeight = mUser.getHeight();
        mUserWeight = mUser.getWeight();
        mNameTv.setText(mUserName);
        mGenderTv.setText(mUser.getGender() == 1 ? R.string.member_info_gender_man : R.string.member_info_gender_woman);
        mBirthTv.setText(mUserBirth);
        mHeightTv.setText(mUserHeight + ThreeInOneApplication.getStringRes(R.string.member_info_height_cm));
        mWeightTv.setText(mUserWeight + ThreeInOneApplication.getStringRes(R.string.member_info_weight_kg));
        Bitmap bitmap = UserUtils.getIcon(mUser);
        if (bitmap != null){
            mPhotoImage.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.member_info_modify_head_rl:
                DialogFactory.photoImageSelectDialog(this, this);
//                ToastUtils.showLong(this, R.string.slide_menu_add_home_doctor);
                break;
            case R.id.member_info_modify_name_rl:
                showNameDialog();
                break;
            case R.id.member_info_modify_gender_rl:
                showGenderDialog();
                break;
            case R.id.member_info_modify_birth_rl:
                showBirthDialog();
                break;
            case R.id.member_info_modify_height_rl:
                showHeightDialog();
                break;
            case R.id.member_info_modify_weight_rl:
                showWeightDialog();
                break;
        }
    }

    private void showNameDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_name, null);
        mEditText = (EditText) view.findViewById(R.id.dialog_name_et);
        mEditText.setText(mUserName);
        mEditText.setSelection(mUserName.length());
        (view.findViewById(R.id.dialog_name_confirm_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mUserName = mEditText.getText().toString().trim();
                setNameText();
            }
        });
        (view.findViewById(R.id.dialog_name_cancel_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 2 / 3;
        dialog.setContentView(view, params);
        dialog.show();
    }

    public void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] genders = new String[2];
        genders[0] = ThreeInOneApplication.getStringRes(R.string.member_info_gender_man);
        genders[1] = ThreeInOneApplication.getStringRes(R.string.member_info_gender_woman);
        builder.setSingleChoiceItems(genders, mUserGender - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUserGender = which == 0 ? MALE : FEMALE;
                setGenderText();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @SuppressLint("NewApi")
    public void showBirthDialog() {
        String birth = mUserBirth == null ? "" : mUserBirth;
        String[] birthArray = null;
        int year = 0;
        int month = 1;
        int day = 1;
        if (birth != null && !"".equals(birth)) {
            birthArray = birth.split("-");
        }
        if (birthArray != null && birthArray.length > 2) {
            year = Integer.parseInt(birthArray[0]);
            month = Integer.parseInt(birthArray[1]);
            day = Integer.parseInt(birthArray[2]);
        } else {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH) + 1;
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        mDatePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (mDatePickerDialog != null) {
                    mUserBirth = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    mDatePickerDialog = null;
                }
            }
        }, year, (month - 1 + 12) % 12, day);
        if (Build.VERSION.SDK_INT > 11) {
            Calendar c = Calendar.getInstance();
            c.set(2039, 1, 1);
            long time = -c.getTimeInMillis();
            mDatePickerDialog.getDatePicker().setMinDate(time);
            mDatePickerDialog.getDatePicker().init(year, (month - 1 + 12) % 12, day, mDatePickerDialog);
        }
        //手动设置按钮
        mDatePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                ThreeInOneApplication.getStringRes(R.string.confirm),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                DatePicker datePicker = mDatePickerDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                mUserBirth = year + "-" + (month + 1) + "-" + day;
                setBirthText(mUserBirth);
            }
        });
        //取消按钮，如果不需要直接不设置即可
        mDatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                ThreeInOneApplication.getStringRes(R.string.cancel),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
//                System.out.println("BUTTON_NEGATIVE~~");
            }
        });
//        mDatePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
//                .setTextSize(ThreeInOneApplication.getDimen(R.dimen.standard_button_text));
//        mDatePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//                .setTextSize(ThreeInOneApplication.getDimen(R.dimen.standard_button_text));
        mDatePickerDialog.show();
    }

    private void showHeightDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_height, null);
        mNumberPicker = (DfthNumberPicker) view.findViewById(R.id.dialog_height_np);
        mNumberPicker.setMinValue(50);
        mNumberPicker.setMaxValue(250);
        mNumberPicker.setValue(mUserHeight);
        mNumberPicker.setNumberPickerDividerColor(mNumberPicker);
        (view.findViewById(R.id.dialog_height_confirm_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mUserHeight = mNumberPicker.getValue();
                setHeightText();
            }
        });
        (view.findViewById(R.id.dialog_height_cancel_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 2 / 3;
        dialog.setContentView(view, params);
        dialog.show();
    }

    private void showWeightDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_height, null);
        mNumberPicker = (DfthNumberPicker) view.findViewById(R.id.dialog_height_np);
        mNumberPicker.setMinValue(30);
        mNumberPicker.setMaxValue(300);
        mNumberPicker.setValue(mUserWeight);
        mNumberPicker.setNumberPickerDividerColor(mNumberPicker);
        (view.findViewById(R.id.dialog_height_confirm_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mUserWeight = mNumberPicker.getValue();
                setWeightText();
            }
        });
        (view.findViewById(R.id.dialog_height_cancel_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 2 / 3;
        dialog.setContentView(view, params);
        dialog.show();
    }

    private void setNameText() {
        mNameTv.setText(mUserName);
    }

    private void setGenderText() {
        mGenderTv.setText(mUserGender == MALE ? R.string.member_info_gender_man : R.string.member_info_gender_woman);
    }

    public void setBirthText(String content) {
        if (content != null && !"".equals(content)) {
            String[] array = content.split("-");
            int year = Integer.parseInt(array[0]);
            int month = Integer.parseInt(array[1]);
            int day = Integer.parseInt(array[2]);
            long birthLong = MathUtils.getLongTime(year, month - 1, day);
            long currentTime = System.currentTimeMillis() / 1000 / 60 / 60 / 24;
            long birth = birthLong / 1000 / 60 / 60 / 24;
            if (birth > currentTime) {
                ToastUtils.showLong(this, R.string.member_info_birth_not_over_now);
                return;
            }
            mBirthTv.setText(content);
        }
    }

    private void setHeightText() {
        mHeightTv.setText(mUserHeight + ThreeInOneApplication.getStringRes(R.string.member_info_height_cm));
    }

    private void setWeightText() {
        mWeightTv.setText(mUserWeight + ThreeInOneApplication.getStringRes(R.string.member_info_weight_kg));
    }

    @Override
    public void onSaveClick() {
        if (!NetworkCheckReceiver.getNetwork()) {
            ToastUtils.showShort(this, R.string.network_not_connect);
            return;
        }
        if (mUser != null && !LoginUtils.isUserNameOk(this, mUserName)) {
            mUser.setBirthday(TimeUtils.getTimeByStr(mUserBirth, "yyyy-MM-dd"));
            mUser.setGender(mUserGender);
            mUser.setHeight(mUserHeight);
            mUser.setWeight(mUserWeight);
            mUser.setName(mUserName);
            DfthSDKManager.getManager().getDfthService().updateMember(mUser).asyncExecute(new DfthServiceCallBack<Void>() {
                @Override
                public void onResponse(DfthServiceResult<Void> dfthServiceResult) {
                    if (dfthServiceResult.mResult == 0) {
                        DfthSDKManager.getManager().getDatabase().updateUser(mUser);
                        ToastUtils.showLong(MemberInfoModifyActivity.this, R.string.member_info_modify_success);
                        MemberInfoModifyActivity.this.finish();
                    } else {
                        ToastUtils.showShort(MemberInfoModifyActivity.this, dfthServiceResult.mMessage);
                    }
                }
            });
        }
    }

    @Override
    public void onSave(Dialog dialog, int type) {
        if (dialog != null) {
            dialog.dismiss();
        }
//        final HomeActivity activity = (HomeActivity) getActivity();
        switch (type) {
            case UserUtils.TAKE_PICTURE: {
                mTempFile = UserUtils.generateTempFile(mUser.getUserId());
                if (ThreeInOnePermissionManager.checkCamerPermission()) {
                    UserUtils.takePhoto(this, mTempFile);
                }else{
                    ThreeInOnePermissionManager.assertCamerPermission(this,101);
                }
            }
            break;
            case UserUtils.LOCAL_ICON:
                UserUtils.selectPhoto(this);
                break;
            case UserUtils.CANCEL:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case UserUtils.TAKE_PICTURE:
                    if (mTempFile != null) {
                        int degree = BitmapUtils.readPictureDegree(mTempFile.getAbsolutePath());
                        if (degree != 0) {
                            mTempFile = UserUtils.getDegreeFile(mTempFile);
                        }
                        if(MIUIUtils.isMIUI()){
                            mFile = null;
                        }else{
                            mFile = UserUtils.generateTempFile(mUser.getUserId());
                        }
                        UserUtils.photoZoom(this, Uri.fromFile(mTempFile), UserUtils.PHOTO_OUT_WIDTH, UserUtils.PHOTO_OUT_HEIGHT, mFile);
                    }
                    break;
                case UserUtils.LOCAL_ICON:
                    if (data == null)
                        break;
                    mFile = UserUtils.generateTempFile(mUser.getUserId());
                    if (Build.VERSION.SDK_INT < 19) {
                        UserUtils.photoZoom(this, data.getData(), UserUtils.PHOTO_OUT_WIDTH,
                                UserUtils.PHOTO_OUT_HEIGHT, mFile);
                    } else {
                        String path = PhotoImageUtil.getPath(this, data.getData());
                        Uri u = Uri.fromFile(new File(path));
                        UserUtils.photoZoom(this, u, UserUtils.PHOTO_OUT_WIDTH,
                                UserUtils.PHOTO_OUT_HEIGHT, mFile);
                    }
                    break;
                case UserUtils.ZOOM_PHOTO: {
                    File toFile = UserUtils.generateLocalFile(mUser.getUserId());
                    Bitmap b;
                    if(mFile == null){
                        b = UserUtils.getZoomDonePhoto(data, toFile);
                    }else{
                        b = UserUtils.getZoomDonePhoto(mFile, toFile);
                    }
                    UserUtils.deleteFile(mTempFile);
                    mPhotoImage.setImageBitmap(b);
                    // TODO: 2017/6/8 保存和上传图片
                    savePicture(UserUtils.generateLocalFile(mUser.getUserId()));
//                    mUser.setPath(toFile.getAbsolutePath());
//                    mUser.addUpdateItem(User.ICON);
                }
                break;
                default:
                    break;
            }
        }
    }

    private void savePicture(File file) {
        DfthNetworkService.uploadUserIcon(mUser.getUserId(),file).asyncExecute(new DfthServiceCallBack<Void>() {
            @Override
            public void onResponse(DfthServiceResult<Void> response) {
                if (response.mResult == 0){
                    ToastUtils.showShort(MemberInfoModifyActivity.this,"头像上传成功");
                }else{
                    ToastUtils.showShort(MemberInfoModifyActivity.this,"头像上传失败");
                    Log.e("error",response.mMessage);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        switch (requestCode) {
            case 101:{
                ThreeInOnePermissionManager.verifyPermission(this,permissions[0],requestCode);
            }
            break;
            default:
                break;
        }
    }

}
