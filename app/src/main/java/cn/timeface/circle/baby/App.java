package cn.timeface.circle.baby;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.activeandroid.ActiveAndroid;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.umeng.analytics.AnalyticsConfig;
import com.wbtech.ums.UmsAgent;

import cn.timeface.circle.baby.constants.URLConstant;
import cn.timeface.circle.baby.managers.recorders.SimpleUploadRecorder;
import cn.timeface.circle.baby.oss.uploadservice.UploadService;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.common.utils.TimeFaceUtilInit;

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
        //初始化db
        ActiveAndroid.initialize(this);
        //数据库ORM
        FlowManager.init(new FlowConfig.Builder(this).build());

        //shared preferences,
        Remember.init(this, BuildConfig.APPLICATION_ID + "_remember");

        //友盟key
//        AnalyticsConfig.setAppkey(this, "");

        initUmsAgent();

        //初始化util
        TimeFaceUtilInit.init(this);

        UploadService.setRecorder(new SimpleUploadRecorder());

        GlideUtil.init(this);

    }

    /**
     * init UmsAgent
     */
    private void initUmsAgent() {
        UmsAgent.setBaseURL(URLConstant.BASE_LOG_URL);
        String userId = FastData.getUserId();
        if (userId != null) {
            UmsAgent.bindUserIdentifier(this, userId);
        }
        if (!BuildConfig.DEBUG) {
            UmsAgent.onError(this);
        }
        UmsAgent.setDefaultReportPolicy(this, 1);
        UmsAgent.postClientData(this);
        UmsAgent.uploadAllLog(this);
    }

}
