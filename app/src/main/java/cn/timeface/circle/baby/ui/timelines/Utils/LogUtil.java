package cn.timeface.circle.baby.ui.timelines.Utils;

import android.util.Log;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.support.api.services.ApiService;

/**
 * Created by wangshuai on 2017/1/12.
 */

public class LogUtil {

    public static final String TAG="test_log";

    public static void showLog(String text){
        if (BuildConfig.SHOW_LOG)
            Log.d(TAG,text);
    }
}
