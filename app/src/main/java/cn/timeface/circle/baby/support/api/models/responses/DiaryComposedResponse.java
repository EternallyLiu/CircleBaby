package cn.timeface.circle.baby.support.api.models.responses;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * Created by lidonglin on 2016/5/25.
 */
public class DiaryComposedResponse extends BaseResponse {

    MediaObj mediaObj;

    public MediaObj getMediaObj() {
        return mediaObj;
    }

    public void setMediaObj(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }
}
