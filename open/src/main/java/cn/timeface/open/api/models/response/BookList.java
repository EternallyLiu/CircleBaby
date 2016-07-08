package cn.timeface.open.api.models.response;

import java.util.List;

import cn.timeface.open.api.models.objs.TFOBookModel;

/**
 * author: rayboot  Created on 16/7/7.
 * email : sy0725work@gmail.com
 */
public class BookList {
    int total_page;
    List<TFOBookModel> book_list;

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public List<TFOBookModel> getBook_list() {
        return book_list;
    }

    public void setBook_list(List<TFOBookModel> book_list) {
        this.book_list = book_list;
    }
}
