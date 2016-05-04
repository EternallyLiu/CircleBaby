package com.wechat.photopicker;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wechat.photopicker.adapter.PhotoAdapter;
import com.wechat.photopicker.event.OnPhotoClickListener;
import com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent;

import java.util.ArrayList;

import cn.timeface.circle.baby.R;

import static com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent.KEY_PHOTO_PATHS;
import static com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent.KEY_SELECTOR_POSITION;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mPickerPhoto;
    public static final String TAG = "MainActivity";
    private ArrayList<String> mPhotoPathList = new ArrayList<>(PickerPhotoActivity.MAX_SELECTOR_SIZE);
    private PhotoAdapter mPhotoAdapter;
    private RecyclerView mRecyclerView;
    private final int starPickerPhotoActivityRequestCode = 1;
    public static final String KEY_SELECTED_PHOTO_SIZE  = "SELECTED_PHOTO_SIZE";
    private Button mClearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopicker);
        ActionBar actionBar = getActionBar();

        Log.d(TAG, (actionBar == null ? true : false) + "");
        mPickerPhoto = (Button) findViewById(R.id.bt_picker);
        mClearButton = (Button) findViewById(R.id.bt_clear);
        mPickerPhoto.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_selector_photo);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        mPhotoAdapter = new PhotoAdapter(MainActivity.this,mPhotoPathList);
        mRecyclerView.setAdapter(mPhotoAdapter);
        mPhotoAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View view, int position, boolean showCamera) {
                BigImageShowIntent bigImageShowIntent = new BigImageShowIntent(MainActivity.this);
                bigImageShowIntent.putExtra(KEY_SELECTOR_POSITION,position);
                bigImageShowIntent.putStringArrayListExtra(KEY_PHOTO_PATHS,mPhotoPathList);
                startActivity(bigImageShowIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case starPickerPhotoActivityRequestCode:
                     mPhotoPathList.addAll(data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS));
                     mPhotoAdapter.notifyDataSetChanged();
                     break;
                default:
                     Log.e(TAG,"onActivityResult requestCode no found!");
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_picker) {
            Intent intent = new Intent(MainActivity.this, PickerPhotoActivity.class);
            intent.putExtra(KEY_SELECTED_PHOTO_SIZE, mPhotoPathList.size());
            startActivityForResult(intent, starPickerPhotoActivityRequestCode);

        } else if (i == R.id.bt_clear) {
            if (mPhotoPathList.removeAll(mPhotoPathList)) {
                Toast.makeText(this, "清理成功！", Toast.LENGTH_SHORT).show();
                mPhotoAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "清理失败！", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
