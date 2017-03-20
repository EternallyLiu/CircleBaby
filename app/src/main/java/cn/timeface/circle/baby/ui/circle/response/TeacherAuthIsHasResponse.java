package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * 查询是否有新的教师认证消息response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class TeacherAuthIsHasResponse extends BaseResponse {
    private int have;   //0 - 没有 1 - 有

    public int getHave() {
        return have;
    }

    public void setHave(int have) {
        this.have = have;
    }
}
