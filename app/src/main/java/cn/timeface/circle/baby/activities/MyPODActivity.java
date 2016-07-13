package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.services.ApiService;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.open.activities.PODActivity;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOPublishObj;

public class MyPODActivity extends PODActivity {

    private String dataList;

    public static void open(Context context, String bookId, int bookType, List<TFOPublishObj> publishObjs, String dataList) {
        Intent intent = new Intent(context, MyPODActivity.class);
        intent.putExtra("book_type", bookType);
        intent.putExtra("book_id", bookId);
        intent.putParcelableArrayListExtra("publish_objs", (ArrayList<? extends Parcelable>) publishObjs);
        intent.putExtra("dataList", dataList);
        context.startActivity(intent);
    }

    public static void open(Context context, String bookId, int bookType, List<TFOPublishObj> publishObjs) {
        Intent intent = new Intent(context, MyPODActivity.class);
        intent.putExtra("book_type", bookType);
        intent.putExtra("book_id", bookId);
        intent.putParcelableArrayListExtra("publish_objs", (ArrayList<? extends Parcelable>) publishObjs);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = getIntent().getStringExtra("dataList");
    }

    @Override
    public void createBookInfo(TFOBookModel bookModel) {
        createBook(bookModel.getBookAuthor(),dataList,bookModel.getBookCover(),bookModel.getBookTitle(),5,bookModel.getBookTotalPage(),bookModel.getBookId());
    }

    private void createBook(String author, String dataList, String bookCover, String bookName, int bookType, int pageNum,String openBookId) {
        ApiFactory.getApi().getApiService().createBook(URLEncoder.encode(author), FastData.getBabyId(), bookCover, "", URLEncoder.encode(bookName), "", bookType, dataList, URLEncoder.encode(bookName), Long.valueOf(openBookId), pageNum)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        ToastUtil.showToast("创建成功");
                    } else {
                        ToastUtil.showToast(response.getInfo());
                    }
                }, error -> {
                    Log.e(TAG, "createBook:");
                });
    }

}
