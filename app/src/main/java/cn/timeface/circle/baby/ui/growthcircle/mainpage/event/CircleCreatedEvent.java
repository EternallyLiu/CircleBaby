package cn.timeface.circle.baby.ui.growthcircle.mainpage.event;

/**
 * 创建圈成功
 */
public class CircleCreatedEvent {

    public long circleId;//创建好的圈子的圈号

    public CircleCreatedEvent(long circleId) {
        this.circleId = circleId;
    }
}
