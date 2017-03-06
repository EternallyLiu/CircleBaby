package cn.timeface.circle.baby.ui.growth.events;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 选择时光详情全选/全部选操作
 * author : YW.SUN Created on 2017/3/4
 * email : sunyw10@gmail.com
 */
public class SelectMediaListEvent {
    private boolean select;
    private List<MediaObj> mediaObjList;

    public SelectMediaListEvent(boolean select, List<MediaObj> mediaObjList) {
        this.select = select;
        this.mediaObjList = mediaObjList;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public List<MediaObj> getMediaObjList() {
        return mediaObjList;
    }

    public void setMediaObjList(List<MediaObj> mediaObjList) {
        this.mediaObjList = mediaObjList;
    }
}
