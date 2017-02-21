package cn.timeface.circle.baby.ui.growth.events;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.PhotoGroupItem;

/**
 * Created by Zhang Jiaofa on 16/8/2.
 */
public class PhotoDataChangeEvent {

    private List<PhotoGroupItem> photoModels = new ArrayList<>();

    public PhotoDataChangeEvent(List<PhotoGroupItem> photoModels) {
        this.photoModels = photoModels;
    }

    public List<PhotoGroupItem> getPhotoModels() {
        return photoModels;
    }
}
