package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.responses.EditBookResponse;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.constant.TFOConstant;
import cn.timeface.open.ui.PODActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MyPODActivity extends PODActivity {

    private String dataList;
    private boolean edit;
    private String bookId;//localBookId
    private int babyId;
    private int localBookType;

    public static void open(Context context, String bookId , String openBookId,int localBookType, int openBookType, List<TFOPublishObj> publishObjs, String dataList, boolean edit, int babyId, ArrayList<String> keys, ArrayList<String> values, int rebuild) {
        Intent intent = new Intent(context, MyPODActivity.class);
        intent.putExtra("book_type", openBookType);//开放平台booktype
        intent.putExtra("book_id", openBookId);
        intent.putParcelableArrayListExtra(TFOConstant.PUBLISH_OBJS, (ArrayList<? extends Parcelable>) publishObjs);
        intent.putExtra("dataList", dataList);
        intent.putExtra("edit", edit);
        intent.putExtra("bookId", bookId);
        intent.putExtra("babyId", babyId);
        intent.putStringArrayListExtra(TFOConstant.POD_KEYS,keys);
        intent.putStringArrayListExtra(TFOConstant.POD_VALUES,values);
        intent.putExtra(TFOConstant.REBUILD_BOOK,rebuild);
        intent.putExtra("local_book_type", localBookType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = getIntent().getStringExtra("dataList");
        edit = getIntent().getBooleanExtra("edit", false);
        babyId = getIntent().getIntExtra("babyId", 0);
        this.localBookType = getIntent().getIntExtra("local_book_type", 0);
        bookId = getIntent().getStringExtra("bookId");
    }

    @Override
    public void createBookInfo(TFOBookModel bookModel) {
        Log.d(TAG, "createBookInfo:");
        if (edit) {
            createBook(
                    bookModel.getBookAuthor(),
                    bookModel.getBookCover(),
                    bookId,
                    bookModel.getBookTitle(),
                    localBookType,
                    bookModel.getBookSummary(),
                    dataList,
                    bookModel.getBookId(),
                    (int) bookModel.getBookType(),
                    bookModel.getBookTotalPage()
            );
        }
    }

    @Override
    protected void editBook(TFOBookModel bookModel) {
        if(TextUtils.isEmpty(bookId)) return;
//        addSubscription(
                ApiFactory.getApi().getApiService().saveProduction(
                        babyId,
                        bookModel.getBookAuthor(),
                        bookModel.getBookCover(),
                        bookId,
                        bookModel.getBookTitle(),
                        localBookType,
                        bookModel.getBookSummary(),
                        dataList,
                        bookModel.getBookId(),
                        (int) bookModel.getBookType(),
                        bookModel.getBookTotalPage())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(editBookResponse -> {
                            //编辑成功 更新编辑时间
                            return editBookResponse.success();
                        })
                        .flatMap(new Func1<EditBookResponse, Observable<BaseResponse>>() {
                            @Override
                            public Observable<BaseResponse> call(EditBookResponse editBookResponse) {
                                if (editBookResponse.success()) {
                                    EventBus.getDefault().post(new BookOptionEvent());
                                } else {
                                    ToastUtil.showToast(editBookResponse.getInfo());
                                }
                                return ApiFactory.getApi().getApiService().updateBookTime(bookId);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    if (!response.success()) {
                                        Log.e(TAG, response.info);
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, "editBookCover:");
                                    throwable.printStackTrace();
                                }
                        );
//        );
    }

    private void createBook(String author, String bookCover, String bookId, String bookName, int bookType, String des, String extra, String openBookId, int openBookType, int pageNum){
//        addSubscription(
                ApiFactory.getApi().getApiService().saveProduction(
                        FastData.getBabyId(),
                        author,
                        bookCover,
                        bookId,
                        bookName,
                        bookType,
                        des,
                        extra,
                        openBookId,
                        openBookType,
                        pageNum)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        this.bookId = response.getDataId();
                                        EventBus.getDefault().post(new BookOptionEvent(BookOptionEvent.BOOK_OPTION_CREATE, localBookType, bookId));
                                    } else {
                                        ToastUtil.showToast(response.info);
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        );
//        );
    }
}
