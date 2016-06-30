package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class MineBookObj extends BaseObj implements Parcelable {
    String coverImage;      //封面图片的url
    long createTime;        //创建当前作品的时间戳
    int pageCount;          //当前作品的页数
    String title;           //当前作品名称
    int type;               //当前作品的类型
    String bookId;          //作品Id
    String author;          //作者
    int printCode;          //状态code码

    public MineBookObj(String coverImage, long createTime, int pageCount, String title, int type , String bookId , String author) {
        this.coverImage = coverImage;
        this.createTime = createTime;
        this.pageCount = pageCount;
        this.title = title;
        this.type = type;
        this.bookId = bookId;
        this.author = author;
    }

    public int getPrintCode() {
        return printCode;
    }

    public void setPrintCode(int printCode) {
        this.printCode = printCode;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coverImage);
        dest.writeLong(this.createTime);
        dest.writeInt(this.pageCount);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeString(this.bookId);
        dest.writeString(this.author);
    }

    public MineBookObj() {
    }

    protected MineBookObj(Parcel in) {
        this.coverImage = in.readString();
        this.createTime = in.readLong();
        this.pageCount = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
        this.bookId = in.readString();
        this.author = in.readString();
    }

    public static final Creator<MineBookObj> CREATOR = new Creator<MineBookObj>() {
        @Override
        public MineBookObj createFromParcel(Parcel source) {
            return new MineBookObj(source);
        }

        @Override
        public MineBookObj[] newArray(int size) {
            return new MineBookObj[size];
        }
    };
}
