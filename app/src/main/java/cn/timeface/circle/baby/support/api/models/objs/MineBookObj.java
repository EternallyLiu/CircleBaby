package cn.timeface.circle.baby.support.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class MineBookObj extends BaseObj implements Parcelable {
    String bookCover;      //封面图片的url
    long updateTime;        //创建当前作品的时间戳
    int pageNum;          //当前作品的页数
    String bookName;           //当前作品名称
    int bookType;               //当前作品的类型
    String bookId;          //作品Id
    String author;          //作者
    int printCode;          //状态code码
    String description;
    int bookSizeId;          //日记卡片size
    String openBookId;       //开放平台bookid
    int openBookType;        //时光书主题id
    String coverTitle;
    int bookPage;
    int babyId;

    public int getBabyId() {
        return babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public String getCoverTitle() {
        return coverTitle;
    }

    public void setCoverTitle(String coverTitle) {
        this.coverTitle = coverTitle;
    }

    public int getBookPage() {
        return bookPage;
    }

    public void setBookPage(int bookPage) {
        this.bookPage = bookPage;
    }

    public int getOpenBookType() {
        return openBookType;
    }

    public void setOpenBookType(int openBookType) {
        this.openBookType = openBookType;
    }

    public int getBookSizeId() {
        return bookSizeId;
    }

    public void setBookSizeId(int bookSizeId) {
        this.bookSizeId = bookSizeId;
    }

    public String getOpenBookId() {
        return openBookId;
    }

    public void setOpenBookId(String openBookId) {
        this.openBookId = openBookId;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookCover);
        dest.writeLong(this.updateTime);
        dest.writeInt(this.pageNum);
        dest.writeString(this.bookName);
        dest.writeInt(this.bookType);
        dest.writeString(this.bookId);
        dest.writeString(this.author);
        dest.writeInt(this.printCode);
        dest.writeString(this.description);
        dest.writeInt(this.bookSizeId);
        dest.writeString(this.openBookId);
        dest.writeInt(this.openBookType);
        dest.writeString(this.coverTitle);
        dest.writeInt(this.bookPage);
        dest.writeInt(this.babyId);
    }

    public MineBookObj() {
    }

    protected MineBookObj(Parcel in) {
        this.bookCover = in.readString();
        this.updateTime = in.readLong();
        this.pageNum = in.readInt();
        this.bookName = in.readString();
        this.bookType = in.readInt();
        this.bookId = in.readString();
        this.author = in.readString();
        this.printCode = in.readInt();
        this.description = in.readString();
        this.bookSizeId = in.readInt();
        this.openBookId = in.readString();
        this.openBookType = in.readInt();
        this.coverTitle = in.readString();
        this.bookPage = in.readInt();
        this.babyId = in.readInt();
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
