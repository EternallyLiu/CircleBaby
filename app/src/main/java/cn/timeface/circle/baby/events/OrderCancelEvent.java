package cn.timeface.circle.baby.events;

/**
 * @author SUN
 * @from 2014/12/22
 * @TODO
 */
public class OrderCancelEvent {
    public String orderId;

    public OrderCancelEvent(String orderId) {
        this.orderId = orderId;
    }
}
