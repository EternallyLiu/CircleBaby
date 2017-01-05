package cn.timeface.circle.baby.support.api.models.responses;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;

/**
 * Created by lidonglin on 2016/8/19.
 */
public class LoginResponse extends BaseResponse {
    UserObj userInfo;
    int babycount;//当前用户的宝宝数量

    public int getBabycount() {
        return babycount;
    }

    public void setBabycount(int babycount) {
        this.babycount = babycount;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }
}
