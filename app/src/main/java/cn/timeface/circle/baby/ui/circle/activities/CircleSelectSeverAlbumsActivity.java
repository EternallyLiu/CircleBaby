package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.responses.GetThemeResponse;
import cn.timeface.circle.baby.support.api.models.responses.ImageExInfoResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectServerAlbumAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObjWrapper;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    int openBookType;
    int bookType;
    String bookId;
    String openBookId;

    public static void open(Context context, String circleId, int bookType, String bookId, int openBookType, String openBookId){
        Intent intent = new Intent(context, CircleSelectSeverAlbumsActivity.class);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("book_type", bookType);
        intent.putExtra("open_book_type", openBookType);
        intent.putExtra("book_id", bookId);
        intent.putExtra("open_book_id", openBookId);
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
        bookType = getIntent().getIntExtra("book_type", BookModel.CIRCLE_BOOK_TYPE_PHOTO);
        openBookType = getIntent().getIntExtra("open_book_type", 175);
        bookId = getIntent().getStringExtra("book_id");
        openBookId = getIntent().getStringExtra("open_book_id");

        if(!TextUtils.isEmpty(bookId)) {
            apiService.circleBookMedias(bookId)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(
                            response -> {
                                allSelectMedias = response.getDataList();
                            },
                            throwable -> {
                                Log.e(TAG, throwable.getLocalizedMessage());
                            }
                    );
        }

        reqDate();
        initPhotoTip();
    }

    private void reqDate() {
        stateView.loading();
        addSubscription(
                apiService.queryCircleAlbums(circleId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    stateView.finish();
                                    if (response.success()) {
                                        setData(response.getDataList());
                                    } else {
                                        ToastUtil.showToast(response.getInfo());
                                    }
                                },
                                throwable -> {
                                    stateView.showException(throwable);
                                    LogUtil.showError(TAG, throwable);
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
        if(item.getItemId() == R.id.menu_complete){

            int pageNum = allSelectMedias.size();
            if(pageNum == 0){
                ToastUtil.showToast("请选择至少一张照片");
                return true;
            }

            addSubscription(
                    apiService.getDefaultTheme(BookModel.CIRCLE_BOOK_TYPE_PHOTO)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .flatMap(new Func1<GetThemeResponse, Observable<ImageExInfoResponse>>() {
                                @Override
                                public Observable<ImageExInfoResponse> call(GetThemeResponse getThemeResponse) {
                                    if(TextUtils.isEmpty(bookId))openBookType = getThemeResponse.getId();
                                    return apiService.queryImageInfo(FastData.getBabyAvatar());
                                }
                            })
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(
                                    response -> {
                                        if(response.success()){
                                            Collections.sort(allSelectMedias);
                                            //跳转开放平台POD接口；

                                            List<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
                                            StringBuffer sb = new StringBuffer("{\"mediaIds\":[");
                                            int index = 0;
                                            for(MediaObj mediaObj : allSelectMedias){
                                                index++;
                                                TFOResourceObj tfoResourceObj = mediaObj.toTFOResourceObj();
                                                tfoResourceObjs.add(tfoResourceObj);
                                                sb.append(mediaObj.getId());
                                                if (index < allSelectMedias.size()) {
                                                    sb.append(",");
                                                } else {
                                                    sb.append("],");
                                                }
                                            }
                                            sb.append("\"circleId\":").append(circleId).append("}");

                                            List<TFOContentObj> tfoContentObjs1 = new ArrayList<>();
                                            TFOContentObj tfoContentObj;
                                            tfoContentObj = new TFOContentObj("", tfoResourceObjs);
                                            tfoContentObjs1.add(tfoContentObj);

                                            ArrayList<String> keys = new ArrayList<>();
                                            ArrayList<String> values = new ArrayList<>();
                                            keys.add("book_author");
                                            keys.add("book_title");

                                            String author = FastData.getUserName();
                                            String bookName = FastData.getBabyNickName() + "的圈照片书";
                                            values.add(author);
                                            values.add(bookName);

                                            TFOPublishObj tfoPublishObj = new TFOPublishObj(bookName, tfoContentObjs1);
                                            List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
                                            tfoPublishObjs.add(tfoPublishObj);

                                            MyPODActivity.open(
                                                    this,
                                                    bookId,
                                                    openBookId,
                                                    BookModel.CIRCLE_BOOK_TYPE_PHOTO,
                                                    openBookType,
                                                    tfoPublishObjs,
                                                    sb.toString(),true,FastData.getBabyId(),keys,values, 1);
                                            finish();
                                        }
                                    },
                                    throwable -> {
                                        Log.e(TAG, throwable.getLocalizedMessage());
                                    }
                            )
            );
            return true;
        } else if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
            tvSelCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_time_select_count), String.valueOf(allSelectMedias.size()))));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK || data == null){
            return;
        }

        if(requestCode == REQUEST_CODE_SELECT_SERVER_PHOTO){
            this.allSelectMedias = (data.getParcelableArrayListExtra("all_select_medias"));
            initPhotoTip();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
