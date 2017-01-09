package cn.timeface.circle.baby.ui.notebook.events;

import java.io.Serializable;

/**
 * Created by JieGuo on 16/10/24.
 */

public class PhotoUploadProgressEvent implements Serializable {

    private static long serialVersionUID = 111L;

    float progress = 0f;

    public PhotoUploadProgressEvent(float progress) {
        this.progress = progress;
    }

    public float getProgress() {
        return progress;
    }
}
