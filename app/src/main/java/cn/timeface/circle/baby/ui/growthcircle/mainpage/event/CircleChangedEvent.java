package cn.timeface.circle.baby.ui.growthcircle.mainpage.event;

/**
 * 圈创建、加入、退出、解散、圈资料变更事件
 */
public class CircleChangedEvent {

    public static final int TYPE_CREATED = 1;//圈创建
    public static final int TYPE_JOINED = 2;//圈加入
    public static final int TYPE_QUIT = 3;//圈退出
    public static final int TYPE_DISBANDED = 4;//圈解散
    public static final int TYPE_INFO_CHANGED = 5;//圈资料变更

    public int type; // 事件类型
    public long circleId; // 创建好的圈子的圈号

    public CircleChangedEvent(int type) {
        this.type = type;
    }

    public CircleChangedEvent(long circleId, int type) {
        this.circleId = circleId;
        this.type = type;
    }

}
