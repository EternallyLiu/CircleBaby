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

import java.lang.reflect.Type;
import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.activities.LoginActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.constants.MiPushConstant;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.support.api.models.objs.MiPushMsgChangeObj;
import cn.timeface.circle.baby.support.api.models.objs.MiPushMsgInfoObj;
import cn.timeface.circle.baby.support.api.models.objs.MiPushMsgObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CirclePassThroughMessageEvent;
import cn.timeface.circle.baby.ui.kiths.KithFragment;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.fragments.TimeFaceDetailFragment;
import cn.timeface.common.utils.DeviceUuidFactory;
import ikidou.reflect.TypeBuilder;
import rx.Observable;
import rx.functions.Func1;

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
        mMessage = message.getContent();
        Log.d("-------->", "-------->onReceivePassThroughMessage mMessage: " + mMessage);
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        if (!TextUtils.isEmpty(mMessage)) {
            handlePassThroughMessage(mMessage);
        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        mToastInfo = message.getDescription();
//        if (!TextUtils.isEmpty(mMessage)) {
//            Message msg = Message.obtain();
//            msg.obj = mMessage;
//            msg.what = DETAIL;
//            App.getHandler().sendMessage(msg);
//        } else {
//            App.getHandler().sendEmptyMessage(MESSAGE);
//        }
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        Log.d("-------->", "-------->onNotificationMessageClicked: " + mMessage);
        if (!TextUtils.isEmpty(mMessage)) {
            LogUtil.showLog("-------->", isAppForeground(context) + "");
            if (!isAppForeground(context)) {
                if (TextUtils.isEmpty(FastData.getUserId())) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    handlePushMessage(context, mMessage);
                }
            }
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
                MiPushClient.setAlias(context.getApplicationContext(), new DeviceUuidFactory(
                        context.getApplicationContext()).getDeviceId(), null);
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
                MiPushClient.setAlias(context.getApplicationContext(), new DeviceUuidFactory(
                        context.getApplicationContext()).getDeviceId(), null);
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
            LogUtil.showLog("-------->", "处理业务逻辑");
            if (isAppForeground(context)) {
                Log.v("-------->", "在前台");
//                Toast.makeText(context, mToastInfo, Toast.LENGTH_LONG).show();
            } else {
                Log.v("-------->", "不在前台");
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

    public static boolean isAppForeground(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        LogUtil.showLog("-------->", "-------->" + (tasks == null ? "null" : tasks.size() + "--" + tasks.isEmpty()));
        if (tasks != null) {
            for (ActivityManager.RunningTaskInfo info : tasks) {
                LogUtil.showLog("-------->", "info  activity==" + info.topActivity.getClassName());
            }
        }
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 处理推送消息
     */
    private void handlePushMessage(Context context, String content) {
        Observable.just(content)
                .map((Func1<String, MiPushMsgObj>) s -> parseJsonObject(s, Object.class))
                .map(pushMsgObj -> {
                    MiPushMsgInfoObj msgInfoObj = new MiPushMsgInfoObj(pushMsgObj.getIdentifier());
                    switch (pushMsgObj.getIdentifier()) {

                        /*---------------------------成长圈PUSH消息类型---------------------------*/
                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_MEMBER: // 新成员加图（定位圈首页）
                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_TEACHER_AUTHORIZATION: // 管理员发起老师认证（定位到认证列表页面）
                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_JOIN_APPLY: // 新成员申请加入，给圈主push （定位到圈成员管理页）
                            // 仅携带circleId
                            MiPushMsgObj<Long> circleID = parseJsonObject(content, Long.class);
                            if (circleID != null && circleID.getInfo() > 0) {
                                msgInfoObj.setCircleId(circleID.getInfo());
                                return msgInfoObj;
                            }
                            break;

                        case MiPushConstant.PUSH_TYPE_CIRCLE_TEACHER_NEW_PRODUCTION: // 老师创建新作品（定位到该作品的预览页）
                        case MiPushConstant.PUSH_TYPE_CIRCLE_PRODUCTION_REFERENCED: // 发布的照片被别人引用做书并订单支付成功（定位到该作品的预览页）
                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_SCHOOL_BOOK: //每学期系统自动生成的家校纪念册 （定位到该作品的预览页）
                            // 携带开放平台的bookId、bookType
                            MiPushMsgObj<MiPushMsgInfoObj> production = parseJsonObject(content, MiPushMsgInfoObj.class);
                            if (production != null && production.getInfo() != null) {
                                msgInfoObj.setBookId(production.getInfo().getBookId());
                                msgInfoObj.setBookType(production.getInfo().getBookType());
                                return msgInfoObj;
                            }
                            break;

                        case MiPushConstant.PUSH_TYPE_CIRCLE_TEACHER_NEW_TIME_LINE: // 老师发布动态（定位到该条动态）
                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_COMMENTS: // 发布信息被评论（定位到该条动态）
                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_GOOD: // 发布信息被点赞（定位到该条动态）
                            // 携带开放平台的circleId、circleTimeId（圈时光id）
                            MiPushMsgObj<MiPushMsgInfoObj> circleTime = parseJsonObject(content, MiPushMsgInfoObj.class);
                            if (circleTime != null && circleTime.getInfo() != null) {
                                msgInfoObj.setCircleId(circleTime.getInfo().getCircleId());
                                msgInfoObj.setCircleTimeId(circleTime.getInfo().getCircleTimeId());
                                return msgInfoObj;
                            }
                            break;

                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_SCHOOL_TASK: // 老师发起新作业（定位到作业该详情页）
                            // 携带circleId、taskId（布置的作业的id）
                            MiPushMsgObj<MiPushMsgInfoObj> schoolTask = parseJsonObject(content, MiPushMsgInfoObj.class);
                            if (schoolTask != null && schoolTask.getInfo() != null) {
                                msgInfoObj.setCircleId(schoolTask.getInfo().getCircleId());
                                msgInfoObj.setTaskId(schoolTask.getInfo().getTaskId());
                                return msgInfoObj;
                            }
                            break;

                        case MiPushConstant.PUSH_TYPE_CIRCLE_MEMBER_REMOVED: // 被圈主移出（定位圈列表页）
                            // 不携带参数，点击直接定位到圈列表页并清空本地圈缓存数据
                            return msgInfoObj;

                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_TAGGED: // 发布的照片被别人加标签（定位到该图片的预览页）
                        case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_LIKED: // 发布的照片被别人加喜欢（定位到该图片的预览页）
                            // 携带circleId、mediaId
                            MiPushMsgObj<MiPushMsgInfoObj> circlePhoto = parseJsonObject(content, MiPushMsgInfoObj.class);
                            if (circlePhoto != null && circlePhoto.getInfo() != null) {
                                msgInfoObj.setCircleId(circlePhoto.getInfo().getCircleId());
                                msgInfoObj.setMediaId(circlePhoto.getInfo().getMediaId());
                                return msgInfoObj;
                            }
                            break;

                        case MiPushConstant.PUSH_TYPE_CIRCLE_HOMEWORK_COMMENTS: // 老师点评了宝宝的作业（定位到作业该详情页）
                            // 仅携带homeworkId
                            MiPushMsgObj<Long> homeworkID = parseJsonObject(content, Long.class);
                            if (homeworkID != null && homeworkID.getInfo() > 0) {
                                msgInfoObj.setHomeworkId(homeworkID.getInfo());
                                return msgInfoObj;
                            }
                            break;


                        /*---------------------------亲友圈PUSH消息类型---------------------------*/
                        case MiPushConstant.PUSH_TYPE_NEW_MEMBER:// 新成员加入（定位到亲友团页面）
                            // 不携带参数，点击直接跳转到亲友团页面
                            return msgInfoObj;
                        case MiPushConstant.PUSH_TYPE_TIME_GOOD:// 动态被赞（定位到该条动态）
                        case MiPushConstant.PUSH_TYPE_TIME_COMMENT:// 动态被评论（定位到该条动态）
                        case MiPushConstant.PUSH_TYPE_NEW_TIME_PUBLISH:// 发布新动态（定位到该条动态）
                            // 携带timeId、mediaType、mediaUrl
                            MiPushMsgObj<MiPushMsgInfoObj> timeObj = parseJsonObject(content, MiPushMsgInfoObj.class);
                            if (timeObj != null && timeObj.getInfo() != null) {
                                msgInfoObj.setTimeId(timeObj.getInfo().getTimeId());
                                msgInfoObj.setMediaType(timeObj.getInfo().getMediaType());
                                msgInfoObj.setMediaUrl(timeObj.getInfo().getMediaUrl());
                                return msgInfoObj;
                            }
                            break;

                    }

                    return null;
                })
                .compose(SchedulersCompat.applyComputationSchedulers())
                .subscribe(
                        msgInfoObj -> {
                            if (msgInfoObj != null) {
                                if (msgInfoObj.isKithPush()) {
                                    Log.d("-------->", "-------->亲友圈推送");
                                    // 亲友圈推送
                                    if (msgInfoObj.getType() == MiPushConstant.PUSH_TYPE_NEW_MEMBER) {
                                        KithFragment.openFromPush(context);
                                    } else {
                                        TimeFaceDetailFragment.openFromPush(context, msgInfoObj.getTimeId());
                                    }
                                } else {
                                    // 成长圈推送
                                    if (App.getInstance().getTopActivity() == null) {
                                        Log.d("-------->", "-------->getTopActivity is null");
                                        TabMainActivity.openCircleFromPush(context, msgInfoObj);
                                    } else {
                                        Log.d("-------->", "-------->getTopActivity not null");
                                        TabMainActivity.openCircle(App.getInstance().getTopActivity(), msgInfoObj);
                                    }
                                }
                            }
                        },
                        throwable -> {
                            Log.e("-------->", "handlePushMessage: ", throwable);
                        }
                );
    }

    /**
     * 处理透传消息
     */
    private void handlePassThroughMessage(String content) {
        Observable.just(content)
                .map((Func1<String, MiPushMsgObj>) s -> parseJsonObject(s, Object.class))
                .map(pushMsgObj -> {
                    CirclePassThroughMessageEvent ptMessageEvent = new CirclePassThroughMessageEvent(pushMsgObj.getIdentifier());
                    switch (pushMsgObj.getIdentifier()) {
                        case MiPushConstant.TYPE_UNREAD_MSG: // 未读消息
                            MiPushMsgObj<MiPushMsgChangeObj> unreadMsg = parseJsonObject(content, MiPushMsgChangeObj.class);

                            if (unreadMsg != null && unreadMsg.getInfo() != null) {
                                // 这里判断UserID是个坑，服务端不能按照DeviceID推送，后期需要优化
                                if (TextUtils.equals(unreadMsg.getInfo().getUserId(), FastData.getUserId())) {
                                    // 未读消息数量变化
                                    EventBus.getDefault().post(
                                            new UnreadMsgEvent(unreadMsg.getInfo().getUnReadMsgCount())
                                    );
                                }
                            }
                            break;
                        case MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED: // 删除成员
                            MiPushMsgObj<GrowthCircleObj> memberDeleted = parseJsonObject(content, GrowthCircleObj.class);
                            if (memberDeleted != null) {
                                ptMessageEvent.circleObj = memberDeleted.getInfo();
                                return ptMessageEvent;
                            }
                            break;
                        case MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION: // 新的教师认证
                            return ptMessageEvent;
                        case MiPushConstant.TYPE_CIRCLE_SCHOOL_TASK: // 新的作业
                            MiPushMsgObj<CircleSchoolTaskObj> schoolTask = parseJsonObject(content, CircleSchoolTaskObj.class);
                            if (schoolTask != null) {
                                ptMessageEvent.schoolTaskObj = schoolTask.getInfo();
                                return ptMessageEvent;
                            }
                            break;
                        case MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZED: // 成为老师身份
                        case MiPushConstant.TYPE_CIRCLE_TEACHER_UNAUTHORIZED: // 取消老师身份
                            MiPushMsgObj<CircleUserInfo> teacherAuthorize = parseJsonObject(content, CircleUserInfo.class);
                            if (teacherAuthorize != null && teacherAuthorize.getInfo() != null) {
                                ptMessageEvent.circleUserInfo = teacherAuthorize.getInfo();

                                // 推送的圈和用户本地缓存的圈一致才弹窗提示，否则不提示
                                if (ptMessageEvent.circleUserInfo.getCircleId() == FastData.getCircleId()) {
                                    return ptMessageEvent;
                                }
                            }
                            break;
                        case MiPushConstant.TYPE_CIRCLE_DISBANDED: // 圈子解散
                            MiPushMsgObj<GrowthCircleObj> disbanded = parseJsonObject(content, GrowthCircleObj.class);
                            if (disbanded != null) {
                                ptMessageEvent.circleObj = disbanded.getInfo();

                                return ptMessageEvent;
                            }
                            break;
                        case MiPushConstant.TYPE_CIRCLE_COMMIT_HOMEWORK: // 最近提交作业
                            MiPushMsgObj<CircleHomeworkObj> commitHomework = parseJsonObject(content, CircleHomeworkObj.class);
                            if (commitHomework != null) {
                                ptMessageEvent.homeworkObj = commitHomework.getInfo();

                                return ptMessageEvent;
                            }
                            break;
                    }

                    return null;
                })
                .compose(SchedulersCompat.applyComputationSchedulers())
                .subscribe(
                        event -> {
                            Log.d("MiPushMessageReceiver", "handlePassThroughMessage -> post event ? " + (event != null));
                            if (event != null) {
                                EventBus.getDefault().post(event);
                            }
                        },
                        throwable -> {
                            Log.e("MiPushMessageReceiver", "handlePassThroughMessage: ", throwable);
                        }
                );
    }


    private <T> MiPushMsgObj<T> parseJsonObject(String jsonString, Class<T> clazz) {
        Type type = TypeBuilder
                .newInstance(MiPushMsgObj.class)
                .addTypeParam(clazz)
                .build();
        MiPushMsgObj<T> obj = null;
        try {
            obj = new Gson().fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.e("MiPushMessageReceiver", "parseJsonObject: ", e);
        }
        return obj;
    }
}
