package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * book obj
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class BookObj extends BaseObj implements Parcelable {
    private UserObj author;//这本书的作者
    private BabyObj baby;//书所属的孩子
    private String bookCover;//书本封面
    private long bookId;//书id
    private String bookName;//书名
    private int bookType;//书类型
    private long createTime;//创建时间
    private int isCustom;//1-系统推荐 2-用户自己创建的
    private long openBookId;//开放平台bookid
    private int pageNum;//书的总页数

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
    }

    public BookObj() {
    }

    protected BookObj(Parcel in) {
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
    }

    public static final Parcelable.Creator<BookObj> CREATOR = new Parcelable.Creator<BookObj>() {
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
