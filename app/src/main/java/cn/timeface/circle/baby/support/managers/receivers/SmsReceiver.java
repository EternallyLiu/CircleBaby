package cn.timeface.circle.baby.support.managers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;


import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.timeface.circle.baby.events.SmsEvent;


/**
 * Created by yusen on 2015/1/3.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i("-------->", "-------->SmsReceiver-->onReceive");
        SmsMessage msg = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (Object p : pdusObj) {
                msg = SmsMessage.createFromPdu((byte[]) p);
                String msgTxt = msg.getMessageBody();//得到消息的内容
//                Log.i("-------->", "------>onReceive:" + msgTxt);
                if (!TextUtils.isEmpty(msgTxt)
//                        && msgTxt.startsWith("亲爱的时光流影注册用户，您的动态验证码为")
                        && (msgTxt.contains("亲爱的时光流影注册用户，您的动态验证码为")
                        || (msgTxt.contains("【时光流影】")))) {
                    String contentReg = "[0-9]{6}";
                    Pattern pattern = Pattern.compile(contentReg);
                    Matcher matcher = pattern.matcher(msgTxt);
                    while (matcher.find()) {
                        String content = matcher.group();
                        EventBus.getDefault().post(new SmsEvent(content));
                    }
                }
            }
            return;
        }

    }
}
