package cn.timeface.circle.baby.ui.babyInfo.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import cn.timeface.circle.baby.support.api.models.objs.UserObj;

/**
 * author : wangshuai Created on 2017/2/3
 * email : wangs1992321@gmail.com
 */
public class BabyChanged implements Parcelable {

    private UserObj userObj;
    private SpannableStringBuilder builder;

    public SpannableStringBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(SpannableStringBuilder builder) {
        this.builder = builder;
    }

    public BabyChanged(UserObj userObj) {
        this.userObj = userObj;
    }

    public UserObj getUserObj() {
        return userObj;
    }

    public void setUserObj(UserObj userObj) {
        this.userObj = userObj;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.userObj, flags);
    }

    public BabyChanged() {
    }

    protected BabyChanged(Parcel in) {
        this.userObj = in.readParcelable(UserObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<BabyChanged> CREATOR = new Parcelable.Creator<BabyChanged>() {
        @Override
        public BabyChanged createFromParcel(Parcel source) {
            return new BabyChanged(source);
        }

        @Override
        public BabyChanged[] newArray(int size) {
            return new BabyChanged[size];
        }
    };
}
