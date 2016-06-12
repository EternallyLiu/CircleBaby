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
import cn.timeface.circle.baby.adapters.CloudAlbumListAdapter;
import cn.timeface.circle.baby.api.models.objs.CloudAlbumObj;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import rx.Subscription;

/**
 * Created by zhsheng on 2016/6/7.
 */
public class CloudAlbumActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<CloudAlbumObj> dataList = new ArrayList<>(8);
    private CloudAlbumListAdapter albumListAdapter;

    public static void open(Activity activity) {
        activity.startActivity(new Intent(activity, CloudAlbumActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_album);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupAlbumView();
        reqCloudAlbumImages();
    }

    private void setupAlbumView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation(), R.color.divider_color);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        albumListAdapter = new CloudAlbumListAdapter(this, dataList);
        recyclerView.setAdapter(albumListAdapter);
    }

    private void reqCloudAlbumImages() {
        Subscription subscribe = apiService.queryCloudAlbumList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        dataList.clear();
                        dataList.addAll(response.getDataList());
                        albumListAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToast(response.getInfo());
                    }
                }, throwable -> {
                    ToastUtil.showToast(R.string.state_error_timeout);
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
            SelectPhotoActivity.open4result(this, 100);
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickCloudAlbum(View view) {
        CloudAlbumObj cloudAlbumObj = (CloudAlbumObj) view.getTag(R.string.tag_obj);
        String cloudAlbumObjId = cloudAlbumObj.getId();
        CloudAlbumDetailActivity.open(this, cloudAlbumObjId);
    }
}
