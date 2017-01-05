package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.HorizontalListViewAdapter3;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.HorizontalListView;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookType;
import cn.timeface.open.api.models.objs.TFOContentObj;
import cn.timeface.open.api.models.objs.TFOPublishObj;
import cn.timeface.open.api.models.objs.TFOResourceObj;

public class SelectThemeActivity extends BaseAppCompatActivity {

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
    private BaseResponse<List<TFOBookType>> listBaseResponse;
    private int cloudAlbum;

    public static void open(Context context) {
        Intent intent = new Intent(context, SelectThemeActivity.class);
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
        dataList = intent.getParcelableArrayListExtra("dataList");
        cloudAlbum = intent.getIntExtra("cloudAlbum", 0);
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
        ApiFactory.getOpenApi().getApiService().bookTypeList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(listBaseResponse -> {
                    tfStateView.finish();
                    this.listBaseResponse = listBaseResponse;
                    setDataList(listBaseResponse.getData());
                }, throwable -> {
                    tfStateView.showException(throwable);
                    Log.e(TAG, "getRelationshipList:", throwable);
                });
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
            if (cloudAlbum == 1) {
                creatBook();
            } else {
                startPhotoPick();
            }
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
        Intent intent = new Intent(this, TimeBookPickerPhotoActivity.class);
        intent.putExtra("bookType", 5);
        intent.putExtra("openBookType", bookTheme);
        intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
//        startActivityForResult(intent, 10);
        startActivity(intent);
        finish();
    }

    private void creatBook() {
        ArrayList<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
        for (ImageInfoListObj obj : dataList) {
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


        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        keys.add("book_author");
        keys.add("book_title");
        values.add(FastData.getUserName());
        values.add(FastData.getBabyName()+"的照片书");

        MyPODActivity.open(this, "","", bookTheme, tfoPublishObjs, new Gson().toJson(dataList),true, FastData.getBabyId(),keys,values,1);
        finish();
    }
}
