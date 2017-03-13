package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/7/7.
 */
public class CardBookSizeObj extends BaseObj implements Parcelable {
    int id;
    String title;
    List<MediaObj> imgList;
    String description;
    String coverTitle;
    int type;
    MediaObj detail;
    int price;
    float height;
    float width;
    int bookSizeId;
    int bookPage;

    public MediaObj getDetail() {
        return detail;
    }

    public void setDetail(MediaObj detail) {
        this.detail = detail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public CardBookSizeObj() {
    }

    public int getBookPage() {
        return bookPage;
    }

    public void setBookPage(int bookPage) {
        this.bookPage = bookPage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MediaObj> getImgList() {
        return imgList;
    }

    public void setImgList(List<MediaObj> imgList) {
        this.imgList = imgList;
    }

    public int getBookSizeId() {
        return bookSizeId;
    }

    public void setBookSizeId(int bookSizeId) {
        this.bookSizeId = bookSizeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverTitle() {
        return coverTitle;
    }

    public void setCoverTitle(String coverTitle) {
        this.coverTitle = coverTitle;
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
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeTypedList(this.imgList);
        dest.writeString(this.description);
        dest.writeString(this.coverTitle);
        dest.writeInt(this.type);
        dest.writeParcelable(this.detail,flags);
        dest.writeInt(this.price);
        dest.writeFloat(this.height);
        dest.writeFloat(this.width);
        dest.writeInt(this.bookSizeId);
        dest.writeInt(this.bookPage);
    }

    protected CardBookSizeObj(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.imgList = in.createTypedArrayList(MediaObj.CREATOR);
        this.description = in.readString();
        this.coverTitle = in.readString();
        this.type = in.readInt();
        this.detail = in.readParcelable(MediaObj.class.getClassLoader());
        this.price = in.readInt();
        this.height = in.readFloat();
        this.width = in.readFloat();
        this.bookSizeId = in.readInt();
        this.bookPage = in.readInt();
    }

    public static final Creator<CardBookSizeObj> CREATOR = new Creator<CardBookSizeObj>() {
        @Override
        public CardBookSizeObj createFromParcel(Parcel source) {
            return new CardBookSizeObj(source);
        }

        @Override
        public CardBookSizeObj[] newArray(int size) {
            return new CardBookSizeObj[size];
        }
    };
}
