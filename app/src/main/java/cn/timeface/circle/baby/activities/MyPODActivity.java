package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.open.activities.PODActivity;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.constants.Constant;

public class MyPODActivity extends PODActivity {

    private String dataList;
    private String bookId;

    public static void open(Context context, String bookId, String openBookId, int openBookType, List<TFOPublishObj> publishObjs, String dataList, boolean edit) {
        Intent intent = new Intent(context, MyPODActivity.class);
        intent.putExtra("book_type", openBookType);
        intent.putExtra("book_id", openBookId);
        intent.putParcelableArrayListExtra(Constant.PUBLISH_OBJS, (ArrayList<? extends Parcelable>) publishObjs);
        intent.putExtra("dataList", dataList);
        intent.putExtra("edit", edit);
        intent.putExtra("bookId", bookId);
        context.startActivity(intent);
    }

    public static void open(Context context, String openBookId, int openBookType, List<TFOPublishObj> publishObjs, boolean edit) {
        Intent intent = new Intent(context, MyPODActivity.class);
        intent.putExtra("book_type", openBookType);
        intent.putExtra("book_id", openBookId);
        intent.putParcelableArrayListExtra(Constant.PUBLISH_OBJS, (ArrayList<? extends Parcelable>) publishObjs);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = getIntent().getStringExtra("dataList");
        bookId = getIntent().getStringExtra("bookId");
    }

    @Override
    public void createBookInfo(TFOBookModel bookModel) {
        Log.d(TAG, "createBookInfo:");
        createBook(bookModel.getBookAuthor(), dataList, bookModel.getBookCover(), bookModel.getBookTitle(), 5, bookModel.getBookTotalPage(), bookModel.getBookId(), bookType);
    }

    @Override
    public void editBookInfo(TFOBookModel bookModel) {

    }

    private void createBook(String author, String dataList, String bookCover, String bookName, int type, int pageNum, String openBookId, int openBookType) {
        System.out.println("bookId ======== " + bookId);
        ApiFactory.getApi().getApiService().createBook(URLEncoder.encode(author), FastData.getBabyId(), bookCover, bookId, URLEncoder.encode(bookName), "", type, dataList, URLEncoder.encode(bookName), Long.valueOf(openBookId), pageNum, openBookType)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        EventBus.getDefault().post(new BookOptionEvent());
                    } else {
                        ToastUtil.showToast(response.getInfo());
                    }
                }, error -> {
                    Log.e(TAG, "createBook:");
                    error.printStackTrace();
                });
    }

}
