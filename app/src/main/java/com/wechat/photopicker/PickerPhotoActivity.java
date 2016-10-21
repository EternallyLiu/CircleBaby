package com.wechat.photopicker;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        checkPermission();
        checkCameraPermission();
        Intent intent = getIntent();
        optionalPhotoSize = MAX_SELECTOR_SIZE - intent.getIntExtra(MainActivity.KEY_SELECTED_PHOTO_SIZE, 0);
        Log.d(TAG, "optionalPhotoSize---" + optionalPhotoSize);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.picker_photo);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        if (savedInstanceState != null) {
//            optionalPhotoSize = (int) savedInstanceState.get(KEY_OPTIONAL_PICTURE_SIZE);
//        }
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

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        RECORD_CAMERA_REQUEST_CODE);
            }
        }
    }

    private void checkCameraPermission() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE);
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
        }else if(requestCode == CAMERA_REQUEST_CODE){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "您拒绝了使用相机的权限", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent();
            intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, mPhotoSelectorAdapter.getSelectedPhotoPaths());
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
