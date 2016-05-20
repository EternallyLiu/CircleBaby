package cn.timeface.common.utils;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by tao on 2015/3/31.
 */
public class GpsUtil {
    public static boolean isGpsOpen(Context context) {
        LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }
}
