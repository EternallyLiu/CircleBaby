package cn.timeface.circle.baby.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.TimeBookPickerPhotoAdapter;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.events.PhotoSelectEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.open.api.models.objs.TFOContentObj;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.api.models.objs.TFOResourceObj;

/**
 * 新建照片书
 * 选择图片界面
 */
public class TimeBookPickerPhotoActivity extends BaseAppCompatActivity implements IEventBus, View.OnClickListener {

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String KEY_OPTIONAL_PICTURE_SIZE = "OPTIONAL_PICTURE_SIZE";
    private static final int RECORD_CAMERA_REQUEST_CODE = 50;
    @Bind(R.id.tv_category)
    TextView tvCategory;
    @Bind(R.id.tv_next)
    TextView tvNext;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.tv_sel_count)
    TextView tvSelCount;
    @Bind(R.id.tv_msg)
    TextView tvMsg;
    @Bind(R.id.tv_tocreate)
    TextView tvTocreate;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    public static final int MAX_SELECTOR_SIZE = 300;
    public static final String TAG = "PickerPhotoActivity2";
    private boolean isMenuInflater = false;
    private MenuItem mMenuDoneItem;
    //可选图片大小
    private int optionalPhotoSize;
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
    private String notifyString = "";
    private String coverTitle;
    private TimeBookPickerPhotoAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timebookpickerphoto);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContent.setLayoutManager(layoutManager);
        initView();
    }

    private void initView() {
        if (dataList == null || dataList.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
        }
        tvTocreate.setText("发布时光");
        tvTocreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublishActivity.open(TimeBookPickerPhotoActivity.this, PublishActivity.PHOTO);
            }
        });

        //将日期相同的图片合并
        List<Object> objects = new ArrayList<>();
        for (int x = 0; x < dataList.size(); x++) {
            if (x == 0 || dataList.get(x).getDate() != dataList.get(x - 1).getDate()) {
                ImageInfoListObj imageInfoListObj = dataList.get(x);
                List<MediaObj> mediaList = new ArrayList<>();
                long date = dataList.get(x).getDate();
                for (int y = x; y < dataList.size(); y++) {
                    if (date == dataList.get(y).getDate()) {
                        mediaList.addAll(dataList.get(y).getMediaList());
                    }
                }
                imageInfoListObj.setMediaList(mediaList);
                imageInfoList.add(imageInfoListObj);
            }
        }

        adapter = new TimeBookPickerPhotoAdapter(this, imageInfoList, optionalPhotoSize);
        rvContent.setAdapter(adapter);
        tvNext.setOnClickListener(this);
    }

    @Subscribe
    public void onEvent(PhotoSelectEvent event) {
        changeSelCount(event.count);
    }

    private void changeSelCount(int count) {
        tvSelCount.setText(Html.fromHtml("已选 " + count + " 张"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                pageNum = adapter.getSelImgs().size();
                String s = new Gson().toJson(imageInfoList);

                //跳转开放平台POD接口；
                bookName = FastData.getBabyName() + "照片书";
                tfoResourceObjs = new ArrayList<TFOResourceObj>();
                for (ImageInfoListObj obj : imageInfoList) {
                    for (MediaObj media : obj.getMediaList()) {
                        if(media.getSelected()==1){
                            TFOResourceObj tfoResourceObj = media.toTFOResourceObj();
                            tfoResourceObjs.add(tfoResourceObj);
                        }
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
                break;
        }
    }
}
