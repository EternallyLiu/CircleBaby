package cn.timeface.circle.baby.api.models.objs;

/**
 * Created by lidonglin on 2016/5/16.
 */
public class Msg {
    String content;
    String relation;
    int type;
    UserObj userInfo;
    TimeLineObj timeInfo;
    long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }

    public TimeLineObj getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(TimeLineObj timeInfo) {
        this.timeInfo = timeInfo;
    }
}
