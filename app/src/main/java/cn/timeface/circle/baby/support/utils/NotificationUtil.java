package cn.timeface.circle.baby.support.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.LoginActivity;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.support.managers.receivers.DownloadCompleteReceiver;
import cn.timeface.circle.baby.support.api.models.PushItem;
import cn.timeface.circle.baby.support.api.models.UnReadMsgItem;
import cn.timeface.circle.baby.support.api.models.responses.PushResponse;

/**
 * @author LiuCongshan (QQ:10907315)
 * @from 2014-2-20上午10:55:26
 * @TODO 通知相关
 */
public class NotificationUtil {
    /**
     * 通用通知ID，支持多条消息合并显示
     */
    public static int NOTIFICATION_ID_GENERAL = 1;

    private static boolean clickDismiss = true;
    private static int unReadPushCount = 0;
    private static List<PushItem> unReadPushList = new ArrayList<>(10);

    //取消通知
    public static void cancelNotification(Context context, int notifyId) {
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.cancel(notifyId);
    }

    //显示推送消息
    public static void showPushMsg(Context context, PushResponse pushData, boolean... dismissValue) {
        if (dismissValue == null || dismissValue.length <= 0) {
            clickDismiss = true;
        } else {
            clickDismiss = dismissValue[0];
        }
        if (pushData == null || pushData.getDatas() == null || pushData.getDatas().size() == 0) {
            return;
        }
        for (PushItem pushInfo : pushData.getDatas()) {
            NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context, pushInfo);
            if (notificationBuilder == null) {
                continue;
            }

            Intent intent = getIntentByPushItem(context, pushInfo);
            if (pushInfo.getDataType() == PushItem.DownloadSuccess) {
                PendingIntent pending = PendingIntent.getBroadcast(context, pushInfo.getPushId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(pending);
                notificationBuilder.addAction(R.drawable.ic_push_small_icon, "点击安装", pending);
            } else {
                PendingIntent pendingIntent = PendingIntent.getActivity(context, pushInfo.getPushId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(pendingIntent);
            }

            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
            Notification notify = notificationBuilder.build();
            if (clickDismiss) {
                notify.flags |= Notification.FLAG_AUTO_CANCEL;
            } else {
                notify.flags |= Notification.FLAG_NO_CLEAR;
            }

            if (pushInfo.getDataType() == PushItem.Cache) {
                mNotificationManager.notify(PushItem.Cache, notify);
            } else if (pushInfo.getDataType() == PushItem.DownloadSuccess) {
                mNotificationManager.notify(PushItem.DownloadSuccess, notify);
            } else if (pushInfo.getDataType() == PushItem.BookCoupons) {
                mNotificationManager.notify(PushItem.BookCoupons, notify);
            } else {
                mNotificationManager.notify(NOTIFICATION_ID_GENERAL, notify);
            }
        }
    }

    //获取Notification Builder
    private static NotificationCompat.Builder getNotificationBuilder(Context context, PushItem pushData) {
        // 设置不接收密函推送
        if (!FastData.getBoolean(TypeConstant.PUSH_SETTING_PRIVATE_MSG_TAG, true)
                && pushData.getDataType() == PushItem.PrivateMsg) {
            return null;
        }
        // 设置不接收系统通知推送
        if (!FastData.getBoolean(TypeConstant.PUSH_SETTING_NOTIFICATION_TAG, true)
                && pushData.getFrom() == 99) {
            return null;
        }

        NotificationCompat.Builder notificationBuilder;
        switch (pushData.getDataType()) {
            case PushItem.Cache:
            case PushItem.DownloadSuccess:
            case PushItem.BookCoupons:
                notificationBuilder = createCommonNotification(context, pushData);
                break;
            default:
                notificationBuilder = createInboxStyleNotification(context, pushData);
                break;
        }

        boolean isVoice = FastData.getBoolean(TypeConstant.PUSH_SETTING_VOICE_TAG, true);
        boolean isVibrate = FastData.getBoolean(TypeConstant.PUSH_SETTING_VIBRATE_TAG, true);
        if (isVibrate && isVoice) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        }
        if (isVibrate && !isVoice) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        if (!isVibrate && isVoice) {
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        return notificationBuilder;
    }

    //生成单条Notification
    private static NotificationCompat.Builder createCommonNotification(Context context, PushItem pushData) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(
                TextUtils.isEmpty(pushData.getTitle()) ? context.getText(R.string.app_name) : pushData.getTitle())
                .setContentText(pushData.getAlert())
                .setSmallIcon(R.drawable.ic_push_small_icon)
                .setAutoCancel(true)
                .setTicker("一条新消息")
//                    .setOnlyAlertOnce(true)//一次而已
//                    .setPriority(2)//优先级
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        return notificationBuilder;
    }

    //生成多条合并的Notification
    private static NotificationCompat.Builder createInboxStyleNotification(Context context, PushItem pushData) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        addUnReadPush(pushData);
        if (unReadPushCount > 1) {
            // 合并多条信息
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(unReadPushCount + "条未读消息");
            inboxStyle.setSummaryText("点击查看");
            for (int i = unReadPushList.size(); i > 0; i--) {
                inboxStyle.addLine(unReadPushList.get(i - 1).getAlert());// 降序
            }

            notificationBuilder.setContentTitle(unReadPushCount + "条未读消息")
                    .setContentText("点击查看")
                    .setSmallIcon(R.drawable.ic_push_small_icon)
                    .setAutoCancel(true)
                    .setTicker(unReadPushCount + "条未读消息")
                    .setStyle(inboxStyle)
                    .setGroup("Group_Key")
                    .setGroupSummary(true)
                    .setNumber(unReadPushCount)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        } else {
            notificationBuilder.setContentTitle(
                    TextUtils.isEmpty(pushData.getTitle()) ? context.getText(R.string.app_name) : pushData.getTitle())
                    .setContentText(pushData.getAlert())
                    .setSmallIcon(R.drawable.ic_push_small_icon)
                    .setAutoCancel(true)
                    .setTicker("一条新消息")
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        }
        return notificationBuilder;
    }

    // 根据不同的push获取相应Intent
    private static Intent getIntentByPushItem(Context context, PushItem pushinfo) {
        // Sets the Activity to start in a new, empty task
//        Intent intent = new Intent(context, MyNoticeActivity.class);
        Intent intent = new Intent(context, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.putExtra(Constant.CLEAR_UNREAD_PUSH_COUNT_TAG, true);//清除未读push消息数量
//        intent.putExtra("push_info", pushinfo);

        if (pushinfo.getDataType() == PushItem.Cache) {
//            return new Intent(context, MyCollectActivity.class);
            return new Intent(context, LoginActivity.class);
        } else if (pushinfo.getDataType() == PushItem.DownloadSuccess) {
            return new Intent(DownloadCompleteReceiver.ACTION_NOTIFICATION_CLICKED);
        } else if (pushinfo.getDataType() == PushItem.BookCoupons) {
            //收到印书劵
//            Intent intent2 = new Intent(context, MyNotificationActivity.class);
            Intent intent2 = new Intent(context, LoginActivity.class);
            intent2.putExtra("type", UnReadMsgItem.TYPE_NOTIFICATION);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.setAction(Intent.ACTION_MAIN);
//            intent2.putExtra(Constant.CLEAR_UNREAD_PUSH_COUNT_TAG, true);//清除未读push消息数量
            return intent2;
        }

        return intent;
    }

    private static void addUnReadPush(PushItem item) {
        unReadPushCount++;
        if (unReadPushList.size() < 10) {
            unReadPushList.add(item);
        } else {
            unReadPushList.remove(0);//删除第一条
            unReadPushList.add(item);
        }
    }

    public static void clearUnReadPush(Context context) {
        unReadPushCount = 0;
        if (unReadPushList != null) {
            unReadPushList.clear();
        }
        cancelNotification(context, NOTIFICATION_ID_GENERAL);
    }
}
