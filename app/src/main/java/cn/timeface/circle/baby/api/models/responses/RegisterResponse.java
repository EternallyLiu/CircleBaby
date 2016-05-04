package cn.timeface.circle.baby.api.models.responses;


import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.UserObj;

/**
 * author: rayboot  Created on 16/1/18.
 * email : sy0725work@gmail.com
 */
public class RegisterResponse extends BaseResponse {
    UserObj userInfo;

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }
}
