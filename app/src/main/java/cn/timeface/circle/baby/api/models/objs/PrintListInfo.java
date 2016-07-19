package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/12.
 */
public class PrintListInfo extends BaseObj implements Parcelable {
    int num;
    int color;
    int paper;
    int pack;
    long date;
    String printId;
    int size;
    float price;
    private boolean isSelect = true;

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getPaper() {
        return paper;
    }

    public void setPaper(int paper) {
        this.paper = paper;
    }

    public int getPack() {
        return pack;
    }

    public void setPack(int pack) {
        this.pack = pack;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPrintId() {
        return printId;
    }

    public void setPrintId(String printId) {
        this.printId = printId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.num);
        dest.writeInt(this.color);
        dest.writeInt(this.paper);
        dest.writeInt(this.pack);
        dest.writeLong(this.date);
        dest.writeString(this.printId);
        dest.writeInt(this.size);
        dest.writeFloat(this.price);
    }

    public PrintListInfo() {
    }

    protected PrintListInfo(Parcel in) {
        this.num = in.readInt();
        this.color = in.readInt();
        this.paper = in.readInt();
        this.pack = in.readInt();
        this.date = in.readLong();
        this.printId = in.readString();
        this.size = in.readInt();
        this.price = in.readFloat();
    }

    public static final Creator<PrintListInfo> CREATOR = new Creator<PrintListInfo>() {
        @Override
        public PrintListInfo createFromParcel(Parcel source) {
            return new PrintListInfo(source);
        }

        @Override
        public PrintListInfo[] newArray(int size) {
            return new PrintListInfo[size];
        }
    };

    public void setData(String size, int color, int pack, int paper, int num) {
        this.size = Integer.valueOf(size);
        this.color = color;
        this.pack = pack;
        this.paper = paper;
        this.num = num;
    }
}
