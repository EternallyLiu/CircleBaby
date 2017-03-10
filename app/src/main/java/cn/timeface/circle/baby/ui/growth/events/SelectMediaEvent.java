package cn.timeface.circle.baby.ui.growth.events;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 选中media event
 * author : YW.SUN Created on 2017/2/21
 * email : sunyw10@gmail.com
 */
public class SelectMediaEvent {
    public final static int TYPE_TIME_MEDIA = 1;
    public final static int TYPE_MEDIA_MEDIA = 2;
    private int type;//1:选择时光下面的照片 2：选择照片
    private boolean select;
    private MediaObj mediaObj;

    public SelectMediaEvent(int type, boolean select, MediaObj mediaObj) {
        this.select = select;
        this.mediaObj = mediaObj;
        this.type = type;
    }

    public int getType() {
        return type;
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
