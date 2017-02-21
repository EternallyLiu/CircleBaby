package cn.timeface.circle.baby.support.mvp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by JieGuo on 16/10/19.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GeneralBookObj implements Parcelable {

    @JsonField(name = "id")
    private long id;

    @JsonField(name = "book_id")
    private String bookId;

    @JsonField(name = "book_cover")
    private String bookCover;

    @JsonField(name = "book_author")
    private String bookAuthor;

    @JsonField(name = "author_avatar")
    private String authorAvatar;

    @JsonField(name = "book_title")
    private String bookTitle;

    @JsonField(name = "book_summary")
    private String bookSummary;

    @JsonField(name = "book_type")
    private int bookType;

    @JsonField(name = "year")
    private String year;

    @JsonField(name = "days")
    private String days;

    @JsonField(name = "extra")
    private String extra;

    @JsonField(name = "book_date")
    private Long date;

    /**
     * 3 已上架 2 审核中 其他的值 都为下架状态
     */
    @JsonField(name = "book_shelve")
    private int bookShelve;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookSummary() {
        return bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public int getBookShelve() {
        return bookShelve;
    }

    public void setBookShelve(int bookShelve) {
        this.bookShelve = bookShelve;
    }

    public GeneralBookObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.bookId);
        dest.writeString(this.bookCover);
        dest.writeString(this.bookAuthor);
        dest.writeString(this.authorAvatar);
        dest.writeString(this.bookTitle);
        dest.writeString(this.bookSummary);
        dest.writeInt(this.bookType);
        dest.writeString(this.year);
        dest.writeString(this.days);
        dest.writeString(this.extra);
        dest.writeValue(this.date);
        dest.writeInt(this.bookShelve);
    }

    protected GeneralBookObj(Parcel in) {
        this.id = in.readLong();
        this.bookId = in.readString();
        this.bookCover = in.readString();
        this.bookAuthor = in.readString();
        this.authorAvatar = in.readString();
        this.bookTitle = in.readString();
        this.bookSummary = in.readString();
        this.bookType = in.readInt();
        this.year = in.readString();
        this.days = in.readString();
        this.extra = in.readString();
        this.date = (Long) in.readValue(Long.class.getClassLoader());
        this.bookShelve = in.readInt();
    }

    public static final Creator<GeneralBookObj> CREATOR = new Creator<GeneralBookObj>() {
        @Override
        public GeneralBookObj createFromParcel(Parcel source) {
            return new GeneralBookObj(source);
        }

        @Override
        public GeneralBookObj[] newArray(int size) {
            return new GeneralBookObj[size];
        }
    };
}
