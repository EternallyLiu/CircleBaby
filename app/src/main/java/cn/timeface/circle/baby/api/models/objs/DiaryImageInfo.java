package cn.timeface.circle.baby.api.models.objs;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/25.
 */
public class DiaryImageInfo extends BaseObj{
    String objectkey;
    int width;
    int height;

    public DiaryImageInfo(String objectkey, int width, int height) {
        this.objectkey = objectkey;
        this.width = width;
        this.height = height;
    }

    public String getObjectkey() {
        return objectkey;
    }

    public void setObjectkey(String objectkey) {
        this.objectkey = objectkey;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
