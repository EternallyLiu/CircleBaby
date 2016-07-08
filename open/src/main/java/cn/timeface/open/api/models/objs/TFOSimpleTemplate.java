package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: rayboot  Created on 16/7/4.
 * email : sy0725work@gmail.com
 */
public class TFOSimpleTemplate implements Parcelable {
    int template_id;
    String thumbnail;

    public int getTemplateId() {
        return template_id;
    }

    public void setTemplateId(int template_id) {
        this.template_id = template_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.template_id);
        dest.writeString(this.thumbnail);
    }

    public TFOSimpleTemplate() {
    }

    protected TFOSimpleTemplate(Parcel in) {
        this.template_id = in.readInt();
        this.thumbnail = in.readString();
    }

    public static final Creator<TFOSimpleTemplate> CREATOR = new Creator<TFOSimpleTemplate>() {
        @Override
        public TFOSimpleTemplate createFromParcel(Parcel source) {
            return new TFOSimpleTemplate(source);
        }

        @Override
        public TFOSimpleTemplate[] newArray(int size) {
            return new TFOSimpleTemplate[size];
        }
    };
}
