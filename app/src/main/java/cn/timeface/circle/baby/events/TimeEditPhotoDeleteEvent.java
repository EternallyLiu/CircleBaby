package cn.timeface.circle.baby.events;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class TimeEditPhotoDeleteEvent {
    public int position;
    public String url;

    public TimeEditPhotoDeleteEvent(int position, String url) {
        this.position = position;
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
