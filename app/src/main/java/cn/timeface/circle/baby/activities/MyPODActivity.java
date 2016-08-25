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
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.constants.Constant;

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

    public static void open(Context context,String bookId, String openBookId, int openBookType, List<TFOPublishObj> publishObjs,boolean edit,int babyId,ArrayList<String> keys,ArrayList<String> values) {
        Intent intent = new Intent(context, MyPODActivity.class);
        intent.putExtra("book_type", openBookType);
        intent.putExtra("book_id", openBookId);
        intent.putParcelableArrayListExtra(Constant.PUBLISH_OBJS, (ArrayList<? extends Parcelable>) publishObjs);
        intent.putExtra("edit", edit);
        intent.putExtra("bookId", bookId);
        intent.putExtra("babyId", babyId);
        intent.putStringArrayListExtra(Constant.POD_KEYS,keys);
        intent.putStringArrayListExtra(Constant.POD_VALUES,values);
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
        createBook(bookModel.getBookAuthor(), dataList, bookModel.getBookCover(), bookModel.getBookTitle(), 5, bookModel.getBookTotalPage(), bookModel.getBookId(), bookType);
    }

    @Override
    public void editCover(TFOBookContentModel left, TFOBookContentModel right) {
        List<TFOBookElementModel> elementList = right.getElementList();
        for(TFOBookElementModel model : elementList){
            String imageUrl = model.getImageContentExpand().getImageUrl();
            String imageId = model.getImageContentExpand().getImageId();
            Log.d(TAG,"imageUrl ========== " + imageUrl);
            Log.d(TAG,"imageId ========== " + imageId);
        }

        //left封底   right封面
//        ApiFactory.getApi().getApiService().editCover(right.get)
//                .compose(SchedulersCompat.applyIoSchedulers())
//                .subscribe(response -> {
//                    bookId = response.getBookId();
//                    if (response.success()) {
//                        EventBus.getDefault().post(new BookOptionEvent());
//                    } else {
//                        ToastUtil.showToast(response.getInfo());
//                    }
//                }, error -> {
//                    Log.e(TAG, "createBook:");
//                    error.printStackTrace();
//                });
    }

    @Override
    public void editContent(TFOBookContentModel left, TFOBookContentModel right) {

    }

    private void createBook(String author, String dataList, String bookCover, String bookName, int type, int pageNum, String openBookId, int openBookType) {
        System.out.println("bookId ======== " + bookId);
        ApiFactory.getApi().getApiService().createBook(URLEncoder.encode(author), babyId, bookCover, bookId, URLEncoder.encode(bookName), "", type, dataList, URLEncoder.encode(bookName), Long.valueOf(openBookId), pageNum, openBookType)
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

    //pod中跟换封面外的图片
    private void editBook(){

    }

}
