package cn.timeface.common.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author rayboot
 * @from 14-5-5 14:44
 * @TODO 公共方法
 */
public class CommonUtil {
    // 隐藏软键盘
    public static void hideSoftInput(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    public static int concurrent(long preDate, long curDate) {
        long gapTime = curDate - preDate;
        int day = (int) (gapTime / (1000 * 60 * 60 * 24));
        return day;
    }
}
