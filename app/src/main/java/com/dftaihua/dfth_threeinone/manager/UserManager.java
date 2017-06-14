package com.dftaihua.dfth_threeinone.manager;

import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.UserConstant;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.user.DfthUser;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/2/23 16:51
*/
public class UserManager {


    private static UserManager instance;

    private UserManager() {
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }


    public DfthUser getDefaultUser(){
        String userId = (String) SharePreferenceUtils.get(ThreeInOneApplication.getInstance(), UserConstant.USERID, "-1");
        return userId.equals("-1")?null: DfthSDKManager.getManager().getDatabase().getUser(userId);
    }

    private DfthUser createUser(){
        DfthUser user = new DfthUser();
        user.setName("王宝强");
        user.setBirthday(1401789633475l);
        user.setUserId("4ba19f14830148bca4c2faaa0269f29e");
        user.setHeight(167);
        user.setWeight(67);
        user.setGender(1);
        return user;
    }

    public void setDefaultUser(String userId){
        SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), UserConstant.USERID, userId);
    }

    public void removeDefaultUser(){
        SharePreferenceUtils.put(ThreeInOneApplication.getInstance(), UserConstant.USERID, "-1");
    }

}
