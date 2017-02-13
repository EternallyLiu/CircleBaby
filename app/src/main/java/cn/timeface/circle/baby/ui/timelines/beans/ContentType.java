package cn.timeface.circle.baby.ui.timelines.beans;

/**
 * author : wangshuai Created on 2017/2/13
 * email : wangs1992321@gmail.com
 */
public class ContentType {
    private int type;
    private Object item;

    public ContentType(int type, Object item) {
        this.type = type;
        this.item = item;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }
}
