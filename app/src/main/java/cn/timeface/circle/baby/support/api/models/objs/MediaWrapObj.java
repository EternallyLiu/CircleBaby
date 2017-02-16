package cn.timeface.circle.baby.support.api.models.objs;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * media wrap obj
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class MediaWrapObj {
    private String date;
    private List<MediaObj> mediaList;
    private int mediaCount;
    private MediaTipObj tip;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<MediaObj> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public MediaTipObj getTip() {
        return tip;
    }

    public void setTip(MediaTipObj tip) {
        this.tip = tip;
    }
}
