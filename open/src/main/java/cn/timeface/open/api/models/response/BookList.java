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

    public int getTotalPage() {
        return total_page;
    }

    public void setTotalPage(int total_page) {
        this.total_page = total_page;
    }

    public List<TFOBookModel> getBookList() {
        return book_list;
    }

    public void setBookList(List<TFOBookModel> book_list) {
        this.book_list = book_list;
    }
}
