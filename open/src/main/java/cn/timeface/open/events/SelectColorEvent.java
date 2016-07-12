package cn.timeface.open.events;

/**
 * Created by zhsheng on 2016/7/11.
 */
public class SelectColorEvent {
    public SelectColorEvent(String color) {
        this.color = color;
    }

    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
