package cn.timeface.circle.baby.events;

import cn.timeface.circle.baby.support.api.models.AddressItem;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class AddressEvent {
    public AddressItem obj;

    public AddressEvent(AddressItem obj) {
        this.obj = obj;
    }

    public AddressItem getObj() {
        return obj;
    }

    public void setObj(AddressItem obj) {
        this.obj = obj;
    }
}
