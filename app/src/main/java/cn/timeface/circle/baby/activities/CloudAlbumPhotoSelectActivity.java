package cn.timeface.circle.baby.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.TimeBookPickerPhotoAdapter;
import cn.timeface.circle.baby.events.PhotoSelectEvent;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.ToastUtil;

/**
 * 云相册更换封面
 * 选择图片界面
 */
public class CloudAlbumPhotoSelectActivity extends BaseAppCompatActivity implements IEventBus {

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String KEY_OPTIONAL_PICTURE_SIZE = "OPTIONAL_PICTURE_SIZE";
    private static final int RECORD_CAMERA_REQUEST_CODE = 50;
    public static final int MAX_SELECTOR_SIZE = 300;
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
    private boolean isMenuInflater = false;
    private MenuItem mMenuDoneItem;
    //可选图片大小
    private int optionalPhotoSize;
    public ArrayList<ImageInfoListObj> dataList;
    private ArrayList<ImageInfoListObj> imageInfoList = new ArrayList<>();
    private TimeBookPickerPhotoAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clounablumphotoselect);
        ButterKnife.bind(this);
//        checkPermission();
        Intent intent = getIntent();
        dataList = intent.getParcelableArrayListExtra("dataList");
        int bookPage = intent.getIntExtra("bookPage", MAX_SELECTOR_SIZE);
        optionalPhotoSize = bookPage;
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
//        if (dataList == null || dataList.size() == 0) {
//            llNoData.setVisibility(View.VISIBLE);
//        }

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
    public void onEvent(PhotoSelectEvent event) {
        changeSelCount(event.count);
    }

    private void changeSelCount(int count) {
        tvSelCount.setText(Html.fromHtml("已选 " + count + " 张"));
    }

    public void clickDone(View view) {
        List<MediaObj> selImgs = adapter.getSelImgs();
        if(selImgs==null||selImgs.size()==0){
            ToastUtil.showToast("请选择一张图片");
            return;
        }
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("result_select_image_list", (ArrayList<? extends Parcelable>) selImgs);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
