package cn.timeface.circle.baby.utils;

import android.widget.Toast;

import cn.timeface.circle.baby.App;

/**
 * @author YW.SUN
 * @from 2016/3/10
 * @TODO
 */
public class ToastUtil {

    public static void showToast(String info){
        Toast.makeText(App.getInstance(), info, Toast.LENGTH_SHORT).show();
    }
}
