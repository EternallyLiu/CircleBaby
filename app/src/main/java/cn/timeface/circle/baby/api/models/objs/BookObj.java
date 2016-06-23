package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * author: rayboot  Created on 16/1/20.
 * email : sy0725work@gmail.com
 */
public class BookObj extends BaseObj implements Parcelable {
    int num;          //打印数量
    String size;
    String color;
    String paper;
    String pack;
    int bookId;
    int bookType;
    int printId;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public int getPrintId() {
        return printId;
    }

    public void setPrintId(int printId) {
        this.printId = printId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.num);
        dest.writeString(this.size);
        dest.writeString(this.color);
        dest.writeString(this.paper);
        dest.writeString(this.pack);
        dest.writeInt(this.bookId);
        dest.writeInt(this.bookType);
        dest.writeInt(this.printId);
    }

    public BookObj() {
    }

    protected BookObj(Parcel in) {
        this.num = in.readInt();
        this.size = in.readString();
        this.color = in.readString();
        this.paper = in.readString();
        this.pack = in.readString();
        this.bookId = in.readInt();
        this.bookType = in.readInt();
        this.printId = in.readInt();
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
