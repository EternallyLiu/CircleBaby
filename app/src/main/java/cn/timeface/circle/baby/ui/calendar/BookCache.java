package cn.timeface.circle.baby.ui.calendar;

import java.util.WeakHashMap;

import cn.timeface.open.api.bean.obj.TFOBookModel;


/**
 * Created by JieGuo on 16/11/3.
 */
public class BookCache {

    private static BookCache ourInstance = new BookCache();

    private WeakHashMap<String, TFOBookModel> bookModels = new WeakHashMap<>(4);

    public synchronized void putModelById(String id, TFOBookModel book) {
        if (bookModels.size() > 3) {
            bookModels.clear();
        }
        bookModels.put(id, book);
    }

    public synchronized TFOBookModel getModelById(String id) {

        if (bookModels.containsKey(id)) {
            return bookModels.get(id);
        }
        return null;
    }

    public synchronized void clear() {
        bookModels.clear();
    }

    public static BookCache getInstance() {
        return ourInstance;
    }

    private BookCache() {
    }
}
