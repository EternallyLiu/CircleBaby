package cn.timeface.circle.baby.support.utils;

import android.widget.Toast;

import cn.timeface.circle.baby.App;

/**
 * @author YW.SUN
 * @from 2016/3/10
 * @TODO
 */
public class ToastUtil {

    public static void showToast(String info) {
        Toast.makeText(App.getInstance(), info, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int strId) {
        Toast.makeText(App.getInstance(), App.getInstance().getText(strId), Toast.LENGTH_SHORT).show();
    }
}
