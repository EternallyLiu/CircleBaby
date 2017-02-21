package cn.timeface.circle.baby.ui.timelines.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * author : wangshuai Created on 2017/2/15
 * email : wangs1992321@gmail.com
 */
public class PermissionUtils {
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static boolean isEMUI() {
        return isPropertiesExist(KEY_EMUI_VERSION_CODE);
    }

    public static boolean isMIUI() {
        try {

            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {

            return false;
        }
    }

    public static boolean isFlyme() {
        try { // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    private static String getPackageName() {
        return "cn.timeface.circle.baby";
    }

    /**
     * 原生系统
     *
     * @param context
     */
    public static void skip(Context context) {
        String SCHEME = "package";
//调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
        final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
//调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
        final String APP_PKG_NAME_22 = "pkg";
//InstalledAppDetails所在包名
        final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
//InstalledAppDetails类名
        final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, getPackageName(), null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, getPackageName());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void skipSpec(Context context) {
        Intent intent = new Intent();
        if (isMIUI()) {
            LogUtil.showLog("miui");
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                skip(context);
                LogUtil.showError(e);
            }
        } else if (isFlyme()) {
            LogUtil.showLog("flyme");
            intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", getPackageName());
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                skip(context);
                LogUtil.showError(e);
            }
        } else {
            LogUtil.showLog("android");
            //三星、索尼可以
            skip(context);
        }
    }

    /**
     * 跳转到应用市场应用详情页面进行评分
     * @param context
     */
    public static void skipRatting(Context context){
        String mAddress = "market://details?id=" + getPackageName();
        Intent marketIntent = new Intent("android.intent.action.VIEW");
        marketIntent.setData(Uri.parse(mAddress ));
        context.startActivity(marketIntent);
    }

    public static boolean isPropertiesExist(String... keys) {
        try {
            BuildProperties prop = BuildProperties.newInstance();
            for (String key : keys) {
                String str = prop.getProperty(key);
                if (str == null)
                    return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
