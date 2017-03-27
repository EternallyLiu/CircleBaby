package cn.timeface.circle.baby.ui.circle.groupmembers.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangwei on 2017/3/22.
 */

public class CircleBabyBriefObj implements Parcelable {

    /**
     * babyAvatarUrl : http://img1.timeface.cn/baby/da7ccada2b967490847e65d0ab052d11.jpg
     * babyId : 156
     * babyName : 王哈哈
     */

    private String babyAvatarUrl;
    private long babyId;
    private String babyName;
    private int selectCount;

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public String getBabyAvatarUrl() {
        return babyAvatarUrl;
    }

    public void setBabyAvatarUrl(String babyAvatarUrl) {
        this.babyAvatarUrl = babyAvatarUrl;
    }

    public long getBabyId() {
        return babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.babyAvatarUrl);
        dest.writeLong(this.babyId);
        dest.writeString(this.babyName);
    }

    public CircleBabyBriefObj() {
    }

    protected CircleBabyBriefObj(Parcel in) {
        this.babyAvatarUrl = in.readString();
        this.babyId = in.readInt();
        this.babyName = in.readString();
    }

    public static final Parcelable.Creator<CircleBabyBriefObj> CREATOR = new Parcelable.Creator<CircleBabyBriefObj>() {
        @Override
        public CircleBabyBriefObj createFromParcel(Parcel source) {
            return new CircleBabyBriefObj(source);
        }

        @Override
        public CircleBabyBriefObj[] newArray(int size) {
            return new CircleBabyBriefObj[size];
        }
    };
}
