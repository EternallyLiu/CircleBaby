package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class PrintParamObj extends BaseObj implements Parcelable{
    private String value; //值
    private String show; //显示名称
    private String imgUrl; //icon url
    private int isActive;
    private boolean isSelect;
    private String color;
    private String paper;
    private String pack;
    private String size;

    public PrintParamObj() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
//        return isActive;
        return isActive == 1;
    }

    public void setActive(boolean isActive) {
//        this.isActive = isActive;
        this.isActive = isActive ? 1 : 0;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<String> getColorList() {
        return parse2List(color);
    }

    public List<String> getPaperList() {
        return parse2List(paper);
    }

    public List<String> getPackList() {
        return parse2List(pack);
    }

    public List<String> getSizeList() {
        return parse2List(size);
    }

    private List<String> parse2List(String arrayStr) {
        List<String> array = null;
        if (!TextUtils.isEmpty(arrayStr)) {
            array = Arrays.asList(arrayStr.split(","));
        }
        return array;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.show);
        dest.writeString(this.imgUrl);
    }

    protected PrintParamObj(Parcel in) {
        this.value = in.readString();
        this.show = in.readString();
        this.imgUrl = in.readString();
    }

    public static final Parcelable.Creator<PrintParamObj> CREATOR = new Parcelable.Creator<PrintParamObj>() {
        @Override
        public PrintParamObj createFromParcel(Parcel source) {
            return new PrintParamObj(source);
        }

        @Override
        public PrintParamObj[] newArray(int size) {
            return new PrintParamObj[size];
        }
    };
}
