package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.ActiveSelectAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.events.ActiveSelectEvent;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.views.DividerItemDecoration;

/**
 * author : wangshuai Created on 2017/3/16
 * email : wangs1992321@gmail.com
 */
public class SelectActiveActivity extends BaseAppCompatActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.topBar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.btn_unjoin)
    Button btnUnjoin;

    private ActiveSelectAdapter adapter = null;
    private LinearLayoutManager layoutManager;

    public static void open(Context context){
        context.startActivity(new Intent(context,SelectActiveActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_active);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText(R.string.activity_select_active_tip);
        adapter = new ActiveSelectAdapter(this);
        adapter.setItemClickLister(new BaseAdapter.OnItemClickLister() {
            @Override
            public void onItemClick(View view, int position) {
                CircleActivityAlbumObj activityAlbumObj = adapter.getItem(position);
                EventBus.getDefault().post(new ActiveSelectEvent(activityAlbumObj));
                finish();
            }
        });
        contentRecyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contentRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation(), R.color.divider_color);
        contentRecyclerView.addItemDecoration(itemDecoration);

    }

    @Override
    protected void onResume() {
        super.onResume();
        reqData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        menu.findItem(R.id.complete).setTitle(R.string.add_active_album);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.complete)
            CreateActiveActivity.open(this);
        return super.onOptionsItemSelected(item);
    }

    private void reqData() {
        addSubscription(apiService.queryActiveList(FastData.getCircleId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(activeSelectListResponse -> {
                    if (activeSelectListResponse.success()) {
                        adapter.addList(true, activeSelectListResponse.getDataList());
                    } else ToastUtil.showToast(this, activeSelectListResponse.getInfo());
                }, throwable -> LogUtil.showError(throwable)));
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();

    }

    @OnClick(R.id.btn_unjoin)
    public void onClick() {
        EventBus.getDefault().post(new ActiveSelectEvent(null, -1));
        finish();
    }
}
