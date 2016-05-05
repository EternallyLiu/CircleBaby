package cn.timeface.circle.baby.api.models.objs;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class Relationship extends BaseObj {
    String relationName;
    int relationId;

    public Relationship(String relationName, int relationId) {
        this.relationName = relationName;
        this.relationId = relationId;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }
}
