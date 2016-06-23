package cn.timeface.circle.baby.events;


import cn.timeface.circle.baby.api.models.responses.LessResponse;

/**
 * @author YW.SUN
 * @from 2015/6/1
 * @TODO
 */
public class CartBuyNowEvent {
    public LessResponse response;
    public int requestCode;
    public int original;

    public CartBuyNowEvent(LessResponse reponse, int code, int original) {
        this.response = reponse;
        this.requestCode = code;
        this.original = original;
    }
}
