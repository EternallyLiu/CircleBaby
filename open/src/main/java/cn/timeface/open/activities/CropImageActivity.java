package cn.timeface.open.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import cn.timeface.open.GlobalSetting;
import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.constants.Constant;
import cn.timeface.open.ucrop.UCrop;
import cn.timeface.open.ucrop.model.AspectRatio;
import cn.timeface.open.ucrop.model.ExifInfo;
import cn.timeface.open.ucrop.model.ImageState;
import cn.timeface.open.ucrop.util.FileUtils;
import cn.timeface.open.ucrop.view.CropImageView;
import cn.timeface.open.ucrop.view.GestureCropImageView;
import cn.timeface.open.ucrop.view.OverlayView;
import cn.timeface.open.ucrop.view.TransformImageView;
import cn.timeface.open.ucrop.view.UCropView;
import cn.timeface.open.utils.Utils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CropImageActivity extends BaseAppCompatActivity {

    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    ImageView ivChangeImage;
    ImageView ivRotation;
    ImageView ivOk;

    ProgressDialog pd;

    TFOBookElementModel elementModel;
    String contentId;
    String newImageUrl;
    float newImageW = 0;
    float newImageH = 0;
    boolean changeImage = false;

    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;

    float aspectRatioX;
    float aspectRatioY;

    private static final int REQUEST_SELECT_PICTURE = 0x01;

    @IntDef({NONE, SCALE, ROTATE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureTypes {

    }

    private int[] mAllowedGestures = new int[]{SCALE, ROTATE, ALL};


    public static void open4result(Activity activity, int requestCode, TFOBookElementModel elementModel, String contentId) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(UCrop.EXTRA_ASPECT_RATIO_X, elementModel.getContentWidth());
        intent.putExtra(UCrop.EXTRA_ASPECT_RATIO_Y, elementModel.getContentHeight());
        Uri in;
        Uri out;
        String url = elementModel.getImageContentExpand().getImageUrl();
        Log.i("open edit url", "open4result: " + url);
        if (TextUtils.isEmpty(url)) {
            in = null;
            out = null;
        } else {
            File file = new File(Glide.getPhotoCacheDir(activity), url.hashCode() + url.substring(url.lastIndexOf(".")));
            if (file.exists()) {
                in = Uri.fromFile(file);
            } else {
                in = Uri.parse(url);
            }
            out = Uri.fromFile(file);
        }
        intent.putExtra(UCrop.EXTRA_INPUT_URI, in);
        intent.putExtra(UCrop.EXTRA_OUTPUT_URI, out);
        intent.putExtra(Constant.ELEMENT_MODEL, elementModel);
        intent.putExtra(Constant.CONTENT_ID, contentId);

        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        this.mUCropView = (UCropView) findViewById(R.id.ucrop);
        this.ivChangeImage = (ImageView) findViewById(R.id.iv_change_image);
        this.ivRotation = (ImageView) findViewById(R.id.iv_rotation);
        this.ivOk = (ImageView) findViewById(R.id.iv_ok);
        mGestureCropImageView = mUCropView.getCropImageView();
        mGestureCropImageView.setRotateEnabled(false);
        mOverlayView = mUCropView.getOverlayView();
        mGestureCropImageView.setTransformImageListener(mImageListener);

        elementModel = getIntent().getParcelableExtra(Constant.ELEMENT_MODEL);
        contentId = getIntent().getStringExtra(Constant.CONTENT_ID);
        newImageW = elementModel.getImageContentExpand().getImageWidth();
        newImageH = elementModel.getImageContentExpand().getImageHeight();

        setImageData(getIntent());
    }


    private void showProgressDialog(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
        }

        if (pd.isShowing()) {
            pd.dismiss();
        }
        pd.setMessage(msg);

        pd.show();
    }

    private void dismissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    private void setImageData(Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        Uri outputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);

        if (inputUri == null) {
            try {
                File emptyFile = new File(Glide.getPhotoCacheDir(this), "tfo_empty_img.png");
                if (!emptyFile.exists()) {
                    Utils.copyFileFromAssets(this, "tfo_empty_img.png", new File(Glide.getPhotoCacheDir(this), "tfo_empty_img.png").getAbsolutePath());
                }
                inputUri = Uri.fromFile(emptyFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (outputUri == null) {
            outputUri = Uri.fromFile(new File(Glide.getPhotoCacheDir(this), "temp.jpg"));
        }

        processOptions(intent);

        if (inputUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                finish();
            }
        } else {
            setResultError(new NullPointerException("Both input and output Uri must be specified"));
            finish();
        }

    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            Log.i(TAG, "onRotate: " + currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
            Log.i(TAG, "onScale: " + currentScale);
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            setResultError(e);
            finish();
        }

    };

    protected void setResultSuccess() {
        setResult(RESULT_OK, new Intent()
                .putExtra(Constant.ELEMENT_MODEL, elementModel)
                .putExtra(Constant.CONTENT_ID, contentId));
    }

    protected void setResultError(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }


    @SuppressWarnings("deprecation")
    private void processOptions(@NonNull Intent intent) {
        // Bitmap compression options
        String compressionFormatName = intent.getStringExtra(UCrop.Options.EXTRA_COMPRESSION_FORMAT_NAME);
        Bitmap.CompressFormat compressFormat = null;
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
        }

        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(intent.getIntExtra(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(intent.getFloatExtra(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(intent.getIntExtra(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));


        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(intent.getBooleanExtra(UCrop.Options.EXTRA_FREE_STYLE_CROP, OverlayView.DEFAULT_FREESTYLE_CROP_ENABLED));

        mOverlayView.setDimmedColor(intent.getIntExtra(UCrop.Options.EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(R.color.ucrop_color_default_dimmed)));
        mOverlayView.setOvalDimmedLayer(intent.getBooleanExtra(UCrop.Options.EXTRA_OVAL_DIMMED_LAYER, OverlayView.DEFAULT_OVAL_DIMMED_LAYER));

        mOverlayView.setShowCropFrame(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_FRAME, OverlayView.DEFAULT_SHOW_CROP_FRAME));
        mOverlayView.setCropFrameColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_frame)));
        mOverlayView.setCropFrameStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width)));

        mOverlayView.setShowCropGrid(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_GRID, OverlayView.DEFAULT_SHOW_CROP_GRID));
        mOverlayView.setCropGridRowCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_ROW_COUNT, OverlayView.DEFAULT_CROP_GRID_ROW_COUNT));
        mOverlayView.setCropGridColumnCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLUMN_COUNT, OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT));
        mOverlayView.setCropGridColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_grid)));
        mOverlayView.setCropGridStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width)));

        // Aspect ratio options
        aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
        aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

        int aspectRationSelectedByDefault = intent.getIntExtra(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioX > 0 && aspectRatioY > 0) {
            mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
        } else if (aspectRatioList != null && aspectRationSelectedByDefault < aspectRatioList.size()) {
            mGestureCropImageView.setTargetAspectRatio(aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioX() /
                    aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioY());
        } else {
            mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }

        // Result bitmap max size options
        int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
        int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
            mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    public void clickClose(View view) {
        finish();
    }

    public void clickChangeImage(View view) {
        pickFromGallery();
    }

    public void clickRotation(View view) {
        rotateByAngle(90);
    }

    private void rotateByAngle(int angle) {
        mGestureCropImageView.postRotate(angle);
        mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    public void clickOK(View view) {
        if (changeImage) {
            makeModify(newImageUrl);
        } else {
            makeModify(elementModel.getImageContentExpand().getImageUrl());
        }

        setResultSuccess();
        finish();
    }

    public void makeModify(String newImageUrl) {
        ImageState imageState = mGestureCropImageView.getImageState();
        ExifInfo exifInfo = mGestureCropImageView.getExifInfo();
        exifInfo.getExifDegrees();
        Log.i(TAG, "makeModify: imageState = " + imageState.toString());
        RectF cropRect = imageState.getCropRect();
        RectF imageRect = imageState.getCurrentImageRect();

        float top = Math.max(Math.round((cropRect.top - imageRect.top) / imageState.getCurrentScale()), 0);
        float left = Math.max(Math.round((cropRect.left - imageRect.left) / imageState.getCurrentScale()), 0);
        int width = Math.round(cropRect.width() / imageState.getCurrentScale());
        int height = Math.round(cropRect.height() / imageState.getCurrentScale());

        int rotation = Math.round(imageState.getCurrentAngle());
        if (rotation < 0) {
            rotation = rotation % 360 + 360;
        }
        int totalRotation = (rotation + exifInfo.getExifDegrees()) % 360;
        float finalImageW = newImageW;
        float finalImageH = newImageH;
        if (exifInfo.getExifDegrees() == 90 || exifInfo.getExifDegrees() == 270) {
            //图片本身有旋转,则调换宽度和高度
            finalImageW = newImageH;
            finalImageH = newImageW;
        }

//        if (totalRotation == 90 || totalRotation == 270) {
//            int temp = width;
//            width = height;
//            height = temp;
//        }

        {
            //旋转后,映射到原图位置
            float tempX;
            switch (totalRotation) {
                case 90:
                    left += width;
                    left = finalImageH - left;

                    tempX = left;
                    left = top;
                    top = tempX;
                    break;
                case 180:
                    left += width;
                    top += height;
                    left = finalImageW - left;
                    top = finalImageH - top;
                    break;
                case 270:
                    top += height;
                    top = finalImageW - top;

                    tempX = left;
                    left = top;
                    top = tempX;
                    break;
            }
        }

        // TODO: 8/10/16 旋转角度是否对该参数有影像??
        float scale = cropRect.width() / elementModel.getContentWidth();
        float finalImageScale = imageState.getCurrentScale() / scale;
        elementModel.setElementContent(newImageUrl);
        elementModel.getImageContentExpand().setImageUrl(newImageUrl);
        elementModel.getImageContentExpand().setImageScale(finalImageScale);
        elementModel.getImageContentExpand().setImageWidth(newImageW);
        elementModel.getImageContentExpand().setImageHeight(newImageH);
        elementModel.getImageContentExpand().setImageStartPointX(left);
        elementModel.getImageContentExpand().setImageStartPointY(top);
        elementModel.getImageContentExpand().setImageRotation(rotation);
    }


    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择照片"), REQUEST_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_SELECT_PICTURE) {
            changeImage = true;
            Intent intent = getIntent();
            intent.putExtra(UCrop.EXTRA_INPUT_URI, data.getData());
            intent.putExtra(UCrop.EXTRA_OUTPUT_URI, data.getData());
            setImageData(intent);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(FileUtils.getPath(this, data.getData()), options);
            newImageW = options.outWidth;
            newImageH = options.outHeight;
            //// TODO: 16/8/2 这个地方可能存在图片有旋转而导致宽度和高度不准确的问题

            doUpload(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void doUpload(Uri uri) {
        Log.i(TAG, "doUpload: 111111  start");
        Observable.just(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<Uri, String>() {
                    @Override
                    public String call(Uri uri) {
                        return GlobalSetting.getInstance().getUploadServices().doUpload(uri);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgressDialog("正在上传");
                        newImageUrl = null;
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        dismissProgressDialog();
                    }
                })
                .subscribe(new Action1<String>() {
                               @Override
                               public void call(String url) {
                                   Log.i(TAG, "doUpload: 111111  222222  end" + url);
                                   newImageUrl = url;
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(CropImageActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "call: ", throwable);
                                setResultError(throwable);
                                finish();
                            }
                        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }
}
