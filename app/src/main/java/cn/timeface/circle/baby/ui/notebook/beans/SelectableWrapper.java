package cn.timeface.circle.baby.ui.notebook.beans;

import java.io.Serializable;

/**
 * Created by JieGuo on 16/11/22.
 */

public class SelectableWrapper<T> implements Serializable {

    private static long serialVersionUID = 11L;

    private boolean selected = false;

    private String name = "";

    private T item;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
