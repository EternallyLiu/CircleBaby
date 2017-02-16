package cn.timeface.circle.baby.support.api.models.objs;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 包含该用户的图片总数
 * author : YW.SUN Created on 2017/2/15
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class UserWrapObj {
    private int userImageCount;
    private UserObj userInfo;

    public int getUserImageCount() {
        return userImageCount;
    }

    public void setUserImageCount(int userImageCount) {
        this.userImageCount = userImageCount;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }
}
