package cn.timeface.circle.baby.ui.circle.response;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;

/**
 * 圈首页信息response
 */
public class CircleIndexInfoResponse extends BaseResponse {

    private GrowthCircleObj growthCircle;
    private int hasRelate; // 0否1是用户第一次进入圈子的时候，圈中有无关联自己宝宝的照片
    private CircleSchoolTaskObj lastSchoolTask; // 最新一条老师布置的作业
    private int relateMediaCount; // 被圈中的照片数量
    private CircleUserInfo userInfo; // 当前用户在圈中的信息

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleIndexInfoResponse that = (CircleIndexInfoResponse) o;
        if (growthCircle != null && that.getGrowthCircle() != null && getUserInfo() != null && that.getUserInfo() != null && growthCircle.getCircleId() == that.getGrowthCircle().getCircleId() && getUserInfo().getCircleUserId() == that.getUserInfo().getCircleUserId())
            return true;
        else return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.growthCircle, flags);
        dest.writeInt(this.hasRelate);
        dest.writeParcelable(this.lastSchoolTask, flags);
        dest.writeInt(this.relateMediaCount);
        dest.writeParcelable(this.userInfo, flags);
    }

    public CircleIndexInfoResponse() {
    }

    protected CircleIndexInfoResponse(Parcel in) {
        this.growthCircle = in.readParcelable(GrowthCircleObj.class.getClassLoader());
        this.hasRelate = in.readInt();
        this.lastSchoolTask = in.readParcelable(CircleSchoolTaskObj.class.getClassLoader());
        this.relateMediaCount = in.readInt();
        this.userInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    public static final Creator<CircleIndexInfoResponse> CREATOR = new Creator<CircleIndexInfoResponse>() {
        @Override
        public CircleIndexInfoResponse createFromParcel(Parcel source) {
            return new CircleIndexInfoResponse(source);
        }

        @Override
        public CircleIndexInfoResponse[] newArray(int size) {
            return new CircleIndexInfoResponse[size];
        }
    };
}
