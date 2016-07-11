package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: rayboot  Created on 16/7/8.
 * email : sy0725work@gmail.com
 */
public class TFOResourceObj implements Parcelable {
    String image_url;
    int image_width;
    int image_height;
    String image_remark;

    public TFOResourceObj(String image_url, int image_width, int image_height, String image_remark) {
        this.image_url = image_url;
        this.image_width = image_width;
        this.image_height = image_height;
        this.image_remark = image_remark;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public int getImageWidth() {
        return image_width;
    }

    public void setImageWidth(int image_width) {
        this.image_width = image_width;
    }

    public int getImageHeight() {
        return image_height;
    }

    public void setImageHeight(int image_height) {
        this.image_height = image_height;
    }

    public String getImageRemark() {
        return image_remark;
    }

    public void setImageRemark(String image_remark) {
        this.image_remark = image_remark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image_url);
        dest.writeInt(this.image_width);
        dest.writeInt(this.image_height);
        dest.writeString(this.image_remark);
    }

    public TFOResourceObj() {
    }

    protected TFOResourceObj(Parcel in) {
        this.image_url = in.readString();
        this.image_width = in.readInt();
        this.image_height = in.readInt();
        this.image_remark = in.readString();
    }

    public static final Parcelable.Creator<TFOResourceObj> CREATOR = new Parcelable.Creator<TFOResourceObj>() {
        @Override
        public TFOResourceObj createFromParcel(Parcel source) {
            return new TFOResourceObj(source);
        }

        @Override
        public TFOResourceObj[] newArray(int size) {
            return new TFOResourceObj[size];
        }
    };
}
