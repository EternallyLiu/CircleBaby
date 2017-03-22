package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.db.AppDatabase;
import cn.timeface.circle.baby.support.utils.FastData;

/**
 * 成长圈对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
@Table(database = AppDatabase.class)
public class GrowthCircleObj extends BaseModel implements Parcelable {

    private static volatile GrowthCircleObj currentGrowthCircleObj = null;

    @PrimaryKey
    protected long circleId;          //圈id
    @Unique
    protected String circleNumber;      //圈号
    @Column
    protected String QRcodeUrl;       //二维码图片地址
    @Column
    protected String cicleCoverUrl;   //圈封面地址
    @Column
    protected String circleName;      //圈名称
    @Column
    protected long createDate;        //创建时间
    @Column
    protected int joinType;           //0 - 我创建的圈 1 - 我加入的圈
    @Column
    protected int mediaCount;         //圈中图片的数量
    @Column
    protected int memberCount;        //圈中成员的数量
    @Column
    protected int openLever;          //0 - 私有圈 1- 公有圈
    @Column
    protected int workCount;          //圈中书的数量

    public GrowthCircleObj() {
    }

    public static GrowthCircleObj getInstance() {
        if (currentGrowthCircleObj == null)
            synchronized (GrowthCircleObj.class) {
                if (currentGrowthCircleObj == null)
                    currentGrowthCircleObj = SQLite.select().from(GrowthCircleObj.class).where(GrowthCircleObj_Table.circleId.eq(FastData.getCircleId())).querySingle();
            }
        return currentGrowthCircleObj;
    }

    public static void clearAll(){
        SQLite.delete().from(GrowthCircleObj.class).query();
        currentGrowthCircleObj=null;
    }

    public static GrowthCircleObj refreshInstance() {
        currentGrowthCircleObj = null;
        return getInstance();
    }

    public long getCircleId() {
        return circleId;
    }

    public void setCircleId(long circleId) {
        this.circleId = circleId;
    }

    public String getCircleNumber() {
        return circleNumber;
    }

    public void setCircleNumber(String circleNumber) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.circleId);
        dest.writeString(this.circleNumber);
        dest.writeString(this.QRcodeUrl);
        dest.writeString(this.cicleCoverUrl);
        dest.writeString(this.circleName);
        dest.writeLong(this.createDate);
        dest.writeInt(this.joinType);
        dest.writeInt(this.mediaCount);
        dest.writeInt(this.memberCount);
        dest.writeInt(this.openLever);
        dest.writeInt(this.workCount);
    }

    protected GrowthCircleObj(Parcel in) {
        this.circleId = in.readLong();
        this.circleNumber = in.readString();
        this.QRcodeUrl = in.readString();
        this.cicleCoverUrl = in.readString();
        this.circleName = in.readString();
        this.createDate = in.readLong();
        this.joinType = in.readInt();
        this.mediaCount = in.readInt();
        this.memberCount = in.readInt();
        this.openLever = in.readInt();
        this.workCount = in.readInt();
    }

    public static final Creator<GrowthCircleObj> CREATOR = new Creator<GrowthCircleObj>() {
        @Override
        public GrowthCircleObj createFromParcel(Parcel source) {
            return new GrowthCircleObj(source);
        }

        @Override
        public GrowthCircleObj[] newArray(int size) {
            return new GrowthCircleObj[size];
        }
    };

    /**
     * 是否为圈创建者
     */
    public boolean isCreator() {
        return joinType == 0;
    }

    /**
     * 是否为公开圈
     */
    public boolean isPublic() {
        return openLever == 1;
    }

}
