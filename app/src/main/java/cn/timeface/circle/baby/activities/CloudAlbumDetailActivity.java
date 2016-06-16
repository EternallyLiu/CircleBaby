package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.CloudAlbumDetailAdapter;
import cn.timeface.circle.baby.api.models.objs.CloudAlbumDetailObj;
import cn.timeface.circle.baby.utils.DeviceUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.LoadingDialog;
import rx.Subscription;

/**
 * Created by zhsheng on 2016/6/7.
 */
public class CloudAlbumDetailActivity extends BaseAppCompatActivity {
    ArrayList<CloudAlbumDetailObj> albumDetailObjs = new ArrayList<>(8);
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private CloudAlbumDetailAdapter albumDetailAdapter;
    private LoadingDialog loadingDialog;
    private LinearLayoutManager layoutManager;

    public static void open(Activity activity, String albumId) {
        Intent intent = new Intent(activity, CloudAlbumDetailActivity.class);
        intent.putExtra("albumId", albumId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_album_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String albumId = getIntent().getStringExtra("albumId");
        setupRecyclerView();
        loadingDialog = LoadingDialog.getInstance();
        loadingDialog.show(getSupportFragmentManager(), "");
        reqCloudAlbumDetail(albumId);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        albumDetailAdapter = new CloudAlbumDetailAdapter(this, albumDetailObjs);
        recyclerView.setAdapter(albumDetailAdapter);
    }

    private void reqCloudAlbumDetail(String albumId) {
        Subscription subscribe = apiService.queryCloudAlbumDetail(albumId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> loadingDialog.dismiss())
                .subscribe(albumDetailResponse -> {
                    if (albumDetailResponse.success()) {
                        List<CloudAlbumDetailObj> detailObjs = albumDetailResponse.getDataList();
                        albumDetailObjs.clear();
                        albumDetailObjs.addAll(detailObjs);
                        albumDetailAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToast(albumDetailResponse.getInfo());
                    }
                }, throwable -> {
                    Log.d(TAG, "reqCloudAlbumDetail: " + throwable.getMessage());
                    ToastUtil.showToast(R.string.state_error_timeout);
                });
        addSubscription(subscribe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cloud_album_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            //编辑模式
            albumDetailAdapter.setAlbumEditState(true);
            int itemPosition = layoutManager.findFirstVisibleItemPosition();
            albumDetailAdapter.setCurrentVisibleItemState(itemPosition);
            albumDetailAdapter.notifyDataSetChanged();
            DeviceUtil.showSoftInput(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickFinishEdit(View view) {
        albumDetailAdapter.setAlbumEditState(false);
        albumDetailAdapter.notifyDataSetChanged();
    }
}
