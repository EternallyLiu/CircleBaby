package cn.timeface.circle.baby.ui.calendar.bean;


import cn.timeface.circle.baby.support.api.models.objs.BookObj;

/**
 * 记事本
 *
 * Created by JieGuo on 16/11/25.
 */

public class NotebookExtObj extends BookObj {

    private long extraId;
    private String extra;

    public long getExtraId() {
        return extraId;
    }

    public void setExtraId(long extraId) {
        this.extraId = extraId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
