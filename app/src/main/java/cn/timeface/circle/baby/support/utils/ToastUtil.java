package cn.timeface.circle.baby.support.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import cn.timeface.circle.baby.App;

/**
 * @author YW.SUN
 * @from 2016/3/10
 * @TODO
 */
public class ToastUtil {

    private static long lastToastTime = 0;
    private static final long TOAST_SKIP_TIME = 1000;

    public static void showToast(Context context, String info) {
        if (!TextUtils.isEmpty(info) && System.currentTimeMillis() - lastToastTime > TOAST_SKIP_TIME) {
            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
            lastToastTime = System.currentTimeMillis();
        }
    }

    public static void showToast(String info) {
        showToast(App.getInstance(), info);
    }

    public static void showToast(int strId) {
        showToast(App.getInstance(), App.getInstance().getString(strId));
    }

    public static void showToast(Context context, int strId) {
        showToast(context, context.getString(strId));
    }
}