package com.dftaihua.dfth_threeinone.network;

import com.dftaihua.dfth_threeinone.network.requestbody.UserIconFileUploadRequestBody;
import com.dftaihua.dfth_threeinone.network.requestbody.UserIconUploadRequestBody;
import com.dftaihua.dfth_threeinone.utils.UserUtils;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.Others.Utils.FileUtils;
import com.dfth.sdk.bluetooth.Action;
import com.dfth.sdk.dispatch.DfthServiceCall;
import com.dfth.sdk.dispatch.DispatchUtils;
import com.dfth.sdk.dispatch.SimpleDfthServiceCall;
import com.dfth.sdk.network.DfthNetwork;
import com.dfth.sdk.network.DfthServiceUtils;
import com.dfth.sdk.network.RealDfthService;
import com.dfth.sdk.network.response.DfthServiceResult;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DfthNetworkService {

    private static DfthNetwork mDfthNetwork;
    private static DfthNetworkService sService;
    private static DfthNetworkRequest mRequset;

    private DfthNetworkService() {
        mDfthNetwork = ((RealDfthService) DfthSDKManager.getManager().getDfthService()).getDfthNetwork();
        mRequset = DfthNetworkRequest.Factory.create();
    }

    private static DfthNetworkRequest getRequest() {
        if (sService == null) {
            sService = new DfthNetworkService();
        }
        return sService.mRequset;
    }

    public static DfthServiceCall<Void> uploadUserIcon(final String userId, final File file) {
        return new SimpleDfthServiceCall<Void>() {
            @Override
            public DfthServiceResult<Void> syncExecute() {
                UserIconUploadRequestBody requestBody = new UserIconUploadRequestBody(file.getName(),file.length());
                Gson gson = new Gson();
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/json"),gson.toJson(requestBody));
                RequestBody outPut = new UserIconFileUploadRequestBody(file);
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.addFormDataPart("fileMetadata",null,fileBody);
                builder.addFormDataPart("file",file.getName(),outPut);
                builder.setType(MediaType.parse("multipart/form-data"));
//                DfthNetworkRequest request = RequestUtils.generateUploadRequest(mDfthNetwork.getBaseUrl(),mDfthNetwork);
                DfthServiceResult<Void> result = DfthServiceUtils.createServiceCall(mRequset.uploadUserIcon(userId, "30101", builder.build())).syncExecute();
                return new DfthServiceResult<>(result.mResult, result.mMessage, result.mData);
            }
        };
    }

    public static void downloadUserIcon(final String userId, final UserIconDownloadListener listener) {
        getRequest().downloadUserIcon(userId, "30101").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DispatchUtils.performAsnycAction(new Action(0) {
                        @Override
                        protected void perform() {
                            try {
                                File file = UserUtils.generateLocalFile(userId);
                                FileUtils.checkFile(file);
                                FileOutputStream fos = new FileOutputStream(file);
                                BufferedInputStream bis = new BufferedInputStream(response.body().byteStream());
                                FileUtils.copyFile(bis, fos);
                                fos.close();
                                bis.close();
                                listener.onComplate(true);
                            } catch (Exception e) {
                                listener.onComplate(false);
                            }
                        }
                    }, Schedulers.io());
                } else {
                    listener.onComplate(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onComplate(false);
            }
        });
    }


}