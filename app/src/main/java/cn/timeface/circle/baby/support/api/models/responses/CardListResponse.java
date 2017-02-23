package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;

/**
 * card list
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CardListResponse extends BaseResponse {
    List<KnowledgeCardObj> dataList;

    public List<KnowledgeCardObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<KnowledgeCardObj> dataList) {
        this.dataList = dataList;
    }
}
