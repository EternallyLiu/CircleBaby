package cn.timeface.circle.baby.ui.timelines.views;

import android.support.v7.widget.GridLayoutManager;

/**
 * author : wangshuai Created on 2017/2/7
 * email : wangs1992321@gmail.com
 */
public class GridStaggerLookup extends GridLayoutManager.SpanSizeLookup {

    public static final int MAX_MEDIA_SIZE_SHOW_GRID = 4;

    private boolean isShowSmail = false;
    private int mediaSize = 0;
    private int count = 0;
    private int columCount = 4;

    private int remoinder = 0;
    private int rowCount = 0;

    public int getColumCount() {
        return columCount;
    }

    public void setColumCount(int columCount) {
        this.columCount = columCount;
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

    public GridStaggerLookup(int mediaSize, int count) {
        this.mediaSize = mediaSize;
        this.count = count;
        this.rowCount = mediaSize / columCount;
        this.remoinder = mediaSize % columCount;
    }

    public GridStaggerLookup(int mediaSize, int count, int columCount) {
        this.mediaSize = mediaSize;
        this.count = count;
        this.columCount = columCount;
        this.rowCount = mediaSize / columCount;
        this.remoinder = mediaSize % columCount;
    }

    public GridStaggerLookup() {
    }

    @Override
    public int getSpanSize(int position) {
        if (mediaSize < MAX_MEDIA_SIZE_SHOW_GRID)
            return getColumCount();
        else if (position < mediaSize)
            return isShowSmail() ? 1 : getColumCount();
        else return getColumCount();
    }


    private int spanSize(int position) {
        if (position > mediaSize - 1)
            return getColumCount();

        if (remoinder == 0)
            return 1;
        else {
            int currentRow = (position / getColumCount()) + 1;
            if (currentRow > rowCount) {
                if (remoinder % 2 == 0)
                    return 2;
                else if (remoinder == 1)
                    return getColumCount();
                else if ((position + 1) % getColumCount() == remoinder)
                    return getColumCount();
                else return 2;
            } else return 1;
        }

    }

    public boolean isShowSmail() {
        return isShowSmail;
    }

    public void setShowSmail(boolean showSmail) {
        isShowSmail = showSmail;
    }
}
