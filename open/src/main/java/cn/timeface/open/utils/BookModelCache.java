package cn.timeface.open.utils;

import cn.timeface.open.api.models.objs.TFOBookModel;

/**
 * author: rayboot  Created on 16/7/5.
 * email : sy0725work@gmail.com
 * <p>
 * 为毛有这个类呢??
 * TFOBookModel可能很大,放在intent里可能超过extra的1M限制,所以做了个缓存
 */
public class BookModelCache {

    TFOBookModel bookModel;

    private static volatile BookModelCache sInst = null;  // volatile

    public static BookModelCache getInstance() {
        BookModelCache inst = sInst;
        if (inst == null) {
            synchronized (BookModelCache.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new BookModelCache();
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    public TFOBookModel getBookModel() {
        return bookModel;
    }

    public void setBookModel(TFOBookModel bookModel) {
        this.bookModel = bookModel;
    }
}
