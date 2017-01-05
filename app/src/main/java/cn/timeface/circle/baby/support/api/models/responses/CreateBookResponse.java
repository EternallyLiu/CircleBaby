package cn.timeface.circle.baby.support.api.models.responses;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class CreateBookResponse extends BaseResponse {

    String bookId;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
