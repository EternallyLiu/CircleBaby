package cn.timeface.circle.baby.events;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class PublishRefreshEvent {
    public List<KnowledgeCardObj> dataList;

    public PublishRefreshEvent(List<KnowledgeCardObj> dataList) {
        this.dataList = dataList;
    }

    public List<KnowledgeCardObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<KnowledgeCardObj> dataList) {
        this.dataList = dataList;
    }
}
