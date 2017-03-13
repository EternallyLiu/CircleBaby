package cn.timeface.circle.baby.support.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.common.utils.DeviceUuidFactory;
import cn.timeface.common.utils.TimeFaceUtilInit;

/**
 * @author wxw
 * @from 2016/1/4
 * @TODO 小米推送 初始化
 */
public class MiPushUtil {

    public static final String APP_ID = "2882303761517513154";
    public static final String APP_KEY = "5641751317154";
    // 此TAG在adb logcat中检索自己所需要的信息，只需在命令行终端输入 adb logcat | grep com.xiaomi.mipushdemo
    public static final String TAG = "cn.timeface.circle.baby";

    public static final int VOICE_SHAKE = 3;
    public static final int VOICE = 1;
    public static final int SHAKE = 2;
    public static final int NULL = 0;

    // 订阅标签
    public static final String SUBSCRIBE_TAG_PRIVATE_MSG = "private_message";
    public static final String SUBSCRIBE_TAG_SYSTEM_NOTICE = "system_notice";

    public static void init(Context context) {
        // 注册push服务，注册成功后会向MiPushMessageReceiver发送广播
        // 可以从MiPushMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit(context)) {
            MiPushClient.registerPush(context, APP_ID, APP_KEY);
            MiPushClient.setAlias(context, new DeviceUuidFactory(TimeFaceUtilInit.getContext()).getDeviceId(), null);
//            Log.d(TAG + "MiPush----->", "DeviceId----->" + new DeviceUuidFactory(TimeFaceUtilInit.getContext()).getDeviceId());
            pushSetting(context);
        }

        if (BuildConfig.DEBUG) {
            LoggerInterface newLogger = new LoggerInterface() {
                @Override
                public void setTag(String tag) {
                    // ignore
                }

                @Override
                public void log(String content, Throwable t) {
//                    Log.d(TAG + "MiPush----->", content, t);
                }

                @Override
                public void log(String content) {
//                    Log.d(TAG + "MiPush----->", content);
                }
            };
            Logger.setLogger(context, newLogger);
        }
    }

    private static boolean shouldInit(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static void pushSetting(Context context) {
        if (!FastData.getBoolean(TypeConstant.PUSH_SETTING_ALL_TAG, true)) {
            MiPushClient.unregisterPush(context);
        } else {
            boolean voice = FastData.getBoolean(TypeConstant.PUSH_SETTING_VOICE_TAG, true);
            boolean vibrate = FastData.getBoolean(TypeConstant.PUSH_SETTING_VIBRATE_TAG, true);
            if (voice && vibrate) {
                MiPushClient.setLocalNotificationType(context, VOICE_SHAKE);
            } else if (voice) {
                MiPushClient.setLocalNotificationType(context, VOICE);
            } else if (vibrate) {
                MiPushClient.setLocalNotificationType(context, SHAKE);
            } else {
                MiPushClient.setLocalNotificationType(context, NULL);
            }

            // 密函 订阅或取消订阅
            if (FastData.getBoolean(TypeConstant.PUSH_SETTING_PRIVATE_MSG_TAG, true)) {
                MiPushClient.subscribe(context, SUBSCRIBE_TAG_PRIVATE_MSG, null);
            } else {
                MiPushClient.unsubscribe(context, SUBSCRIBE_TAG_PRIVATE_MSG, null);
            }

            // 系统通知 订阅或取消订阅
            if (FastData.getBoolean(TypeConstant.PUSH_SETTING_NOTIFICATION_TAG, true)) {
                MiPushClient.subscribe(context, SUBSCRIBE_TAG_SYSTEM_NOTICE, null);
            } else {
                MiPushClient.unsubscribe(context, SUBSCRIBE_TAG_SYSTEM_NOTICE, null);
            }
        }
    }

}
