package cn.timeface.circle.baby.ui.growth.events;

import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;

/**
 * 选择时光详情图片全选/不选event
 * author : YW.SUN Created on 2017/2/21
 * email : sunyw10@gmail.com
 */
public class ServerTimePhotoAllSelectEvent {
    private TimeLineObj timeLineObj;
    private boolean allSelect;
    private boolean timeLineSelect;

    public ServerTimePhotoAllSelectEvent(TimeLineObj timeLineObj, boolean allSelect) {
        this.timeLineObj = timeLineObj;
        this.allSelect = allSelect;
    }

    public ServerTimePhotoAllSelectEvent(TimeLineObj timeLineObj, boolean allSelect, boolean timeLineSelect) {
        this.timeLineObj = timeLineObj;
        this.allSelect = allSelect;
        this.timeLineSelect = timeLineSelect;
    }

    public TimeLineObj getTimeLineObj() {
        return timeLineObj;
    }

    public void setTimeLineObj(TimeLineObj timeLineObj) {
        this.timeLineObj = timeLineObj;
    }

    public boolean getAllSelect() {
        return allSelect;
    }

    public void setAllSelect(boolean allSelect) {
        this.allSelect = allSelect;
    }

    public boolean getTimeLineSelect() {
        return timeLineSelect;
    }

    public void setTimeLineSelect(boolean timeLineSelect) {
        this.timeLineSelect = timeLineSelect;
    }
}
