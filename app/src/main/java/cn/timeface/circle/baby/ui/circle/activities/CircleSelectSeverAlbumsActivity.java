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
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectServerAlbumAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObjWrapper;
import cn.timeface.circle.baby.ui.circle.response.CirclePhotoBookResponse;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈选择照片页面
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectSeverAlbumsActivity extends BasePresenterAppCompatActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.cb_all_sel)
    CheckBox cbAllSel;
    @Bind(R.id.tv_sel_count)
    TextView tvSelCount;
    @Bind(R.id.rl_photo_tip)
    RelativeLayout rlPhotoTip;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    String circleId;
    CircleSelectServerAlbumAdapter selectServerAlbumAdapter;

    public static void open(Context context, String circleId){
        Intent intent = new Intent(context, CircleSelectSeverAlbumsActivity.class);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_sever_photos);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("圈照片书");

        circleId = getIntent().getStringExtra("circle_id");
        reqDate();
    }

    private void reqDate(){
        stateView.loading();
        if (!BuildConfig.DEBUG) {
            List<CircleActivityAlbumObjWrapper> albumObjWrappers = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                CircleActivityAlbumObjWrapper albumObjWrapper = new CircleActivityAlbumObjWrapper();
                albumObjWrapper.setAtcityAlbumCoverUrl("http://img1.timeface.cn/baby/45e71214e0af15a36d270f5cb381a37c.jpg");
                CircleActivityAlbumObj albumObj = new CircleActivityAlbumObj();
                albumObj.setAlbumId(i << 2);
                albumObj.setAlbumName("手工课");
                albumObj.setMediaCount(i << 2);
                albumObj.setAlbumUrl("http://img1.timeface.cn/baby/45e71214e0af15a36d270f5cb381a37c.jpg");
                albumObjWrapper.setAtcityAlbum(albumObj);
                albumObjWrappers.add(albumObjWrapper);
            }

            setData(albumObjWrappers);
            stateView.finish();
        } else {
            addSubscription(
                    apiService.queryCircleAlbums(circleId)
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

    private void setData(List<CircleActivityAlbumObjWrapper> albumObjWrappers){
        if(selectServerAlbumAdapter == null){
            rvContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            selectServerAlbumAdapter = new CircleSelectServerAlbumAdapter(this, albumObjWrappers, this);
            rvContent.setAdapter(selectServerAlbumAdapter);
            rvContent.addItemDecoration(
                    new HorizontalDividerItemDecoration.Builder(this)
                            .size(1)
                            .colorResId(R.color.line_color)
                            .build());
        } else {
            selectServerAlbumAdapter.setListData(albumObjWrappers);
            selectServerAlbumAdapter.notifyDataSetChanged();
        }

        if(selectServerAlbumAdapter.getListData().isEmpty()){
            stateView.empty(R.string.no_list_data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_root:
                CircleActivityAlbumObjWrapper albumObjWrapper = (CircleActivityAlbumObjWrapper) view.getTag(R.string.tag_obj);
                CircleSelectServerPhotosActivity.open(
                        this,
                        albumObjWrapper.getAtcityAlbum().getAlbumName(),
                        String.valueOf(albumObjWrapper.getAtcityAlbum().getAlbumId()));
                break;
        }
    }
}
