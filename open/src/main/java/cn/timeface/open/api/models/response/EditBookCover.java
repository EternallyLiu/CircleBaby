package cn.timeface.open.api.models.response;

/**
 * author: shiyan  Created on 8/25/16.
 * email : sy0725work@gmail.com
 */
public class EditBookCover {
    String book_id;//	YES	string	时光书在开放平台内的唯一ID
    String book_cover;//	YES	string	时光书封面地址

    public String getBookId() {
        return book_id;
    }

    public void setBookId(String book_id) {
        this.book_id = book_id;
    }

    public String getBookCover() {
        return book_cover;
    }

    public void setBookCover(String book_cover) {
        this.book_cover = book_cover;
    }
}
