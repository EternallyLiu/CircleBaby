package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * circle time line 补充
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleTimeLineExObj implements Parcelable, Comparable {
    private CircleTimelineObj circleTimeline;
    private String publishDate;
    private String week;

    public CircleTimelineObj getCircleTimeline() {
        return circleTimeline;
    }

    public void setCircleTimeline(CircleTimelineObj circleTimeline) {
        this.circleTimeline = circleTimeline;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.circleTimeline, flags);
        dest.writeString(this.publishDate);
        dest.writeString(this.week);
    }

    public CircleTimeLineExObj() {
    }

    protected CircleTimeLineExObj(Parcel in) {
        this.circleTimeline = in.readParcelable(CircleTimelineObj.class.getClassLoader());
        this.publishDate = in.readString();
        this.week = in.readString();
    }

    public static final Parcelable.Creator<CircleTimeLineExObj> CREATOR = new Parcelable.Creator<CircleTimeLineExObj>() {
        @Override
        public CircleTimeLineExObj createFromParcel(Parcel source) {
            return new CircleTimeLineExObj(source);
        }

        @Override
        public CircleTimeLineExObj[] newArray(int size) {
            return new CircleTimeLineExObj[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CircleTimeLineExObj){
            if(((CircleTimeLineExObj) obj).getCircleTimeline() != null
                    && getCircleTimeline() != null
                    && getCircleTimeline().equals(((CircleTimeLineExObj) obj).getCircleTimeline())){
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        CircleTimeLineExObj another = (CircleTimeLineExObj) o;
        if(getCircleTimeline().getRecordDate() > another.getCircleTimeline().getRecordDate()){
            return 1;
        } else if(getCircleTimeline().getRecordDate() == another.getCircleTimeline().getRecordDate()){
            return 0;
        } else {
            return -1;
        }
    }
}
