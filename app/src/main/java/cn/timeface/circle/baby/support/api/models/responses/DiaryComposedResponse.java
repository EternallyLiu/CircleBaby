package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.DiaryCardObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 日记卡片合成response
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class DiaryComposedResponse extends BaseResponse {

    private DiaryCardObj diaryCardObj;

    public DiaryCardObj getDiaryCardObj() {
        return diaryCardObj;
    }

    public void setDiaryCardObj(DiaryCardObj diaryCardObj) {
        this.diaryCardObj = diaryCardObj;
    }
}
