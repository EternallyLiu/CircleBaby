package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * author : wangshuai Created on 2017/2/13
 * email : wangs1992321@gmail.com
 */
public class TimeConttent extends BaseObj implements Parcelable {

    private NearLocationObj locationInfo;
    private String content;
    private ArrayList<MediaObj> mediaList;
    private int milestone;
    private long time;


    public TimeConttent(NearLocationObj locationInfo, String content, ArrayList<MediaObj> mediaList, int milestone, long time) {
        this.locationInfo = locationInfo;
        this.content = content;
        this.mediaList = mediaList;
        this.milestone = milestone;
        this.time = time;
    }

    public NearLocationObj getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(NearLocationObj locationInfo) {
        this.locationInfo = locationInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<MediaObj> getMediaList() {
        if (mediaList==null)
            mediaList=new ArrayList<>();
        return mediaList;
    }

    public void setMediaList(ArrayList<MediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public int getMilestone() {
        return milestone;
    }

    public void setMilestone(int milestone) {
        this.milestone = milestone;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.locationInfo, flags);
        dest.writeString(this.content);
        dest.writeTypedList(this.mediaList);
        dest.writeInt(this.milestone);
        dest.writeLong(this.time);
    }

    public TimeConttent() {
    }

    protected TimeConttent(Parcel in) {
        this.locationInfo = in.readParcelable(NearLocationObj.class.getClassLoader());
        this.content = in.readString();
        this.mediaList = in.createTypedArrayList(MediaObj.CREATOR);
        this.milestone = in.readInt();
        this.time = in.readLong();
    }

    public static final Parcelable.Creator<TimeConttent> CREATOR = new Parcelable.Creator<TimeConttent>() {
        @Override
        public TimeConttent createFromParcel(Parcel source) {
            return new TimeConttent(source);
        }

        @Override
        public TimeConttent[] newArray(int size) {
            return new TimeConttent[size];
        }
    };
}
