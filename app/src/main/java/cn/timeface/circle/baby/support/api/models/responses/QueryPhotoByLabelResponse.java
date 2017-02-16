package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaTipObj;

/**
 * 按标签查询所有的图片
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryPhotoByLabelResponse extends BaseResponse {
    private int mediaCount;
    private List<MediaObj> mediaList;
    private MediaTipObj tip;

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public List<MediaObj> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public MediaTipObj getTip() {
        return tip;
    }

    public void setTip(MediaTipObj tip) {
        this.tip = tip;
    }
}
