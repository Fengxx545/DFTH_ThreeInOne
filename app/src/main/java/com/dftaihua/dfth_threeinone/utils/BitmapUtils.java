package com.dftaihua.dfth_threeinone.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import com.dfth.sdk.Others.Utils.Logger.Logger;

import java.io.File;
import java.io.IOException;

public class BitmapUtils {

	
	
	/** 
	* 转换图片成圆形 
	* @param bitmap 传入Bitmap对象 
	* status  是否加环
	* @return 
	*/ 
public static Bitmap toRoundBitmap(Bitmap bitmap) {
	int width = bitmap.getWidth(); 
	int height = bitmap.getHeight(); 
	float roundPx; 
	float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom; 
	if (width <= height) { 
		roundPx = width / 2; 
		top = 0; 
		bottom = width; 
		left = 0; 
		right = width; 
		height = width; 
		dst_left = 0; 
		dst_top = 0; 
		dst_right = width; 
		dst_bottom = width; 
	} else { 
		roundPx = height / 2; 
		float clip = (width - height) / 2; 
		left = clip; 
		right = width - clip; 
		top = 0; 
		bottom = height; 
		width = height; 
		dst_left = 0; 
		dst_top = 0; 
		dst_right = height; 
		dst_bottom = height; 
	} 
		Bitmap output = Bitmap.createBitmap(width,
		height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242; 
		final Paint paint = new Paint();
		final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
		final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint); 
		return output; 
	} 

	public static Drawable toRoundDrawable(Context context, int resId){
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
		bitmap = toRoundBitmap(bitmap);
		return new BitmapDrawable(context.getResources(), bitmap);
	}
	


    public static int[] getBitmapSize(Context context, int res){
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        Bitmap b = BitmapFactory.decodeResource(context.getResources(),res);
        return new int[]{b.getWidth(),b.getHeight()};
    }


	//通过传入位图,新的宽.高比进行位图的缩放操作  
	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
	        Bitmap BitmapOrg = bitmap;
	        int width = BitmapOrg.getWidth();  
	        int height = BitmapOrg.getHeight();  
	        int newWidth = w;  
	        int newHeight = h;  
	        float scaleWidth = ((float) newWidth) / width;  
	        float scaleHeight = ((float) newHeight) / height;  
	        Matrix matrix = new Matrix();
	        matrix.postScale(scaleWidth, scaleHeight);  
	        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
	                height, matrix, true);  
	        if(BitmapOrg != null){
	        	BitmapOrg.recycle();
            }
	        return resizedBitmap;  
	    }  
	
	
	@SuppressWarnings("deprecation")
	public static Bitmap createDrawableByFile(String path){
		Bitmap b = BitmapFactory.decodeFile(path);
		return b;
	}
	
	/**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                Logger.e(e, null);
        }
        return degree;
    }
   /*
    * 旋转图片 
    * @param angle 
    * @param bitmap 
    * @return Bitmap 
    */ 
   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
	   int width = bitmap.getWidth();
	   int height = bitmap.getHeight();
       Bitmap b = Bitmap.createBitmap(width,height, Config.RGB_565);
       Canvas c = new Canvas(b);
       c.drawBitmap(bitmap,0,0,null);
       c.save();
       c.rotate(angle);
       c.restore();
       return b;
   }
   
   public static Bitmap getBitmap(String path, int w, int h, int degree){
       long l = System.currentTimeMillis();
	   BitmapFactory.Options options = new BitmapFactory.Options();
	   options.inJustDecodeBounds = true;
	   BitmapFactory.decodeFile(path, options);
	   int w1 = options.outWidth;
	   int h1 = options.outHeight;
	   int s1 = 0,s2 = 0;
	   if(w1 >= w){
		   s1 = w1 / w;
	   }
	   if(h1 >= h){
		   s2 = h1 / h;
	   }
	   s1 = Math.max(1, Math.max(s1, s2));
	   options.inJustDecodeBounds = false;
	   options.inSampleSize = s1;
       Bitmap bm = BitmapFactory.decodeFile(path, options);
       Matrix m = new Matrix();
       m.setRotate(degree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
       float targetX, targetY;
       if (degree == 90) {
           targetX = bm.getHeight();
           targetY = 0;
       } else {
           targetX = bm.getHeight();
           targetY = bm.getWidth();
       }
           final float[] values = new float[9];
           m.getValues(values);
           float x1 = values[Matrix.MTRANS_X];
           float y1 = values[Matrix.MTRANS_Y];
           m.postTranslate(targetX - x1, targetY - y1);
           Bitmap b = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), options.inPreferredConfig);
           Canvas c = new Canvas(b);
           c.drawBitmap(bm, m, null);
           return b;
   }

	public static Bitmap getDiskBitmap(String pathString)
	{
		Bitmap bitmap = null;
		try
		{
			File file = new File(pathString);
			if(file.exists())
			{
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}


		return bitmap;
	}

}
