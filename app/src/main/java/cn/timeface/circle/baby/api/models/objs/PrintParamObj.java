package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class PrintParamObj extends BaseObj implements Parcelable{
    private String value; //值
    private String show; //显示名称
    private String imgUrl; //icon url
    private boolean isActive = true;
    private boolean isSelect;

    public PrintParamObj() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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
