package cn.timeface.circle.baby.ui.growthcircle.mainpage.event;

/**
 * 收到圈子透传消息后 回调服务端接口改为已读状态
 */
public class CirclePassThroughMessageCallbackEvent {

    public static final int TYPE_RECEIVED_TEACHER_CHANGED = 1; // 确认收到老师身份变化消息
    public static final int TYPE_RECEIVED_MEMBER_REMOVED = 2; // 确认收到被移除消息
    public static final int TYPE_RECEIVED_CIRCLE_DISBAND = 3; // 确认收到圈子被解散消息

    public int type;
    public long circleId; // 圈Id

    public CirclePassThroughMessageCallbackEvent(int type, long circleId) {
        this.type = type;
        this.circleId = circleId;
    }
}
