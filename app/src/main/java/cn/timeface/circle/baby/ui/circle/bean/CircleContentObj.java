package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 圈内容的基类
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleContentObj extends BaseObj implements Parcelable {
    protected String content;                   //内容
    protected long createDate;                  //创建时间
    protected List<MediaObj> mediaList;   //图片列表
    protected String title;                     //标题

    public CircleContentObj() {
    }

    protected CircleContentObj(Parcel in) {
        super(in);
        content = in.readString();
        createDate = in.readLong();
        mediaList = in.createTypedArrayList(MediaObj.CREATOR);
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(content);
        dest.writeLong(createDate);
        dest.writeTypedList(mediaList);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleContentObj> CREATOR = new Creator<CircleContentObj>() {
        @Override
        public CircleContentObj createFromParcel(Parcel in) {
            return new CircleContentObj(in);
        }

        @Override
        public CircleContentObj[] newArray(int size) {
            return new CircleContentObj[size];
        }
    };

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public List<MediaObj> getMediaList() {
        if (mediaList == null) mediaList = new ArrayList<>(0);
        return mediaList;
    }

    public void setMediaList(List<MediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
