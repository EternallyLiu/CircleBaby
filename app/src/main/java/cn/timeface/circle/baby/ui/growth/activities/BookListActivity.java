package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MineBookActivity;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.activities.SelectThemeActivity;
import cn.timeface.circle.baby.activities.TimeBookPickerPhotoActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.ProductionMenuDialog;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.BookPresenter;
import cn.timeface.circle.baby.support.utils.BookPrintHelper;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.adapters.BookListAdapter;

/**
 * 书作品列表
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class BookListActivity extends ProductionListActivity implements BookPresentation.View, View.OnClickListener, IEventBus{
    BookListAdapter bookListAdapter;
    BookPresenter bookPresenter;
    ProductionMenuDialog productionMenuDialog;

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
        btnAskPrint.setVisibility(View.GONE);
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

        tvEmptyInfo.setText(FastData.getBabyName() + BookModel.getGrowthBookName(bookType) + "为空哦，赶紧发布内容，制作一本吧~");
        llEmpty.setVisibility(bookListAdapter.getListData().isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            switch (bookType) {
                //精装照片书
                case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                    SelectThemeActivity.open(this);
                    break;
                //绘画集
                case BookModel.BOOK_TYPE_PAINTING:
                    SelectServerPhotoActivity.open(this, bookType, 111, "", "");
                    break;
                //成长纪念册
                case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                    SelectServerTimeActivity.open(this, bookType, 113, "", "");
                    break;
                //成长语录
                case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                    SelectServerTimeActivity.open(this, bookType, 114, "", "");
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        BookObj bookObj = (BookObj) view.getTag(R.string.tag_obj);

        switch (view.getId()) {
            case R.id.iv_menu:
                if (productionMenuDialog == null) {
                    ProductionMenuDialog productionMenuDialog = ProductionMenuDialog.newInstance(
                            bookType,
                            String.valueOf(bookObj.getBookId()),
                            bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK);
                    productionMenuDialog.show(getSupportFragmentManager(), "");
                }
                break;

            case R.id.tv_print:
                new BookPrintHelper(
                        this,
                        bookObj.getBookType(),
                        bookObj.getPageNum(),
                        0,//识图卡片没有booksizeid，传值0
                        String.valueOf(bookObj.getBookId()),
                        bookObj.getBookCover(),
                        FastData.getBabyName() + "的识图卡片",
                        System.currentTimeMillis(),
                        CartPrintPropertyDialog.REQUEST_CODE_RECOGNIZE_CARD).reqPrintStatus();
                break;

            case R.id.fl_book_cover:
                //跳转POD预览
                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> values = new ArrayList<>();
                keys.add("book_author");
                keys.add("book_title");
                values.add(FastData.getUserName());
                values.add(FastData.getBabyName()+"的照片书");
                MyPODActivity.open(
                        BookListActivity.this,
                        String.valueOf(bookObj.getBookId()),
                        String.valueOf(bookObj.getOpenBookId()),
                        bookObj.getBookType(),
                        bookObj.getOpenBookType(),
                        null,
                        "",
                        false,
                        bookObj.getBaby().getBabyId(),keys,values,0);
                break;

            case R.id.tv_edit:
                //精装照片书&绘画集
                if(bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK
                        || bookObj.getBookType() == BookModel.BOOK_TYPE_PAINTING){
                    SelectServerPhotoActivity.open(this, bookType, bookObj.getOpenBookType(), String.valueOf(bookObj.getBookId()), String.valueOf(bookObj.getOpenBookId()));
                //成长纪念册&成长语录
                } else if(bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK
                        || bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_QUOTATIONS){
                    SelectServerTimeActivity.open(this, bookType, bookObj.getOpenBookType(), String.valueOf(bookObj.getBookId()), String.valueOf(bookObj.getOpenBookId()));
                } else {
                    Log.e(TAG, "无法识别的书籍类型");
                }
                break;
        }
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent){
        bookPresenter.loadData(bookType);
//        if(optionEvent.getBookType() == bookType){
//            //删除书籍操作
//            if(optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_DELETE){
//                int index = -1;
//                for(BookObj bookObj : bookListAdapter.getListData()){
//                    index++;
//                    if(bookObj.getBookId() == bookObj.getBookId()\){
//                        bookListAdapter.notifyItemRemoved(index);
//                        productionMenuDialog.dismiss();
//                        break;
//                    }
//                }
//            }
//        }
    }
}
