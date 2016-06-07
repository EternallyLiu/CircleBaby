package cn.timeface.circle.baby.events;

import java.util.List;

import cn.timeface.circle.baby.api.models.objs.MediaObj;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class CardEvent {
    public List<MediaObj> mediaObjs;

    public CardEvent(List<MediaObj> mediaObjs) {
        this.mediaObjs = mediaObjs;
    }

    public List<MediaObj> getMediaObjs() {
        return mediaObjs;
    }

    public void setMediaObjs(List<MediaObj> mediaObjs) {
        this.mediaObjs = mediaObjs;
    }
}
