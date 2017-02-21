package cn.timeface.circle.baby.ui.growth.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.ProductionMenuDialog;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.BookPresenter;
import cn.timeface.circle.baby.support.utils.BookPrintHelper;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.growth.activities.BookListActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerPhotoActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerTimeActivity;
import cn.timeface.circle.baby.ui.growth.adapters.BookListAdapter;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 书籍相关作品列表页面
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class BookListFragment extends BasePresenterFragment implements BookPresentation.View, View.OnClickListener, IEventBus {
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.rv_books)
    RecyclerView rvBooks;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
    @Bind(R.id.btn_ask_for_print)
    Button btnAskForPrint;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    @Bind(R.id.content_book_list)
    RelativeLayout contentBookList;

    int bookType;
    BookListAdapter bookListAdapter;
    BookPresenter bookPresenter;
    ProductionMenuDialog productionMenuDialog;

    public static BookListFragment newInstance(int bookType) {
        BookListFragment fragment = new BookListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("book_type", bookType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, view);
        this.bookType = getArguments().getInt("book_type", 0);
        tvTip.setVisibility(View.GONE);
        bookPresenter = new BookPresenter(this);
        bookPresenter.loadData(bookType);
        btnAskForPrint.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
                    productionMenuDialog.show(getChildFragmentManager(), "");
                }
                break;

            case R.id.tv_print:
                new BookPrintHelper(
                        (BasePresenterAppCompatActivity) getActivity(),
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
                        getActivity(),
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
                    SelectServerPhotoActivity.open(getActivity(), bookType, bookObj.getOpenBookType(), String.valueOf(bookObj.getBookId()), String.valueOf(bookObj.getOpenBookId()));
                    //成长纪念册&成长语录
                } else if(bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK
                        || bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_QUOTATIONS){
                    SelectServerTimeActivity.open(getActivity(), bookType, bookObj.getOpenBookType(), String.valueOf(bookObj.getBookId()), String.valueOf(bookObj.getOpenBookId()));
                } else {
                    Log.e(TAG, "无法识别的书籍类型");
                }
                break;
        }
    }

    @Override
    public void showErr(String errMsg) {
        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setStateView(boolean loading) {
        if(loading){
            tfStateView.setVisibility(View.VISIBLE);
            tfStateView.loading();
        } else {
            tfStateView.finish();
        }
    }

    @Override
    public void setBookData(List<BookObj> bookObjs) {
        if(bookListAdapter == null){
            bookListAdapter = new BookListAdapter(getActivity(), bookObjs, this);
            rvBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvBooks.setAdapter(bookListAdapter);
        } else {
            bookListAdapter.setListData(bookObjs);
            bookListAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent){
        if(optionEvent.getBookType() == bookType){
            //删除书籍操作
            if(optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_DELETE){
                int index = -1;
                for(BookObj bookObj : bookListAdapter.getListData()){
                    index++;
                    if(bookObj.getBookId() == bookObj.getBookId()){
                        bookListAdapter.notifyItemRemoved(index);
                        productionMenuDialog.dismiss();
                        break;
                    }
                }
            }
        }
    }
}
