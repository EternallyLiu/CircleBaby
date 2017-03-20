package cn.timeface.circle.baby.ui.circle.timelines.bean;

/**
 * author : wangshuai Created on 2017/3/15
 * email : wangs1992321@gmail.com
 */
public class ItemObj {

    private int type;
    private Object item;

    public ItemObj() {
    }

    public ItemObj(int type, Object item) {

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
