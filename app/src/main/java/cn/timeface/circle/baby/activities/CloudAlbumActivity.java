package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.timeface.refreshload.PullRefreshLoadRecyclerView;
import com.timeface.refreshload.headfoot.LoadMoreView;
import com.timeface.refreshload.headfoot.RefreshView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.CloudAlbumListAdapter;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.support.api.models.objs.CloudAlbumObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Subscription;

/**
 * Created by zhsheng on 2016/6/7.
 */
public class CloudAlbumActivity extends BaseAppCompatActivity implements IEventBus, View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    PullRefreshLoadRecyclerView prlRecyclerView;
    @Bind(R.id.stateView)
    TFStateView tfStateView;
    @Bind(R.id.sv)
    ScrollView sv;
    @Bind(R.id.title)
    TextView title;
    private List<CloudAlbumObj> dataList = new ArrayList<>(8);
    private CloudAlbumListAdapter albumListAdapter;
    private boolean showGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_album);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText("云相册");
        setupAlbumView();
        tfStateView.setOnRetryListener(this::reqCloudAlbumImages);
        tfStateView.loading();
//        reqCloudAlbumImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reqCloudAlbumImages();
    }

    public static void open(Activity activity) {
        activity.startActivity(new Intent(activity, CloudAlbumActivity.class));
    }


    private void setupAlbumView() {
        RecyclerView recyclerView = prlRecyclerView.getRecyclerView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation(), R.color.divider_color);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        albumListAdapter = new CloudAlbumListAdapter(this, dataList);
        prlRecyclerView.setAdapter(albumListAdapter);
        prlRecyclerView.setLoadRefreshListener(new PullRefreshLoadRecyclerView.LoadRefreshListener() {
            @Override
            public void onRefresh(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, RefreshView refreshView) {
                reqCloudAlbumImages();
            }

            @Override
            public void onLoadMore(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, LoadMoreView loadMoreView) {

            }
        });
    }

    private void reqCloudAlbumImages() {
        Subscription subscribe = apiService.queryCloudAlbumList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> {
                    prlRecyclerView.complete();
                })
                .subscribe(response -> {
                    tfStateView.finish();
                    if (response.success()) {
                        dataList.clear();
                        List<CloudAlbumObj> dataList = response.getDataList();
                        List<CloudAlbumObj> cloudAlbumObjs = new ArrayList<>();
                        for (CloudAlbumObj obj : dataList) {
                            if (!TextUtils.isEmpty(obj.getContentInfo())) {
                                cloudAlbumObjs.add(obj);
                            }
                        }
                        this.dataList.addAll(cloudAlbumObjs);
                        albumListAdapter.notifyDataSetChanged();
                        if (this.dataList.size() == 0) {
                            tfStateView.setImageResource(R.drawable.nodata);
                            tfStateView.setTitle("没有内容");
                            tfStateView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        ToastUtil.showToast(response.getInfo());
                    }
                }, throwable -> {
                    ToastUtil.showToast(R.string.state_error_timeout);
                    tfStateView.showException(throwable);
                    Log.d(TAG, "reqCloudAlbumImages: " + throwable.getMessage());
                });
        addSubscription(subscribe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cloud_album_upload, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_upload) {
            //进入图片选择页面

//            MyPODActivity.open(this, "", 23, null);
            PublishActivity.open(this, PublishActivity.PHOTO);
//             SelectPhotoActivity.openToPublish(this, TypeConstants.PHOTO_COUNT_MAX);
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickCloudAlbum(View view) {
        CloudAlbumObj cloudAlbumObj = (CloudAlbumObj) view.getTag(R.string.tag_obj);
        String cloudAlbumObjId = cloudAlbumObj.getId();
        String title = cloudAlbumObj.getDesc();
        CloudAlbumEditActivity.open(this, cloudAlbumObjId, cloudAlbumObj.getType(), title);
    }

    @Subscribe
    public void onEvent(HomeRefreshEvent event) {
        reqCloudAlbumImages();
    }

    @Override
    public void onClick(View v) {
    }
}
