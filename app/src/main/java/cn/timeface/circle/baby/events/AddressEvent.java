package cn.timeface.circle.baby.events;

import cn.timeface.circle.baby.api.models.objs.AddressObj;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class AddressEvent {
    public AddressObj obj;

    public AddressEvent(AddressObj obj) {
        this.obj = obj;
    }

    public AddressObj getObj() {
        return obj;
    }

    public void setObj(AddressObj obj) {
        this.obj = obj;
    }
}
