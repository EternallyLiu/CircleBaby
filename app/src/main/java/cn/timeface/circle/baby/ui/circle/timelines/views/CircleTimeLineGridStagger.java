package cn.timeface.circle.baby.ui.circle.timelines.views;

import android.support.v7.widget.GridLayoutManager;

/**
 * author : wangshuai Created on 2017/3/21
 * email : wangs1992321@gmail.com
 */
public class CircleTimeLineGridStagger extends GridLayoutManager.SpanSizeLookup {

    public static final int MAX_MEDIA_SIZE_SHOW_GRID = 16;

    private int beginCount = 0;

    private boolean isShowSmail = false;
    private int mediaSize = 0;
    private int count = 0;
    private int columCount = 4;

    public CircleTimeLineGridStagger(int columCount) {
        this.columCount = columCount;
    }

    public CircleTimeLineGridStagger(int beginCount, int mediaSize, int columCount) {
        this.beginCount = beginCount;
        this.mediaSize = mediaSize;
        this.columCount = columCount;
    }

    @Override
    public int getSpanSize(int position) {
        if (mediaSize < MAX_MEDIA_SIZE_SHOW_GRID)
            return getColumCount();
        else if (position >= getBeginCount() && position < mediaSize + getBeginCount())
            return isShowSmail() ? 1 : getColumCount();
        else return getColumCount();
    }

    public boolean isShowSmail() {
        return isShowSmail;
    }

    public void setShowSmail(boolean showSmail) {
        isShowSmail = showSmail;
    }

    public int getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(int mediaSize) {
        this.mediaSize = mediaSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getColumCount() {
        return columCount;
    }

    public void setColumCount(int columCount) {
        this.columCount = columCount;
    }

    public int getBeginCount() {
        return beginCount;
    }

    public void setBeginCount(int beginCount) {
        this.beginCount = beginCount;
    }
}
