package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * author: rayboot  Created on 16/1/20.
 * email : sy0725work@gmail.com
 */
public class BookObj extends BaseObj implements Parcelable {
    int count;          //打印数量
    String size;
    String color;
    String paper;
    String bind;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeString(this.size);
        dest.writeString(this.color);
        dest.writeString(this.paper);
        dest.writeString(this.bind);
    }

    public BookObj() {
    }

    protected BookObj(Parcel in) {
        this.count = in.readInt();
        this.size = in.readString();
        this.color = in.readString();
        this.paper = in.readString();
        this.bind = in.readString();
    }

    public static final Creator<BookObj> CREATOR = new Creator<BookObj>() {
        @Override
        public BookObj createFromParcel(Parcel source) {
            return new BookObj(source);
        }

        @Override
        public BookObj[] newArray(int size) {
            return new BookObj[size];
        }
    };
}
