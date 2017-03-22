package cn.timeface.circle.baby.ui.circle.timelines.views;

import android.support.v7.widget.GridLayoutManager;

import cn.timeface.circle.baby.ui.circle.timelines.adapter.PublishAdapter;

/**
 * author : wangshuai Created on 2017/3/16
 * email : wangs1992321@gmail.com
 */
public class CircleGridStaggerLookup extends GridLayoutManager.SpanSizeLookup {

    private int type = PublishAdapter.TYPE_TIMELINE;

    private int columCount = 3;
    private boolean isSync = false;

    @Override
    public int getSpanSize(int position) {
        switch (getType()) {
            case PublishAdapter.TYPE_TIMELINE: {
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        return getColumCount();
                    case 4:
                        return isSync() ? getColumCount() : 1;
                    default:
                        return 1;
                }
            }
            case PublishAdapter.TYPE_SCHOOL:
            case PublishAdapter.TYPE_WORK: {
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                        return getColumCount();
                    default:
                        return 1;
                }
            }
        }
        return 0;
    }

    public CircleGridStaggerLookup(int type, int columCount, boolean isSync) {
        this.type = type;
        this.columCount = columCount;
        this.isSync = isSync;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getColumCount() {
        return columCount;
    }

    public void setColumCount(int columCount) {
        this.columCount = columCount;
    }
}
