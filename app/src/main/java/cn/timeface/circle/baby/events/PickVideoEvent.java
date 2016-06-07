package cn.timeface.circle.baby.events;

import cn.timeface.circle.baby.api.models.VideoInfo;

/**
 * Created by lidonglin on 2016/6/1.
 */
public class PickVideoEvent {
    public VideoInfo videoInfo;

    public PickVideoEvent(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }
}
