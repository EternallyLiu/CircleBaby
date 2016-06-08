package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.CloudAlbumDetailAdapter;
import cn.timeface.circle.baby.api.models.objs.CloudAlbumDetailObj;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
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
        reqCloudAlbumDetail(albumId);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        albumDetailAdapter = new CloudAlbumDetailAdapter(this, albumDetailObjs);
        recyclerView.setAdapter(albumDetailAdapter);
    }

    private void reqCloudAlbumDetail(String albumId) {
        Subscription subscribe = apiService.queryCloudAlbumDetail(albumId)
                .compose(SchedulersCompat.applyIoSchedulers())
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


}
