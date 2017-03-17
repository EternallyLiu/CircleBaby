package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleTimeObj;

/**
 * 圈首页response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleIndexResponse extends BaseResponse {
    private List<CircleTimelineObj> dataList;
    private GrowthCircleObj growthCircle;           //圈对象
    private int hasRelate;                          //0否1是用户第一次进入圈子的时候，圈中有无关联自己宝宝的照片
    private CircleSchoolTaskObj lastSchoolTask;     //最新一条老师布置的作业
    private int relateMediaCount;                   //被圈中的照片数量
    private CircleUserInfo userInfo;                //当前用户在圈中的信息


    public GrowthCircleObj getGrowthCircle() {
        return growthCircle;
    }

    public void setGrowthCircle(GrowthCircleObj growthCircle) {
        this.growthCircle = growthCircle;
    }

    public int getHasRelate() {
        return hasRelate;
    }

    public void setHasRelate(int hasRelate) {
        this.hasRelate = hasRelate;
    }

    public CircleSchoolTaskObj getLastSchoolTask() {
        return lastSchoolTask;
    }

    public void setLastSchoolTask(CircleSchoolTaskObj lastSchoolTask) {
        this.lastSchoolTask = lastSchoolTask;
    }

    public int getRelateMediaCount() {
        return relateMediaCount;
    }

    public void setRelateMediaCount(int relateMediaCount) {
        this.relateMediaCount = relateMediaCount;
    }

    public CircleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CircleUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<CircleTimelineObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleTimelineObj> dataList) {
        this.dataList = dataList;
    }
}
