package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
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

    public static void clearAll(){
        SQLite.delete().from(CircleUserInfo.class).query();
        currentUserInfo=null;
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

    public CircleUserInfo() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleUserInfo that = (CircleUserInfo) o;

        if (getCircleUserId() > 0 && getCircleUserId() == that.getCircleUserId()) return true;

        return false;
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
    }

    protected CircleUserInfo(Parcel in) {
        this.circleUserId = in.readLong();
        this.circleId = in.readLong();
        this.circleUserType = in.readInt();
        this.circleAvatarUrl = in.readString();
        this.circleNickName = in.readString();
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
