package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

    public static void open(Context context, String albumName, String albumId){
        Intent intent = new Intent(context, CircleSelectServerPhotosActivity.class);
        intent.putExtra("album_name", albumName);
        intent.putExtra("album_id", albumId);
        context.startActivity(intent);
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
        if(item.getItemId() == R.id.menu_complete){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void reqDate(){
        stateView.loading();
        if (BuildConfig.DEBUG) {
            List<MediaWrapObj> mediaWrapObjList = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                MediaWrapObj mediaWrapObj = new MediaWrapObj();
                List<MediaObj> mediaObjList = new ArrayList<>();
                mediaWrapObj.setMediaCount(200);
                mediaWrapObj.setDate("2016年10月");
                mediaWrapObj.setMediaList(mediaObjList);
                mediaWrapObjList.add(mediaWrapObj);
                for(int j = 0 ; j < 100; j++){
                    MediaObj mediaObj = new MediaObj();
                    mediaObj.setW(100);
                    mediaObj.setImgUrl("http://img1.timeface.cn/baby/45e71214e0af15a36d270f5cb381a37c.jpg");
                    mediaObj.setContent("content");
                    mediaObj.setDate(System.currentTimeMillis());
                    mediaObj.setFavoritecount(10);
                    mediaObj.setH(100);
                    mediaObj.setId(j << 3);
                    mediaObj.setImageOrientation(0);
                    mediaObj.setIsFavorite(1);
                    mediaObj.setTimeId(j << 2);
                    mediaObj.setW(100);
                    mediaObjList.add(mediaObj);
                }
            }

            setData(mediaWrapObjList);
            stateView.finish();
        } else {
            addSubscription(
                    apiService.queryAlbumPhotos(albumId)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .doOnCompleted(() -> stateView.finish())
                            .subscribe(
                                    response -> {
                                        if(response.success()){
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
    }

    private void setData(List<MediaWrapObj> mediaWrapObjs){
        if(serverPhotosAdapter == null){
            rvContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            serverPhotosAdapter = new SelectServerPhotosAdapter(this, mediaWrapObjs, Integer.MAX_VALUE, 1, new ArrayList<>());
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

}
