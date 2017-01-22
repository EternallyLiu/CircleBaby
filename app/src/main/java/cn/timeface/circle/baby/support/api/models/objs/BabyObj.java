package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.db.AppDatabase;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * Created by lidonglin on 2016/4/28.
 */
@Table(database = AppDatabase.class)
public class BabyObj extends BaseModel implements Parcelable {
    private static volatile BabyObj currentBabyObj = null;

    public static BabyObj getInstance(int babyId) {
        if (currentBabyObj == null || currentBabyObj.getBabyId() != babyId)
            synchronized (BabyObj.class) {
                if (currentBabyObj == null || currentBabyObj.getBabyId() != babyId)
                    currentBabyObj = SQLite.select().from(BabyObj.class).where(BabyObj_Table.babyId.eq(babyId)).querySingle();
            }
        LogUtil.showLog("babyId==" + babyId);
        LogUtil.showLog("currentBabyObj==" + (currentBabyObj == null));
        return currentBabyObj;
    }

    private String realName;//大名
    private int showRealName;//是否展示真实姓名
    @PrimaryKey
    int babyId;
    @Column
    String age;
    @Column(name = "icon")
    String avatar;
    @Column(name = "sccsdcsd")
    long bithday;
    @Column(name = "blood")
    String blood;
    @Column(name = "constellation")
    String constellation;
    @Column(name = "sex")
    int gender;
    @Column(name = "baby_name")
    String name;
    @Column(name = "user_id")
    String UserId;

    public BabyObj() {
    }

    public BabyObj(String age, String avatar, int babyId, long bithday, String blood, String constellation, int gender, String name) {
        this.age = age;
        this.avatar = avatar;
        this.babyId = babyId;
        this.bithday = bithday;
        this.blood = blood;
        this.constellation = constellation;
        this.gender = gender;
        this.name = name;
    }


    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getShowRealName() {
        return showRealName;
    }

    public void setShowRealName(int showRealName) {
        this.showRealName = showRealName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getBabyId() {
        return babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public long getBithday() {
        return bithday;
    }

    public void setBithday(long bithday) {
        this.bithday = bithday;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        if (getShowRealName() == 0)
            return TextUtils.isEmpty(getRealName()) ? getName() : getRealName();
        else return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BabyObj babyObj = (BabyObj) o;

        if (showRealName != babyObj.showRealName) return false;
        if (babyId != babyObj.babyId) return false;
        if (bithday != babyObj.bithday) return false;
        if (gender != babyObj.gender) return false;
        if (realName != null ? !realName.equals(babyObj.realName) : babyObj.realName != null)
            return false;
        if (age != null ? !age.equals(babyObj.age) : babyObj.age != null) return false;
        if (avatar != null ? !avatar.equals(babyObj.avatar) : babyObj.avatar != null) return false;
        if (blood != null ? !blood.equals(babyObj.blood) : babyObj.blood != null) return false;
        if (constellation != null ? !constellation.equals(babyObj.constellation) : babyObj.constellation != null)
            return false;
        return name != null ? name.equals(babyObj.name) : babyObj.name == null;

    }

    @Override
    public int hashCode() {
        int result = realName != null ? realName.hashCode() : 0;
        result = 31 * result + showRealName;
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + babyId;
        result = 31 * result + (int) (bithday ^ (bithday >>> 32));
        result = 31 * result + (blood != null ? blood.hashCode() : 0);
        result = 31 * result + (constellation != null ? constellation.hashCode() : 0);
        result = 31 * result + gender;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BabyObj{" +
                "age='" + age + '\'' +
                ", avatar='" + avatar + '\'' +
                ", babyId=" + babyId +
                ", bithday=" + bithday +
                ", blood='" + blood + '\'' +
                ", constellation='" + constellation + '\'' +
                ", gender=" + gender +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.realName);
        dest.writeInt(this.showRealName);
        dest.writeString(this.age);
        dest.writeString(this.avatar);
        dest.writeInt(this.babyId);
        dest.writeLong(this.bithday);
        dest.writeString(this.blood);
        dest.writeString(this.constellation);
        dest.writeInt(this.gender);
        dest.writeString(this.name);
        dest.writeString(this.realName);
        dest.writeInt(this.showRealName);
    }

    protected BabyObj(Parcel in) {
        this.realName = in.readString();
        this.showRealName = in.readInt();
        this.age = in.readString();
        this.avatar = in.readString();
        this.babyId = in.readInt();
        this.bithday = in.readLong();
        this.blood = in.readString();
        this.constellation = in.readString();
        this.gender = in.readInt();
        this.name = in.readString();
        this.realName = in.readString();
        this.showRealName = in.readInt();
    }

    public static final Creator<BabyObj> CREATOR = new Creator<BabyObj>() {
        @Override
        public BabyObj createFromParcel(Parcel source) {
            return new BabyObj(source);
        }

        @Override
        public BabyObj[] newArray(int size) {
            return new BabyObj[size];
        }
    };

    public static void saveAll(List<BabyObj> list) {
        if (list == null || list.size() <= 0)
            return;
        FlowManager.getModelAdapter(BabyObj.class).saveAll(list);
    }
}
