package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.CloudAlbumDetailAdapter;
import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.DeviceUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.BottomMenuDialog;
import cn.timeface.circle.baby.views.dialog.LoadingDialog;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class CloudAlbumEditActivity extends BaseAppCompatActivity implements BottomMenuDialog.OnMenuClickListener {


    private static final int REQ_SELECT_PHOTO = 202;
    private static final int REQ_SELECT_COVER = 203;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.iv_header)
    ImageView ivHeader;
    @Bind(R.id.view_Cover_trans)
    View viewCoverTrans;
    @Bind(R.id.btn_changeCover)
    Button btnChangeCover;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.et_inputText)
    EditText etInputText;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.main_content)
    CoordinatorLayout mainContent;
    private LoadingDialog loadingDialog;
    private LinearLayoutManager layoutManager;
    private CloudAlbumDetailAdapter albumDetailAdapter;
    List<MediaObj> mediaObjs = new ArrayList<>(8);
    private MenuItem menu;
    private boolean currentStateEdit;
    private MediaObj mediaObjHeader;
    private String albumId;

    public static void open(Activity activity, String albumId) {
        Intent intent = new Intent(activity, CloudAlbumEditActivity.class);
        intent.putExtra("albumId", albumId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_album_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.ic_back);
        albumId = getIntent().getStringExtra("albumId");
        setupRecyclerView();
        loadingDialog = LoadingDialog.getInstance();
        loadingDialog.show(getSupportFragmentManager(), "");
        reqCloudAlbumDetail(albumId);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        albumDetailAdapter = new CloudAlbumDetailAdapter(this, mediaObjs);
        recyclerView.setAdapter(albumDetailAdapter);
    }

    private void reqCloudAlbumDetail(String albumId) {
        Subscription subscribe = apiService.queryCloudAlbumDetail(albumId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> loadingDialog.dismiss())
                .subscribe(albumDetailResponse -> {
                    if (albumDetailResponse.success()) {
                        List<MediaObj> dataList = albumDetailResponse.getDataList();

                        for (MediaObj mediaObj : dataList) {
                            if (mediaObj.getIsCover() == 1) {
                                mediaObjHeader = mediaObj;
                                break;
                            }
                        }
                        if (mediaObjHeader == null) {//没有封面图片，使用第一个
                            mediaObjHeader = dataList.get(0);
                        }
                        setupHeaderView();
                        mediaObjs.clear();
                        dataList.remove(mediaObjHeader);
                        mediaObjs.addAll(dataList);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu.getItem(0);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            BottomMenuDialog menuDialog = BottomMenuDialog.getInstance();
            menuDialog.setOnMenuClick(this);
            menuDialog.show(getSupportFragmentManager(), "");
        } else if (item.getItemId() == android.R.id.home) {
            if (currentStateEdit) {
                goCommState();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void goCommState() {
        this.currentStateEdit = false;
        this.menu.setVisible(true);
        fab.setVisibility(View.GONE);
        etInputText.setEnabled(false);
        String headerContent = etInputText.getText().toString();
        if (TextUtils.isEmpty(headerContent)) {
            etInputText.setVisibility(View.GONE);
        } else {
            etInputText.setVisibility(View.VISIBLE);
            mediaObjHeader.setContent(headerContent);
        }
        viewCoverTrans.setVisibility(View.GONE);
        btnChangeCover.setVisibility(View.INVISIBLE);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        albumDetailAdapter.setAlbumEditState(false);
        albumDetailAdapter.notifyDataSetChanged();
        DeviceUtil.hideSoftInput(this);
    }

    private void goEditState() {
        this.currentStateEdit = true;
        this.menu.setVisible(false);
        fab.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(R.drawable.ic_delete);
        etInputText.setEnabled(true);
        etInputText.setVisibility(View.VISIBLE);
        btnChangeCover.setVisibility(View.VISIBLE);
        viewCoverTrans.setVisibility(View.VISIBLE);
        albumDetailAdapter.setAlbumEditState(true);
        albumDetailAdapter.notifyDataSetChanged();
    }

    public void clickFinishEdit(View view) {
        goCommState();
        //将修改的内容传递给接口
        loadingDialog.show(getSupportFragmentManager(), "");
        completeEdit();
    }

    private void completeEdit() {
        mediaObjs.add(mediaObjHeader);
        String list = new Gson().toJson(mediaObjs);
        Log.d(TAG, "clickFinishEdit: " + list);
        Subscription subscribe = apiService.editCloudAlbum(albumId, Uri.encode(list))
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> loadingDialog.dismiss())
                .subscribe(baseResponse -> {
                    if (baseResponse.success()) {
                        ToastUtil.showToast("编辑成功");
                    } else {
                        ToastUtil.showToast(baseResponse.getInfo());
                    }
                }, throwable -> {
                    Log.d(TAG, throwable.getMessage());
                    ToastUtil.showToast(R.string.state_error_timeout);
                }, () -> reqCloudAlbumDetail(albumId));
        addSubscription(subscribe);
    }

    public void setupHeaderView() {
        Glide.with(this)
                .load(mediaObjHeader.getImgUrl())
                .asBitmap()
                .atMost()
                .into(ivHeader);
        tvDate.setText(DateUtil.formatDate("yyyy.MM.dd", mediaObjHeader.getPhotographTime()));
        String content = mediaObjHeader.getContent();
        if (!TextUtils.isEmpty(content)) {
            etInputText.setText(content);
        } else {
            etInputText.setVisibility(View.GONE);
        }
        etInputText.setEnabled(false);
    }

    public void clickDeleteImg(View view) {
        //删除照片,待接口
        MediaObj detailObj = (MediaObj) view.getTag(R.string.tag_obj);
        Subscription subscribe = apiService.deleteSingleImage(String.valueOf(detailObj.getId()))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        if (baseResponse.success()) {
                            int position = mediaObjs.indexOf(detailObj);
                            mediaObjs.remove(detailObj);
                            albumDetailAdapter.notifyItemRemoved(position);
                            ToastUtil.showToast("删除成功");
                        } else {
                            ToastUtil.showToast(baseResponse.getInfo());
                        }
                    }
                }, throwable -> {
                    ToastUtil.showToast(R.string.state_error_timeout);
                });
        addSubscription(subscribe);
    }

    public void clickBtnChangeCover(View view) {
        SelectPhotoActivity.open4result(this, 1, REQ_SELECT_COVER);
    }

    @Override
    public void clickMenu(@IdRes int resId) {
        switch (resId) {
            case R.id.rl_add_content:
                //添加内容
                SelectPhotoActivity.open4result(this, TypeConstants.PHOTO_COUNT_MAX, REQ_SELECT_PHOTO);
                break;
            case R.id.rl_edit_state:
                //编辑模式
                goEditState();
                break;
            case R.id.rl_book_pre:
                //进入POD预览
                break;
            case R.id.rl_delete_album:
                //删除相册
                deleteAlbum();
                break;
            case R.id.cancel:
                break;
        }
    }

    private void deleteAlbum() {
        Subscription subscribe = apiService.deleteCloudAlbum(albumId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(baseResponse -> {
                    if (baseResponse.success()) {
                        ToastUtil.showToast("删除成功");
                    } else {
                        ToastUtil.showToast(baseResponse.getInfo());
                    }
                }, throwable -> {
                    ToastUtil.showToast(R.string.state_error_timeout);
                });
        addSubscription(subscribe);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ArrayList<ImgObj> imgObjs = data.getParcelableArrayListExtra("result_select_image_list");
            if (imgObjs == null || imgObjs.size() == 0) return;
            switch (requestCode) {
                case REQ_SELECT_COVER:
                    loadingDialog.show(getSupportFragmentManager(), "");
                    Subscription subscribe = OSSManager.getOSSManager(this).uploadPicToBabys(imgObjs.get(0).getLocalPath())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(objectKey -> {
                                mediaObjHeader.setIsCover(1);
                                mediaObjHeader.setImgUrl(objectKey);
                                completeEdit();
                            });
                    addSubscription(subscribe);
                    break;
                case REQ_SELECT_PHOTO:
                    //1、上传选择的图片
                    //2、转化为MediaObj
                    //3、转化为JSON
                    //4、提交请求
                    uploadImgToCloudAlbum(imgObjs);
                    break;
            }
        }
    }

    private void uploadImgToCloudAlbum(ArrayList<ImgObj> imgObjs) {
        LoadingDialog loadingDialog = LoadingDialog.getInstance();
        loadingDialog.setLoadingMsg("正在提交");
        loadingDialog.show(getSupportFragmentManager(), "");
        Subscription subscribe = Observable.from(imgObjs)
                .flatMap(
                        imgObj -> {
                            return OSSManager.getOSSManager(this).uploadPicToBabys(imgObj.getLocalPath());
                        }, new Func2<ImgObj, String, MediaObj>() {
                            @Override
                            public MediaObj call(ImgObj imgObj, String objectKey) {
                                return new MediaObj(imgObj.getContent(), objectKey, imgObj.getWidth(), imgObj.getHeight(), imgObj.getDateMills());
                            }
                        })
                .filter(mediaObj -> !TextUtils.isEmpty(mediaObj.getImgUrl()))
                .toList()
                .map(new Func1<List<MediaObj>, String>() {
                    @Override
                    public String call(List<MediaObj> objs) {
                        mediaObjs.addAll(objs);
                        return new Gson().toJson(objs);
                    }
                })
                .flatMap(s -> apiService.addPicToCloudAlbum(albumId, s))
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(loadingDialog::dismiss)
                .subscribe(baseResponse -> {
                    if (baseResponse.success()) {
                        ToastUtil.showToast("添加成功");
                        albumDetailAdapter.notifyDataSetChanged();
                    }

                }, throwable -> {
                    Log.d(TAG, "uploadImgToCloudAlbum: " + throwable.getMessage());
                });
        addSubscription(subscribe);


    }
}