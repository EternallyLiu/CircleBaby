package cn.timeface.circle.baby.ui.growthcircle.mainpage.event;

/**
 * 选中推荐圈封面
 */
public class CircleCoverSelectedEvent {

    public String coverUrl; // 选中的圈封面

    public CircleCoverSelectedEvent() {
    }

    public CircleCoverSelectedEvent(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
