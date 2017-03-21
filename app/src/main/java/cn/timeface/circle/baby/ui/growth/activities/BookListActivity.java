package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyOrderConfirmActivity;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.CreateCalendarDialog;
import cn.timeface.circle.baby.dialogs.ProductionMenuDialog;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.BookPresenter;
import cn.timeface.circle.baby.support.utils.BookPrintHelper;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.calendar.CalendarPreviewActivity;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectServerTimesActivity;
import cn.timeface.circle.baby.ui.growth.adapters.BookListAdapter;

/**
 * 书作品列表
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class BookListActivity extends ProductionListActivity implements BookPresentation.View, View.OnClickListener, IEventBus {
    BookListAdapter bookListAdapter;
    BookPresenter bookPresenter;
    ProductionMenuDialog productionMenuDialog;

    private boolean hasPic;

    public static void open(Context context, int bookType) {
        Intent intent = new Intent(context, BookListActivity.class);
        intent.putExtra("book_type", bookType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTip.setVisibility(View.GONE);
        bookPresenter = new BookPresenter(this);
        bookPresenter.loadData(bookType, 1);
        btnAskPrint.setVisibility(View.GONE);
        getSupportActionBar().setTitle(FastData.getBabyNickName() + "的" + BookModel.getGrowthBookName(bookType));
    }

    @Override
    public void showErr(String errMsg) {
        showToast(errMsg);
    }

    @Override
    public void setStateView(boolean loading) {
        if (loading) {
            stateView.setVisibility(View.VISIBLE);
            stateView.loading();
        } else {
            stateView.finish();
        }
    }

    @Override
    public void setBookData(List<BookObj> bookObjs, boolean hasPic) {
        if (bookListAdapter == null) {
            bookListAdapter = new BookListAdapter(this, bookObjs, this);
            rvBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvBooks.setAdapter(bookListAdapter);
        } else {
            bookListAdapter.setListData(bookObjs);
            bookListAdapter.notifyDataSetChanged();
        }

        this.hasPic = hasPic;
        if (bookListAdapter.getListData().isEmpty()) {
            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyViewContent(hasPic);
        } else {
            llEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            switch (bookType) {
                //精装照片书
                case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
//                    addSubscription(
//                            apiService.getDefaultTheme(bookType)
//                                    .compose(SchedulersCompat.applyIoSchedulers())
//                                    .subscribe(
//                                            response -> {
//                                                if(response.success()){
//                                                    SelectServerPhotoActivity.open(this, BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, response.getId(), "", "", FastData.getBabyId());
//                                                }
//                                            },
//                                            throwable -> {
//                                                Log.e(TAG, throwable.getLocalizedMessage());
//                                            }
//                                    ));

                    CircleSelectServerTimesActivity.open(
                            this,
                            BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK,
                            TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK,
                            "",
                            "",
                            "123456"
                    );

                    break;
                //绘画集
                case BookModel.BOOK_TYPE_PAINTING:
                    SelectServerPhotoActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_PAINTING, "", "", FastData.getBabyId());
                    break;
                //成长纪念册
                case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                    SelectServerTimeActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK, "", "", FastData.getBabyId());
                    break;
                //成长语录
                case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                    SelectServerTimeActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_GROWTH_QUOTATIONS, "", "", FastData.getBabyId());
                    break;
                //台历
                case BookModel.BOOK_TYPE_CALENDAR:
                    showCreateCalendarDialog();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateClick() {
        switch (bookType) {
            //精装照片书
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                if (hasPic) {
                    addSubscription(
                            apiService.getDefaultTheme(bookType)
                                    .compose(SchedulersCompat.applyIoSchedulers())
                                    .subscribe(
                                            response -> {
                                                if(response.success()){
                                                    SelectServerPhotoActivity.open(this, BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, response.getId(), "", "", FastData.getBabyId());
                                                }
                                            },
                                            throwable -> {
                                                Log.e(TAG, throwable.getLocalizedMessage());
                                            }
                                    ));
                } else {
                    PublishActivity.open(this, PublishActivity.PHOTO);
                }
                break;
            //绘画集
            case BookModel.BOOK_TYPE_PAINTING:
                if (hasPic) {
                    SelectServerPhotoActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_PAINTING, "", "", FastData.getBabyId());
                } else {
                    PublishActivity.open(this, PublishActivity.PHOTO);
                }
                break;
            //成长纪念册
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                if (hasPic) {
                    SelectServerTimeActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK, "", "", FastData.getBabyId());
                } else {
                    PublishActivity.open(this, PublishActivity.PHOTO);
                }
                break;
            //成长语录
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                if (hasPic) {
                    SelectServerTimeActivity.open(this, bookType, TypeConstants.OPEN_BOOK_TYPE_GROWTH_QUOTATIONS, "", "", FastData.getBabyId());
                } else {
                    PublishActivity.open(this, PublishActivity.VOICE);
                }
                break;
            //台历
            case BookModel.BOOK_TYPE_CALENDAR:
                showCreateCalendarDialog();
                break;
        }
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
                            bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, String.valueOf(bookObj.getOpenBookId()), bookObj.getBaby().getBabyId());
                    productionMenuDialog.show(getSupportFragmentManager(), "");
                }
                break;

            case R.id.tv_print:
                new BookPrintHelper(
                        this,
                        bookObj.getBookType(),
                        bookObj.getOpenBookType(),
                        bookObj.getPageNum(),
                        0,//识图卡片没有booksizeid，传值0
                        String.valueOf(bookObj.getBookId()),
                        bookObj.getBookCover(),
                        bookObj.getBookName(),
                        System.currentTimeMillis(),
                        CartPrintPropertyDialog.REQUEST_CODE_BOOK_LIST).reqPrintStatus();
                break;

            case R.id.rl_book_cover:
                if(bookType == BookModel.BOOK_TYPE_CALENDAR){
                    CalendarPreviewActivity.open(
                            this,
                            String.valueOf(bookObj.getOpenBookId()),
                            String.valueOf(bookObj.getBookType()),
                            String.valueOf(bookObj.getBookId()));
                } else {
                    //跳转POD预览
                    ArrayList<String> keys = new ArrayList<>();
                    ArrayList<String> values = new ArrayList<>();
                    keys.add("book_author");
                    keys.add("book_title");
                    values.add(FastData.getUserName());
                    values.add(FastData.getBabyNickName() + "的照片书");
                    MyPODActivity.open(
                            BookListActivity.this,
                            String.valueOf(bookObj.getBookId()),
                            String.valueOf(bookObj.getOpenBookId()),
                            bookObj.getBookType(),
                            bookObj.getOpenBookType(),
                            null,
                            "",
                            false,
                            bookObj.getBaby().getBabyId(), keys, values, 0);
                }
                break;

            case R.id.tv_edit:
                //精装照片书&绘画集
                if (bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK
                        || bookObj.getBookType() == BookModel.BOOK_TYPE_PAINTING) {
                    SelectServerPhotoActivity.open(this, bookType, bookObj.getOpenBookType(), String.valueOf(bookObj.getBookId()), String.valueOf(bookObj.getOpenBookId()), bookObj.getBaby().getBabyId());
                //成长纪念册&成长语录
                } else if (bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK
                        || bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_QUOTATIONS) {
                    SelectServerTimeActivity.open(this, bookType, bookObj.getOpenBookType(), String.valueOf(bookObj.getBookId()), String.valueOf(bookObj.getOpenBookId()), bookObj.getBaby().getBabyId());
                //台历
                } else if(bookObj.getBookType() == BookModel.BOOK_TYPE_CALENDAR){
                    CalendarPreviewActivity.open(
                            this,
                            String.valueOf(bookObj.getOpenBookId()),
                            String.valueOf(bookObj.getBookType()),
                            String.valueOf(bookObj.getBookId()));
                } else {
                    Log.e(TAG, "无法识别的书籍类型");
                }
                break;
        }
    }

    private void showCreateCalendarDialog() {
        CreateCalendarDialog createCalendarDialog = CreateCalendarDialog.newInstance();
        createCalendarDialog.setCancelable(true);
        createCalendarDialog.show(getSupportFragmentManager(), "CreateCalendarDialog");
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent) {
        bookPresenter.loadData(bookType, 1);
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

    public void doDialogItemClick(View view) {
        EventBus.getDefault().post(new CartItemClickEvent(view));
    }

    @Subscribe
    public void onEvent(CartBuyNowEvent event) {
        if (event != null &&
                (event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_DIARY_CARD
                        || event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_RECOGNIZE_CARD
                        || event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_BOOK_LIST)) {
            if (event.response.success()) {
                MyOrderConfirmActivity.open(this, event.response.getOrderId(), event.baseObjs);
            } else {
                Toast.makeText(this, event.response.getInfo(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
