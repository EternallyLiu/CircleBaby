package cn.timeface.circle.baby.events;

import cn.timeface.circle.baby.api.models.objs.MediaObj;

/**
 * Created by yusen on 2015/1/3.
 */
public class MediaObjEvent {
    public MediaObj mediaObj;

    public MediaObjEvent(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }

    public MediaObj getMediaObj() {
        return mediaObj;
    }

    public void setMediaObj(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }
}
