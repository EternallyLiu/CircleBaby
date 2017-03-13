package cn.timeface.circle.baby.events;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.PrintPropertyTypeObj;
import cn.timeface.circle.baby.support.api.models.responses.LessResponse;

/**
 * @author YW.SUN
 * @from 2015/6/1
 * @TODO
 */
public class CartBuyNowEvent {
    public LessResponse response;
    public int requestCode;
    public int original;
    public List<PrintPropertyTypeObj> baseObjs;

    public CartBuyNowEvent(LessResponse reponse, int code, int original) {
        this.response = reponse;
        this.requestCode = code;
        this.original = original;
    }

    public CartBuyNowEvent(LessResponse response, int requestCode, int original, List<PrintPropertyTypeObj> baseObjs) {
        this.response = response;
        this.requestCode = requestCode;
        this.original = original;
        this.baseObjs = baseObjs;
    }
}
