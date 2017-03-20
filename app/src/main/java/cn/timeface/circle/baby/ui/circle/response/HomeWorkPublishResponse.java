package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;

/**
 * 发布作业response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class HomeWorkPublishResponse extends BaseResponse {
    private CircleSchoolTaskObj schoolTask;

    public CircleSchoolTaskObj getCircleTimeline() {
        return schoolTask;
    }

    public void setCircleTimeline(CircleSchoolTaskObj schoolTask) {
        this.schoolTask = schoolTask;
    }
}
