package cn.timeface.circle.baby.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;
import java.util.Map;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.BuildConfig;


/**
 * author: rayboot  Created on 16/1/14.
 * email : sy0725work@gmail.com
 */
public class NetUtils {

    public static Map<String, String> getHttpHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("LOC", "CN");
        headers.put("OUTFLAG", "JSON");
        headers.put("CHANNEL", ChannelUtil.getChannel(App.getInstance()));
        headers.put("USERID", FastData.getUserId());
        headers.put("BABYID", FastData.getBabyId() + "");
        headers.put("DEVICEID", new DeviceUuidFactory(App.getInstance()).getDeviceId());
        headers.put("TOKEN", FastData.getUserToken());
        headers.put("VERSION", BuildConfig.VERSION_CODE + "");
        System.out.println("USERID======" + FastData.getUserId());
        System.out.println("BABYID======" + FastData.getBabyId());
        System.out.println("DEVICEID======" + new DeviceUuidFactory(App.getInstance()).getDeviceId());
        System.out.println("TOKEN======" + FastData.getUserToken());
        System.out.println("VERSION======" + BuildConfig.VERSION_CODE);
        System.out.println("CHANNEL======" + ChannelUtil.getChannel(App.getInstance()));

        return headers;
    }


    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
}
