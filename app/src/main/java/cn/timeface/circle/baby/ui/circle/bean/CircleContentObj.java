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
    protected List<CircleMediaObj> mediaList;   //图片列表
    protected String title;                     //标题

    public CircleContentObj() {
    }

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

    public List<CircleMediaObj> getMediaList() {
        if (mediaList == null) mediaList = new ArrayList<>(0);
        return mediaList;
    }

    public void setMediaList(List<CircleMediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.content);
        dest.writeLong(this.createDate);
        dest.writeTypedList(this.mediaList);
        dest.writeString(this.title);
    }

    protected CircleContentObj(Parcel in) {
        super(in);
        this.content = in.readString();
        this.createDate = in.readLong();
        this.mediaList = in.createTypedArrayList(CircleMediaObj.CREATOR);
        this.title = in.readString();
    }

}
