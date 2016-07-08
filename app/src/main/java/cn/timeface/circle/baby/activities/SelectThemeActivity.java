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
import com.wechat.photopicker.PickerPhotoActivity2;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.HorizontalListViewAdapter3;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.HorizontalListView;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookType;

public class SelectThemeActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.iv_theme)
    RatioImageView ivTheme;
    @Bind(R.id.lv_horizontal)
    HorizontalListView lvHorizontal;
    private HorizontalListViewAdapter3 adapter;
    private int bookType;
    private String templateName;
    private ArrayList<ImageInfoListObj> dataList;
    private BaseResponse<List<TFOBookType>> listBaseResponse;

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
        reqData();

        lvHorizontal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlideUtil.displayImage(listBaseResponse.getData().get(position).getTemplatePic(), ivTheme);
                adapter.setSelectIndex(position);
                adapter.notifyDataSetChanged();
                bookType = listBaseResponse.getData().get(position).getBookType();
            }
        });
    }

    private void reqData() {
        ApiFactory.getOpenApi().getApiService().bookTypeList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(listBaseResponse -> {
                    this.listBaseResponse = listBaseResponse;
                    setDataList(listBaseResponse.getData());
                }, throwable -> {
                    Log.e(TAG, "getRelationshipList:", throwable);
                });
    }


    public void setDataList(List<TFOBookType> dataList) {
        if (adapter == null) {
            adapter = new HorizontalListViewAdapter3(this, dataList);
            GlideUtil.displayImage(dataList.get(0).getTemplatePic(), ivTheme);
            adapter.setSelectIndex(0);
            bookType = dataList.get(0).getBookType();
            templateName = dataList.get(0).getTemplateName();
        } else {
            adapter.setList(dataList);
            adapter.notifyDataSetChanged();
        }
        lvHorizontal.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private void startPhotoPick(List<ImageInfoListObj> dataList) {
        Intent intent = new Intent(this, PickerPhotoActivity2.class);
        intent.putExtra("bookType",5);
        intent.putParcelableArrayListExtra("dataList", (ArrayList<? extends Parcelable>) dataList);
//        startActivityForResult(intent, 10);
        startActivity(intent);
        finish();
    }
}
