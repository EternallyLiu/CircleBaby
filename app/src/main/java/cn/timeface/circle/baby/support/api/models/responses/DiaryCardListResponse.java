package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.DiaryCardObj;

/**
 * 日记卡片 list response
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class DiaryCardListResponse {
    private List<DiaryCardObj> dataList;

    public List<DiaryCardObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<DiaryCardObj> dataList) {
        this.dataList = dataList;
    }
}
