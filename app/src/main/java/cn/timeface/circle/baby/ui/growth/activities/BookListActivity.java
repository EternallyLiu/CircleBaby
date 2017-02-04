package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.SelectThemeActivity;
import cn.timeface.circle.baby.activities.TimeBookPickerPhotoActivity;
import cn.timeface.circle.baby.dialogs.ProductionMenuDialog;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.BookPresenter;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.adapters.BookListAdapter;

/**
 * 书作品列表
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class BookListActivity extends ProductionListActivity implements BookPresentation.View, View.OnClickListener{
    BookListAdapter bookListAdapter;
    BookPresenter bookPresenter;

    public static void open(Context context, int bookType){
        Intent intent = new Intent(context, BookListActivity.class);
        intent.putExtra("book_type", bookType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTip.setVisibility(View.GONE);
        bookPresenter = new BookPresenter(this);
        bookPresenter.loadData(bookType);
        getSupportActionBar().setTitle(FastData.getBabyName() + "的" + BookModel.getGrowthBookName(bookType));
    }

    @Override
    public void showErr(String errMsg) {
        showToast(errMsg);
    }

    @Override
    public void setStateView(boolean loading) {
        if(loading){
            stateView.setVisibility(View.VISIBLE);
            stateView.loading();
        } else {
            stateView.finish();
        }
    }

    @Override
    public void setBookData(List<BookObj> bookObjs) {
        if(bookListAdapter == null){
            bookListAdapter = new BookListAdapter(this, bookObjs, this);
            rvBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvBooks.setAdapter(bookListAdapter);
        } else {
            bookListAdapter.setListData(bookObjs);
            bookListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            switch (bookType) {
                //精装照片书
                case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                    apiService.queryImageInfoList("", BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                if (response.success()) {
                                    List<ImageInfoListObj> dataList = response.getDataList();
                                    for (ImageInfoListObj obj : dataList) {
                                        List<MediaObj> mediaList = obj.getMediaList();
                                        int timeId = obj.getTimeId();
                                        for (MediaObj mediaObj : mediaList) {
                                            mediaObj.setTimeId(timeId);
                                        }
                                    }
                                    //直接进入挑选照片页面
                                    TimeBookPickerPhotoActivity.open(this, BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, dataList);
                                } else {
                                    ToastUtil.showToast(response.getInfo());
                                }
                            }, error -> {
                                Log.e(TAG, "queryImageInfoList:");
                            });
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_menu){
            BookObj bookObj = (BookObj) view.getTag(R.string.tag_obj);
            ProductionMenuDialog productionMenuDialog = ProductionMenuDialog.newInstance(
                    bookType,
                    String.valueOf(bookObj.getBookId()),
                    bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK);
            productionMenuDialog.show(getSupportFragmentManager(), "");
        }
    }
}
