package cn.timeface.circle.baby.ui.growthcircle.mainpage.mipush;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.constants.MiPushConstant;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.CircleMainActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.CircleAlertDialog;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleChangedEvent;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CirclePassThroughMessageCallbackEvent;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CirclePassThroughMessageEvent;

/**
 * 圈首页 透传消息处理（用于处理用户在圈浏览时，弹出提示框并做页面跳转逻辑操作）
 */
public class CircleMainPushHandler implements IEventBus {

    protected final String TAG = this.getClass().getSimpleName();

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(priority = 999, threadMode = ThreadMode.POSTING)
    public void onEvent(CirclePassThroughMessageEvent event) {
        Log.i("-------->", "-------->CirclePushMessageEvent--> CircleMainActivity");

        // 拦截教师认证发生改变、圈解散、被圈主移除 事件
        if (event.type == MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZED
                || event.type == MiPushConstant.TYPE_CIRCLE_TEACHER_UNAUTHORIZED
                || event.type == MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED
                || event.type == MiPushConstant.TYPE_CIRCLE_DISBANDED) {
            if (EventBus.getDefault().hasSubscriberForEvent(CirclePassThroughMessageEvent.class)) {
                Log.d(TAG, "CircleMainActivity --> cancel CirclePushMessageEvent Delivery");
                // 此处收到事件表示用户正在圈浏览过程中，所以这里要消费掉该事件，不再往下传递
                EventBus.getDefault().cancelEventDelivery(event);
            }

            Context context = App.getInstance().getTopActivity();
            if (context != null) {
                CircleAlertDialog dialog = new CircleAlertDialog(context);

                switch (event.type) {
                    case MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZED:
                    case MiPushConstant.TYPE_CIRCLE_TEACHER_UNAUTHORIZED:
                        // 教师认证发生改变
                        dialog.setMessage(event.type == MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZED ?
                                "您在本圈的教师资格认证已通过" : "您在本圈的教师资格已被取消");
                        dialog.setPositiveClickListener(v -> {
                            dialog.dismiss();

                            // 发送已读状态
                            EventBus.getDefault().post(
                                    new CirclePassThroughMessageCallbackEvent(
                                            CirclePassThroughMessageCallbackEvent.TYPE_RECEIVED_TEACHER_CHANGED,
                                            event.circleUserInfo.getCircleId())
                            );

                            // 退出到圈首页并刷新身份
                            EventBus.getDefault().post(
                                    new CircleChangedEvent(event.circleUserInfo.getCircleId(),
                                            CircleChangedEvent.TYPE_INFO_CHANGED)
                            );
                            CircleMainActivity.openFromPush(context);
                        });
                        break;

                    case MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED:
                    case MiPushConstant.TYPE_CIRCLE_DISBANDED:
                        // 圈解散、被圈主移除
                        if (event.circleObj != null) {
                            String circleName = event.circleObj.getCircleName();
                            dialog.setMessage(event.type == MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED ?
                                    "【" + circleName + "】的圈主已经将你移除" :
                                    "【" + circleName + "】已被圈主解散");
                            dialog.setPositiveClickListener(v -> {
                                dialog.dismiss();

                                // 发送已读状态
                                EventBus.getDefault().post(
                                        new CirclePassThroughMessageCallbackEvent(
                                                event.type == MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED ?
                                                        CirclePassThroughMessageCallbackEvent.TYPE_RECEIVED_MEMBER_REMOVED :
                                                        CirclePassThroughMessageCallbackEvent.TYPE_RECEIVED_CIRCLE_DISBAND,
                                                event.circleObj.getCircleId())
                                );

                                // 退出到圈列表并刷新数据
                                CircleMainActivity.openFromPush(context);
                                EventBus.getDefault().post(
                                        new CircleChangedEvent(event.circleObj.getCircleId(),
                                                event.type == MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED ?
                                                        CircleChangedEvent.TYPE_QUIT :
                                                        CircleChangedEvent.TYPE_DISBANDED
                                        )
                                );
                            });
                        }
                        break;
                }
                dialog.show();
            }
        }
    }

}
