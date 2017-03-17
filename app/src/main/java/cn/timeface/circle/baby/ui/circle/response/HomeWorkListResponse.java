package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.bean.HomeWorkListObj;

/**
 * 获取圈作业列表response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class HomeWorkListResponse extends BaseResponse {
    private List<HomeWorkListObj> dataList;
    private GrowthCircleObj growthCircle;             //圈对象
    private int hasTeacherCertification;              //是否有老师认证消息 1- 有 0 - 无
    private CircleHomeworkObj lastSubmitHomework;     //最新提交的作业

    public List<HomeWorkListObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<HomeWorkListObj> dataList) {
        this.dataList = dataList;
    }

    public GrowthCircleObj getGrowthCircle() {
        return growthCircle;
    }

    public void setGrowthCircle(GrowthCircleObj growthCircle) {
        this.growthCircle = growthCircle;
    }

    public int getHasTeacherCertification() {
        return hasTeacherCertification;
    }

    public void setHasTeacherCertification(int hasTeacherCertification) {
        this.hasTeacherCertification = hasTeacherCertification;
    }

    public CircleHomeworkObj getLastSubmitHomework() {
        return lastSubmitHomework;
    }

    public void setLastSubmitHomework(CircleHomeworkObj lastSubmitHomework) {
        this.lastSubmitHomework = lastSubmitHomework;
    }
}
