package com.dftaihua.dfth_threeinone.utils;

import android.content.Context;

import com.dftaihua.dfth_threeinone.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/19 11:21
*/
public class LoginUtils {


    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,15}$";
    public static final String REGEX_USER_NAME = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{1,20}$";


    public static boolean isTelNum(String telNum){
        Pattern p = Pattern.compile("(1)[0-9]{10}");
        Matcher m = p.matcher(telNum);
        return m.matches();
    }

    public static boolean isPhoneNum(String telNum){
        Pattern p = Pattern.compile("(1)[0-9]{10}");
        Matcher m = p.matcher(telNum);
        return m.matches();
    }

    public static boolean isCodeNum(String telNum){
        Pattern p = Pattern.compile("[0-9]{4}");
        Matcher m = p.matcher(telNum);
        return m.matches();
    }
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    public static boolean isUserName(String userName) {
        return Pattern.matches(REGEX_USER_NAME, userName);
    }


    public static boolean isUserNameOk(Context context, String content){
        if(content == null || "".equals(content)){
            ToastUtils.showShort(context, R.string.username_is_null);
            return true;
        }
        if (!isUserName(content)){
            ToastUtils.showShort(context, R.string.username_is_error);
            return true;
        }
        return false;
    }


    public static boolean isPasswordEmpty(Context context, String content){
        if(content == null || "".equals(content)){
            ToastUtils.showShort(context, R.string.password_is_null);
            return true;
        }
        if (!isPassword(content)){
            ToastUtils.showShort(context, R.string.password_is_error);
            return true;
        }
        return false;
    }

    public static boolean isJudgeTelValid(Context context, String content){
        if(content == null || "".equals(content)){
            ToastUtils.showShort(context, R.string.tel_is_null);
            return false;
        }
        if(!isTelNum(content)){
            ToastUtils.showShort(context,R.string.tel_is_invalid);
            return false;
        }
        return true;
    }

    public static boolean isJudgeCodeValid(Context context,String content){
        if(content == null || "".equals(content)){
            ToastUtils.showShort(context,R.string.code_is_null);
            return false;
        }
        if(!isCodeNum(content)){
            ToastUtils.showShort(context,R.string.code_is_invalid);
            return false;
        }
        return true;
    }

    public static boolean judgeLoginIsValid(Context context,String phoneNum,String password){
        return  isJudgeTelValid(context,phoneNum) && !isPasswordEmpty(context,password);
    }

    public static boolean judgeRegistIsValid(Context context,String phoneNum,String password,String code){
        return  isJudgeTelValid(context,phoneNum) && !isPasswordEmpty(context,password)&&isJudgeCodeValid(context,code);
    }

}
