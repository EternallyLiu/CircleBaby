package cn.timeface.circle.baby.support.managers.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tbruyelle.rxpermissions.RxPermissions;

import cn.timeface.circle.baby.LoadMediaService;
import cn.timeface.circle.baby.support.managers.services.SavePicInfoService;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * author : wangshuai Created on 2017/4/5
 * email : wangs1992321@gmail.com
 */
public class CameraCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.showLog("action intent", "action intent==" + intent.getAction());
        if (intent.getAction().equalsIgnoreCase("android.hardware.action.NEW_PICTURE")) {
            try {
                SavePicInfoService.open(context.getApplicationContext());
            } catch (Exception e) {
            }
        } else if (intent.getAction().equalsIgnoreCase("android.hardware.action.NEW_VIDEO")) {
            try {
                context.startService(new Intent(context, LoadMediaService.class));
            } catch (Exception e) {
            }
        }

    }
}
