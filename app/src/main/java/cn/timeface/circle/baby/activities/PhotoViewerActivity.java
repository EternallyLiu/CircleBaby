package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.PhotosViewPagerAdapter;
import cn.timeface.circle.baby.support.managers.PhotoDataSave;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.utils.ToastUtil;


/**
 * Created by rayboot on 15/3/27.
 */
public class PhotoViewerActivity extends BaseAppCompatActivity {
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private List<PhotoModel> allImgs;
    private int maxCount;
    private List<PhotoModel> allSelImgs;
    private int selImgInBucket;
    private PhotosViewPagerAdapter adapter;
    @Bind(R.id.cb_sel)
    CheckBox cbSel;

    public static void open4result(Activity activity,
                                   int index,
                                   int maxCount,
                                   int requestCode) {
        Intent intent = new Intent(activity, PhotoViewerActivity.class);
        intent.putExtra("data_index", index);
        intent.putExtra("max_count", maxCount);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_photo_viewer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        maxCount = getIntent().getIntExtra("max_count", 0);
        allImgs = PhotoDataSave.getInstance().getImgs();
        allSelImgs = PhotoDataSave.getInstance().getSelImgs();
        for (PhotoModel p : allSelImgs) {
            if (allImgs.contains(p)) {
                selImgInBucket++;
            }
        }
        setTitle();
        setupView();
    }

    private void setupView() {
        int index = getIntent().getIntExtra("data_index", 0);
        adapter = new PhotosViewPagerAdapter(this, allImgs);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index);
        setCheckState(allImgs.get(index));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCheckState(allImgs.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void close() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra("result_select_image_list", (ArrayList<? extends Parcelable>) allSelImgs);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        viewPager.clearOnPageChangeListeners();
        PhotoDataSave.getInstance().clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        close();
        super.onBackPressed();
    }

    public void clickSelect(View view) {
        PhotoModel curPhotoModel = allImgs.get(viewPager.getCurrentItem());
        if (((CheckBox) view).isChecked()) {
            if (selImgInBucket >= maxCount) {
                ((CheckBox) view).setChecked(false);
                ToastUtil.showToast("最多只能选择" + maxCount + "张照片");
                return;
            }
            selImgInBucket++;
            allSelImgs.add(curPhotoModel);
        } else {
            selImgInBucket--;
            allSelImgs.remove(curPhotoModel);
        }
        setTitle();
        close();
    }

    public void setCheckState(PhotoModel photoModel) {
        cbSel.setChecked(allSelImgs.contains(photoModel));
    }

    public void setTitle() {
        getSupportActionBar().setTitle(selImgInBucket + "/" + allImgs.size());
    }
}
