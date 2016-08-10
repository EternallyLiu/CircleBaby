package com.wechat.photopicker;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wechat.photopicker.adapter.PhotoSelectorAdapter2;
import com.wechat.photopicker.event.OnItemCheckListener2;
import com.wechat.photopicker.fragment.PickerPhotoFragment2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.open.api.models.objs.TFOContentObj;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.api.models.objs.TFOResourceObj;

/**
 * 新建识图卡片书和日记卡片书
 * 选择图片界面
 */
public class PickerPhotoActivity2 extends BaseAppCompatActivity implements IEventBus{

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String KEY_OPTIONAL_PICTURE_SIZE = "OPTIONAL_PICTURE_SIZE";
    private static final int RECORD_CAMERA_REQUEST_CODE = 50;
    protected PickerPhotoFragment2 mPickerPhotoFragment;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.container)
    FrameLayout container;
    @Bind(R.id.tv_msg)
    TextView tvMsg;
    @Bind(R.id.tv_tocreate)
    TextView tvTocreate;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    private PhotoSelectorAdapter2 mPhotoSelectorAdapter;
    public static final int MAX_SELECTOR_SIZE = 99;
    public static final String TAG = "PickerPhotoActivity2";
    private boolean isMenuInflater = false;
    private MenuItem mMenuDoneItem;
    //可选图片大小
    private int optionalPhotoSize;
    private Button btPreview;
    public ArrayList<ImageInfoListObj> dataList;
    private ArrayList<ImageInfoListObj> imageInfoList = new ArrayList<>();
    private int bookType;
    private String bookSizeId;
    private int pageNum;
    private String bookName = "";
    private ArrayList<TFOResourceObj> tfoResourceObjs;
    private int openBookType;
    private String bookId = "";
    private String openBookId = "";
    private int bookPage;
    private String notifyString;
    private String coverTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo2);
        ButterKnife.bind(this);
//        checkPermission();
        Intent intent = getIntent();
        bookType = intent.getIntExtra("bookType", 0);
        bookSizeId = intent.getStringExtra("bookSizeId");
        bookId = intent.getStringExtra("bookId");
        openBookId = intent.getStringExtra("openBookId");
        openBookType = intent.getIntExtra("openBookType", 0);
        dataList = intent.getParcelableArrayListExtra("dataList");
        coverTitle = intent.getStringExtra("coverTitle");
        bookPage = intent.getIntExtra("bookPage", MAX_SELECTOR_SIZE);
        optionalPhotoSize = bookPage;
        Log.d(TAG, "optionalPhotoSize---" + optionalPhotoSize);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.picker_photo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null) {
            optionalPhotoSize = (int) savedInstanceState.get(KEY_OPTIONAL_PICTURE_SIZE);
        }
        mPickerPhotoFragment = (PickerPhotoFragment2) getSupportFragmentManager().findFragmentById(R.id.pick_photo_fragment);
        mPhotoSelectorAdapter = mPickerPhotoFragment.getPhotoSelectorAdapter();
        btPreview = (Button) mPickerPhotoFragment.getView().findViewById(R.id.bt_preview);
        mPhotoSelectorAdapter.setOnItemCheckListener(new OnItemCheckListener2() {
            @Override
            public boolean OnItemCheck(int position, MediaObj photo, boolean isCheck, int mSelectorPhotoSize) {
                if (mSelectorPhotoSize == 0) {
                    mMenuDoneItem.setVisible(false);
                    btPreview.setVisibility(View.GONE);
                    Log.d(TAG, "btPreview is GONE");
                } else if (btPreview.getVisibility() == View.GONE) {
                    mMenuDoneItem.setVisible(true);
//                    btPreview.setVisibility(View.VISIBLE);
                    Log.d(TAG, "btPreview is VISIBLE");
                }
                mMenuDoneItem.setTitle(getString(R.string.done_selector_size, mPhotoSelectorAdapter.getSelectedItemCount(), optionalPhotoSize));
                btPreview.setText(getString(R.string.preview_selector_paths, mPhotoSelectorAdapter.getSelectedItemCount(), optionalPhotoSize));
                Log.d(TAG, mPhotoSelectorAdapter.getSelectedItemCount() + "");
                return true;
            }

        });
        initView();
    }

    private void initView() {
        if (dataList == null || dataList.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
        }
        tvTocreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookType==2){
                    //日记卡片
                    PublishActivity.open(PickerPhotoActivity2.this, PublishActivity.DIALY);
                }else if(bookType == 3){
                    //识图卡片
                    PublishActivity.open(PickerPhotoActivity2.this, PublishActivity.CARD);
                }
            }
        });

    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        RECORD_CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == RECORD_CAMERA_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "您拒绝了选择照片的权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isMenuInflater) {
            getMenuInflater().inflate(R.menu.menu_picker, menu);
            mMenuDoneItem = menu.findItem(R.id.done);
            isMenuInflater = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            super.onBackPressed();

        } else if (i == R.id.done) {
            List<Integer> timeIds = mPhotoSelectorAdapter.getTimeIds();
            HashSet<Integer> integers = new HashSet<>(timeIds);
            timeIds.clear();
            timeIds.addAll(integers);
            pageNum = mPhotoSelectorAdapter.getSelectedPhotos().size();

            if (pageNum < bookPage) {
                if (bookType == 2) {
                    //日记卡片
                    notifyString = "印制" + coverTitle + "日记卡需要" + bookPage + "张，只选了" + pageNum + "张还少" + (bookPage - pageNum) + "张";
                } else if (bookType == 3) {
                    //识图卡片
                    notifyString = "印制识图卡需要" + bookPage + "张，只选了" + pageNum + "张还少" + (bookPage - pageNum) + "张";
                }
                ToastUtil.showToast(notifyString);
                return true;
            }

            imageInfoList.clear();
            for (ImageInfoListObj obj : dataList) {
                if (timeIds.contains(obj.getTimeId())) {
                    imageInfoList.add(obj);
                }
            }

            String s = new Gson().toJson(imageInfoList);

            if (bookType == 2) {
                bookName = FastData.getBabyName() + "日记卡片书";
                createBook(s);
            } else if (bookType == 3) {
                bookName = FastData.getBabyName() + "识图卡片书";
                createBook(s);
            } else if (bookType == 5) {
                //跳转开放平台POD接口；
                bookName = FastData.getBabyName() + "照片书";
                tfoResourceObjs = new ArrayList<TFOResourceObj>();
                for (ImageInfoListObj obj : imageInfoList) {
                    for (MediaObj media : obj.getMediaList()) {
                        TFOResourceObj tfoResourceObj = media.toTFOResourceObj();
                        tfoResourceObjs.add(tfoResourceObj);
                    }
                }
                TFOContentObj tfoContentObj = new TFOContentObj("", tfoResourceObjs);
                List<TFOContentObj> tfoContentObjs1 = new ArrayList<>();
                tfoContentObjs1.add(tfoContentObj);
                TFOPublishObj tfoPublishObj = new TFOPublishObj("", tfoContentObjs1);
                List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
                tfoPublishObjs.add(tfoPublishObj);
                MyPODActivity.open(this, openBookId, openBookType, tfoPublishObjs, s);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void createBook(String s) {
        apiService.createBook(URLEncoder.encode(FastData.getUserInfo().getNickName()), FastData.getBabyId(), mPhotoSelectorAdapter.getSelectedPhotos().get(0).getImgUrl(), bookId, URLEncoder.encode(bookName), bookSizeId, bookType, s, URLEncoder.encode(bookName), 0, pageNum, openBookType)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        EventBus.getDefault().post(new BookOptionEvent());
                        finish();
                    } else {
                        ToastUtil.showToast(response.getInfo());
                    }
                }, error -> {
                    Log.e(TAG, "createBook:");
                });
    }

    @Subscribe
    public void onEvent(HomeRefreshEvent event) {
//        reqData();
    }

    private void reqData() {
        apiService.queryImageInfoList("", bookType)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        List<ImageInfoListObj> dataList = response.getDataList();
                        List<ImageInfoListObj> dataList1 = mPhotoSelectorAdapter.getDataList();
                        dataList1.add(dataList.get(0));
                        mPhotoSelectorAdapter.notifyDataSetChanged();
                    }
                }, error -> {
                    Log.e(TAG, "queryImageInfoList:");
                });
    }

}
