package cn.timeface.common.utils;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

/**
 * Created by yusen on 2014/11/8.
 * 获取manifest中的常量
 */
public class MetaDataUtil {
    /**
     * 获取<activity><activity/>标签内的常量
     *
     * @param context
     * @return
     */
    public static String getMetaDataInActivity(Activity context, String key) {
        try {
            ActivityInfo info = context.getPackageManager().getActivityInfo(context.getComponentName(),
                    PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 获取在<Applocation></Applocation>标签中的常量值
     *
     * @param context
     * @param key
     * @return
     */
    public static String getMetaDataInApplication(Context context, String key) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            String msg = appInfo.metaData.getString(key);
            return msg;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取在<service></service>标签中的常量值
     *
     * @param service
     * @param key
     * @return
     */
    public static String getMetaDataInServier(Service service, String key) {
        try {
            ComponentName cn = new ComponentName(service.getApplication(), service.getClass());
            ServiceInfo info = service.getPackageManager().getServiceInfo(cn, PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 获取在<reciver></reciver>标签中的常量值
     *
     * @param context
     * @param receiver
     * @param key
     * @return
     */
    public static String getMetaDataInReceiver(Context context, BroadcastReceiver receiver, String key) {
        try {
            ComponentName cn = new ComponentName(context, receiver.getClass());
            ActivityInfo info = context.getPackageManager().getReceiverInfo(cn, PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (Exception e) {

        }
        return "";
    }
}
