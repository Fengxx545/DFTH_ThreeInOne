package com.dftaihua.dfth_threeinone.network.responsebody;

/*    *****      *     *        
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/18 11:18
*/
public class GetSMSCodeResponseBody {
    public String getVeriCode() {
        return veriCode;
    }

    public void setVeriCode(String veriCode) {
        this.veriCode = veriCode;
    }

    private String veriCode;
}
