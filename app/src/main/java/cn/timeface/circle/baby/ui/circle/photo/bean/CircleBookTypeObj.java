package cn.timeface.circle.baby.ui.circle.photo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 圈作品新增作品首页对象
 * Created by lidonglin on 2017/3/22.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleBookTypeObj extends BaseObj implements Parcelable {
    protected int bookType;
    protected String description;
    protected String iconUrl;
    protected String title;


    public CircleBookTypeObj() {
    }


    protected CircleBookTypeObj(Parcel in) {
        super(in);
        bookType = in.readInt();
        description = in.readString();
        iconUrl = in.readString();
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(bookType);
        dest.writeString(description);
        dest.writeString(iconUrl);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleBookTypeObj> CREATOR = new Creator<CircleBookTypeObj>() {
        @Override
        public CircleBookTypeObj createFromParcel(Parcel in) {
            return new CircleBookTypeObj(in);
        }

        @Override
        public CircleBookTypeObj[] newArray(int size) {
            return new CircleBookTypeObj[size];
        }
    };

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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
