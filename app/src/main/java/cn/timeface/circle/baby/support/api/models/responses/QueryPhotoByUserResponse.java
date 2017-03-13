package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 按成员查询所有的图片
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryPhotoByUserResponse extends BaseResponse {
    private String date;
    private List<MediaObj> mediaList;

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
}
