package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * author : YW.SUN Created on 2017/2/16
 * email : sunyw10@gmail.com
 */
public class PaintingCollectionCustomDataObj implements Parcelable {
    private TFOResourceObj imgInfo;
    private PaintingCollectionRemarkObj remark;

    public TFOResourceObj getImgInfo() {
        return imgInfo;
    }

    public void setImgInfo(TFOResourceObj imgInfo) {
        this.imgInfo = imgInfo;
    }

    public PaintingCollectionRemarkObj getRemark() {
        return remark;
    }

    public void setRemark(PaintingCollectionRemarkObj remark) {
        this.remark = remark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.imgInfo, flags);
        dest.writeParcelable(this.remark, flags);
    }

    public PaintingCollectionCustomDataObj() {
    }

    protected PaintingCollectionCustomDataObj(Parcel in) {
        this.imgInfo = in.readParcelable(TFOResourceObj.class.getClassLoader());
        this.remark = in.readParcelable(PaintingCollectionRemarkObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<PaintingCollectionCustomDataObj> CREATOR = new Parcelable.Creator<PaintingCollectionCustomDataObj>() {
        @Override
        public PaintingCollectionCustomDataObj createFromParcel(Parcel source) {
            return new PaintingCollectionCustomDataObj(source);
        }

        @Override
        public PaintingCollectionCustomDataObj[] newArray(int size) {
            return new PaintingCollectionCustomDataObj[size];
        }
    };
}
