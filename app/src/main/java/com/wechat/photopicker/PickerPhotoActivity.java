package com.wechat.photopicker;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wechat.photopicker.adapter.PhotoSelectorAdapter;
import com.wechat.photopicker.endity.Photo;
import com.wechat.photopicker.event.OnItemCheckListener;
import com.wechat.photopicker.fragment.PickerPhotoFragment;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;

/**
 * 选择图界面
 */
public class PickerPhotoActivity extends BaseAppCompatActivity {

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String KEY_OPTIONAL_PICTURE_SIZE = "OPTIONAL_PICTURE_SIZE";
    private static final int RECORD_CAMERA_REQUEST_CODE = 50;
    private static final int CAMERA_REQUEST_CODE = 51;
    protected PickerPhotoFragment mPickerPhotoFragment;
    private PhotoSelectorAdapter mPhotoSelectorAdapter;
    public static final int MAX_SELECTOR_SIZE = 9;
    public static final String TAG = "PickerPhotoActivity";
    private boolean isMenuInflater = false;
    private MenuItem mMenuDoneItem;
    //可选图片大小
    private int optionalPhotoSize;
    private Button btPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        optionalPhotoSize = MAX_SELECTOR_SIZE - intent.getIntExtra(MainActivity.KEY_SELECTED_PHOTO_SIZE, 0);
        Log.d(TAG, "optionalPhotoSize---" + optionalPhotoSize);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.picker_photo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mPickerPhotoFragment = (PickerPhotoFragment) getSupportFragmentManager().findFragmentById(R.id.pick_photo_fragment);
        mPhotoSelectorAdapter = mPickerPhotoFragment.getPhotoSelectorAdapter();
        btPreview = (Button) mPickerPhotoFragment.getView().findViewById(R.id.bt_preview);
        mPhotoSelectorAdapter.setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean OnItemCheck(int position, Photo photo, boolean isCheck, int mSelectorPhotoSize) {

                if (mSelectorPhotoSize == 0) {
                    mMenuDoneItem.setVisible(false);
                    btPreview.setVisibility(View.GONE);
                    Log.d(TAG, "btPreview is GONE");
                } else if (btPreview.getVisibility() == View.GONE) {
                    mMenuDoneItem.setVisible(true);
                    btPreview.setVisibility(View.VISIBLE);
                    Log.d(TAG, "btPreview is VISIBLE");
                }
                mMenuDoneItem.setTitle(getString(R.string.done_selector_size, mPhotoSelectorAdapter.getSelectedItemCount(), optionalPhotoSize));
                btPreview.setText(getString(R.string.preview_selector_paths, mPhotoSelectorAdapter.getSelectedItemCount(), optionalPhotoSize));
                Log.d(TAG, mPhotoSelectorAdapter.getSelectedItemCount() + "");

                if(mPhotoSelectorAdapter.getSelectedItemCount() == optionalPhotoSize){
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, mPhotoSelectorAdapter.getSelectedPhotoPaths());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return true;
            }

        });
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
            Intent intent = new Intent();
            intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, mPhotoSelectorAdapter.getSelectedPhotoPaths());
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
