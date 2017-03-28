package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyOrderConfirmActivity;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.dialogs.ProductionMenuDialog;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.BookPrintHelper;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleBookListAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleBookObj;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈作品
 * Created by lidonglin on 2017/3/22.
 */
public class CircleBookActivity extends BasePresenterAppCompatActivity implements IEventBus, View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.rv_books)
    RecyclerView rvBooks;
    @Bind(R.id.tv_empty_info)
    TextView tvEmptyInfo;
    @Bind(R.id.btn_create)
    Button btnCreate;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
    @Bind(R.id.btn_ask_for_print)
    Button btnAskForPrint;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    @Bind(R.id.content_book_list)
    RelativeLayout contentBookList;
    private long circleId;
    private List<CircleBookObj> data;
    private CircleBookListAdapter circleBookListAdapter;
    private ProductionMenuDialog productionMenuDialog;

    public static void open(Context context, long circleId) {
        Intent intent = new Intent(context, CircleBookActivity.class);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_circle_book_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("圈作品");
        circleId = getIntent().getLongExtra("circle_id", 0);

        tvTip.setVisibility(View.GONE);
        btnAskForPrint.setVisibility(View.GONE);

        reqData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void reqData() {
        apiService.circleBookList(circleId, 2)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            tfStateView.finish();
                            if (response.success()) {
                                setData(response.getDataList());
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        },
                        throwable -> {
                            tfStateView.finish();
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }

    public void setData(List<CircleBookObj> circleBookObjs) {
        if (circleBookListAdapter == null) {
            circleBookListAdapter = new CircleBookListAdapter(this, circleBookObjs, this);
            rvBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvBooks.setAdapter(circleBookListAdapter);
        } else {
            circleBookListAdapter.setListData(circleBookObjs);
            circleBookListAdapter.notifyDataSetChanged();
        }
        updateEmptyView(circleBookObjs);
    }

    private void updateEmptyView(List<CircleBookObj> circleBookObjs) {
        if (circleBookObjs.size() > 0) {
            llEmpty.setVisibility(View.GONE);
        } else {
            llEmpty.setVisibility(View.VISIBLE);
            setupEmptyView();
        }
    }

    private void setupEmptyView() {
        tvEmptyInfo.setText("本圈还没有新的作品哦，\n点击右上角加号发布本圈第一部作品吧");
        btnCreate.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mine_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            AddCircleBookActivity.open(this);
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onClick(View view) {
        BookObj bookObj = (BookObj) view.getTag(R.string.tag_obj);

        switch (view.getId()) {
            case R.id.iv_menu:
                if (productionMenuDialog == null) {
                    productionMenuDialog = ProductionMenuDialog.newInstance(
                            bookObj,
                            bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK);
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
                //跳转POD预览
                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> values = new ArrayList<>();
                keys.add("book_author");
                keys.add("book_title");
                values.add(FastData.getUserName());
                values.add(FastData.getBabyNickName() + "的照片书");
                MyPODActivity.open(
                        this,
                        String.valueOf(bookObj.getBookId()),
                        String.valueOf(bookObj.getOpenBookId()),
                        bookObj.getBookType(),
                        bookObj.getOpenBookType(),
                        null,
                        "",
                        false,
                        bookObj.getBaby().getBabyId(), keys, values, 0);
                break;

            case R.id.tv_edit:
                if (bookObj.getAuthor().getUserId().equals(FastData.getUserId())) {
                    //圈照片书
                    if (bookObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_PHOTO) {
                        CircleSelectSeverAlbumsActivity.open(
                                this,
                                String.valueOf(circleId),
                                bookObj.getBookType(),
                                String.valueOf(bookObj.getBookId()),
                                bookObj.getOpenBookType(),
                                String.valueOf(bookObj.getOpenBookId()));
                        //圈时光书
                    } else if(bookObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_TIME){
                        CircleSelectServerTimesActivity.open(
                                this,
                                bookObj.getBookType(),
                                bookObj.getOpenBookType(),
                                String.valueOf(bookObj.getBookId()),
                                String.valueOf(bookObj.getOpenBookId()),
                                String.valueOf(circleId)
                        );
                        //家校纪念册
                    } else if(bookObj.getBookType() == BookModel.CIRCLE_BOOK_TYPE_FAMILY_SCHOOL){
                        CircleSelectServerHomeWorksEditActivity.openEdit(
                                this, String.valueOf(circleId), String.valueOf(bookObj.getBookId()),
                                String.valueOf(bookObj.getOpenBookId())
                        );
                    } else {
                        Log.e(TAG, "无法识别的书籍类型: " + bookObj.getBookType());
                    }
                } else {
                    //跳转POD预览
                    ArrayList<String> keys1 = new ArrayList<>();
                    ArrayList<String> values1 = new ArrayList<>();
                    keys1.add("book_author");
                    keys1.add("book_title");
                    values1.add(FastData.getUserName());
                    values1.add(FastData.getBabyNickName() + "的照片书");
                    MyPODActivity.open(
                            this,
                            String.valueOf(bookObj.getBookId()),
                            String.valueOf(bookObj.getOpenBookId()),
                            bookObj.getBookType(),
                            bookObj.getOpenBookType(),
                            null,
                            "",
                            false,
                            bookObj.getBaby().getBabyId(), keys1, values1, 0);
                }

                break;
        }
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent) {
            //删除书籍操作
            if (optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_DELETE) {
                for (int i = 0; i < circleBookListAdapter.getListData().size(); i++) {
                    BookObj bookObj = circleBookListAdapter.getListData().get(i);
                    if(optionEvent.getBookType() == bookObj.getBookType()
                            && TextUtils.equals(optionEvent.getBookId(), String.valueOf(bookObj.getBookId()))){
                        circleBookListAdapter.getListData().remove(i);
                        circleBookListAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
                updateEmptyView(circleBookListAdapter.getListData());
            } else {
                reqData();
            }
    }
}
