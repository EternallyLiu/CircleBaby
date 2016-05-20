package cn.timeface.circle.baby.api.models.objs;

/**
 * Created by lidonglin on 2016/5/16.
 */
public class SystemMsg {
    String avatar;
    String content;
    String name;
    long time;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
