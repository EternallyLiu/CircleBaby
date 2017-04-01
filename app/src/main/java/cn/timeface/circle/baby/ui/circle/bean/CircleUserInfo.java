package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.timeface.circle.baby.support.api.models.db.AppDatabase;
import cn.timeface.circle.baby.support.utils.FastData;

/**
 * 圈用户对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
@Table(database = AppDatabase.class)
public class CircleUserInfo extends BaseModel implements Parcelable {

    private static volatile CircleUserInfo currentUserInfo = null;

    public static CircleUserInfo getInstance() {
        if (currentUserInfo == null)
            synchronized (CircleUserInfo.class) {
                if (currentUserInfo == null)
                    currentUserInfo = SQLite.select().from(CircleUserInfo.class).where(CircleUserInfo_Table.circleUserId.eq(FastData.getCircleUserId())).and(CircleUserInfo_Table.circleId.eq(FastData.getCircleId()))
                            .querySingle();
            }
        return currentUserInfo;
    }

    public static void clearAll() {
        SQLite.delete().from(CircleUserInfo.class).query();
        currentUserInfo = null;
    }

    public static CircleUserInfo refresh() {
        currentUserInfo = null;
        return getInstance();
    }

    @PrimaryKey
    protected long circleUserId;      //成长圈用户id
    @Column
    protected long circleId;          //成长圈id
    @Column
    protected int circleUserType;     //成长圈用户类型  1-创建者 2-老师 3-普通成员
    @Column
    protected String circleAvatarUrl; //成长圈用户头像
    @Column
    protected String circleNickName;  //成长圈用户昵称
    @Column
    protected int circleExpertType;  //用户的副类型

    public CircleUserInfo() {
    }

    public CircleUserInfo(long circleUserId, long circleId, int circleUserType, String circleAvatarUrl, String circleNickName, int circleExpertType) {
        this.circleUserId = circleUserId;
        this.circleId = circleId;
        this.circleUserType = circleUserType;
        this.circleAvatarUrl = circleAvatarUrl;
        this.circleNickName = circleNickName;
        this.circleExpertType = circleExpertType;
    }

    public CircleUserInfo(int circleUserType, String circleAvatarUrl, String circleNickName) {
        this.circleUserType = circleUserType;
        this.circleAvatarUrl = circleAvatarUrl;
        this.circleNickName = circleNickName;
    }

    public CircleUserInfo(String circleAvatarUrl, String circleNickName, long circleId, long circleUserId, int circleUserType) {
        this.circleAvatarUrl = circleAvatarUrl;
        this.circleNickName = circleNickName;
        this.circleId = circleId;
        this.circleUserId = circleUserId;
        this.circleUserType = circleUserType;
    }

    public String getCircleAvatarUrl() {
        return circleAvatarUrl;
    }

    public void setCircleAvatarUrl(String circleAvatarUrl) {
        this.circleAvatarUrl = circleAvatarUrl;
    }

    public static CircleUserInfo getCurrentUserInfo() {
        return currentUserInfo;
    }

    public static void setCurrentUserInfo(CircleUserInfo currentUserInfo) {
        CircleUserInfo.currentUserInfo = currentUserInfo;
    }

    public int getCircleExpertType() {
        return circleExpertType;
    }

    public void setCircleExpertType(int circleExpertType) {
        this.circleExpertType = circleExpertType;
    }

    public static Creator<CircleUserInfo> getCREATOR() {
        return CREATOR;
    }

    public String getCircleNickName() {
        return circleNickName;
    }

    public void setCircleNickName(String circleNickName) {
        this.circleNickName = circleNickName;
    }

    public long getCircleId() {
        return circleId;
    }

    public void setCircleId(long circleId) {
        this.circleId = circleId;
    }

    public long getCircleUserId() {
        return circleUserId;
    }

    public void setCircleUserId(long circleUserId) {
        this.circleUserId = circleUserId;
    }

    public int getCircleUserType() {
        return circleUserType;
    }

    public void setCircleUserType(int circleUserType) {
        this.circleUserType = circleUserType;
    }

    public boolean isTeacher() {
        return circleUserType == 2;
    }

    public boolean isCreater() {
        return circleUserType == 1;
    }

    public static void deleteAll() {
        SQLite.delete().from(CircleUserInfo.class).query();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleUserInfo that = (CircleUserInfo) o;

        if (getCircleUserId() != 0 && getCircleUserId() == that.getCircleUserId()) return true;

        return false;
    }

    @Override
    public String toString() {
        return "CircleUserInfo{" +
                "circleUserId=" + circleUserId +
                ", circleId=" + circleId +
                ", circleUserType=" + circleUserType +
                ", circleAvatarUrl='" + circleAvatarUrl + '\'' +
                ", circleNickName='" + circleNickName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.circleUserId);
        dest.writeLong(this.circleId);
        dest.writeInt(this.circleUserType);
        dest.writeString(this.circleAvatarUrl);
        dest.writeString(this.circleNickName);
        dest.writeInt(this.circleExpertType);
    }

    protected CircleUserInfo(Parcel in) {
        this.circleUserId = in.readLong();
        this.circleId = in.readLong();
        this.circleUserType = in.readInt();
        this.circleAvatarUrl = in.readString();
        this.circleNickName = in.readString();
        this.circleExpertType = in.readInt();
    }

    public static final Creator<CircleUserInfo> CREATOR = new Creator<CircleUserInfo>() {
        @Override
        public CircleUserInfo createFromParcel(Parcel source) {
            return new CircleUserInfo(source);
        }

        @Override
        public CircleUserInfo[] newArray(int size) {
            return new CircleUserInfo[size];
        }
    };
}
