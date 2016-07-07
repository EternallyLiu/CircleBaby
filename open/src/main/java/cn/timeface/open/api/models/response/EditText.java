package cn.timeface.open.api.models.response;

import cn.timeface.open.api.models.objs.TFOBookElementModel;

/**
 * author: rayboot  Created on 16/6/28.
 * email : sy0725work@gmail.com
 */
public class EditText {
    String book_id;

    TFOBookElementModel element_model;

    public String getBookId() {
        return book_id;
    }

    public void setBookId(String book_id) {
        this.book_id = book_id;
    }

    public TFOBookElementModel getElementModel() {
        return element_model;
    }

    public void setElementModel(TFOBookElementModel element_model) {
        this.element_model = element_model;
    }

}
