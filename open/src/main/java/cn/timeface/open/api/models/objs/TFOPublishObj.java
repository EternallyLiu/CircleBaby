package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * author: rayboot  Created on 16/7/8.
 * email : sy0725work@gmail.com
 */
public class TFOPublishObj implements Parcelable {
    String title;
    String content;
    String content_id;//"在第三方平台中的数据ID，可以为空，pod排版后会原样返回"
    List<TFOContentObj> content_list;
    List<TFOResourceObj> resource_list;
    String custom_data;

    public TFOPublishObj(String title, List<TFOContentObj> content_list) {
        this.title = title;
        this.content_list = content_list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TFOResourceObj> getResourceList() {
        return resource_list;
    }

    public void setResourceList(List<TFOResourceObj> resource_list) {
        this.resource_list = resource_list;
    }

    public String getCustomData() {
        return custom_data;
    }

    public void setCustomData(String custom_data) {
        this.custom_data = custom_data;
    }

    public List<TFOContentObj> getContentList() {
        return content_list;
    }

    public void setContentlList(List<TFOContentObj> content_list) {
        this.content_list = content_list;
    }

    public String getContentId() {
        return content_id;
    }

    public void setContentId(String content_id) {
        this.content_id = content_id;
    }

    public TFOPublishObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.content_id);
        dest.writeTypedList(this.content_list);
        dest.writeTypedList(this.resource_list);
        dest.writeString(this.custom_data);
    }

    protected TFOPublishObj(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.content_id = in.readString();
        this.content_list = in.createTypedArrayList(TFOContentObj.CREATOR);
        this.resource_list = in.createTypedArrayList(TFOResourceObj.CREATOR);
        this.custom_data = in.readString();
    }

    public static final Creator<TFOPublishObj> CREATOR = new Creator<TFOPublishObj>() {
        @Override
        public TFOPublishObj createFromParcel(Parcel source) {
            return new TFOPublishObj(source);
        }

        @Override
        public TFOPublishObj[] newArray(int size) {
            return new TFOPublishObj[size];
        }
    };
}
