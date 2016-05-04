package cn.timeface.circle.baby.api.models.responses;

import cn.timeface.circle.baby.api.models.base.BaseResponse;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class RelationIdResponse extends BaseResponse {
    int relationId;

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }
}
