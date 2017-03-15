package cn.timeface.circle.baby.ui.growthCircles.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : wangshuai Created on 2017/3/15
 * email : wangs1992321@gmail.com
 */
public class GrowthCircleObj implements Parcelable {
    private String QRcodeUrl;
    private String cicleCoverUrl;
    private long circleId;
    private String circleName;
    private long circleNumber;
    private long createDate;
    private int joinType;
    private int mediaCount;
    private int memberCount;
    private int openLever;
    private int workCount;

    public String getQRcodeUrl() {
        return QRcodeUrl;
    }

    public void setQRcodeUrl(String QRcodeUrl) {
        this.QRcodeUrl = QRcodeUrl;
    }

    public String getCicleCoverUrl() {
        return cicleCoverUrl;
    }

    public void setCicleCoverUrl(String cicleCoverUrl) {
        this.cicleCoverUrl = cicleCoverUrl;
    }

    public long getCircleId() {
        return circleId;
    }

    public void setCircleId(long circleId) {
        this.circleId = circleId;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public long getCircleNumber() {
        return circleNumber;
    }

    public void setCircleNumber(long circleNumber) {
        this.circleNumber = circleNumber;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getJoinType() {
        return joinType;
    }

    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getOpenLever() {
        return openLever;
    }

    public void setOpenLever(int openLever) {
        this.openLever = openLever;
    }

    public int getWorkCount() {
        return workCount;
    }

    public void setWorkCount(int workCount) {
        this.workCount = workCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.QRcodeUrl);
        dest.writeString(this.cicleCoverUrl);
        dest.writeLong(this.circleId);
        dest.writeString(this.circleName);
        dest.writeLong(this.circleNumber);
        dest.writeLong(this.createDate);
        dest.writeInt(this.joinType);
        dest.writeInt(this.mediaCount);
        dest.writeInt(this.memberCount);
        dest.writeInt(this.openLever);
        dest.writeInt(this.workCount);
    }

    public GrowthCircleObj() {
    }

    protected GrowthCircleObj(Parcel in) {
        this.QRcodeUrl = in.readString();
        this.cicleCoverUrl = in.readString();
        this.circleId = in.readLong();
        this.circleName = in.readString();
        this.circleNumber = in.readLong();
        this.createDate = in.readLong();
        this.joinType = in.readInt();
        this.mediaCount = in.readInt();
        this.memberCount = in.readInt();
        this.openLever = in.readInt();
        this.workCount = in.readInt();
    }

    public static final Parcelable.Creator<GrowthCircleObj> CREATOR = new Parcelable.Creator<GrowthCircleObj>() {
        @Override
        public GrowthCircleObj createFromParcel(Parcel source) {
            return new GrowthCircleObj(source);
        }

        @Override
        public GrowthCircleObj[] newArray(int size) {
            return new GrowthCircleObj[size];
        }
    };
}
