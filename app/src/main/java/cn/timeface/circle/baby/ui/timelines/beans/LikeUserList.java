package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.UserObj;

/**
 * author : wangshuai Created on 2017/2/7
 * email : wangs1992321@gmail.com
 */
public class LikeUserList implements Parcelable {
    private List<UserObj> list;

    public List<UserObj> getList() {
        return list;
    }

    public void setList(List<UserObj> list) {
        this.list = list;
    }

    public LikeUserList(List<UserObj> list) {
        this.list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.list);
    }

    protected LikeUserList(Parcel in) {
        this.list = in.createTypedArrayList(UserObj.CREATOR);
    }

    public static final Parcelable.Creator<LikeUserList> CREATOR = new Parcelable.Creator<LikeUserList>() {
        @Override
        public LikeUserList createFromParcel(Parcel source) {
            return new LikeUserList(source);
        }

        @Override
        public LikeUserList[] newArray(int size) {
            return new LikeUserList[size];
        }
    };
}
