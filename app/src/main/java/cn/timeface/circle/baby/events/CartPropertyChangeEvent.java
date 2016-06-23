package cn.timeface.circle.baby.events;


import cn.timeface.circle.baby.api.models.objs.BasePrintProperty;

/**
 * @author YW.SUN
 * @from 2015/11/19
 * @TODO
 */
public class CartPropertyChangeEvent {
    public BasePrintProperty printPropertyObj;
    public float price;

    public CartPropertyChangeEvent(BasePrintProperty obj) {
        this.printPropertyObj = obj;
    }

    public CartPropertyChangeEvent(BasePrintProperty printPropertyObj, float price) {
        this.printPropertyObj = printPropertyObj;
        this.price = price;
    }
}
