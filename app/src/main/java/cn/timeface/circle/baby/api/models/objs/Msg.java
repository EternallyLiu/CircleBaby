package cn.timeface.circle.baby.api.models.objs;

/**
 * Created by lidonglin on 2016/5/16.
 */
public class Msg {
    String content;
    int id;
    long time;
    TimeLineObj timeInfo;
    UserObj userInfo;
    int type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public TimeLineObj getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(TimeLineObj timeInfo) {
        this.timeInfo = timeInfo;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
