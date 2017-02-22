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
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.constant.TFOConstant;
import cn.timeface.open.ui.PODActivity;

public class MyPODActivity extends PODActivity {

    private String dataList;
    private boolean edit;
    private String bookId;
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
        bookId = getIntent().getStringExtra("bookId");
        babyId = getIntent().getIntExtra("babyId", 0);
        this.localBookType = getIntent().getIntExtra("local_book_type", 0);
    }

    @Override
    public void createBookInfo(TFOBookModel bookModel) {
        Log.d(TAG,"createBookInfo:");
        if(edit){
//            createBook(bookModel.getBookAuthor(), dataList, bookModel.getBookCover(), bookModel.getBookTitle(), 5, bookModel.getBookTotalPage(), bookModel.getBookId(), bookType);

            createBook(
                    bookModel.getBookAuthor(),
                    bookModel.getBookCover(),
                    bookId,
                    bookModel.getBookTitle(),
                    localBookType,
                    "des",
                    dataList,
                    bookModel.getBookId(),
                    (int)bookModel.getBookType(),
                    bookModel.getBookTotalPage()
            );
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
            String imageId = model.getImageContentExpand().getImageId();
            String imageUrl = model.getImageContentExpand().getImageUrl();
            if(!TextUtils.isEmpty(imageId)&&!TextUtils.isEmpty(imageUrl)){
                editContent(imageId,imageUrl);
            }
        }
    }

    private void createBook(String author, String bookCover, String bookId, String bookName, int bookType, String des, String extra, String openBookId, int openBookType, int pageNum){
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
                pageNum
        )
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if(response.success()){
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
