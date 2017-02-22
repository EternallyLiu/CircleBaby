package cn.timeface.circle.baby.ui.growth.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.activities.SelectThemeActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.CreateCalendarDialog;
import cn.timeface.circle.baby.dialogs.ProductionMenuDialog;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.BookPresenter;
import cn.timeface.circle.baby.support.utils.FastData;
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
    @Bind(R.id.tv_empty_info)
    TextView tvEmptyInfo;
    @Bind(R.id.btn_create)
    Button btnCreate;
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
        if (view.getId() == R.id.iv_menu) {
            BookObj bookObj = (BookObj) view.getTag(R.string.tag_obj);
            if (productionMenuDialog == null) {
                productionMenuDialog = ProductionMenuDialog.newInstance(
                        bookType,
                        String.valueOf(bookObj.getBookId()),
                        bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK);
                productionMenuDialog.show(getChildFragmentManager(), "");
            }
        }
    }

    @Override
    public void showErr(String errMsg) {
        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setStateView(boolean loading) {
        if (loading) {
            tfStateView.setVisibility(View.VISIBLE);
            tfStateView.loading();
        } else {
            tfStateView.finish();
        }
    }

    @Override
    public void setBookData(List<BookObj> bookObjs, boolean hasPic) {
        if (bookListAdapter == null) {
            bookListAdapter = new BookListAdapter(getActivity(), bookObjs, this);
            rvBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvBooks.setAdapter(bookListAdapter);
        } else {
            bookListAdapter.setListData(bookObjs);
            bookListAdapter.notifyDataSetChanged();
        }

        if (bookListAdapter.getListData().isEmpty()) {
            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyView(hasPic);
        } else {
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void setupEmptyView(boolean hasPic) {
        String babyName = FastData.getBabyName();
        switch (bookType) {
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                tvEmptyInfo.setText(hasPic ? babyName + "的成长纪念册为空哦，赶紧制作一本属于" + babyName + "的成长纪念册吧~"
                        : babyName + "的成长纪念册为空哦，赶紧发布内容，制作一本吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
            case BookModel.BOOK_TYPE_CALENDAR:
                tvEmptyInfo.setText(babyName + "的台历为空哦，赶紧制作一本属于" + babyName + "的台历吧~");
                btnCreate.setText("立即制作");
                break;
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                tvEmptyInfo.setText(hasPic ? babyName + "的照片书为空哦，赶紧制作一本属于" + babyName + "的照片书吧~"
                        : babyName + "的照片书为空哦，赶紧上传照片，制作照片书吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                tvEmptyInfo.setText(hasPic ? babyName + "的成长语录为空哦，赶紧制作一本属于" + babyName + "的成长语录吧~"
                        : babyName + "的成长语录为空哦，赶紧发布内容，制作一本吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
            case BookModel.BOOK_TYPE_NOTEBOOK:
                tvEmptyInfo.setText(babyName + "的记事本为空哦，赶紧制作一本属于" + babyName + "的记事本吧~");
                btnCreate.setText("立即制作");
                break;
            case BookModel.BOOK_TYPE_PAINTING:
                tvEmptyInfo.setText(hasPic ? babyName + "的绘画集为空哦，赶紧制作一本属于" + babyName + "的绘画集吧~"
                        : babyName + "的绘画集为空哦，赶紧发布内容，制作一本吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
        }

        btnCreate.setOnClickListener(v -> {
            switch (bookType) {
                //精装照片书
                case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                    if (hasPic) {
                        SelectThemeActivity.open(getContext());
                    } else {
                        PublishActivity.open(getContext(), PublishActivity.PHOTO);
                    }
                    break;
                //绘画集
                case BookModel.BOOK_TYPE_PAINTING:
                    if (hasPic) {
                        SelectServerPhotoActivity.open(getContext(), bookType, TypeConstants.OPEN_BOOK_TYPE_PAINTING);
                    } else {
                        PublishActivity.open(getContext(), PublishActivity.PHOTO);
                    }
                    break;
                //成长纪念册
                case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                    if (hasPic) {
                        SelectServerTimeActivity.open(getContext(), bookType, TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK);
                    } else {
                        PublishActivity.open(getContext(), PublishActivity.PHOTO);
                    }
                    break;
                //成长语录
                case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                    if (hasPic) {
                        SelectServerTimeActivity.open(getContext(), bookType, TypeConstants.OPEN_BOOK_TYPE_GROWTH_QUOTATIONS);
                    } else {
                        PublishActivity.open(getContext(), PublishActivity.VOICE);
                    }
                    break;
                //台历
                case BookModel.BOOK_TYPE_CALENDAR:
                    CreateCalendarDialog createCalendarDialog = CreateCalendarDialog.newInstance();
                    createCalendarDialog.setCancelable(true);
                    createCalendarDialog.show(getActivity().getSupportFragmentManager(), "CreateCalendarDialog");
                    break;
            }
        });
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent) {
        if (optionEvent.getBookType() == bookType) {
            //删除书籍操作
            if (optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_DELETE) {
                int index = -1;
                for (BookObj bookObj : bookListAdapter.getListData()) {
                    index++;
                    if (bookObj.getBookId() == bookObj.getBookId()) {
                        bookListAdapter.notifyItemRemoved(index);
                        productionMenuDialog.dismiss();
                        break;
                    }
                }
            }
        }
    }
}
