package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/7/7.
 */
public class CardBookSizeObj extends BaseObj implements Parcelable {
    int id;
    String title;
    List<MediaObj> imgList;
    int bookSizeId;
    String description;
    String coverTitle;
    int type;
    public CardBookSizeObj() {
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
        dest.writeInt(this.bookSizeId);
        dest.writeString(this.description);
        dest.writeString(this.coverTitle);
        dest.writeInt(this.type);
    }

    protected CardBookSizeObj(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.imgList = in.createTypedArrayList(MediaObj.CREATOR);
        this.bookSizeId = in.readInt();
        this.description = in.readString();
        this.coverTitle = in.readString();
        this.type = in.readInt();
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
