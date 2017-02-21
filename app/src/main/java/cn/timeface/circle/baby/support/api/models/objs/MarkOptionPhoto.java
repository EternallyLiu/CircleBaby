package cn.timeface.circle.baby.support.api.models.objs;

import com.amap.api.maps2d.model.MarkerOptions;

/**
 * Created by Zhang Jiaofa on 16/8/10.
 */
public class MarkOptionPhoto {

    private MarkerOptions markerOptions;
    private MediaObj mediaObj;

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void setMediaObj(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }

    public MediaObj getMediaObj() {
        return mediaObj;
    }
}
