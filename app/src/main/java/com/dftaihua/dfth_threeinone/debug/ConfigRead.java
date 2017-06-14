package com.dftaihua.dfth_threeinone.debug;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/5/11 10:02
*/

import com.dfth.sdk.config.DfthConfig;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigRead {
    private static ConfigRead mConfigRead;

    private ConfigRead (){

    }

    public static ConfigRead getConfig(){
        if (mConfigRead == null){
            mConfigRead = new ConfigRead();
        }
        return mConfigRead;
    }

    public String read(){
        try{
            Gson gson = new Gson();
            return JsonFormatTool.formatJson(gson.toJson(DfthConfig.getConfig()));
        }catch (Exception e){
            return "读取错误";
        }
    }
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
