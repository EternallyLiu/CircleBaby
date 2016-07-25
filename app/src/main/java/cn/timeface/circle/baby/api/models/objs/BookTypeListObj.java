package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class BookTypeListObj extends BaseObj implements Parcelable {
    int id;
    String coverTitle;      //封面作品名称
    String description;     //作品详情
    List<MediaObj> imgList; //介绍当前作品类型的图片数组
    String title;           //当前作品名称
    int type;               //当前作品的类型
    MediaObj detail;
    int price;
    float height;
    float width;
    int bookSizeId;
    int bookPage;

    public BookTypeListObj(int id, String coverTitle, String description, List<MediaObj> imgList, String title, int type) {
        this.id = id;
        this.coverTitle = coverTitle;
        this.description = description;
        this.imgList = imgList;
        this.title = title;
        this.type = type;
    }

    public BookTypeListObj(WorkObj obj) {
        this.id = obj.getId();
        this.coverTitle = obj.getCoverTitle();
        this.description = obj.getDescription();
        this.imgList = obj.getImgList();
        this.title = obj.getTitle();
        this.type = obj.getType();
    }

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

    public int getBookSizeId() {
        return bookSizeId;
    }

    public void setBookSizeId(int bookSizeId) {
        this.bookSizeId = bookSizeId;
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

    public String getCoverTitle() {
        return coverTitle;
    }

    public void setCoverTitle(String coverTitle) {
        this.coverTitle = coverTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MediaObj> getImgList() {
        return imgList;
    }

    public void setImgList(List<MediaObj> imgList) {
        this.imgList = imgList;
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
        dest.writeInt(this.id);
        dest.writeString(this.coverTitle);
        dest.writeString(this.description);
        dest.writeTypedList(this.imgList);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeParcelable(this.detail,flags);
        dest.writeInt(this.price);
        dest.writeFloat(this.height);
        dest.writeFloat(this.width);
        dest.writeInt(this.bookSizeId);
        dest.writeInt(this.bookPage);

    }

    public BookTypeListObj() {
    }

    protected BookTypeListObj(Parcel in) {
        this.id = in.readInt();
        this.coverTitle = in.readString();
        this.description = in.readString();
        this.imgList = in.createTypedArrayList(MediaObj.CREATOR);
        this.title = in.readString();
        this.type = in.readInt();
        this.detail = in.readParcelable(MediaObj.class.getClassLoader());
        this.price = in.readInt();
        this.height = in.readFloat();
        this.width = in.readFloat();
        this.bookSizeId = in.readInt();
        this.bookPage = in.readInt();
    }

    public static final Creator<BookTypeListObj> CREATOR = new Creator<BookTypeListObj>() {
        @Override
        public BookTypeListObj createFromParcel(Parcel source) {
            return new BookTypeListObj(source);
        }

        @Override
        public BookTypeListObj[] newArray(int size) {
            return new BookTypeListObj[size];
        }
    };
}
