package cn.timeface.circle.baby.support.oss.token;

import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: rayboot  Created on 15/12/29.
 * email : sy0725work@gmail.com
 */
public class FederationTokenGetter {

    public static OSSFederationToken getToken(String stsUrl) {
        OSSFederationToken token = getTokenFromServer(stsUrl);
        return token;
    }

    private static OSSFederationToken getTokenFromServer(String stsUrl) {
        String responseStr = null;
        try {
            OkHttpClient httpClient = new OkHttpClient();

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(1, TimeUnit.HOURS)
                    .build();

            Request request = new Request.Builder()
                    .url(stsUrl)
                    .cacheControl(cacheControl)
                    .get()
                    .build();

            Response response = httpClient.newCall(request).execute();
            responseStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (responseStr == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            String ak = jsonObject.getString("tempAK");
            String sk = jsonObject.getString("tempSK");
            String securityToken = jsonObject.getString("token");
            long expireTime = jsonObject.getLong("expiration");
            return new OSSFederationToken(ak, sk, securityToken, expireTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
