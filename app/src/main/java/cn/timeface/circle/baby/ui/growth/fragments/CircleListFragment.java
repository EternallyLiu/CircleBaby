package cn.timeface.circle.baby.ui.growth.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.CreateCalendarDialog;
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
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.calendar.CalendarPreviewActivity;
import cn.timeface.circle.baby.ui.circle.activities.AddCircleBookActivity;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectServerHomeWorksEditActivity;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectServerTimesActivity;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectSeverAlbumsActivity;
import cn.timeface.circle.baby.ui.circle.bean.CircleBookObj;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerPhotoActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerTimeActivity;
import cn.timeface.circle.baby.ui.growth.adapters.BookListAdapter;
import cn.timeface.circle.baby.ui.growth.adapters.CirclcListAdapter;
import cn.timeface.circle.baby.views.TFStateView;


public class CircleListFragment extends BasePresenterFragment implements BookPresentation.View, View.OnClickListener, IEventBus {


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

    private int bookType;
    private BookListAdapter bookListAdapter;
    private CirclcListAdapter circlcListAdapter;
    private BookPresenter bookPresenter;
    private ProductionMenuDialog productionMenuDialog;

    private boolean hasPic;

    public static CircleListFragment newInstance(int bookType) {
        CircleListFragment fragment = new CircleListFragment();
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
        bookPresenter.loadCircleDate();
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
        CircleBookObj bookObj = (CircleBookObj) view.getTag(R.string.tag_obj);

        switch (view.getId()) {
            case R.id.iv_menu:
                if (productionMenuDialog == null) {
                    productionMenuDialog = ProductionMenuDialog.newInstance(
                            bookObj,
                            bookObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_PHOTO);
                    productionMenuDialog.show(getChildFragmentManager(), "");
                } else {
                    productionMenuDialog = ProductionMenuDialog.newInstance(
                            bookObj,
                            bookObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_PHOTO);
                    productionMenuDialog.show(getChildFragmentManager(), "");
                }
                break;

            case R.id.tv_print:
                new BookPrintHelper(
                        (BasePresenterAppCompatActivity) getActivity(),
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
                if (bookType == BookModel.BOOK_TYPE_CALENDAR) {
                    CalendarPreviewActivity.open(
                            getContext(),
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
                            getContext(),
                            String.valueOf(bookObj.getBookId()),
                            String.valueOf(bookObj.getOpenBookId()),
                            bookObj.getBookType(),
                            bookObj.getOpenBookType(),
                            null,
                            "",
                            true,
                            bookObj.getBaby().getBabyId(), keys, values, 0);
                }
                break;

            case R.id.tv_edit:
                //精装照片书&绘画集
                if (bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK
                        || bookObj.getBookType() == BookModel.BOOK_TYPE_PAINTING) {
                    SelectServerPhotoActivity.open(
                            getActivity(),
                            bookType,
                            bookObj.getOpenBookType(),
                            String.valueOf(bookObj.getBookId()),
                            String.valueOf(bookObj.getOpenBookId()),
                            bookObj.getBaby().getBabyId(),
                            bookObj.getAuthor().getNickName(),
                            bookObj.getBookName());
                    //成长纪念册&成长语录
                } else if (bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK
                        || bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_QUOTATIONS) {
                    SelectServerTimeActivity.open(
                            getActivity(),
                            bookType,
                            bookObj.getOpenBookType(),
                            String.valueOf(bookObj.getBookId()),
                            String.valueOf(bookObj.getOpenBookId()),
                            bookObj.getBaby().getBabyId(),
                            bookObj.getAuthor().getNickName(),
                            bookObj.getBookName());
                } else if (bookObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_PHOTO) {
                    //圈照片书
                    CircleSelectSeverAlbumsActivity.open(
                            getActivity(),
                            String.valueOf(bookObj.getCircleNumber()),
                            bookObj.getBookType(),
                            String.valueOf(bookObj.getBookId()),
                            bookObj.getOpenBookType(),
                            String.valueOf(bookObj.getOpenBookId()));
                    //圈时光书
                } else if (bookObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_TIME) {
                    CircleSelectServerTimesActivity.open(
                            getActivity(),
                            bookObj.getBookType(),
                            bookObj.getOpenBookType(),
                            String.valueOf(bookObj.getBookId()),
                            String.valueOf(bookObj.getOpenBookId()),
                            String.valueOf(bookObj.getCircleNumber())
                    );
                    //家校纪念册
                } else if (bookObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_FAMILY_SCHOOL) {
                    CircleSelectServerHomeWorksEditActivity.openEdit(
                            getActivity(), String.valueOf(bookObj.getCircleNumber()), String.valueOf(bookObj.getBookId()),
                            String.valueOf(bookObj.getOpenBookId())
                    );
                } else {
                    Log.e(TAG, "无法识别的书籍类型: " + bookObj.getBookType());
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
        if (loading) {
            tfStateView.setVisibility(View.VISIBLE);
            tfStateView.loading();
        } else {
            tfStateView.finish();
        }
    }

    @Override
    public void setBookData(List<BookObj> bookObjs, boolean hasPic) {

    }

    @Override
    public void setCircleBookData(List<CircleBookObj> bookObjs, boolean hasPic) {
        if (circlcListAdapter == null) {
            circlcListAdapter = new CirclcListAdapter(getActivity(), bookObjs, this);
            rvBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvBooks.setAdapter(circlcListAdapter);
        } else {
            circlcListAdapter.setListData(bookObjs);
            circlcListAdapter.notifyDataSetChanged();
        }

        this.hasPic = hasPic;
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (circlcListAdapter.getListData().isEmpty()) {
            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyView(hasPic);
        } else {
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void setupEmptyView(boolean hasPic) {
        String babyName = FastData.getBabyNickName();
        btnCreate.setVisibility(View.VISIBLE);
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
            case BookModel.CIRCLE_BOOK_TYPE_TIME:
                btnCreate.setVisibility(View.GONE);
                tvEmptyInfo.setText(babyName + "的成长圈作品为空哦，赶紧制作一本属于" + babyName + "的成长圈作品吧~");
                btnCreate.setText(hasPic ? "立即制作" : "立即上传");
                break;
        }

        btnCreate.setOnClickListener(v -> {
            switch (bookType) {
                //精装照片书
                case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                    if (hasPic) {
                        addSubscription(
                                apiService.getDefaultTheme(bookType)
                                        .compose(SchedulersCompat.applyIoSchedulers())
                                        .subscribe(
                                                response -> {
                                                    if (response.success()) {
                                                        SelectServerPhotoActivity.open(getActivity(), BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, response.getId(), "", "", FastData.getBabyId());
                                                    }
                                                },
                                                throwable -> {
                                                    Log.e(TAG, throwable.getLocalizedMessage());
                                                }
                                        ));
                    } else {
                        PublishActivity.open(getContext(), PublishActivity.PHOTO);
                    }
                    break;
                //绘画集
                case BookModel.BOOK_TYPE_PAINTING:
                    if (hasPic) {
                        SelectServerPhotoActivity.open(getContext(), bookType, TypeConstants.OPEN_BOOK_TYPE_PAINTING, "", "", FastData.getBabyId());
                    } else {
                        PublishActivity.open(getContext(), PublishActivity.PHOTO);
                    }
                    break;
                //成长纪念册
                case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                    if (hasPic) {
                        SelectServerTimeActivity.open(
                                getContext(),
                                bookType,
                                TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK,
                                "",
                                "",
                                FastData.getBabyId(),
                                FastData.getUserName(),
                                FastData.getBabyNickName() + "的成长纪念册");
                    } else {
                        PublishActivity.open(getContext(), PublishActivity.PHOTO);
                    }
                    break;
                //成长语录
                case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                    if (hasPic) {
                        SelectServerTimeActivity.open(
                                getContext(),
                                bookType,
                                TypeConstants.OPEN_BOOK_TYPE_GROWTH_QUOTATIONS,
                                "",
                                "",
                                FastData.getBabyId(),
                                FastData.getUserName(),
                                FastData.getBabyNickName() + "的成长语录");
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
                case BookModel.CIRCLE_BOOK_TYPE_TIME:
                    AddCircleBookActivity.open(getActivity());
                    break;
            }
        });
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent) {

        //删除书籍操作
        if (optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_DELETE) {
            for (int i = 0; i < circlcListAdapter.getListData().size(); i++) {
                BookObj bookObj = circlcListAdapter.getListData().get(i);
                if (TextUtils.equals(optionEvent.getBookId(), String.valueOf(bookObj.getBookId()))) {
                    circlcListAdapter.getListData().remove(i);
                    circlcListAdapter.notifyItemRemoved(i);
                    break;
                }
            }
            updateEmptyView();
        } else {
            bookPresenter.loadData(bookType, 2);
        }
    }

}
