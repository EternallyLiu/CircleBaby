package cn.timeface.circle.baby.api.models.objs;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/17.
 */
public class BabyListInfo extends BaseObj{
    BabyObj babyInfo;
    int owner;

    public BabyObj getBabyInfo() {
        return babyInfo;
    }

    public void setBabyInfo(BabyObj babyInfo) {
        this.babyInfo = babyInfo;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }
}
