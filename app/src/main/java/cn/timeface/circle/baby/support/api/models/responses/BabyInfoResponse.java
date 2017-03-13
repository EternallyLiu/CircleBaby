package cn.timeface.circle.baby.support.api.models.responses;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class BabyInfoResponse extends BaseResponse {
    BabyObj babyInfo;

    public BabyObj getBabyInfo() {
        return babyInfo;
    }

    public void setBabyInfo(BabyObj babyInfo) {
        this.babyInfo = babyInfo;
    }
}
