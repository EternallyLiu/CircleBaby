package cn.timeface.circle.baby.api.models.objs;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class UserObj extends BaseObj {
    String avatar;
    BabyObj babyObj;
    String nickName;
    String userId;

    public UserObj() {
    }

    public UserObj(String avatar, BabyObj babyObj, String nickName, String userId) {
        this.avatar = avatar;
        this.babyObj = babyObj;
        this.nickName = nickName;
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BabyObj getBabyObj() {
        return babyObj;
    }

    public void setBabyObj(BabyObj babyObj) {
        this.babyObj = babyObj;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
