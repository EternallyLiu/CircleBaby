package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.gson.annotations.SerializedName;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * book obj
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class BookObj extends BaseObj implements Parcelable {
    @SerializedName(value = "autor", alternate = {"author"})
    private UserObj author;//这本书的作者
    private BabyObj baby;//书所属的孩子
    private String bookCover;//书本封面
    private long bookId;//书id
    private String bookName;//书名
    private int bookType;//书类型
    private long createTime;//创建时间
    private int isCustom;//0-系统推荐 1-用户自己创建的
    private long openBookId;//开放平台bookid
    private int pageNum;//书的总页数
    private int openBookType;//
    private long updateTime;

    public int getOpenBookType() {
        return openBookType;
    }

    public void setOpenBookType(int openBookType) {
        this.openBookType = openBookType;
    }

    public UserObj getAuthor() {
        return author;
    }

    public void setAuthor(UserObj author) {
        this.author = author;
    }

    public BabyObj getBaby() {
        return baby;
    }

    public void setBaby(BabyObj baby) {
        this.baby = baby;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(int isCustom) {
        this.isCustom = isCustom;
    }

    public long getOpenBookId() {
        return openBookId;
    }

    public void setOpenBookId(long openBookId) {
        this.openBookId = openBookId;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public BookObj(UserObj author, BabyObj baby, String bookCover, long bookId, String bookName, int bookType, long createTime, int isCustom, long openBookId, int pageNum, int openBookType, long updateTime) {
        this.author = author;
        this.baby = baby;
        this.bookCover = bookCover;
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookType = bookType;
        this.createTime = createTime;
        this.isCustom = isCustom;
        this.openBookId = openBookId;
        this.pageNum = pageNum;
        this.openBookType = openBookType;
        this.updateTime = updateTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public static Creator<BookObj> getCREATOR() {
        return CREATOR;
    }

    public BookObj(Parcel in, UserObj author, BabyObj baby, String bookCover, long bookId, String bookName, int bookType, long createTime, int isCustom, long openBookId, int pageNum, int openBookType, long updateTime) {
        super(in);
        this.author = author;
        this.baby = baby;
        this.bookCover = bookCover;
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookType = bookType;
        this.createTime = createTime;
        this.isCustom = isCustom;
        this.openBookId = openBookId;
        this.pageNum = pageNum;
        this.openBookType = openBookType;
        this.updateTime = updateTime;
    }

    public boolean showAuthor(){
        return isCustom == 1;
    }

    public BookObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.baby, flags);
        dest.writeString(this.bookCover);
        dest.writeLong(this.bookId);
        dest.writeString(this.bookName);
        dest.writeInt(this.bookType);
        dest.writeLong(this.createTime);
        dest.writeInt(this.isCustom);
        dest.writeLong(this.openBookId);
        dest.writeInt(this.pageNum);
        dest.writeInt(this.openBookType);
        dest.writeLong(this.updateTime);
    }

    protected BookObj(Parcel in) {
        super(in);
        this.author = in.readParcelable(UserObj.class.getClassLoader());
        this.baby = in.readParcelable(BabyObj.class.getClassLoader());
        this.bookCover = in.readString();
        this.bookId = in.readLong();
        this.bookName = in.readString();
        this.bookType = in.readInt();
        this.createTime = in.readLong();
        this.isCustom = in.readInt();
        this.openBookId = in.readLong();
        this.pageNum = in.readInt();
        this.openBookType = in.readInt();
        this.updateTime = in.readLong();
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
