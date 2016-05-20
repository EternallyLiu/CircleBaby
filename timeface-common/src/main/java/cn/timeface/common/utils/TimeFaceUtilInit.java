package cn.timeface.common.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * @author rayboot
 * @from 14/10/30 16:10
 * @TODO
 */
public class TimeFaceUtilInit {
    private static Context mContext;

    public static void init(Context mContext) {
        TimeFaceUtilInit.mContext = mContext;
    }

    public static Context getContext() {
        return mContext;
    }

}
