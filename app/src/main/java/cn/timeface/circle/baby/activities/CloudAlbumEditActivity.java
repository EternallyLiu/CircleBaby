package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import cn.timeface.circle.baby.api.models.objs.CloudAlbumDetailObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.DeviceUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.BottomMenuDialog;
import cn.timeface.circle.baby.views.dialog.LoadingDialog;
import rx.Subscription;
import rx.functions.Action1;

public class CloudAlbumEditActivity extends BaseAppCompatActivity implements BottomMenuDialog.OnMenuClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private LoadingDialog loadingDialog;
    private LinearLayoutManager layoutManager;
    private CloudAlbumDetailAdapter albumDetailAdapter;
    ArrayList<CloudAlbumDetailObj> albumDetailObjs = new ArrayList<>(8);
    private MenuItem menu;
    private boolean currentStateEdit;
    private Button btnChangeCover;
    private TextView tvDate;
    private ImageView ivAlbumImg;
    private EditText etInputText;
    private View coverView;
    private View headerView;
    private List<CloudAlbumDetailObj> detailObjs;
    private CloudAlbumDetailObj albumDetailObjHeader;
    private String albumId;

    public static void open(Activity activity, String albumId) {
        Intent intent = new Intent(activity, CloudAlbumEditActivity.class);
        intent.putExtra("albumId", albumId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_album_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        albumId = getIntent().getStringExtra("albumId");
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
        headerView = LayoutInflater.from(this).inflate(R.layout.item_cloud_album_edit_header, null);
        btnChangeCover = (Button) headerView.findViewById(R.id.btn_changeCover);
        tvDate = (TextView) headerView.findViewById(R.id.tv_date);
        ivAlbumImg = (ImageView) headerView.findViewById(R.id.iv_album_image);
        etInputText = (EditText) headerView.findViewById(R.id.et_inputText);
        coverView = headerView.findViewById(R.id.view_Cover_trans);
    }

    private void reqCloudAlbumDetail(String albumId) {
        Subscription subscribe = apiService.queryCloudAlbumDetail(albumId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> loadingDialog.dismiss())
                .subscribe(albumDetailResponse -> {
                    if (albumDetailResponse.success()) {
                        detailObjs = albumDetailResponse.getDataList();
                        int size = detailObjs.size();
                        albumDetailObjHeader = detailObjs.get(0);
                        setupHeaderView();
                        albumDetailObjs.clear();
                        if (size > 1) {
                            albumDetailObjs.addAll(detailObjs.subList(1, detailObjs.size()));
                        }
                        albumDetailAdapter.addHeader(headerView);
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
            albumDetailObjHeader.setContent(headerContent);
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        getSupportActionBar().setTitle(R.string.cloudAlbum);
        coverView.setVisibility(View.GONE);
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.trans)));
        getSupportActionBar().setTitle("");
        etInputText.setEnabled(true);
        etInputText.setVisibility(View.VISIBLE);
        btnChangeCover.setVisibility(View.VISIBLE);
        coverView.setVisibility(View.VISIBLE);
        albumDetailAdapter.setAlbumEditState(true);
        albumDetailAdapter.notifyDataSetChanged();
    }

    public void clickFinishEdit(View view) {
        goCommState();
        //将修改的内容传递给接口
        String list = new Gson().toJson(detailObjs);
        Log.d(TAG, "clickFinishEdit: " + list);
        loadingDialog.show(getSupportFragmentManager(), "");
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
                });
        addSubscription(subscribe);
    }

    public void setupHeaderView() {
        Glide.with(this)
                .load(albumDetailObjHeader.getImgUrl())
                .asBitmap()
                .atMost()
                .into(ivAlbumImg);
        tvDate.setText(DateUtil.formatDate("yyyy.MM.dd", albumDetailObjHeader.getPhotographTime()));
        String content = albumDetailObjHeader.getContent();
        if (!TextUtils.isEmpty(content)) {
            etInputText.setText(content);
        } else {
            etInputText.setVisibility(View.GONE);
        }
        etInputText.setEnabled(false);
    }

    public void clickDeleteImg(View view) {
        //删除照片,待接口
        CloudAlbumDetailObj detailObj = (CloudAlbumDetailObj) view.getTag(R.string.tag_obj);
        Subscription subscribe = apiService.deleteSingleImage(detailObj.getId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        if (baseResponse.success()) {
                            int position = albumDetailObjs.indexOf(detailObj);
                            albumDetailObjs.remove(detailObj);
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

    }

    @Override
    public void clickMenu(@IdRes int resId) {
        switch (resId) {
            case R.id.rl_add_content:
                //添加内容
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
}
