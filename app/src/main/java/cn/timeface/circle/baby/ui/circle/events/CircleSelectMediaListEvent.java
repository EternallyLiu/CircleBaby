package cn.timeface.circle.baby.ui.circle.events;

import java.util.List;

import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;

/**
 * 选择时光详情全选/全部选操作
 * author : sunyanwei Created on 17-3-21
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectMediaListEvent {

    public final static int TYPE_TIME_MEDIA = 1;
    public final static int TYPE_MEDIA_MEDIA = 2;
    private int type;//1:选择时光下面的照片 2：选择照片
    private boolean select;
    private List<CircleMediaObj> mediaObjList;

    public CircleSelectMediaListEvent(int type, boolean select, List<CircleMediaObj> mediaObjList) {
        this.select = select;
        this.mediaObjList = mediaObjList;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public List<CircleMediaObj> getMediaObjList() {
        return mediaObjList;
    }

    public void setMediaObjList(List<CircleMediaObj> mediaObjList) {
        this.mediaObjList = mediaObjList;
    }
}
