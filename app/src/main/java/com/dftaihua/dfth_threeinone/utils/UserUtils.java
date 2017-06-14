package com.dftaihua.dfth_threeinone.utils;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/6/8 17:23
*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dfth.sdk.Others.Utils.FileUtils;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.user.DfthUser;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;

import static com.dfth.sdk.Others.Utils.FileUtils.BASE_PROGRAM_ROOT_PATH;

public class UserUtils {

    public static final int TAKE_PICTURE = 10;//拍照
    public static final int LOCAL_ICON   = 11;//从相册选择
    public static final int CANCEL       = 12;//取消
    public static final int ZOOM_PHOTO   = 13;//裁剪图片
    public static final int PHOTO_OUT_WIDTH = MIUIUtils.isMIUI() ? 120 : 480;
    public static final int PHOTO_OUT_HEIGHT = MIUIUtils.isMIUI() ? 120 : 480;
    public static String IMAGE_PATH = BASE_PROGRAM_ROOT_PATH + "com.dftaihua.dfth_threeinone"+"/image";


    public static File generateTempFile(String userId){
        StringBuffer buffer = new StringBuffer();
        buffer.append(IMAGE_PATH).append("/").append(userId).append("_")
                .append("icon_temp").append(".png");
        File f = new File(buffer.toString());
        FileUtils.checkFile(f);
        return f;
    }

    /**
     * 执行照相功能
     */
    public static void takePhoto(Activity activity, File file){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        activity.startActivityForResult(intent ,TAKE_PICTURE,null);
    }
    /**
     *执行从相册中选取
     */
    @SuppressLint("InlinedApi")
    public static void selectPhoto(Activity activity){
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*");
        if (Build.VERSION.SDK_INT <19) {
            intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        }else {
            intentFromGallery.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        activity.startActivityForResult(intentFromGallery,
                LOCAL_ICON,null);
    }

    /**
     * 执行裁剪功能
     */
    public static void photoZoom(Activity activity, Uri uri, int outWidth, int outHeight, File outPutFile){
        Intent edit = new Intent("com.android.camera.action.CROP");
        edit.setDataAndType(uri,
                "image/*");
        edit.putExtra("crop", "true");
        edit.putExtra("aspectX", 1);
        edit.putExtra("aspectY", 1);
        edit.putExtra("outputX", outWidth);
        edit.putExtra("outputY", outHeight);
        edit.putExtra("scale", true);
        edit.putExtra("noFaceDetection", true);
        edit.putExtra("scaleUpIfNeeded", true);
        edit.putExtra("setWallpaper", false);
        if(MIUIUtils.isMIUI()){
            Log.e("true","true");
            edit.putExtra("return-data", true);
        }else{
            edit.putExtra("return-data", false);
            edit.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(outPutFile));
        }
        activity.startActivityForResult(edit, ZOOM_PHOTO,null);
    }

    /**
     * 处理裁剪完毕的照片
     */
    public static Bitmap getZoomDonePhoto(Intent data,File toFile){
        if(data == null){
            return null;
        }
        Bundle bundle = data.getExtras();
        if (bundle == null) {
            return null;
        }
        Bitmap b1 = (Bitmap) bundle.get("data");
        if(b1 == null){
            toFile.delete();
            return null;
        }
        Bitmap b = BitmapUtils.toRoundBitmap(b1);
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(toFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Logger.e(e, null);
        } finally {
            close(fos);
        }
        return b;
    }


    /**
     * 处理裁剪完毕的照片
     */
    public static Bitmap getZoomDonePhoto(File file,File toFile){
        Bitmap b1 = BitmapFactory.decodeFile(file.getAbsolutePath());
        if(b1 == null){
            toFile.delete();
            return null;
        }
        Bitmap b = BitmapUtils.toRoundBitmap(b1);
        FileOutputStream fos = null;
        try{
            file.delete();
            fos = new FileOutputStream(toFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Logger.e(e, null);
        } finally {
            close(fos);
        }
        return b;
    }

    public static File generateLocalFile(String id){
        StringBuilder buffer = new StringBuilder();
        buffer.append(IMAGE_PATH).append("/").append(id).append("/").append(id).append("_")
                .append("icon").append(".png");
        File f = new File(buffer.toString());
        FileUtils.checkFile(f);
        return f;
    }


    public static File getDegreeFile(File file){
        final int degree = BitmapUtils.readPictureDegree(file.getAbsolutePath());
        Bitmap b = null;
        FileOutputStream fos = null;
        try{
            b = BitmapUtils.getBitmap(file.getAbsolutePath(), ThreeInOneApplication.getScreenWidth(),
                    ThreeInOneApplication.getScreenHeight(),degree);
            file.delete();
            file = new File(file.getAbsolutePath());
            fos = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }catch(Exception e){
            Logger.e(e, null);
        }finally{
            if(b != null) {
                b.recycle();
            }
            close(fos);
        }
        return file;
    }

    private static void close(Object c){
        if(c != null){
            try {
                if(c instanceof Cursor){
                    ((Cursor)c).close();
                }else if(c instanceof Closeable){
                    ((Closeable)c).close();
                }
            }catch (Exception e) {
                Logger.e(e, null);
            }
        }
    }

    public static void deleteFile(File file){
        if(file == null){
            return;
        }
        if(file.isDirectory()){
            return;
        }

        if(file.exists()){
            file.delete();
        }
    }

    public static Bitmap getIcon(DfthUser user) {
        File toFile = UserUtils.generateLocalFile(user.getUserId());
        Bitmap b = null;
        if(toFile == null){

        }else{
            b = BitmapUtils.getDiskBitmap(toFile.getAbsolutePath());
        }
        return b;
    }

}
