package cn.timeface.circle.baby.events;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class PublishRefreshEvent {
    public List<MediaObj> dataList;

    public PublishRefreshEvent(List<MediaObj> dataList) {
        this.dataList = dataList;
    }

    public List<MediaObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MediaObj> dataList) {
        this.dataList = dataList;
    }
}
