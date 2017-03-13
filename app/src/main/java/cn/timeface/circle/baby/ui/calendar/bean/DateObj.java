package cn.timeface.circle.baby.ui.calendar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Locale;
import java.util.Map;

import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;


/**
 * 纪念日
 * Created by JieGuo on 16/9/29.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class DateObj implements Parcelable, Comparable<DateObj> {

    private String year;

    private String month;

    private String day;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDateStringMD() {
        return String.format(Locale.CHINESE, "%s月%s日",
                month,
                day);
    }

    public String getDateStringYMD() {
        return String.format(Locale.CHINESE,
                "%s年%s月%s日",
                year,
                month,
                day);
    }

    public boolean isSameDay(DateObj obj) {
        return obj.getDateStringMD().equals(getDateStringMD());
    }

    public DateObj() {
    }

    public DateObj(String year, String month, String day, String content) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.content = content;
    }

    public DateObj(CalendarPresentation.CommemorationParamsBuilder builder) {
        Map<String, String> data = builder.build();
        this.setContent(data.get("content"));
        setYear(data.get("year"));
        setMonth(data.get("month"));
        setDay(data.get("day"));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.year);
        dest.writeString(this.month);
        dest.writeString(this.day);
        dest.writeString(this.content);
    }

    protected DateObj(Parcel in) {
        this.year = in.readString();
        this.month = in.readString();
        this.day = in.readString();
        this.content = in.readString();
    }

    public static final Creator<DateObj> CREATOR = new Creator<DateObj>() {
        @Override
        public DateObj createFromParcel(Parcel source) {
            return new DateObj(source);
        }

        @Override
        public DateObj[] newArray(int size) {
            return new DateObj[size];
        }
    };

    @Override
    public int compareTo(DateObj another) {
        if (another == null || another.day == null) return 0;
        try {
            Integer currentDay = Integer.valueOf(day);
            Integer anotherDay = Integer.valueOf(another.day);
            return currentDay.compareTo(anotherDay);
        } catch (NumberFormatException e) {
            Log.e("Error of compareTo", "error", e);
            return 0;
        }
    }
}
