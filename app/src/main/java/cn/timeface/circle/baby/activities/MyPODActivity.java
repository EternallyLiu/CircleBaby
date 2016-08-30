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
import cn.timeface.circle.baby.api.services.ApiService;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.open.activities.PODActivity;
import cn.timeface.open.api.OpenApiFactory;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.api.models.response.BookCoverInfo;
import cn.timeface.open.constants.Constant;
import rx.functions.Action1;

public class MyPODActivity extends PODActivity {

    private String dataList;
    private boolean edit;
    private String bookId;
    private int babyId;

    public static void open(Context context, String bookId , String openBookId, int openBookType, List<TFOPublishObj> publishObjs, String dataList,boolean edit,int babyId,ArrayList<String> keys,ArrayList<String> values,int rebuild) {
        Intent intent = new Intent(context, MyPODActivity.class);
        intent.putExtra("book_type", openBookType);
        intent.putExtra("book_id", openBookId);
        intent.putParcelableArrayListExtra(Constant.PUBLISH_OBJS, (ArrayList<? extends Parcelable>) publishObjs);
        intent.putExtra("dataList", dataList);
        intent.putExtra("edit", edit);
        intent.putExtra("bookId", bookId);
        intent.putExtra("babyId", babyId);
        intent.putStringArrayListExtra(Constant.POD_KEYS,keys);
        intent.putStringArrayListExtra(Constant.POD_VALUES,values);
        intent.putExtra(Constant.REBUILD_BOOK,rebuild);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = getIntent().getStringExtra("dataList");
        edit = getIntent().getBooleanExtra("edit", false);
        bookId = getIntent().getStringExtra("bookId");
        babyId = getIntent().getIntExtra("babyId", 0);
    }

    @Override
    public void createBookInfo(TFOBookModel bookModel) {
        Log.d(TAG,"createBookInfo:");
        if(edit){
            createBook(bookModel.getBookAuthor(), dataList, bookModel.getBookCover(), bookModel.getBookTitle(), 5, bookModel.getBookTotalPage(), bookModel.getBookId(), bookType);
        }
    }

    @Override
    public void editCover(TFOBookModel bookModel) {
        editCover(bookModel.getBookAuthor(),bookModel.getBookTitle(),bookModel.getBookTitle(),bookModel.getBookCover());
    }

//    public void editCover(String openBookId) {
//        OpenApiFactory.getOpenApi().getApiService().bookcover(openBookId)
//                .compose(SchedulersCompat.applyIoSchedulers())
//                .subscribe(new Action1<BaseResponse<BookCoverInfo>>() {
//                               @Override
//                               public void call(BaseResponse<BookCoverInfo> bookCoverInfoBaseResponse) {
//                                   BookCoverInfo data = bookCoverInfoBaseResponse.getData();
//                                   List<String> book_cover = data.getBook_cover();
//                                   Log.d(TAG,"book_cover ======= "+book_cover);
//                               }
//                           }
//                        , new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//                            }
//                        });
//    }

    @Override
    public void editContent(TFOBookContentModel left, TFOBookContentModel right) {
        List<TFOBookElementModel> newList = new ArrayList<>();
        List<TFOBookElementModel> elementList = left.getElementList();
        elementList.addAll(right.getElementList());
        for(TFOBookElementModel model : elementList){
            if(model.getElementType() == TFOBookElementModel.TYPE_IMAGE){
                newList.add(model);
            }
        }
        for(TFOBookElementModel model : newList){
            editContent(model.getImageContentExpand().getImageId(),model.getImageContentExpand().getImageUrl());
        }
    }

    private void createBook(String author, String dataList, String bookCover, String bookName, int type, int pageNum, String openBookId, int openBookType) {
        Log.d("创建书","bookId ======== " + bookId);
        ApiFactory.getApi().getApiService().createBook(author, babyId, bookCover, bookId, bookName, "", type, dataList, bookName, Long.valueOf(openBookId), pageNum, openBookType)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    bookId = response.getBookId();
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

    //pod中更换封面外的图片
    private void editContent(String imageId, String imageUrl) {
        Log.d("pod中更换内容","mediaId ======= "+imageId);
        Log.d("pod中更换内容","imageUrl ======= "+imageUrl);
        ApiFactory.getApi().getApiService().updateTimeInfoForOpenApi(imageId,imageUrl)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        EventBus.getDefault().post(new BookOptionEvent());
                    } else {
//                        ToastUtil.showToast(response.getInfo());
                    }
                }, error -> {
                    Log.e(TAG, "editBookCover:");
                    error.printStackTrace();
                });
    }

    //pod中更换封面
    private void editCover(String bookAuthor, String bookTitle, String title, String bookCover){
        Log.d("pod中更换封面","bookCover ======= "+bookCover);
        ApiFactory.getApi().getApiService().editBookCover(bookId, babyId, bookCover, bookAuthor, bookTitle, title)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    bookId = response.getBookId();
                    if (response.success()) {
                        EventBus.getDefault().post(new BookOptionEvent());
                    } else {
                        ToastUtil.showToast(response.getInfo());
                    }
                }, error -> {
                    Log.e(TAG, "editBookCover:");
                    error.printStackTrace();
                });
    }

}
