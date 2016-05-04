package cn.timeface.circle.baby.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * sharesdk分享
 *
 * @author yusen (QQ:1522289706)
 * @from 2014-3-7上午11:45:22
 * @TODO 分享 分享图片是支持2种，一种是本地图片文件，一种是网络图片URL
 */
public class ShareSdkUtil {

    public static final String FROM = "  (来自#时光流影#)"; //
    private static final String DEFAULT_ICON = "icon.jpg";

    /**
     * 直接分享到新浪微博
     *
     * @param context
     * @param title
     * @param content
     * @param imgUrl
     * @param url
     */
    public static void shareToSina(Context context, String title,
                                   String content, String imgUrl, String url) {
        ShareParams sp = new ShareParams();
        sp.setTitle(title);
        sp.setText(content);
        if (imgUrl != null && !imgUrl.trim().equals("")) {
            // 设置图片URL
            if (imgUrl.startsWith("http")) {
                sp.setImageUrl(imgUrl);
            } else {
                sp.setImagePath(imgUrl);
            }
        }
        if (url != null && !url.equals("")) {
            sp.setUrl(url);
        }
        Platform weibo = ShareSDK.getPlatform(context, SinaWeibo.NAME);
        weibo.share(sp);
    }

    public static void share(final Context context, String title, final String content,
                             String imgUrl, String url, Platform platform) {
        OnekeyShare oks = new OnekeyShare();
//        oks.setNotification(context.getResources().getIdentifier("ic_launcher", "drawable", context.getPackageName()),
//                context.getString(context.getResources().getIdentifier("app_name", "string", context.getPackageName())));
        // 设置标题
        oks.setTitle(title);
        // 设置文本
        oks.setText(content);
        if (TextUtils.isEmpty(imgUrl)) {
//            imgUrl = getDefaultIcon(context);
        }
        // 设置图片URL
        if (imgUrl.startsWith("http")) {
            oks.setImageUrl(imgUrl);
        } else {
            oks.setImagePath(imgUrl);
        }
        if (!TextUtils.isEmpty(url)) {
            // 设置微信和微信朋友圈跳转URL
            oks.setUrl(url);
        }
        if (platform != null) {
            oks.setPlatform(platform.getName());
        }
        // 不直接分享
        oks.setSilent(true);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (WechatMoments.NAME.equals(platform.getName()) || Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setText(paramsToShare.getTitle());
                }
            }
        });
        oks.show(context);
    }

    /**
     * 第三方登录
     *
     * @param context
     * @param platform 使用 Platform plat = ShareSDK.getPlatform(getActivity(),
     *                 QZone.NAME); 来获取平台 新浪微博为SinaWeibo.NAME,QQ使用QZone.NAME
     */
    public static void login(final Context context, Platform platform,
                             final LoginCallback callback) {
        if (platform.isValid()) {
            platform.removeAccount();
        }
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                arg2.printStackTrace();
                callback.loginErr();
                arg0.removeAccount();
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                callback.callback(arg0);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                callback.loginCancel();
            }
        });
        // platform.SSOSetting(false);
        platform.showUser(null);
    }

    /**
     * 第三方登录登出,取消验证
     *
     * @param context
     */
    public static void logout(Context context) {
        Platform qq = ShareSDK.getPlatform(context, QZone.NAME);
        Platform sina = ShareSDK.getPlatform(context, SinaWeibo.NAME);
        if (qq.isValid()) {
            qq.removeAccount();
        }
        if (sina.isValid()) {
            sina.removeAccount();
        }
    }

    public static String getImgStrByResource(Context context, int resId) {
        try {
            String cachePath = com.mob.tools.utils.R.getCachePath(context, null);
            String imgPath = "";
            if (resId == context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName())) {
                imgPath = cachePath + DEFAULT_ICON;
            } else {
                imgPath = cachePath + System.currentTimeMillis() + ".jpg";
            }
            File file = new File(imgPath);
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeResource(context.getResources(), resId);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }
            return imgPath;
        } catch (Exception e) {

        }
        return "http://ww1.sinaimg.cn/square/8d1ce310gw1efxkm5subuj20280280sk.jpg";
    }

//    /**
//     * 获取默认分享图片，即app_logo
//     *
//     * @param context
//     * @return
//     */
//    public static String getDefaultIcon(Context context)
//    {
//        return getImgStrByResource(context, context.getResources().getIdentifier("ic_launcher","drawable",context.getPackageName()));
//    }

    /**
     * 第三方登录成功之后回调此接口
     *
     * @author yusen (QQ:1522289706)
     * @from 2014-3-18上午9:25:35
     * @TODO TODO
     */
    public interface LoginCallback {
        void callback(Platform plat);

        void loginCancel();

        void loginErr();
    }
}
