package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 *  成长圈中的图片对象
 * Created by lidonglin on 2017/3/14.
 */

public class CircleMediaObj extends MediaObj implements Parcelable {
    List<RelateBabyObj> relateBabys;

    protected CircleMediaObj(Parcel in) {
        super(in);
        relateBabys = in.createTypedArrayList(RelateBabyObj.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(relateBabys);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleMediaObj> CREATOR = new Creator<CircleMediaObj>() {
        @Override
        public CircleMediaObj createFromParcel(Parcel in) {
            return new CircleMediaObj(in);
        }

        @Override
        public CircleMediaObj[] newArray(int size) {
            return new CircleMediaObj[size];
        }
    };

    public List<RelateBabyObj> getRelateBabys() {
        return relateBabys;
    }

    public void setRelateBabys(List<RelateBabyObj> relateBabys) {
        this.relateBabys = relateBabys;
    }
}
