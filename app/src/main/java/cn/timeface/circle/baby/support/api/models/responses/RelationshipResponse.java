package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.Relationship;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class RelationshipResponse extends BaseResponse {

    List<Relationship> dataList;

    public List<Relationship> getDataList() {
        return dataList;
    }

    public void setDataList(List<Relationship> dataList) {
        this.dataList = dataList;
    }
}
