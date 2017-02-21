package cn.timeface.circle.baby.ui.growth.events;

import java.io.Serializable;

/**
 * Created by Zhang Jiaofa on 16/7/29.
 */
public class PhotoTimeSortEvent implements Serializable {

    public static final int SORT_PHOTO_TIME_ALL = 0;
    public static final int SORT_PHOTO_TIME_DATE = 1;
    public static final int SORT_PHOTO_TIME_MONTH = 2;
    public static final int SORT_PHOTO_TIME_YEAR = 3;
    public static final int SORT_PHOTO_TIME_MAP = 4;

    private int sortEventType;
    private String tabTitle;

    public PhotoTimeSortEvent(int sortEventType, String tabTitle) {
        this.sortEventType = sortEventType;
        this.tabTitle = tabTitle;
    }

    public int getSortEventType() {
        return sortEventType;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }
}
