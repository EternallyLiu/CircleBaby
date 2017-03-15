package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 成长圈对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GrowthCircleObj extends BaseObj implements Parcelable {
    protected String QRcodeUrl;       //二维码图片地址
    protected String cicleCoverUrl;   //圈封面地址
    protected String circleName;      //圈名称
    protected long createDate;        //创建时间
    protected int joinType;           //0 - 我创建的圈 1 - 我加入的圈
    protected int mediaCount;         //圈中图片的数量
    protected int memberCount;        //圈中成员的数量
    protected int openLever;          //0 - 私有圈 1- 公有圈
    protected int workCount;          //圈中书的数量
    protected long circleId;          //圈id
    protected long circleNumber;      //圈号

    public GrowthCircleObj() {
    }

    protected GrowthCircleObj(Parcel in) {
        super(in);
        QRcodeUrl = in.readString();
        cicleCoverUrl = in.readString();
        circleName = in.readString();
        createDate = in.readLong();
        joinType = in.readInt();
        mediaCount = in.readInt();
        memberCount = in.readInt();
        openLever = in.readInt();
        workCount = in.readInt();
        circleId = in.readLong();
        circleNumber = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(QRcodeUrl);
        dest.writeString(cicleCoverUrl);
        dest.writeString(circleName);
        dest.writeLong(createDate);
        dest.writeInt(joinType);
        dest.writeInt(mediaCount);
        dest.writeInt(memberCount);
        dest.writeInt(openLever);
        dest.writeInt(workCount);
        dest.writeLong(circleId);
        dest.writeLong(circleNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GrowthCircleObj> CREATOR = new Creator<GrowthCircleObj>() {
        @Override
        public GrowthCircleObj createFromParcel(Parcel in) {
            return new GrowthCircleObj(in);
        }

        @Override
        public GrowthCircleObj[] newArray(int size) {
            return new GrowthCircleObj[size];
        }
    };

    public long getCircleId() {
        return circleId;
    }

    public void setCircleId(long circleId) {
        this.circleId = circleId;
    }

    public long getCircleNumber() {
        return circleNumber;
    }

    public void setCircleNumber(long circleNumber) {
        this.circleNumber = circleNumber;
    }

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

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
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
}
