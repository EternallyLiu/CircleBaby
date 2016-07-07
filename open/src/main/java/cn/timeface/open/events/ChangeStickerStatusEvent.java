package cn.timeface.open.events;

import cn.timeface.open.views.StickerView;

/**
 * author: rayboot  Created on 16/6/27.
 * email : sy0725work@gmail.com
 */
public class ChangeStickerStatusEvent {
    int status;
    StickerView stickerView;

    public StickerView getStickerView() {
        return stickerView;
    }

    public ChangeStickerStatusEvent(int status, StickerView stickerView) {
        this.status = status;
        this.stickerView = stickerView;
    }

    public int getStatus() {
        return status;
    }
}
