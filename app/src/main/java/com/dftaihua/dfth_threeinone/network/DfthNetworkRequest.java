package com.dftaihua.dfth_threeinone.network;

import com.dftaihua.dfth_threeinone.network.requestbody.GetSMSCodeRequestBody;
import com.dftaihua.dfth_threeinone.network.responsebody.GetSMSCodeResponseBody;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.network.RealDfthService;
import com.dfth.sdk.network.response.DfthServiceResult;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/18 11:15
*/
public interface DfthNetworkRequest{

    public static class Factory{
        public static DfthNetworkRequest create(){
            return ((RealDfthService) DfthSDKManager.getManager().getDfthService()).getDfthNetwork()
                    .getRetrofit().create(DfthNetworkRequest.class);
        }
    }

    @POST("open/1.0/sms/user/retake/vericode")
    Call<GetSMSCodeResponseBody> getSmsCode(@Body GetSMSCodeRequestBody body);

    /**
     * 上传用户头像
     * @param userId
     * @param fileType
     * @param body
     * @return
     */

    @POST("store/2.0/biz_id/{biz_id}/file_type/{fileType}/files/multipart_upload")
    Call<DfthServiceResult<Void>> uploadUserIcon(@Path("biz_id") String userId, @Path("fileType") String fileType, @Body MultipartBody body);

    /**
     * 下载头像
     * @param UserId
     * @param fileType
     * @return
     */

    @Streaming
    @GET("store/2.0/biz_id/{biz_id}/file_type/{fileType}/files/download")
    Call<ResponseBody> downloadUserIcon(@Path("biz_id") String UserId, @Path("fileType") String fileType);

}
