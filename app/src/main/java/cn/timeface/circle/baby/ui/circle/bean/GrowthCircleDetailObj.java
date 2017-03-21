package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * 成长圈详情对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GrowthCircleDetailObj extends GrowthCircleObj implements Parcelable {
    protected String circleDescription;           //圈描述
    protected int mediaAchievement;               //图片超过比例
    protected List<CircleUserInfo> memberList;    //圈中用户列表 (不超过10个)
    protected String rule;                        //圈规则
    protected int wrokAchievement;                //作品超过比例

    public GrowthCircleDetailObj() {
    }

    protected GrowthCircleDetailObj(Parcel in) {
        super(in);
        circleDescription = in.readString();
        mediaAchievement = in.readInt();
        memberList = in.createTypedArrayList(CircleUserInfo.CREATOR);
        rule = in.readString();
        wrokAchievement = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(circleDescription);
        dest.writeInt(mediaAchievement);
        dest.writeTypedList(memberList);
        dest.writeString(rule);
        dest.writeInt(wrokAchievement);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GrowthCircleDetailObj> CREATOR = new Creator<GrowthCircleDetailObj>() {
        @Override
        public GrowthCircleDetailObj createFromParcel(Parcel in) {
            return new GrowthCircleDetailObj(in);
        }

        @Override
        public GrowthCircleDetailObj[] newArray(int size) {
            return new GrowthCircleDetailObj[size];
        }
    };

    public String getCircleDescription() {
        return circleDescription;
    }

    public void setCircleDescription(String circleDescription) {
        this.circleDescription = circleDescription;
    }

    public int getMediaAchievement() {
        return mediaAchievement;
    }

    public void setMediaAchievement(int mediaAchievement) {
        this.mediaAchievement = mediaAchievement;
    }

    public List<CircleUserInfo> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<CircleUserInfo> memberList) {
        this.memberList = memberList;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public int getWrokAchievement() {
        return wrokAchievement;
    }

    public void setWrokAchievement(int wrokAchievement) {
        this.wrokAchievement = wrokAchievement;
    }
}