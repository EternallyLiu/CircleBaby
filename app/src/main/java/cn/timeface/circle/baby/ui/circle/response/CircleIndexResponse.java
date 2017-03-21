package cn.timeface.circle.baby.ui.circle.response;

import android.os.Parcel;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;

/**
 * 圈首页response
 */
public class CircleIndexResponse extends BaseResponse {

    private List<CircleTimelineObj> dataList;
    private GrowthCircleObj growthCircle;
    private int hasRelate; // 0否1是用户第一次进入圈子的时候，圈中有无关联自己宝宝的照片
    private CircleSchoolTaskObj lastSchoolTask; // 最新一条老师布置的作业
    private int relateMediaCount; // 被圈中的照片数量
    private CircleUserInfo userInfo; // 当前用户在圈中的信息

    public List<CircleTimelineObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleTimelineObj> dataList) {
        this.dataList = dataList;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.dataList);
        dest.writeParcelable(this.growthCircle, flags);
        dest.writeInt(this.hasRelate);
        dest.writeParcelable(this.lastSchoolTask, flags);
        dest.writeInt(this.relateMediaCount);
        dest.writeParcelable(this.userInfo, flags);
    }

    public CircleIndexResponse() {
    }

    protected CircleIndexResponse(Parcel in) {
        this.dataList = in.createTypedArrayList(CircleTimelineObj.CREATOR);
        this.growthCircle = in.readParcelable(GrowthCircleObj.class.getClassLoader());
        this.hasRelate = in.readInt();
        this.lastSchoolTask = in.readParcelable(CircleSchoolTaskObj.class.getClassLoader());
        this.relateMediaCount = in.readInt();
        this.userInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    public static final Creator<CircleIndexResponse> CREATOR = new Creator<CircleIndexResponse>() {
        @Override
        public CircleIndexResponse createFromParcel(Parcel source) {
            return new CircleIndexResponse(source);
        }

        @Override
        public CircleIndexResponse[] newArray(int size) {
            return new CircleIndexResponse[size];
        }
    };
}
