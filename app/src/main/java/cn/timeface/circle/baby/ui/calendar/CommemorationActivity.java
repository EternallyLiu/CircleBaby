package cn.timeface.circle.baby.ui.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.exception.ResultException;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;
import cn.timeface.circle.baby.support.mvp.presenter.CommemorationPresenter;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.ui.calendar.adapter.DateAdapter;
import cn.timeface.circle.baby.ui.calendar.bean.DateObj;
import cn.timeface.circle.baby.ui.calendar.events.CommemorationDeleteEvent;
import cn.timeface.circle.baby.views.StateView;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.circle.baby.views.VerticalSpaceItemDecoration;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

/**
 * Created by JieGuo on 16/9/29.
 */

public class CommemorationActivity extends BasePresenterAppCompatActivity implements
        CalendarPresentation.CommemorationPresentation.View, IPTRRecyclerListener,
        Toolbar.OnMenuItemClickListener, DateAdapter.Action,
        StateView.RetryListener, View.OnClickListener {

    private static final String MONTH_ID = "month:id";
    private static final String CALENDAR_ID = "calendar:id";

    private static final String[] MONTH_NAME = new String[]{
            "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"
    };

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.stateView)
    TFStateView stateView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.btn_create_now)
    AppCompatButton btnCreateNow;

    CalendarPresentation.CommemorationPresentation.Presenter presenter
            = new CommemorationPresenter(this);

    int month = 0;
    DateAdapter adapter;
    TFPTRRecyclerViewHelper helper;
    private TFProgressDialog progressDialog;

    /**
     * 打开管理纪念日
     *
     * @param context context
     * @param month   月, 1月就传1月,2月就传2, 加载所有的就传 0
     */
    public static void open(Activity context, int month, String calendarId) {
        Intent intent = new Intent(context, CommemorationActivity.class);
        intent.putExtra(MONTH_ID, month);
        intent.putExtra(CALENDAR_ID, calendarId);
        //context.startActivity(intent);
        context.startActivityForResult(intent, 789);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_commemoration_manager);
        ButterKnife.bind(this);

        progressDialog = TFProgressDialog.getInstance("正在进行");

        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        toolbar.inflateMenu(R.menu.menu_activity_edit_complete);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.getMenu().findItem(R.id.edit_complete).setTitle("添加");
        month = getIntent().getIntExtra(MONTH_ID, 0);
        stateView.loading();
        if (month == 0) {
            toolbar.setTitle("添加纪念日");
        } else {
            toolbar.setTitle(String.format(Locale.CHINESE, "添加纪念日（%s月）", MONTH_NAME[month - 1]));
        }

        btnCreateNow.setOnClickListener(this);
        stateView.setOnRetryListener(this);
        initAdapter();
        requestData(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null && stateView.getVisibility() == View.GONE) {
            stateView.setVisibility(View.VISIBLE);
            stateView.loading();
            adapter.getListData().clear();
            requestData(false);
        }
    }

    private void initAdapter() {

        adapter = new DateAdapter(this, new ArrayList<>());
        adapter.setAction(this);
        contentRecyclerView.addItemDecoration(
                new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.size_12))
        );
        helper = new TFPTRRecyclerViewHelper(
                this, contentRecyclerView, swipeRefreshLayout
        );
        contentRecyclerView.setAdapter(adapter);
        helper.tfPtrListener(this);
        helper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START);
    }

    private void requestData(boolean canCreateble) {

        if (month > -1 && month < 13) {

            presenter.list("0", "2017", String.valueOf(month), response -> {
                stateView.finish();
                if (response.getData().size() < 1 && canCreateble) {

                    UpdateCommemorationActivity.open(this, String.valueOf(month));
                }
                helper.finishTFPTRRefresh();
                adapter.getListData().addAll(response.getData());
                adapter.notifyDataSetChanged();

            }, throwable -> {
                if (throwable instanceof ResultException) {
                    showToast(throwable.getMessage());
                }
                Log.e(TAG, "error", throwable);
                stateView.showException(throwable);
            });
        } else {
            showToast("月份数据有问题");
            Log.e(TAG, "month data error");
        }
    }

    @Override
    public void onTFPullDownToRefresh(View refreshView) {
        adapter.getListData().clear();
        requestData(false);
    }

    @Override
    public void onTFPullUpToRefresh(View refreshView) {

    }

    @Override
    public void onScrollUp(int firstVisibleItem) {

    }

    @Override
    public void onScrollDown(int firstVisibleItem) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.edit_complete) {
            UpdateCommemorationActivity.open(this, String.valueOf(month));
        }
        return false;
    }

    @Override
    public void delete(int position) {
        progressDialog.show(getSupportFragmentManager(), "deleteRemoteNotebook...");
        DateObj dateObj = adapter.getItem(position);

        EventBus.getDefault().post(new CommemorationDeleteEvent(
                dateObj.getMonth(), dateObj.getDay(), dateObj.getContent()
        ));

        presenter.delete(dateObj, response -> {
            adapter.getListData().remove(position);
            adapter.notifyItemRemoved(position);
            Log.e(TAG, "removed : " + position);
            progressDialog.dismiss();
        }, throwable -> {
            progressDialog.dismiss();
            Log.e(TAG, "error", throwable);
        });
    }

    @Override
    public void update(int position) {
        UpdateCommemorationActivity.open(
                this,
                adapter.getListData().get(position)
        );
    }

    @Override
    public void onRetry() {
        requestData(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_now:
                UpdateCommemorationActivity.open(this, String.valueOf(month));
                break;

            default:
                break;
        }
    }
}
