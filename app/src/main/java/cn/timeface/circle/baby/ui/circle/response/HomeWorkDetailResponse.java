package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskDetailObj;

/**
 * 老师布置作业详情列表response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class HomeWorkDetailResponse extends BaseResponse {
    private CircleSchoolTaskDetailObj schoolTaskDetailObj;

    public CircleSchoolTaskDetailObj getSchoolTaskDetailObj() {
        return schoolTaskDetailObj;
    }

    public void setSchoolTaskDetailObj(CircleSchoolTaskDetailObj schoolTaskDetailObj) {
        this.schoolTaskDetailObj = schoolTaskDetailObj;
    }
}
