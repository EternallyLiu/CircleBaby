package cn.timeface.circle.baby.events;

import com.wechat.photopicker.adapter.PhotoSelectorAdapter2;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class NewPickerAdapterEvent {
    public PhotoSelectorAdapter2 photoSelectorAdapter2;

    public NewPickerAdapterEvent(PhotoSelectorAdapter2 photoSelectorAdapter2) {
        this.photoSelectorAdapter2 = photoSelectorAdapter2;
    }

    public PhotoSelectorAdapter2 getPhotoSelectorAdapter2() {
        return photoSelectorAdapter2;
    }

    public void setPhotoSelectorAdapter2(PhotoSelectorAdapter2 photoSelectorAdapter2) {
        this.photoSelectorAdapter2 = photoSelectorAdapter2;
    }
}
