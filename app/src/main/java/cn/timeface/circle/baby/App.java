package cn.timeface.circle.baby;

import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.activeandroid.ActiveAndroid;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.managers.recorders.SimpleUploadRecorder;
import cn.timeface.circle.baby.oss.uploadservice.UploadService;
import cn.timeface.circle.baby.push.MiPushMessageReceiver;
import cn.timeface.circle.baby.utils.ChannelUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.MiPushUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.common.utils.DeviceUuidFactory;
import cn.timeface.common.utils.TimeFaceUtilInit;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class App extends MultiDexApplication {
    private static App app = new App();
    private static MiPushMessageReceiver.DemoHandler handler = null;

    public static App getInstance() {
        if (app == null) {
            app = new App();
        }
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //初始化db
        ActiveAndroid.initialize(this);
        //数据库ORM
        FlowManager.init(new FlowConfig.Builder(this).build());

        //shared preferences,
        Remember.init(this, BuildConfig.APPLICATION_ID + "_remember");

        //友盟key
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "570b24bbe0f55a4fc7000c00", ChannelUtil.getChannel(this)));
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);

        //初始化util
        TimeFaceUtilInit.init(this);

//        pushSetting();
        MiPushUtil.init(this);

        UploadService.setRecorder(new SimpleUploadRecorder());

        GlideUtil.init(this);

        if (handler == null) {
            handler = new MiPushMessageReceiver.DemoHandler(getApplicationContext());
        }

//        if (BuildConfig.DEBUG) {
//            LeakCanary.install(this);
//        }

        Fresco.initialize(this);
    }

    /**
     * 设置push的相关提示
     */
    private void pushSetting() {
        if (FastData.getPush()) {
            if (shouldInit()) {
                MiPushClient.registerPush(this, TypeConstants.MI_PUSH_APP_ID, TypeConstants.MI_PUSH_APP_KEY);
                MiPushClient.setAlias(this, new DeviceUuidFactory(
                        getApplicationContext()).getDeviceId(), null);
            }
            MiPushClient.setLocalNotificationType(app, TypeConstants.PUSH_VOICE_SHAKE);
        } else {
            MiPushClient.unregisterPush(app);
        }
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


    public static MiPushMessageReceiver.DemoHandler getHandler() {
        return handler;
    }

}
