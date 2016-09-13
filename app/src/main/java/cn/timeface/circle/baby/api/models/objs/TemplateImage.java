package cn.timeface.circle.baby.api.models.objs;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/27.
 */
public class TemplateImage extends BaseObj{
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
}
