package cn.timeface.circle.baby.ui.growth.events;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 选中media event
 * author : YW.SUN Created on 2017/2/21
 * email : sunyw10@gmail.com
 */
public class SelectMediaEvent {
    private boolean select;
    private MediaObj mediaObj;

    public SelectMediaEvent(boolean select, MediaObj mediaObj) {
        this.select = select;
        this.mediaObj = mediaObj;
    }

    public boolean getSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public MediaObj getMediaObj() {
        return mediaObj;
    }

    public void setMediaObj(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }
}
