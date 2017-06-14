package com.dftaihua.dfth_threeinone.manager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dfth.sdk.permission.DfthPermissionException;

import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.internal.Utils;



public class ThreeInOnePermissionManager {
    public static final String CAMERA = Manifest.permission.CAMERA;
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkCamerPermission(){
        return ThreeInOneApplication.getInstance().checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static void assertCamerPermission() throws DfthPermissionException {
        if(!Utils.isOverMarshmallow()) {
            return;
        }
        if(!checkCamerPermission()){
            throw new DfthPermissionException(CAMERA);

        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static String verifyPermission(Context context, String permission, int code){
        if(!Utils.isOverMarshmallow()) {
            return "";
        }
        if(permission.equals(CAMERA) || !checkCamerPermission()){
            String content = "照相权限没有打开，请在\"系统设置\"或授权对话框中允许\"拍照\"权限";
            if(((Activity)context).shouldShowRequestPermissionRationale(CAMERA)){
            }
            return content;
        }
        return "";
    }
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermission(Context context, String permission,int code){
        if(!Utils.isOverMarshmallow()) {
            return;
        }
        if(context instanceof Activity){
            ((Activity)context).requestPermissions(new String[]{permission},code);
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public static void assertCamerPermission(Context context,int code){
        if(!Utils.isOverMarshmallow()) {
            return;
        }
        List<String> permissions = new ArrayList<>();
        if(!checkCamerPermission()){
            permissions.add(CAMERA);
        }

        String[] permission = new String[permissions.size()];
        for(int i = 0; i < permission.length; i++){
            permission[i] = permissions.get(i);
        }
        if(context instanceof Activity && permission.length > 0){
            ((Activity)context).requestPermissions(permission,code);
        }
    }

}