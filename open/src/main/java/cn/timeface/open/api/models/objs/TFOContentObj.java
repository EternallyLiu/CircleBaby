package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * author: rayboot  Created on 16/7/8.
 * email : sy0725work@gmail.com
 */
public class TFOContentObj implements Parcelable {
    String subtitle;//"副标题",
    String content;//"正文内容",
    String sub_content_id;//"在第三方平台中的数据ID，可以为空，pod排版后会原样返回",
    List<TFOResourceObj> resource_list;

    public TFOContentObj(String subtitle, List<TFOResourceObj> resource_list) {
        this.subtitle = subtitle;
        this.resource_list = resource_list;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubContentId() {
        return sub_content_id;
    }

    public void setSubContentId(String sub_content_id) {
        this.sub_content_id = sub_content_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subtitle);
        dest.writeString(this.content);
        dest.writeString(this.sub_content_id);
        dest.writeTypedList(this.resource_list);
    }

    protected TFOContentObj(Parcel in) {
        this.subtitle = in.readString();
        this.content = in.readString();
        this.sub_content_id = in.readString();
        this.resource_list = in.createTypedArrayList(TFOResourceObj.CREATOR);
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
