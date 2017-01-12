package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * Created by wangshuai on 2017/1/9.
 */

public class MonthRecord extends BaseObj implements Parcelable {
    private String babyAge;
    private int year;
    private int recordcount;
    private int moth;
    private List<MediaObj> medias;

    public String getBabyAge() {
        return babyAge;
    }

    public void setBabyAge(String babyAge) {
        this.babyAge = babyAge;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRecordcount() {
        return recordcount;
    }

    public void setRecordcount(int recordcount) {
        this.recordcount = recordcount;
    }

    public int getMoth() {
        return moth;
    }

    public void setMoth(int moth) {
        this.moth = moth;
    }

    public List<MediaObj> getMedias() {
        return medias;
    }

    public void setMedias(List<MediaObj> medias) {
        this.medias = medias;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.babyAge);
        dest.writeInt(this.year);
        dest.writeInt(this.recordcount);
        dest.writeInt(this.moth);
        dest.writeList(this.medias);
    }

    public MonthRecord() {
    }

    protected MonthRecord(Parcel in) {
        this.babyAge = in.readString();
        this.year = in.readInt();
        this.recordcount = in.readInt();
        this.moth = in.readInt();
        this.medias = new ArrayList<MediaObj>();
        in.readList(this.medias, MediaObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<MonthRecord> CREATOR = new Parcelable.Creator<MonthRecord>() {
        @Override
        public MonthRecord createFromParcel(Parcel source) {
            return new MonthRecord(source);
        }

        @Override
        public MonthRecord[] newArray(int size) {
            return new MonthRecord[size];
        }
    };

    @Override
    public String toString() {
        return "MonthRecord{" +
                "babyAge='" + babyAge + '\'' +
                ", year=" + year +
                ", recordcount=" + recordcount +
                ", moth=" + moth +
                ", medias=" + medias +
                '}';
    }
}
