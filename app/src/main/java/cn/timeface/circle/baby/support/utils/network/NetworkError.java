package cn.timeface.circle.baby.support.utils.network;


import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;

import java.net.UnknownHostException;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.views.HttpStatusCodes;
import retrofit2.adapter.rxjava.HttpException;

public class NetworkError {

    public static void showException(Context context, Throwable throwable) {
        if (throwable != null && context != null) {
            if (throwable instanceof UnknownHostException) {
                toastMessage(context, HttpStatusCodes.getMessage(HttpStatusCodes.NO_CONNECT));
            } else if (throwable instanceof HttpException) {
                toastMessage(context, HttpStatusCodes.getMessage(((HttpException) throwable).code()));
            } else if (throwable instanceof JsonSyntaxException) {
                toastMessage(context, HttpStatusCodes.getMessage(HttpStatusCodes.PARSE_ERROR));
            } else {
                toastMessage(context, HttpStatusCodes.getMessage(HttpStatusCodes.UNKNOWN_ERROR));
            }
            if (BuildConfig.DEBUG)
                throwable.printStackTrace();
        }
    }

    private static void toastMessage(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
