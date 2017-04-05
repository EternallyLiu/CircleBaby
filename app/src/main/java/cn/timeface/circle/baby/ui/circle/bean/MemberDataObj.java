package cn.timeface.circle.baby.ui.circle.bean;

import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleBabyBriefObj;

/**
 * Created by wangwei on 2017/4/5.
 */

public class MemberDataObj {
    private CircleUserInfo userInfo;
    private CircleBabyBriefObj babyBrief;

    public CircleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CircleUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public CircleBabyBriefObj getBabyBrief() {
        return babyBrief;
    }

    public void setBabyBrief(CircleBabyBriefObj babyBrief) {
        this.babyBrief = babyBrief;
    }

    public MemberDataObj(CircleUserInfo userInfo, CircleBabyBriefObj babyBrief) {
        this.userInfo = userInfo;
        this.babyBrief = babyBrief;

    }
}
