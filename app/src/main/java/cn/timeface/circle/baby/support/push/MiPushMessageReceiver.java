package cn.timeface.circle.baby.support.push;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.activities.LoginActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.support.api.models.objs.MiPushMsgObj;
import cn.timeface.circle.baby.support.utils.FastData;

/**
 * 小米推送的receiver
 * Created by lidonglin on 2016/6/28.
 */
public class MiPushMessageReceiver extends PushMessageReceiver {

    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private static String mTopic;
    private String mAlias;
    private String mEndTime;
    public static int MESSAGE = 1;
    public static int DETAIL = 2;
    private static String mToastInfo;


    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        System.out.println("===== onReceivePassThroughMessage ====");
        mMessage = message.getContent();
        Log.d("-------->", "-------->onReceivePassThroughMessage mMessage: " + mMessage);
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        if (!TextUtils.isEmpty(mMessage)) {
            MiPushMsgObj pushMsgObj = null;
            try {
                pushMsgObj = new Gson().fromJson(mMessage, MiPushMsgObj.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (pushMsgObj != null) {
                if (pushMsgObj.getMsgChange() != null
                        // 这里判断UserID是个坑，服务端不能按照DeviceID推送，后期需要优化
                        && TextUtils.equals(pushMsgObj.getMsgChange().getUserId(), FastData.getUserId())) {
                    // 未读消息数量变化
                    EventBus.getDefault().post(new UnreadMsgEvent(pushMsgObj.getMsgChange().getUnReadMsgCount()));
                }
            }
        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        mToastInfo = message.getDescription();
        if (!TextUtils.isEmpty(mMessage)) {
            Message msg = Message.obtain();
            msg.obj = mMessage;
            msg.what = DETAIL;
            App.getHandler().sendMessage(msg);
        } else {
            App.getHandler().sendEmptyMessage(MESSAGE);
        }
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
//        EventBus.getDefault().post(new HomeRefreshEvent());
        EventBus.getDefault().post(new UnreadMsgEvent());
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mEndTime = cmdArg2;
            }
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        }
    }

    public static class DemoHandler extends Handler {

        private Context context;

        public DemoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
//            if (msg.what == DETAIL) {
//                String s = (String) msg.obj;
//                DynamicDetailActivity.open(FireApp.getApp(), Intent.FLAG_ACTIVITY_NEW_TASK, s);
//            } else if (msg.what == MESSAGE) {
            if (isAppForground(context)) {
                Log.v("MiPushMessageReceiver", "在前台");
//                Toast.makeText(context, mToastInfo, Toast.LENGTH_LONG).show();
            } else {
                Log.v("MiPushMessageReceiver", "不在前台");
                if (!TextUtils.isEmpty(FastData.getUserId())) {
                    Intent intent = new Intent(context, TabMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
//
//                MessageActivity.open(context);
//            }
        }
    }

    public static boolean isAppForground(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }
        return true;
    }
}
