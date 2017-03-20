package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;

/**
 * 查询圈时光上次发布的活动相册response
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryCircleLastActivityResponse extends BaseResponse {
    private CircleActivityAlbumObj activityAlbum;

    public CircleActivityAlbumObj getCommentInfo() {
        return activityAlbum;
    }

    public void setCommentInfo(CircleActivityAlbumObj activityAlbum) {
        this.activityAlbum = activityAlbum;
    }
}
