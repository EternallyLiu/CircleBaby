package cn.timeface.circle.baby.support.api.models.objs;

/**
 * Created by lidonglin on 2016/5/16.
 */
public class FamilyMemberInfo {
    int count;
    long time;
    UserObj userInfo;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }
}
