package cn.timeface.open.events;

import cn.timeface.open.api.models.response.CoverColor;

/**
 * Created by zhsheng on 2016/7/11.
 */
public class SelectColorEvent {

    private CoverColor coverColor;

    public SelectColorEvent(CoverColor color) {
        this.coverColor = color;
    }

    public CoverColor getCoverColor() {
        return coverColor;
    }

    public void setCoverColor(CoverColor coverColor) {
        this.coverColor = coverColor;
    }
}
