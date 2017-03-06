package cn.timeface.circle.baby.ui.growth.events;

import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;

/**
 * 选择时光event
 * author : YW.SUN Created on 2017/2/21
 * email : sunyw10@gmail.com
 */
public class SelectTimeLineEvent {
    private boolean select;
    private TimeLineObj timeLineObj;

    public SelectTimeLineEvent(boolean select, TimeLineObj timeLineObj) {
        this.select = select;
        this.timeLineObj = timeLineObj;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public TimeLineObj getTimeLineObj() {
        return timeLineObj;
    }

    public void setTimeLineObj(TimeLineObj timeLineObj) {
        this.timeLineObj = timeLineObj;
    }
}
