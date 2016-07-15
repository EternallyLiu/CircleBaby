package cn.timeface.circle.baby.utils;

import cn.timeface.common.utils.Remember;

/**
 * Created by rayboot on 12/7/15.
 */
public class Once {
    public void show(String tagKey, OnceCallback callback) {
        boolean moreTime = Remember.getBoolean(tagKey, false);
        if (moreTime) {
            callback.onMore();
        } else {
            Remember.putBoolean(tagKey, true);
            callback.onOnce();
        }
    }

    public interface OnceCallback {
        void onOnce();
        void onMore();
    }
}
