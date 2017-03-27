package cn.timeface.circle.baby.ui.circle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectServerAlbumAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObjWrapper;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.growth.adapters.SelectServerPhotosAdapter;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈作品选择照片页面
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerPhotosActivity extends BasePresenterAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    String albumName;
    String albumId;
    SelectServerPhotosAdapter serverPhotosAdapter;
    List<MediaObj> selMedias;

    public void open(Context context, String albumName, String albumId, List<MediaObj> selMedias){
        Intent intent = new Intent(context, CircleSelectServerPhotosActivity.class);
        intent.putExtra("album_name", albumName);
        intent.putExtra("album_id", albumId);
        intent.putParcelableArrayListExtra("select_medias", (ArrayList<? extends Parcelable>) selMedias);
        context.startActivity(intent);
    }

    public static void open4Result(Context context,int reqCode, String albumName, String albumId, List<MediaObj> selMedias){
        Intent intent = new Intent(context, CircleSelectServerPhotosActivity.class);
        intent.putExtra("album_name", albumName);
        intent.putExtra("album_id", albumId);
        intent.putParcelableArrayListExtra("select_medias", (ArrayList<? extends Parcelable>) selMedias);
        ((Activity)context).startActivityForResult(intent, reqCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_server_photos);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        albumName = getIntent().getStringExtra("album_name");
        albumId = getIntent().getStringExtra("album_id");
        selMedias = getIntent().getParcelableArrayListExtra("select_medias");
        getSupportActionBar().setTitle(albumName);
        reqDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_complete ||
                item.getItemId() == android.R.id.home){
            close();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        close();
        super.onBackPressed();
    }

    private void reqDate() {
        stateView.loading();
        addSubscription(
                apiService.queryAlbumPhotos(albumId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .doOnCompleted(() -> stateView.finish())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        setData(response.getDataList());
                                    } else {
                                        ToastUtil.showToast(response.getInfo());
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )
        );
    }

    private void setData(List<MediaWrapObj> mediaWrapObjs){
        if(serverPhotosAdapter == null){
            rvContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            serverPhotosAdapter = new SelectServerPhotosAdapter(this, mediaWrapObjs, Integer.MAX_VALUE, 1, selMedias);
            rvContent.setAdapter(serverPhotosAdapter);
            rvContent.addItemDecoration(
                    new HorizontalDividerItemDecoration.Builder(this)
                            .size(1)
                            .colorResId(R.color.line_color)
                            .build());
        } else {
            serverPhotosAdapter.setListData(mediaWrapObjs);
            serverPhotosAdapter.notifyDataSetChanged();
        }
    }

    private void close(){
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("all_select_medias", (ArrayList<? extends Parcelable>) serverPhotosAdapter.getSelImgs());
        intent.putExtra("photo_count", serverPhotosAdapter.getCurSelectCount());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void clickPhotoView(View view){}

}
