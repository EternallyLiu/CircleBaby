package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;

/**
 * 家长提交作业的详情response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class HomeWorkSubmitResponse extends BaseResponse {
    private CircleHomeworkObj homework;

    public CircleHomeworkObj getCircleTimeline() {
        return homework;
    }

    public void setCircleTimeline(CircleHomeworkObj homework) {
        this.homework = homework;
    }
}
