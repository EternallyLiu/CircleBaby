package cn.timeface.circle.baby.ui.guides;

import android.text.TextUtils;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * author : wangshuai Created on 2017/2/28
 * email : wangs1992321@gmail.com
 */
public class GuideUtils {

    /**
     * 返回是否需要显示新手指引
     *
     * @param name
     * @return
     */
    public static boolean checkVersion(String name) {
        name += "_guide";
        boolean flag = false;
        String content = FastData.getString(name, "");
        try {
            if (TextUtils.isEmpty(content)) {
                flag = true;
                content = "false_" + BuildConfig.VERSION_CODE;
                FastData.putString(name, content);
            } else {
                String[] split = content.split("_");
                if (split.length == 2) {
                    int version = Integer.parseInt(split[1]);
                    if (version < BuildConfig.VERSION_CODE) {
                        flag = true;
                        content = "false_" + BuildConfig.VERSION_CODE;
                        FastData.putString(name, content);
                    }
                } else {
                    flag = true;
                    content = "false_" + BuildConfig.VERSION_CODE;
                    FastData.putString(name, content);
                }
            }
        } catch (Exception e) {
            flag = true;
            content = "false_" + BuildConfig.VERSION_CODE;
            FastData.putString(name, content);
            LogUtil.showError(e);
        }
        return flag;
    }

}
