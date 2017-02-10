package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/27.
 */
public class TemplateImage extends BaseObj implements Parcelable {
    float degree;           //旋转角度 [-15,15]
    float h;                //裁剪高度
    int imageH;             //原图高度
    int imageW;             //原图宽度
    float w;                //裁剪宽度
    float x;                //相对原图x坐标  left
    float y;                //相对原图y坐标  top
    String yurl;            //原图地址
    long creationDate;      //照片拍摄时间

    public TemplateImage(float degree, float h, int imageH, int imageW, float w, float x, float y, String yurl, long creationDate) {
        this.degree = degree;
        this.h = h;
        this.imageH = imageH;
        this.imageW = imageW;
        this.w = w;
        this.x = x;
        this.y = y;
        this.yurl = yurl;
        this.creationDate = creationDate;
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }

    public int getImageH() {
        return imageH;
    }

    public void setImageH(int imageH) {
        this.imageH = imageH;
    }

    public int getImageW() {
        return imageW;
    }

    public void setImageW(int imageW) {
        this.imageW = imageW;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getYurl() {
        return yurl;
    }

    public void setYurl(String yurl) {
        this.yurl = yurl;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "TemplateImage{" +
                "degree=" + degree +
                ", h=" + h +
                ", imageH=" + imageH +
                ", imageW=" + imageW +
                ", w=" + w +
                ", x=" + x +
                ", y=" + y +
                ", yurl='" + yurl + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.degree);
        dest.writeFloat(this.h);
        dest.writeInt(this.imageH);
        dest.writeInt(this.imageW);
        dest.writeFloat(this.w);
        dest.writeFloat(this.x);
        dest.writeFloat(this.y);
        dest.writeString(this.yurl);
        dest.writeLong(this.creationDate);
    }

    protected TemplateImage(Parcel in) {
        this.degree = in.readFloat();
        this.h = in.readFloat();
        this.imageH = in.readInt();
        this.imageW = in.readInt();
        this.w = in.readFloat();
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.yurl = in.readString();
        this.creationDate = in.readLong();
    }

    public static final Parcelable.Creator<TemplateImage> CREATOR = new Parcelable.Creator<TemplateImage>() {
        @Override
        public TemplateImage createFromParcel(Parcel source) {
            return new TemplateImage(source);
        }

        @Override
        public TemplateImage[] newArray(int size) {
            return new TemplateImage[size];
        }
    };
}
