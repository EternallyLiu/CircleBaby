package cn.timeface.circle.baby.events;


import cn.timeface.circle.baby.api.models.AddressItem;

public class AddAddressFinishEvent {

    public final static int TYPE_SELECT = 1;
    public final static int TYPE_DELET = 2;
    public final static int TYPE_CHANGE = 3;
    public final static int TYPE_ADD = 4;
    public AddressItem addressModule;
    public int type;
    public int count;

    public AddAddressFinishEvent(AddressItem dict, int type) {
        addressModule = dict;
        this.type = type;
    }

    public AddAddressFinishEvent(AddressItem dict, int type, int count) {
        addressModule = dict;
        this.type = type;
        this.count = count;
    }
}
