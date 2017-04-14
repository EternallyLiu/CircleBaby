package cn.timeface.circle.baby.ui.growthcircle.mainpage.mipush;

import android.content.Context;
import android.util.Log;

import com.wechat.photopicker.fragment.BigImageFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.constants.MiPushConstant;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.objs.MiPushMsgInfoObj;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.timelines.activity.CircleTimeLineDetailActivitiy;
import cn.timeface.circle.baby.ui.circle.timelines.activity.HomeWorkActivity;
import cn.timeface.circle.baby.ui.circle.timelines.activity.SchoolTaskDetailActivity;
import cn.timeface.circle.baby.ui.circle.timelines.activity.TeacherAuthoActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.CircleMainActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.CircleAlertDialog;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleChangedEvent;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CirclePassThroughMessageCallbackEvent;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CirclePassThroughMessageEvent;

/**
 * 透传消息处理（用于处理用户不在圈浏览时，弹出提示框）
 * 推送消息处理（用于处理用户点击推送通知，跳转到相应界面）
 */
public class TabMainPushHandler implements IEventBus {

    protected final String TAG = this.getClass().getSimpleName();

    private ApiService apiService = ApiFactory.getApi().getApiService();

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        EventBus.getDefault().unregister(this);
    }

    /*-------------------------------------------透传消息处理-------------------------------------------*/
    @Subscribe(priority = 1, threadMode = ThreadMode.POSTING)
    public void onEvent(CirclePassThroughMessageEvent event) {
        Log.i("-------->", "-------->CirclePushMessageEvent--> TabMainActivity");
        if (event.type == MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED
                || event.type == MiPushConstant.TYPE_CIRCLE_DISBANDED) {
            // 圈解散、被圈主移除
            // 此处收到事件表示用户不在圈浏览过程中，所以仅仅弹出提示框

            // 先刷新圈列表数据
            EventBus.getDefault().post(new CircleChangedEvent(
                    event.type == MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED ?
                            CircleChangedEvent.TYPE_QUIT :
                            CircleChangedEvent.TYPE_DISBANDED
            ));

            Context context = App.getInstance().getTopActivity();
            if (context != null && event.circleObj != null) {
                CircleAlertDialog dialog = new CircleAlertDialog(context);
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

                    // 刷新圈列表数据
                    EventBus.getDefault().post(
                            new CircleChangedEvent(event.circleObj.getCircleId(),
                                    event.type == MiPushConstant.TYPE_CIRCLE_MEMBER_REMOVED ?
                                            CircleChangedEvent.TYPE_QUIT :
                                            CircleChangedEvent.TYPE_DISBANDED
                            )
                    );
                });
                dialog.show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(CirclePassThroughMessageCallbackEvent event) {
        Log.i("-------->", "-------->CirclePushMessageCallbackEvent--> TabMainActivity");
        switch (event.type) {
            case CirclePassThroughMessageCallbackEvent.TYPE_RECEIVED_CIRCLE_DISBAND:
            case CirclePassThroughMessageCallbackEvent.TYPE_RECEIVED_MEMBER_REMOVED:
                receivedCircleStatusChanged(event.circleId, event.type);
                break;
            case CirclePassThroughMessageCallbackEvent.TYPE_RECEIVED_TEACHER_CHANGED:
                receivedTeacherChanged(event.circleId);
                break;
        }
    }

    private void receivedCircleStatusChanged(long circleId, int type) {
        apiService.receivedCircleStatusChanged(circleId, type)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                            Log.d(TAG, "receivedCircleStatusChanged: " + response.info);
                        },
                        throwable -> {
                            Log.e(TAG, "receivedCircleStatusChanged: ", throwable);
                        }
                );
    }

    private void receivedTeacherChanged(long circleId) {
        apiService.receivedTeacherChanged(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                            Log.d(TAG, "receivedTeacherChanged: " + response.info);
                        },
                        throwable -> {
                            Log.e(TAG, "receivedTeacherChanged: ", throwable);
                        }
                );
    }
    /*-------------------------------------------透传消息处理-------------------------------------------*/


    /*-------------------------------------------推送消息处理-------------------------------------------*/
    public void handleCirclePushMessage(MiPushMsgInfoObj pushMsgInfo) {
        if (pushMsgInfo.getCircleId() > 0 &&
                FastData.getCircleId() != pushMsgInfo.getCircleId()) {
            // 当前圈子未缓存，先获取最新圈数据
            reqCircleInfo(pushMsgInfo);
        } else if (pushMsgInfo.getType() == MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_LIKED
                || pushMsgInfo.getType() == MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_TAGGED) {
            reqCirclePhotoDetail(pushMsgInfo);
        } else {
            dispatchCirclePushMessage(pushMsgInfo);
        }
    }

    private void dispatchCirclePushMessage(MiPushMsgInfoObj pushMsgInfo) {
        Context context = App.getInstance().getTopActivity();
        if (context == null) return;

        if (context instanceof TabMainActivity) {
            ((TabMainActivity) context).setClearCircleCacheFlag(true);
        }
        switch (pushMsgInfo.getType()) {
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_MEMBER: // 新成员加图（定位圈首页）
                // 仅携带circleId
                CircleMainActivity.open(context);
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_TEACHER_AUTHORIZATION: // 管理员发起老师认证（定位到认证列表页面）
                // 仅携带circleId
                TeacherAuthoActivity.open(context, pushMsgInfo.getCircleId());
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_TEACHER_NEW_PRODUCTION: // 老师创建新作品（定位到该作品的预览页）
            case MiPushConstant.PUSH_TYPE_CIRCLE_PRODUCTION_REFERENCED: // 发布的照片被别人引用做书并订单支付成功（定位到该作品的预览页）
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_SCHOOL_BOOK: //每学期系统自动生成的家校纪念册 （定位到该作品的预览页）
                // 携带开放平台的bookId、bookType
                MyPODActivity.open(context, pushMsgInfo.getBookId(), pushMsgInfo.getBookType());
                break;

            case MiPushConstant.PUSH_TYPE_CIRCLE_TEACHER_NEW_TIME_LINE: // 老师发布动态（定位到该条动态）
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_COMMENTS: // 发布信息被评论（定位到该条动态）
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_GOOD: // 发布信息被点赞（定位到该条动态）
                // 携带开放平台的circleId、circleTimeId（圈时光id）
                CircleTimeLineDetailActivitiy.open(context, pushMsgInfo.getCircleTimeId());
                break;

            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_SCHOOL_TASK: // 老师发起新作业（定位到作业该详情页）
                // 携带circleId、taskId（布置的作业的id）
                SchoolTaskDetailActivity.open(context, pushMsgInfo.getTaskId());
                break;

            case MiPushConstant.PUSH_TYPE_CIRCLE_MEMBER_REMOVED: // 被圈主移出（定位圈列表页）
                // 不携带参数，点击直接定位到圈列表页并清空本地圈缓存数据
                TabMainActivity.openClearTop(context);
                break;

            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_TAGGED: // 发布的照片被别人加标签（定位到该图片的预览页）
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_LIKED: // 发布的照片被别人加喜欢（定位到该图片的预览页）
                // 仅携带图片id

                break;

            case MiPushConstant.PUSH_TYPE_CIRCLE_HOMEWORK_COMMENTS: // 老师点评了宝宝的作业（定位到作业该详情页）
                // 仅携带homeworkId
                HomeWorkActivity.open(context, pushMsgInfo.getHomeworkId());
                break;
        }
    }

    private void reqCircleInfo(MiPushMsgInfoObj pushMsgInfo) {
        apiService.queryCircleIndexInfo(pushMsgInfo.getCircleId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if (response.success()) {
//                                FastData.clearCircleData();
                                FastData.setGrowthCircleObj(response.getGrowthCircle());
                                FastData.setCircleUserInfo(response.getUserInfo());
                                dispatchCirclePushMessage(pushMsgInfo);
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "reqCircleInfo: ", throwable);
                        }
                );
    }

    private void reqCirclePhotoDetail(MiPushMsgInfoObj pushMsgInfo) {
        apiService.queryCirclePhotoById(pushMsgInfo.getPhotoId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if (response.success()) {
                                // 发布的照片被别人加标签（定位到该图片的预览页）
                                // 发布的照片被别人加喜欢（定位到该图片的预览页）

                                Context context = App.getInstance().getTopActivity();
                                if (context == null) return;

                                if (context instanceof TabMainActivity) {
                                    ((TabMainActivity) context).setClearCircleCacheFlag(true);
                                }
                                BigImageFragment.open(context, response.getCircleMedia(),
                                        BigImageFragment.CIRCLE_MEDIA_IMAGE_EDITOR, true, false);
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "reqCircleInfo: ", throwable);
                        }
                );
    }
    /*-------------------------------------------推送消息处理-------------------------------------------*/

}
