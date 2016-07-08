package cn.timeface.open.api;

import android.os.Environment;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.timeface.open.BuildConfig;
import cn.timeface.open.api.services.ApiService;
import cn.timeface.open.constants.Constant;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * author: rayboot  Created on 15/11/26.
 * email : sy0725work@gmail.com
 */
public class OpenApi {
    public static final String BASE_URL = BuildConfig.API_URL;
    //    public static final String BASE_URL = "http://dev1.v5time.net/timefacepod/";
    final ApiService apiService;

    OpenApi() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(60, TimeUnit.SECONDS);
        httpClientBuilder.cache(new Cache(Environment.getDataDirectory(), 10 * 1024 * 1024));
        httpClientBuilder.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                HttpUrl url = chain.request().url()
                        .newBuilder()
                        .addQueryParameter("access_token", Constant.ACCESS_TOKEN)
                        .addQueryParameter("unionid", Constant.UNIONID)
                        .build();
                Request request = chain.request().newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });


//        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.interceptors().add(interceptor);
//        }

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        Retrofit retrofit = retrofitBuilder
                .baseUrl(BASE_URL)
                .client(httpClientBuilder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ToStringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

    }

    public ApiService getApiService() {
        return apiService;
    }
}
