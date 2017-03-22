package cn.timeface.circle.baby.ui.circle.events;

import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;

/**
 * 选择时光event
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectTimeLineEvent {
    private boolean select;
    private CircleTimeLineExObj timeLineExObj;

    public CircleSelectTimeLineEvent(boolean select, CircleTimeLineExObj timeLineObj) {
        this.select = select;
        this.timeLineExObj = timeLineObj;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public CircleTimeLineExObj getTimeLineExObj() {
        return timeLineExObj;
    }

    public void setTimeLineExObj(CircleTimeLineExObj timeLineExObj) {
        this.timeLineExObj = timeLineExObj;
    }
}
