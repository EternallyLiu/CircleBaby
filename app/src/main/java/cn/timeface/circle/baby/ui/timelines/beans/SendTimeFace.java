package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * author : wangshuai Created on 2017/2/13
 * email : wangs1992321@gmail.com
 */
public class SendTimeFace extends BaseObj implements Parcelable {

    private int type;
    private ArrayList<TimeConttent> dataList;

    public SendTimeFace() {
    }

    public SendTimeFace(int type) {
        this.type = type;
    }

    public SendTimeFace(int type, ArrayList<TimeConttent> dataList) {
        this.type = type;
        this.dataList = dataList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<TimeConttent> getDataList() {
        if (dataList==null)
            dataList=new ArrayList<>();
        return dataList;
    }

    public void setDataList(ArrayList<TimeConttent> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeTypedList(this.dataList);
    }

    protected SendTimeFace(Parcel in) {
        this.type = in.readInt();
        this.dataList = in.createTypedArrayList(TimeConttent.CREATOR);
    }

    public static final Parcelable.Creator<SendTimeFace> CREATOR = new Parcelable.Creator<SendTimeFace>() {
        @Override
        public SendTimeFace createFromParcel(Parcel source) {
            return new SendTimeFace(source);
        }

        @Override
        public SendTimeFace[] newArray(int size) {
            return new SendTimeFace[size];
        }
    };
}
