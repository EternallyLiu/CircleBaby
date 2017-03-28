package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 成长圈中的图片对象
 * Created by lidonglin on 2017/3/14.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleMediaObj extends MediaObj implements Parcelable {
    List<GetCircleAllBabyObj> relateBabys;    //图片关联的宝宝
    long publisherId;                   //图片上传者的id

    public CircleMediaObj() {
    }

    public CircleMediaObj(String content, String imgUrl, int w, int h, long photographTime) {
        setContent(content);
        setImgUrl(imgUrl);
        setW(w);
        setH(h);
        setPhotographTime(photographTime);
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof MediaObj) {
            CircleMediaObj mediaObj = (CircleMediaObj) o;
            //mediaid 一样
            if (mediaObj != null && (getId() != 0 && getId() == mediaObj.getId()))
                return true;
            else if (mediaObj != null && getId() <= 0) {
                //判断本地唯一标记是否相同，相同那么就返回true 否则比较本地地址是否为空，不为空返回地址是否相同
                return !TextUtils.isEmpty(getLocalIdentifier()) && !TextUtils.isEmpty(mediaObj.getLocalIdentifier())
                        ? getLocalIdentifier().equals(mediaObj.getLocalIdentifier())
                        : !TextUtils.isEmpty(getLocalPath()) && !TextUtils.isEmpty(mediaObj.getLocalPath()) ? getLocalPath().equals(mediaObj.getLocalPath()) : !TextUtils.isEmpty(getImgUrl()) && !TextUtils.isEmpty(mediaObj.getImgUrl()) ? getImgUrl().equals(mediaObj.getImgUrl()) : false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (relateBabys != null ? relateBabys.hashCode() : 0);
        result = 31 * result + (int) (publisherId ^ (publisherId >>> 32));
        return result;
    }

    public List<GetCircleAllBabyObj> getRelateBabys() {
        if (relateBabys == null) relateBabys = new ArrayList<>(0);
        return relateBabys;
    }

    public void setRelateBabys(List<GetCircleAllBabyObj> relateBabys) {
        this.relateBabys = relateBabys;
    }

    public long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(long publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.relateBabys);
        dest.writeLong(this.publisherId);
    }

    protected CircleMediaObj(Parcel in) {
        super(in);
        this.relateBabys = in.createTypedArrayList(GetCircleAllBabyObj.CREATOR);
        this.publisherId = in.readLong();
    }

    public static final Creator<CircleMediaObj> CREATOR = new Creator<CircleMediaObj>() {
        @Override
        public CircleMediaObj createFromParcel(Parcel source) {
            return new CircleMediaObj(source);
        }

        @Override
        public CircleMediaObj[] newArray(int size) {
            return new CircleMediaObj[size];
        }
    };
}
