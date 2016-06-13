package cn.timeface.circle.baby;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.umeng.analytics.AnalyticsConfig;

import cn.timeface.circle.baby.managers.recorders.SimpleUploadRecorder;
import cn.timeface.circle.baby.oss.uploadservice.UploadService;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class App extends MultiDexApplication {
    private static App app = new App();

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
        //数据库ORM
        FlowManager.init(new FlowConfig.Builder(this).build());

        //shared preferences,
        Remember.init(this, BuildConfig.APPLICATION_ID + "_remember");

        //友盟key
        AnalyticsConfig.setAppkey(this, "");

        UploadService.setRecorder(new SimpleUploadRecorder());

        GlideUtil.init(this);

    }

}
