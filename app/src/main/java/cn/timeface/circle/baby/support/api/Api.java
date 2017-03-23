package cn.timeface.circle.baby.support.api;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.api.services.WechatApiService;
import cn.timeface.circle.baby.support.utils.NetUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * author: rayboot  Created on 15/11/26.
 * email : sy0725work@gmail.com
 */
public class Api {
    final WechatApiService apiWechatService;
    final ApiService apiService;

    Api() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(60, TimeUnit.SECONDS);
        httpClientBuilder.networkInterceptors().add(chain -> {
            Request.Builder req = chain.request().newBuilder();
            for (Map.Entry<String, String> entry : NetUtils.getHttpHeaders().entrySet()) {
                req.addHeader(entry.getKey(), entry.getValue());
            }
            return chain.proceed(req.build());
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.interceptors().add(interceptor);
        }

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        Retrofit retrofit = retrofitBuilder
                .baseUrl(BuildConfig.API_URL)
                .client(httpClientBuilder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .addConverterFactory(LoganSquareConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        retrofit = retrofitBuilder
                .baseUrl(WechatApiService.BASE_URL)
                .client(httpClientBuilder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .addConverterFactory(LoganSquareConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiWechatService = retrofit.create(WechatApiService.class);

    }

    public ApiService getApiService() {
        return apiService;
    }

    public WechatApiService getApiWechatService() {
        return apiWechatService;
    }

}