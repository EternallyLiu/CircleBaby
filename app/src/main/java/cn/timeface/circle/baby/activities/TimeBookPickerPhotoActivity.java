package cn.timeface.circle.baby.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.TimeBookPickerPhotoAdapter;
import cn.timeface.circle.baby.events.PhotoSelectCountEvent;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * 新建照片书
 * 选择图片界面
 */
public class TimeBookPickerPhotoActivity extends BaseAppCompatActivity implements IEventBus{

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String KEY_OPTIONAL_PICTURE_SIZE = "OPTIONAL_PICTURE_SIZE";
    private static final int RECORD_CAMERA_REQUEST_CODE = 50;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
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

    public static void open(Context context, int bookType, List<ImageInfoListObj> dataList){
        Intent intent = new Intent(context, TimeBookPickerPhotoActivity.class);
        intent.putExtra("bookType", bookType);
        intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
        context.startActivity(intent);
    }

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
        /*for (int x = 0; x < dataList.size(); x++) {
            if (x == 0 || !DateUtil.formatDate("yyyy.MM.dd", dataList.get(x).getDate()).equals(DateUtil.formatDate("yyyy.MM.dd", dataList.get(x - 1).getDate()))) {
                ImageInfoListObj imageInfoListObj = dataList.get(x);
                List<MediaObj> mediaList = new ArrayList<>();
                long date = dataList.get(x).getDate();
                for (int y = x; y < dataList.size(); y++) {
                    if (DateUtil.formatDate("yyyy.MM.dd", date).equals(DateUtil.formatDate("yyyy.MM.dd", dataList.get(y).getDate()))) {
                        mediaList.addAll(dataList.get(y).getMediaList());
                    }
                }
                imageInfoListObj.setMediaList(mediaList);
                imageInfoList.add(imageInfoListObj);
            }
        }*/

        adapter = new TimeBookPickerPhotoAdapter(this, dataList, optionalPhotoSize);
        rvContent.setAdapter(adapter);
    }

    @Subscribe
    public void onEvent(PhotoSelectCountEvent event) {
        changeSelCount(event.count);
    }

    private void changeSelCount(int count) {
        tvSelCount.setText(Html.fromHtml("已选 " + count + " 张"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.next) {
            pageNum = adapter.getSelImgs().size();
            if(pageNum == 0){
                ToastUtil.showToast("请选择至少一张照片");
                return true;
            }
            String s = new Gson().toJson(dataList);

            //跳转开放平台POD接口；
            bookName = FastData.getBabyNickName() + "时光书";
            tfoResourceObjs = new ArrayList<TFOResourceObj>();
            for (ImageInfoListObj obj : dataList) {
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
            TFOPublishObj tfoPublishObj = new TFOPublishObj(bookName, tfoContentObjs1);
            List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
            tfoPublishObjs.add(tfoPublishObj);

            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            keys.add("book_author");
            keys.add("book_title");
            values.add(FastData.getUserName());
            values.add(FastData.getBabyNickName()+"的照片书");

            MyPODActivity.open(this, bookId, openBookId, BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, openBookType, tfoPublishObjs, s,true,FastData.getBabyId(),keys,values,1);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
