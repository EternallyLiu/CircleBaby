package cn.timeface.circle.baby.ui.circle.timelines.events;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.ui.circle.bean.HomeWorkListObj;

/**
 * author : wangshuai Created on 2017/3/28
 * email : wangs1992321@gmail.com
 */
public class HomeWorkListEvent implements Parcelable {
    private HomeWorkListObj homeWorkList;

    public HomeWorkListEvent(HomeWorkListObj homeWorkList) {
        this.homeWorkList = homeWorkList;
    }

    public HomeWorkListObj getHomeWorkList() {

        return homeWorkList;
    }

    public void setHomeWorkList(HomeWorkListObj homeWorkList) {
        this.homeWorkList = homeWorkList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.homeWorkList, flags);
    }

    public HomeWorkListEvent() {
    }

    protected HomeWorkListEvent(Parcel in) {
        this.homeWorkList = in.readParcelable(HomeWorkListObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<HomeWorkListEvent> CREATOR = new Parcelable.Creator<HomeWorkListEvent>() {
        @Override
        public HomeWorkListEvent createFromParcel(Parcel source) {
            return new HomeWorkListEvent(source);
        }

        @Override
        public HomeWorkListEvent[] newArray(int size) {
            return new HomeWorkListEvent[size];
        }
    };
}
