package cn.timeface.open.api.models.response;

import java.util.List;

import cn.timeface.open.api.models.objs.TFOBookContentModel;

/**
 * author: shiyan  Created on 8/25/16.
 * email : sy0725work@gmail.com
 */
public class BookCoverInfo {
    String book_id;//	YES	string	时光书在开放平台内的唯一ID
    List<String> book_cover;//	YES	string	时光书封面地址
    List<TFOBookContentModel> content_model;//	YES	object	时光书封面完整数据

    public String getBookId() {
        return book_id;
    }

    public void setBookId(String book_id) {
        this.book_id = book_id;
    }

    public List<String> getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(List<String> book_cover) {
        this.book_cover = book_cover;
    }

    public List<TFOBookContentModel> getContentModel() {
        return content_model;
    }

    public void setContentModel(List<TFOBookContentModel> content_model) {
        this.content_model = content_model;
    }
}
