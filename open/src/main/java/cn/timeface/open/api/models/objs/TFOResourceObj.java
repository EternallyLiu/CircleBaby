package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: rayboot  Created on 16/7/8.
 * email : sy0725work@gmail.com
 */
public class TFOResourceObj implements Parcelable {
    String image_id;//":"图片ID，可以由第三方维护",
    String image_url;//":"图片绝对路径",
    int image_width;//":"图片尺寸－宽度",
    int image_height;//":"图片尺寸－高度",
    float image_scale;//":"图片显示在当前位置的缩放比",
    String image_remark;//":"图片图注，不超过14个汉字，超出部分会被截取",
    String image_content;//":"图片配文，不超过300个汉字，超出部分会被截取",
    long image_date;//":130000,
    String image_primary_color;//":"图片主色值",

    public TFOResourceObj(String image_url, int image_width, int image_height, String image_remark) {
        this.image_url = image_url;
        this.image_width = image_width;
        this.image_height = image_height;
        this.image_remark = image_remark;
    }

    public String getImageId() {
        return image_id;
    }

    public void setImageId(String image_id) {
        this.image_id = image_id;
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

    public float getImageScale() {
        return image_scale;
    }

    public void setImageScale(float image_scale) {
        this.image_scale = image_scale;
    }

    public String getImageRemark() {
        return image_remark;
    }

    public void setImageRemark(String image_remark) {
        this.image_remark = image_remark;
    }

    public String getImageContent() {
        return image_content;
    }

    public void setImageContent(String image_content) {
        this.image_content = image_content;
    }

    public long getImageDate() {
        return image_date;
    }

    public void setImageDate(long image_date) {
        this.image_date = image_date;
    }

    public String getImagePrimaryColor() {
        return image_primary_color;
    }

    public void setImagePrimaryColor(String image_primary_color) {
        this.image_primary_color = image_primary_color;
    }

    public TFOResourceObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image_id);
        dest.writeString(this.image_url);
        dest.writeInt(this.image_width);
        dest.writeInt(this.image_height);
        dest.writeFloat(this.image_scale);
        dest.writeString(this.image_remark);
        dest.writeString(this.image_content);
        dest.writeLong(this.image_date);
        dest.writeString(this.image_primary_color);
    }

    protected TFOResourceObj(Parcel in) {
        this.image_id = in.readString();
        this.image_url = in.readString();
        this.image_width = in.readInt();
        this.image_height = in.readInt();
        this.image_scale = in.readFloat();
        this.image_remark = in.readString();
        this.image_content = in.readString();
        this.image_date = in.readLong();
        this.image_primary_color = in.readString();
    }

    public static final Creator<TFOResourceObj> CREATOR = new Creator<TFOResourceObj>() {
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
