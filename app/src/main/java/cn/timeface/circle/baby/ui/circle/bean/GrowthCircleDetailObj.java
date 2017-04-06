package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 成长圈详情对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GrowthCircleDetailObj extends GrowthCircleObj implements Parcelable {
    protected String circleDescription;           //圈描述
    protected int mediaAchievement;               //图片超过比例
    protected List<MemberDataObj> memberList;
    protected String rule;                        //圈规则
    protected int wrokAchievement;                //作品超过比例

    public GrowthCircleDetailObj() {
    }

    public GrowthCircleDetailObj(String circleDescription, int mediaAchievement, List<MemberDataObj> memberList, String rule, int wrokAchievement) {
        this.circleDescription = circleDescription;
        this.mediaAchievement = mediaAchievement;
        this.memberList = memberList;
        this.rule = rule;
        this.wrokAchievement = wrokAchievement;
    }

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

    public List<MemberDataObj> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<MemberDataObj> memberList) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.circleDescription);
        dest.writeInt(this.mediaAchievement);
        dest.writeList(this.memberList);
        dest.writeString(this.rule);
        dest.writeInt(this.wrokAchievement);
    }

    protected GrowthCircleDetailObj(Parcel in) {
        super(in);
        this.circleDescription = in.readString();
        this.mediaAchievement = in.readInt();
        this.memberList = new ArrayList<MemberDataObj>();
        in.readList(this.memberList, MemberDataObj.class.getClassLoader());
        this.rule = in.readString();
        this.wrokAchievement = in.readInt();
    }

    public static final Creator<GrowthCircleDetailObj> CREATOR = new Creator<GrowthCircleDetailObj>() {
        @Override
        public GrowthCircleDetailObj createFromParcel(Parcel source) {
            return new GrowthCircleDetailObj(source);
        }

        @Override
        public GrowthCircleDetailObj[] newArray(int size) {
            return new GrowthCircleDetailObj[size];
        }
    };
}
