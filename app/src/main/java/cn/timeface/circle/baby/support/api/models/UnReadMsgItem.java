package cn.timeface.circle.baby.support.api.models;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseModule;


/**
 * @author rayboot
 * @from 14-3-26 9:38
 * @TODO 未读消息Item
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class UnReadMsgItem extends BaseModule {
    public static final int TYPE_PRIVATE_MSG = 1;
    public static final int TYPE_AT = 2;
    public static final int TYPE_COMMENT = 3;
    public static final int TYPE_PRISE = 4;
    public static final int TYPE_NOTIFICATION = 5;
    private static final long serialVersionUID = 1L;
    private int type;
    private int count;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCountString() {
        if (count > 99) {
            return "99+";
        }
        return count + "";
    }
}
