package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.github.rayboot.widget.ratioview.RatioImageView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.HorizontalListViewAdapter3;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.PaintingCollectionCustomDataObj;
import cn.timeface.circle.baby.support.api.models.objs.PaintingCollectionRemarkObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerPhotoActivity;
import cn.timeface.circle.baby.views.HorizontalListView;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookType;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;
import cn.timeface.open.model.TFOpenDataProvider;

public class SelectThemeActivity extends BaseAppCompatActivity implements IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.iv_theme)
    RatioImageView ivTheme;
    @Bind(R.id.lv_horizontal)
    HorizontalListView lvHorizontal;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    private HorizontalListViewAdapter3 adapter;
    private int bookTheme;
    private String templateName;
    private ArrayList<ImageInfoListObj> dataList;
    private TFOBaseResponse<List<TFOBookType>> listBaseResponse;
    private int cloudAlbum;
    private String bookId = "";
    private String openBookId = "";
    private int babyId;
//    private int openBookType;

    public static void open(Context context, String bookId, String openBookId, int babyId) {
        Intent intent = new Intent(context, SelectThemeActivity.class);
        intent.putExtra("book_id", bookId);
        intent.putExtra("open_book_id", openBookId);
        intent.putExtra("baby_id", babyId);
//        intent.putExtra("open_book_type", openBookType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecttheme);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        dataList = intent.getParcelableArrayListExtra("data_list");
        this.bookId = getIntent().getStringExtra("book_id");
        this.openBookId = getIntent().getStringExtra("open_book_id");
//        this.openBookType = getIntent().getIntExtra("open_book_type", 0);
        cloudAlbum = intent.getIntExtra("cloudAlbum", 0);
        babyId = intent.getIntExtra("baby_id", 0);
        tfStateView.setOnRetryListener(() -> reqData());
        reqData();

        lvHorizontal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlideUtil.displayImage(listBaseResponse.getData().get(position).getTemplatePic(), ivTheme);
                adapter.setSelectIndex(position);
                adapter.notifyDataSetChanged();
                bookTheme = listBaseResponse.getData().get(position).getBookType();
            }
        });
    }

    private void reqData() {
        tfStateView.loading();
        // 7-开放平台照片书 pod type
        addSubscription(
                TFOpenDataProvider.get().bookTypeList(0, 7, 0, 0)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(listBaseResponse -> {
                    tfStateView.finish();
                    this.listBaseResponse = listBaseResponse;
                    setDataList(listBaseResponse.getData());
                }, throwable -> {
                    tfStateView.showException(throwable);
                    Log.e(TAG, "getRelationshipList:", throwable);
                }));
    }


    public void setDataList(List<TFOBookType> dataList) {
        if (adapter == null) {
            adapter = new HorizontalListViewAdapter3(this, dataList);
            GlideUtil.displayImage(dataList.get(0).getTemplatePic(), ivTheme);
            adapter.setSelectIndex(0);
            bookTheme = dataList.get(0).getBookType();
            templateName = dataList.get(0).getTemplateName();
        } else {
            adapter.setList(dataList);
            adapter.notifyDataSetChanged();
        }
        lvHorizontal.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.complete){
//            if (cloudAlbum == 1) {
                creatBook(bookId, openBookId);
//            } else {
//                startPhotoPick();
//            }
        }
        return super.onOptionsItemSelected(item);
    }

//    private void startPhotoPick() {
//        Intent intent = new Intent(this, PickerPhotoActivity2.class);
//        intent.putExtra("bookType",5);
//        intent.putExtra("openBookType",bookTheme);
//        intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
////        startActivityForResult(intent, 10);
//        startActivity(intent);
//        finish();
//    }

    private void startPhotoPick() {
//        Intent intent = new Intent(this, TimeBookPickerPhotoActivity.class);
//        intent.putExtra("bookType", BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK);
//        intent.putExtra("openBookType", bookTheme);
//        intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
//        startActivity(intent);
//        finish();
        SelectServerPhotoActivity.open(this, BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, bookTheme, "", "", babyId);
    }

    private void creatBook(String bookId, String openBookId) {
        addSubscription(
                apiService.bookMedias(bookId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if(response.success()){
                                        //跳转开放平台POD接口；
                                        String bookName = FastData.getBabyName() + "的照片书";
                                        List<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
                                        StringBuffer sb = new StringBuffer("{\"dataList\":[");
                                        int index = 0;
                                        for(MediaObj mediaObj : response.getDataList()){
                                            index++;
                                            TFOResourceObj tfoResourceObj = mediaObj.toTFOResourceObj();
                                            tfoResourceObjs.add(tfoResourceObj);
                                            sb.append(mediaObj.getId());
                                            if (index < response.getDataList().size()) {
                                                sb.append(",");
                                            } else {
                                                sb.append("]}");
                                            }
                                        }


                                        List<TFOContentObj> tfoContentObjs1 = new ArrayList<>();
                                        TFOContentObj tfoContentObj;
                                        tfoContentObj = new TFOContentObj("", tfoResourceObjs);
                                        tfoContentObjs1.add(tfoContentObj);

                                        ArrayList<String> keys = new ArrayList<>();
                                        ArrayList<String> values = new ArrayList<>();
                                        keys.add("book_author");
                                        keys.add("book_title");

                                        values.add(FastData.getUserName());
                                        values.add(bookName);


                                        TFOPublishObj tfoPublishObj = new TFOPublishObj(bookName, tfoContentObjs1);
                                        List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
                                        tfoPublishObjs.add(tfoPublishObj);

                                        MyPODActivity.open(this, bookId, openBookId, BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, bookTheme, tfoPublishObjs, sb.toString(),true,FastData.getBabyId(),keys,values,1);
                                        finish();
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )
        );
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent){
        if(optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_CREATE){
            finish();
        }
    }
}
