package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * author: rayboot  Created on 16/7/8.
 * email : sy0725work@gmail.com
 */
public class TFOContentObj implements Parcelable {
    String subtitle;
    List<TFOResourceObj> resource_list;

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<TFOResourceObj> getResourceList() {
        return resource_list;
    }

    public void setResourceList(List<TFOResourceObj> resource_list) {
        this.resource_list = resource_list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subtitle);
        dest.writeList(this.resource_list);
    }

    public TFOContentObj() {
    }

    protected TFOContentObj(Parcel in) {
        this.subtitle = in.readString();
        this.resource_list = new ArrayList<TFOResourceObj>();
        in.readList(this.resource_list, TFOResourceObj.class.getClassLoader());
    }

    public static final Creator<TFOContentObj> CREATOR = new Creator<TFOContentObj>() {
        @Override
        public TFOContentObj createFromParcel(Parcel source) {
            return new TFOContentObj(source);
        }

        @Override
        public TFOContentObj[] newArray(int size) {
            return new TFOContentObj[size];
        }
    };
}
