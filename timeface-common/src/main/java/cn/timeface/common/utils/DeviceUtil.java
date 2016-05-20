package cn.timeface.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.ViewConfiguration;

import java.io.File;
import java.util.Locale;

import cn.timeface.common.R;

/**
 * @author rayboot
 * @from 2014-2-24上午9:32:33
 * @TODO 设备相关
 */
public class DeviceUtil {
    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        String number = tm.getLine1Number();
        return number;
    }

    public static String getLanguageInfo() {
        String language = TimeFaceUtilInit.getContext()
                .getResources()
                .getConfiguration().locale.getLanguage();
        if (language.toLowerCase(Locale.getDefault()).contains("zh")) {
            return "CN";
        } else if (language.toLowerCase(Locale.getDefault()).contains("tw")) {
            return "TW";
        } else {
            return "US";
        }
    }

    public static int getStatusBarHeight(Context context) {
        Rect rect = new Rect();
        ((Activity) context).getWindow()
                .getDecorView()
                .getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    public static int getScreenWidth(Activity context) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        context.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    //该方法获取高度  包括 状态栏   但是不包括底部虚拟按键
    public static int getScreenHeight(Activity context) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        context.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    //获取导航栏高度
    public static int getNavigationBarHeight(Activity context) {
        if (!checkDeviceHasNavigationBar(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        int resourceId =
                resources.getIdentifier("navigation_bar_height", "dimen",
                        "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dpToPx(Resources res, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                res.getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    public static final int spToPx(Context context, float sp) {
        final float scaledDensity =
                context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaledDensity + 0.5f);
    }

    /**
     * 判断有无navigationbar
     */
    public static boolean checkDeviceHasNavigationBar(Context activity) {

        //Emulator
        if (Build.FINGERPRINT.startsWith("generic"))
            return true;

        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    /**
     * 是否为横屏
     * @param context
     * @return
     */
    public static boolean isLandscape(Context context) {
        Configuration configuration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = configuration.orientation ; //获取屏幕方向

        return ori == configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 屏幕高宽比
     * @param context
     * @return
     */
    public static float getScreenRate(Activity context) {
        int w = getScreenWidth(context);
        int h = getScreenHeight(context);
        return h / w;
    }

    public static void setCameraDisplayOrientation ( Activity activity , int cameraId , android.hardware.Camera camera ) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo(); android.hardware.Camera.getCameraInfo ( cameraId , info );
        int rotation = activity.getWindowManager ().getDefaultDisplay ().getRotation ();
        int degrees = 0 ;
        switch ( rotation )
        {
            case Surface.ROTATION_0 :
                degrees = 0 ;
                break ;
            case Surface.ROTATION_90 :
                degrees = 90 ;
                break ;
            case Surface.ROTATION_180 :
                degrees = 180 ;
                break ;
            case Surface.ROTATION_270 :
                degrees = 270 ;
                break ;
        }
        int result ;
        if ( info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT )
        {
            result = ( info.orientation + degrees ) % 360 ;
            result = ( 360 - result ) % 360 ; // compensate the mirror
        } else
        { // back-facing
            result = ( info.orientation - degrees + 360 ) % 360 ;
        }
        camera.setDisplayOrientation (result);
    }

    /**
     * 扫描、刷新相册
     */
    public static void scanPhotos(String filePath, Context context)
            throws NullPointerException {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    //
    //public static void createShortCut(Context context, Class cls,
    //        String appName)
    //{
    //    //创建快捷方式的Intent
    //    Intent shortcutintent =
    //            new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    //    //不允许重复创建
    //    shortcutintent.putExtra("duplicate", false);
    //    //需要现实的名称
    //    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
    //    //快捷图片
    //    Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
    //            R.drawable.ic_launcher);
    //    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
    //    //点击快捷图片，运行的程序主入口
    //    Intent intent = new Intent(context, cls);
    //    intent.setAction(Intent.ACTION_MAIN);
    //    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    //    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
    //    //发送广播。OK
    //    context.sendBroadcast(shortcutintent);
    //}
    //
    //public static boolean isAddShortCut(Context context, String appName)
    //{
    //
    //    boolean isInstallShortcut = false;
    //    final ContentResolver cr = context.getContentResolver();
    //
    //    String AUTHORITY =
    //            android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO
    //                    ? "com.android.launcher2.settings"
    //                    : "com.android.launcher.settings";
    //
    //    final Uri CONTENT_URI =
    //            Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
    //    Cursor c =
    //            cr.query(CONTENT_URI, new String[] { "title", "iconResource" },
    //                    "title=?", new String[] { appName }, null);
    //
    //    if (c != null && c.getCount() > 0)
    //    {
    //        isInstallShortcut = true;
    //    }
    //    return isInstallShortcut;
    //}
}