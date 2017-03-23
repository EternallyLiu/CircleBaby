package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectServerAlbumAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObjWrapper;
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
    final int REQUEST_CODE_SELECT_SERVER_PHOTO = 100;
    List<MediaObj> allSelectMedias = new ArrayList<>();
    int position = 0;//记录点击的那一个item

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
        initPhotoTip();
    }

    private void reqDate() {
        stateView.loading();
        addSubscription(
                apiService.queryCircleAlbums(circleId)
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
                position = (int) view.getTag(R.string.tag_index);
                CircleSelectServerPhotosActivity.open4Result(
                        this,
                        REQUEST_CODE_SELECT_SERVER_PHOTO,
                        albumObjWrapper.getActivityAlbum().getAlbumName(),
                        String.valueOf(albumObjWrapper.getActivityAlbum().getAlbumId()),
                        allSelectMedias);
                break;
        }
    }

    private void initPhotoTip(){
        if(allSelectMedias.isEmpty()){
            rlPhotoTip.setVisibility(View.GONE);
        } else {
            rlPhotoTip.setVisibility(View.VISIBLE);
            tvSelCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_photo_select_count), String.valueOf(allSelectMedias.size()))));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK || data == null){
            return;
        }

        if(requestCode == REQUEST_CODE_SELECT_SERVER_PHOTO){
            this.allSelectMedias = (data.getParcelableArrayListExtra("all_select_medias"));
//            int photoCount = data.getIntExtra("photo_count", 0);
//            selectServerAlbumAdapter.getItem(position).getActivityAlbum().setMediaCount(photoCount);
//            selectServerAlbumAdapter.notifyItemChanged(position);
            initPhotoTip();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
