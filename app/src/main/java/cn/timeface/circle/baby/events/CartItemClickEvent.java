package cn.timeface.circle.baby.events;

import android.view.View;

/**
 * @author YW.SUN
 * @from 2015/5/27
 * @TODO
 */
public class CartItemClickEvent {
    public View view;

    public CartItemClickEvent(View view) {
        this.view = view;
    }
}
