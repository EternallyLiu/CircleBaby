package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.PhotosViewPagerAdapter;
import cn.timeface.circle.baby.api.models.PhotoDataResult;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.events.PhotoSelectEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import de.greenrobot.event.Subscribe;

public class PhotoViewerNewActivity extends BaseAppCompatActivity implements IEventBus {
    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    PhotosViewPagerAdapter mAdapter;

    MenuItem okMenuItem;
    List<ImgObj> imageItems = new ArrayList<>(10);
    List<ImgObj> selItems = new ArrayList<>(10);
    int maxCount;

    public static void openForResult(Context context,
                                     int index,
                                     int maxCount,
                                     int requestCode) {
        Intent intent = new Intent(context, PhotoViewerNewActivity.class);
        intent.putExtra("data_index", index);
        intent.putExtra("max_count", maxCount);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer_new);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        maxCount = getIntent().getIntExtra("max_count", 0);
        imageItems = PhotoDataResult.getInstance().getImgObjs();
        selItems = PhotoDataResult.getInstance().getSelImgObjs();
        setupView();
    }

    private void setupView() {
        int index = getIntent().getIntExtra("data_index", 0);
        mAdapter = new PhotosViewPagerAdapter(this, imageItems, selItems, maxCount);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_photo_slection, menu);
        okMenuItem = menu.findItem(R.id.finish_title);
        okMenuItem.setTitle("完成(" + selItems.size() + "/" + maxCount + ")");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.finish_title:
                close();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void close() {
        Intent data = new Intent();
//        data.putParcelableArrayListExtra("result_select_image_list", (ArrayList<? extends Parcelable>) mAdapter.getSelImgs());
        data.putExtra("result_select_image_list", (Serializable) mAdapter.getSelImgs());
        setResult(RESULT_OK, data);
        finish();
    }

    @Subscribe
    public void onEvent(PhotoSelectEvent event) {
        okMenuItem.setTitle("完成(" + (mAdapter.getSelImgs().size()) + "/" + maxCount + ")");
    }

    @Override
    public void onBackPressed() {
        close();
        super.onBackPressed();
    }

    @Override
    public void onEvent(Object event) {

    }
}
