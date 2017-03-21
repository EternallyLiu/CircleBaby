package cn.timeface.circle.baby.ui.circle.events;

import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;

/**
 * 圈时光详情选中media event
 * author : sunyanwei Created on 17-3-21
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectMediaEvent {

    public final static int TYPE_TIME_MEDIA = 1;
    public final static int TYPE_MEDIA_MEDIA = 2;
    private int type;//1:选择时光下面的照片 2：选择照片
    private boolean select;
    private CircleMediaObj mediaObj;

    public CircleSelectMediaEvent(int type, boolean select, CircleMediaObj mediaObj) {
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

    public CircleMediaObj getMediaObj() {
        return mediaObj;
    }

    public void setMediaObj(CircleMediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }
}
