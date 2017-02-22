package cn.timeface.circle.baby.ui.calendar.bean;


import cn.timeface.circle.baby.support.api.models.objs.BookObj;

/**
 * 接开放平台以后的台历类型就是这玩意
 *
 * Created by JieGuo on 16/10/19.
 */

public class CalendarBookObj extends BookObj {

    /**
     * 特殊用的ID
     * 扩展用,  为什么要用这个,请别问,说多了都是泪。  本来这个是可以没有的
     */
    private long extraId;

    public long getExtraId() {
        return extraId;
    }

    public void setExtraId(long extraId) {
        this.extraId = extraId;
    }
}
