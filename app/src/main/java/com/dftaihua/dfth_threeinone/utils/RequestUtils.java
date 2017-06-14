package com.dftaihua.dfth_threeinone.utils;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/6/13 10:23
*/

import com.dftaihua.dfth_threeinone.network.DfthNetworkRequest;
import com.dfth.sdk.network.DfthNetwork;
import com.dfth.sdk.network.interceptor.AuthenticationInterceptor;
import com.dfth.sdk.network.interceptor.CommonParamsInterceptor;
import com.dfth.sdk.network.interceptor.LoggerInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RequestUtils{

    public static DfthNetworkRequest generateUploadRequest(String baseUrl, DfthNetwork network){
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.addInterceptor(new AuthenticationInterceptor(network));
        okBuilder.addInterceptor(new CommonParamsInterceptor(network));
        okBuilder.addInterceptor(new LoggerInterceptor(network));
        okBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okBuilder.readTimeout(90, TimeUnit.SECONDS);
        okBuilder.writeTimeout(90, TimeUnit.SECONDS);
        okBuilder.retryOnConnectionFailure(false);
        builder.client(okBuilder.build());
        Retrofit retrofit = builder.build();
        return retrofit.create(DfthNetworkRequest.class);
    }

}
