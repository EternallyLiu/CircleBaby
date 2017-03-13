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
    private String book_id;

    @JsonField(name = "book_cover")
    private String book_cover;

    @JsonField(name = "book_author")
    private String book_author;

    @JsonField(name = "author_avatar")
    private String author_avatar;

    @JsonField(name = "book_title")
    private String book_title;

    @JsonField(name = "book_summary")
    private String book_summary;

    @JsonField(name = "book_type")
    private int book_type;

    @JsonField(name = "year")
    private String year;

    @JsonField(name = "days")
    private String days;

    @JsonField(name = "extra")
    private String extra;

    @JsonField(name = "book_date")
    private Long book_date;

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

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public String getAuthor_avatar() {
        return author_avatar;
    }

    public void setAuthor_avatar(String author_avatar) {
        this.author_avatar = author_avatar;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getBook_summary() {
        return book_summary;
    }

    public void setBook_summary(String book_summary) {
        this.book_summary = book_summary;
    }

    public int getBook_type() {
        return book_type;
    }

    public void setBook_type(int book_type) {
        this.book_type = book_type;
    }

    public Long getBook_date() {
        return book_date;
    }

    public void setBook_date(Long book_date) {
        this.book_date = book_date;
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
        dest.writeString(this.book_id);
        dest.writeString(this.book_cover);
        dest.writeString(this.book_author);
        dest.writeString(this.author_avatar);
        dest.writeString(this.book_title);
        dest.writeString(this.book_summary);
        dest.writeInt(this.book_type);
        dest.writeString(this.year);
        dest.writeString(this.days);
        dest.writeString(this.extra);
        dest.writeValue(this.book_date);
        dest.writeInt(this.bookShelve);
    }

    protected GeneralBookObj(Parcel in) {
        this.id = in.readLong();
        this.book_id = in.readString();
        this.book_cover = in.readString();
        this.book_author = in.readString();
        this.author_avatar = in.readString();
        this.book_title = in.readString();
        this.book_summary = in.readString();
        this.book_type = in.readInt();
        this.year = in.readString();
        this.days = in.readString();
        this.extra = in.readString();
        this.book_date = (Long) in.readValue(Long.class.getClassLoader());
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
