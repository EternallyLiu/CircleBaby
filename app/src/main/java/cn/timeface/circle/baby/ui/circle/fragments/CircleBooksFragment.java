package cn.timeface.circle.baby.ui.circle.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.ui.calendar.CalendarPreviewActivity;
import cn.timeface.circle.baby.ui.circle.activities.AddCircleBookActivity;
import cn.timeface.circle.baby.ui.circle.adapters.CircleBookListAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleBookObj;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerPhotoActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerTimeActivity;
import cn.timeface.circle.baby.ui.growth.adapters.BookListAdapter;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈作品
 * Created by lidonglin on 2017/3/22.
 */
public class CircleBooksFragment extends BasePresenterFragment implements BookPresentation.View, View.OnClickListener, IEventBus {

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
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;

    private long circleId;
    private int bookType;
    private BookListAdapter bookListAdapter;
    private BookPresenter bookPresenter;
    private ProductionMenuDialog productionMenuDialog;

    private boolean hasPic;
    private CircleBookListAdapter circleBookListAdapter;

    public static CircleBooksFragment newInstance(long circleId) {
        CircleBooksFragment fragment = new CircleBooksFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("circle_id", circleId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        circleId = getArguments().getLong("circle_id");
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_book_list, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("圈作品");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tvTip.setVisibility(View.GONE);
        bookPresenter = new BookPresenter(this);
        bookPresenter.circleBooks(circleId, 2);
//        bookPresenter.loadData(5, 2);
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
                    productionMenuDialog = ProductionMenuDialog.newInstance(
                            bookObj,
                            bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK);
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
                            false,
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

        this.hasPic = hasPic;
    }

    @Override
    public void setCircleBookData(List<CircleBookObj> circleBookObjs) {
        if (circleBookListAdapter == null) {
            circleBookListAdapter = new CircleBookListAdapter(getActivity(), circleBookObjs, this);
            rvBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvBooks.setAdapter(circleBookListAdapter);
        } else {
            circleBookListAdapter.setListData(circleBookObjs);
            circleBookListAdapter.notifyDataSetChanged();
        }
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (circleBookListAdapter.getListData().isEmpty()) {
            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyView();
        } else {
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void setupEmptyView() {
        String babyName = FastData.getBabyNickName();
        tvEmptyInfo.setText("本圈还没有新的作品哦，\n点击右上角加号发布本圈第一部作品吧");
        btnCreate.setVisibility(View.GONE);
//        btnCreate.setText("立即制作");
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent) {
        if (optionEvent.getBookType() == bookType) {
            //删除书籍操作
            if (optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_DELETE) {
                for (int i = 0; i < bookListAdapter.getListData().size(); i++) {
                    BookObj bookObj = bookListAdapter.getListData().get(i);
                    if (TextUtils.equals(optionEvent.getBookId(), String.valueOf(bookObj.getBookId()))) {
                        bookListAdapter.getListData().remove(i);
                        bookListAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
                updateEmptyView();
            } else {
                bookPresenter.circleBooks(circleId, 2);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mine_book, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            AddCircleBookActivity.open(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }
}
