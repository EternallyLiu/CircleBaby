package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;

/**
 * 识图卡片合成response
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class KnowledgeComposedResponse extends BaseResponse {
    private KnowledgeCardObj knowledgeCardObj;

    public KnowledgeCardObj getKnowledgeCardObj() {
        return knowledgeCardObj;
    }

    public void setKnowledgeCardObj(KnowledgeCardObj knowledgeCardObj) {
        this.knowledgeCardObj = knowledgeCardObj;
    }
}
