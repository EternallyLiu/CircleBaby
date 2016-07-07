package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.open.managers.interfaces.IMoveParams;
import cn.timeface.open.managers.interfaces.IPageScale;

/**
 * @author liuxz:
 * @version OpenPlatPod 1.0 (子节点图片信息)
 * @date 2016-4-18 上午9:33:57
 */
public class TFOBookImageModel implements Parcelable, IPageScale, IMoveParams {

    String image_id;
    float image_width;// 图片尺寸－宽度
    float image_height;// 图片尺寸－高度
    float image_scale;// 图片显示在当前位置的缩放比(选填)
    float image_start_pointX;// 图片显示区域起始点X 相对于 element_left
    float image_start_pointY;// 图片显示区域起始点Y 相对于 element_top
    String image_remark;// 图片图注，不超过14个汉字，超出部分会被截取
    String image_content;// 图片配文，不超过300个汉字，超出部分会被截取
    long image_date;// 图片日期
    String image_primary_color;// 图片主色值
    String image_url;// 图片绝对路径
    float image_padding_top;
    float image_padding_left;

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

    public float getImageWidth() {
        return image_width;
    }

    public void setImageWidth(float image_width) {
        this.image_width = image_width;
    }

    public float getImageHeight() {
        return image_height;
    }

    public void setImageHeight(float image_height) {
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

    public float getImageStartPointX() {
        return image_start_pointX;
    }

    public void setImageStartPointX(float image_start_pointX) {
        this.image_start_pointX = image_start_pointX;
    }

    public float getImageStartPointY() {
        return image_start_pointY;
    }

    public void setImageStartPointY(float image_start_pointY) {
        this.image_start_pointY = image_start_pointY;
    }

    public float getImagePaddingTop() {
        return image_padding_top;
    }

    public void setImagePaddingTop(float image_padding_top) {
        this.image_padding_top = image_padding_top;
    }

    public float getImagePaddingLeft() {
        return image_padding_left;
    }

    public void setImagePaddingLeft(float image_padding_left) {
        this.image_padding_left = image_padding_left;
    }

    public TFOBookImageModel() {
    }

    @Override
    public void setPageScale(float scale) {
        this.image_padding_left *= scale;
        this.image_padding_top *= scale;
    }

    @Override
    public void resetPageScale(float scale) {
        this.image_padding_left /= scale;
        this.image_padding_top /= scale;
    }

    @Override
    public void moveParams(float movedX, float movedY, int eleW, int eleH, float scale, float rotation) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image_id);
        dest.writeFloat(this.image_width);
        dest.writeFloat(this.image_height);
        dest.writeFloat(this.image_scale);
        dest.writeFloat(this.image_start_pointX);
        dest.writeFloat(this.image_start_pointY);
        dest.writeString(this.image_remark);
        dest.writeString(this.image_content);
        dest.writeLong(this.image_date);
        dest.writeString(this.image_primary_color);
        dest.writeString(this.image_url);
        dest.writeFloat(this.image_padding_top);
        dest.writeFloat(this.image_padding_left);
    }

    protected TFOBookImageModel(Parcel in) {
        this.image_id = in.readString();
        this.image_width = in.readFloat();
        this.image_height = in.readFloat();
        this.image_scale = in.readFloat();
        this.image_start_pointX = in.readFloat();
        this.image_start_pointY = in.readFloat();
        this.image_remark = in.readString();
        this.image_content = in.readString();
        this.image_date = in.readLong();
        this.image_primary_color = in.readString();
        this.image_url = in.readString();
        this.image_padding_top = in.readFloat();
        this.image_padding_left = in.readFloat();
    }

    public static final Creator<TFOBookImageModel> CREATOR = new Creator<TFOBookImageModel>() {
        @Override
        public TFOBookImageModel createFromParcel(Parcel source) {
            return new TFOBookImageModel(source);
        }

        @Override
        public TFOBookImageModel[] newArray(int size) {
            return new TFOBookImageModel[size];
        }
    };
}