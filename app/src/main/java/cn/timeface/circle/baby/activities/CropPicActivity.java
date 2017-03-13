package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.views.cropper.CropImageView;
import cn.timeface.circle.baby.support.utils.ImageUtil;
import cn.timeface.common.utils.DeviceUtil;
import cn.timeface.common.utils.StorageUtil;

/**
 * @author SUN
 * @from 2014/12/9
 * @TODO 裁剪图片
 */
public class CropPicActivity extends BaseAppCompatActivity {
    private static final int ROTATE_NINETY_DEGREES = 90;

    @Bind(R.id.toolbar)
    Toolbar toolBar;
    @Bind(R.id.cropimageview)
    CropImageView mCropImageView;
    int ratioW;
    int ratioH;
    int outW;
    int outH;
    private Bitmap bitmap;

    //path 文件路径
    //ratioW 宽度比例
    //ratioH 高度比例
    //outW 输出宽度   <0时原样输出  >0时压缩成目标outW输出
    //outH 输出高度   <0时原样输出  >0时压缩成目标outH输出
    public static void openForResult(Context context, String path, int ratioW,
                                     int ratioH, int outW, int outH, int requestCode) {
        Intent intent = new Intent(context, CropPicActivity.class);
        intent.putExtra("path", path);
        intent.putExtra("ratio_w", ratioW);
        intent.putExtra("ratio_h", ratioH);
        intent.putExtra("out_w", outW);
        intent.putExtra("out_h", outH);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        int bitMapHeight = options.outHeight;
        int bitMapWidth = options.outWidth;
        int height = bitMapHeight;
        int width = bitMapWidth;
        float aspectRatio = (float) width / height;
        int inSampleSize = 1;

        // 计算可以显示的宽和高
        if (height > reqHeight || width > reqWidth) {
            if (height >= width) {
                width = reqWidth;
                height = (int) (width / aspectRatio);
                inSampleSize = Math.round((float) bitMapHeight / (float) height);
            } else if (width > height) {
                height = reqHeight;
                width = (int) (height * aspectRatio);
                inSampleSize = Math.round((float) bitMapWidth / (float) width);
            }

        }
        return inSampleSize;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_pic);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(255, 0, 0, 0)));

        String path = getIntent().getStringExtra("path");
        ratioW = getIntent().getIntExtra("ratio_w", 100);
        ratioH = getIntent().getIntExtra("ratio_h", 100);
        outW = getIntent().getIntExtra("out_w", 150);
        outH = getIntent().getIntExtra("out_h", 150);
        String imgPath = path;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options); // 此时返回的bitmap为null

        //图片能显示最大宽度
        int width = DeviceUtil.getScreenWidth(this);
        // 图片能显示的最大高度
        int height = DeviceUtil.getScreenHeight(this) - DeviceUtil.getStatusBarHeight(this) - DeviceUtil.dpToPx(getResources(), 55);
        options.inSampleSize = calculateInSampleSize(options, width / 2, height / 2);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(imgPath, options);

        if (ratioH > 0 && ratioW > 0) {
            mCropImageView.setAspectRatio(ratioW, ratioH);
            mCropImageView.setFixedAspectRatio(true);
        }
        mCropImageView.setImageBitmap(bitmap);
    }

    public void clickCrop(View view) {
        Bitmap bitmap = mCropImageView.getCroppedImage();
        if (outW > 0 && outH > 0) {
            bitmap = ImageUtil.getDefault().ratio(bitmap, outW, outH);
        }

        File photo = StorageUtil.getTFPhotoPath();
        try {
            ImageUtil.getDefault()
                    .storeImage(bitmap, photo.getAbsolutePath(), 90);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = this.getIntent();
        intent.putExtra("crop_path", photo.getAbsolutePath());

        this.setResult(RESULT_OK, intent);
        finish();
        bitmap.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_crop_pic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rotate:
                mCropImageView.rotateImage(ROTATE_NINETY_DEGREES);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}