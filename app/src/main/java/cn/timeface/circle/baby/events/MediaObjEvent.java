package cn.timeface.circle.baby.events;

import cn.timeface.circle.baby.api.models.objs.MediaObj;

/**
 * Created by lidonglin on 2016/6/1.
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
