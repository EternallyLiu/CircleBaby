package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: rayboot  Created on 16/7/7.
 * email : sy0725work@gmail.com
 */
public class TFOBookType implements Parcelable {
    int bookType;
    String templatePic;
    String templateName;

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getTemplatePic() {
        return templatePic;
    }

    public void setTemplatePic(String templatePic) {
        this.templatePic = templatePic;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bookType);
        dest.writeString(this.templatePic);
        dest.writeString(this.templateName);
    }

    public TFOBookType() {
    }

    protected TFOBookType(Parcel in) {
        this.bookType = in.readInt();
        this.templatePic = in.readString();
        this.templateName = in.readString();
    }

    public static final Creator<TFOBookType> CREATOR = new Creator<TFOBookType>() {
        @Override
        public TFOBookType createFromParcel(Parcel source) {
            return new TFOBookType(source);
        }

        @Override
        public TFOBookType[] newArray(int size) {
            return new TFOBookType[size];
        }
    };
}
