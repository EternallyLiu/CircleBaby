package cn.timeface.circle.baby.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.SelectPhotosAdapter;
import cn.timeface.circle.baby.events.PhotoSelectEvent;
import cn.timeface.circle.baby.events.PicSaveCompleteEvent;
import cn.timeface.circle.baby.fragments.PhotoCategoryFragment;
import cn.timeface.circle.baby.support.managers.PhotoDataSave;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.managers.services.SavePicInfoService;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.PhotoGroupItem;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.support.utils.ImageUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.mediastore.MediaStoreBucket;
import cn.timeface.circle.baby.views.dialog.LoadingDialog;
import cn.timeface.common.utils.DateUtil;
import cn.timeface.common.utils.StorageUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SelectPhotoActivity extends BaseAppCompatActivity implements IEventBus, MediaScannerConnection.OnScanCompletedListener {

    public static final int NO_MAX = Integer.MAX_VALUE;
    public static final int PHOTO_SELECT_CAMERA_REQUEST_CODE = 1002;
    final int PHOTO_SELECT_PHOTO_VIEWER_REQUEST_CODE = 102;
    private static final int CAMERA_REQUEST_CODE = 51;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.srl_refresh_layout)
    SwipeRefreshLayout srlRefreshLayout;
    SelectPhotosAdapter adapter;
    PhotoCategoryFragment fragment;

    boolean fragmentShow = false;
    List<MediaStoreBucket> buckets = new ArrayList<>();
    String curBucketId = null;
    @Bind(R.id.tv_category)
    TextView tvCategory;
    int maxCount = NO_MAX;
    @Bind(R.id.tv_sel_count)
    TextView tvSelCount;
    long startTime = 0;
    private boolean forResult;
    private LoadingDialog loadingDialog;
    private ArrayList<ImgObj> selImgs;
    private View header;
    private File mPhotoFile;

    public static void openToPublish(Activity activity, int maxCount) {
        Intent intent = new Intent(activity, SelectPhotoActivity.class);
        intent.putExtra("max_count", maxCount);
        intent.putExtra("forResult", false);
        intent.putExtra("forPublish", true);
        activity.startActivity(intent);
    }

    public static void open4result(Activity activity, int requestCode) {
        open4result(activity, NO_MAX, 0, requestCode);
    }

    public static void open4result(Activity activity, int maxCount, int requestCode) {
        open4result(activity, maxCount, 0, requestCode);
    }

    public static void open4result(Activity activity, long startTime, int requestCode) {
        open4result(activity, NO_MAX, startTime, requestCode);
    }

    public static void open4result(Activity activity, int maxCount, long startTime, int requestCode) {
        Intent intent = new Intent(activity, SelectPhotoActivity.class);
        intent.putExtra("max_count", maxCount);
        intent.putExtra("start_time", startTime);
        intent.putExtra("forResult", true);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openForResult(Context context, List<ImgObj> selImages, int maxCount, int requestCode) {
        Intent intent = new Intent(context, SelectPhotoActivity.class);
        intent.putParcelableArrayListExtra("sel_image_list", (ArrayList<ImgObj>) selImages);
        intent.putExtra("max_count", maxCount);
        intent.putExtra("forResult", true);
        ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, null);
    }

    public static void openForResult(Context context, int maxCount, int requestCode) {
        Intent intent = new Intent(context, SelectPhotoActivity.class);
        intent.putExtra("max_count", maxCount);
        intent.putExtra("forResult", true);
        ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        checkCameraPermission();
        ButterKnife.bind(this);
        forResult = getIntent().getBooleanExtra("forResult", false);
        this.maxCount = getIntent().getIntExtra("max_count", NO_MAX);
        this.startTime = getIntent().getLongExtra("start_time", 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContent.setLayoutManager(layoutManager);
        srlRefreshLayout.setEnabled(false);
        selImgs = getIntent().getParcelableArrayListExtra("sel_image_list");
        changeSelCount(0);
//        if (!SavePicInfoService.saveComplete) {
//            if (loadingDialog == null) {
//                loadingDialog = LoadingDialog.getInstance();
//            }
//            loadingDialog.setLoadingMsg("正在加载...");
//            loadingDialog.show(getSupportFragmentManager(), "");
//        } else {
            reqData();
            reqBucket();
//        }
        setupGalleryView();
    }

    private void setupGalleryView() {
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (Math.abs(dy) > 5 && fragmentShow) {
                    clickCategory(tvCategory);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        header = getLayoutInflater().inflate(R.layout.header_photo_selection, null);
    }

    private void reqBucket() {
        Subscription s =
                PhotoModel.getAllBuckets()
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                buckets.clear();
                                buckets.add(0, new MediaStoreBucket("", "全部相册", null));
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(new Func1<PhotoModel, MediaStoreBucket>() {
                            @Override
                            public MediaStoreBucket call(PhotoModel photoModel) {
                                MediaStoreBucket bucket = new MediaStoreBucket(photoModel.getBucketId(), photoModel.getBucketDisplayName(), photoModel.getUri());
                                bucket.setTotalCount((int) PhotoModel.getCountFrom(photoModel.getBucketId()));
                                return bucket;
                            }
                        })
                        .subscribe(
                                bucket -> {
                                    buckets.add(bucket);
                                    if (buckets.get(0).getPhotoUri() == null) {
                                        buckets.get(0).setPhotoUri(bucket.getPhotoUri());
                                    }
                                    buckets.get(0).setTotalCount(buckets.get(0).getTotalCount() + bucket.getTotalCount());
                                }
                                , error -> {
                                }
                        );
        addSubscription(s);
    }

    private void reqData() {
        Subscription s =
                PhotoModel
                        .getAllDateString(curBucketId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(photoModel -> photoModel.getStringDate())
                        .map(date -> {
                            List<PhotoModel> imgs = PhotoModel.getAllFrom(date);
                            if (imgs != null && imgs.size() > 0) {
                                return new PhotoGroupItem(DateUtil.formatDate(DateUtil.YYYYMMDD_P, imgs.get(0).getDateTaken()), imgs);
                            }
                            return null;
                        })
                        .filter(photoGroupItem -> photoGroupItem != null)
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    setListData(result);
                                    completePhotoModelLoad();
                                }
                                , error -> {

                                });
        addSubscription(s);
    }

    private void completePhotoModelLoad() {
        if (selImgs == null || selImgs.size() == 0) return;
        Subscription subscribe = Observable.from(selImgs)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<ImgObj, PhotoModel>() {
                    @Override
                    public PhotoModel call(ImgObj imgObj) {
                        return PhotoModel.getPhotoModel(imgObj.getId(), imgObj.getLocalPath(), imgObj.getUrl());
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<PhotoModel>>() {
                            @Override
                            public void call(List<PhotoModel> photoModels) {
                                if (photoModels != null && photoModels.size() > 0) {
                                    setSelectedImg((ArrayList<PhotoModel>) photoModels);
                                }
                            }
                        }
                        , throwable -> {

                        }
                );
        addSubscription(subscribe);
    }

    private void reqDataFrom(String bucketId) {
        Subscription s =
                Observable
                        .just(bucketId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap(new Func1<String, Observable<PhotoModel>>() {
                            @Override
                            public Observable<PhotoModel> call(String bucketId) {
                                return PhotoModel.getAllDateString(bucketId);
                            }
                        })
                        .map(new Func1<PhotoModel, PhotoGroupItem>() {
                            @Override
                            public PhotoGroupItem call(PhotoModel photoModel) {
                                return new PhotoGroupItem(DateUtil.formatDate(DateUtil.YYYYMMDD_P, photoModel.getDateTaken()), PhotoModel.getAllFrom(bucketId, photoModel.getStringDate()));
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                data -> {
                                    setListData(data);
                                }
                                , error -> {

                                }
                        );
        addSubscription(s);
    }

    private void setListData(List<PhotoGroupItem> datas) {
        List<PhotoGroupItem> data = new ArrayList<>();
        for(PhotoGroupItem item : datas){
            String title = item.getTitle();
            List<PhotoModel> imgList = item.getImgList();
            List<PhotoModel> photoModels = new ArrayList<>();
            for(PhotoModel photoModel : imgList){
                if(ImageFactory.photoFilter(photoModel.getLocalPath())){
                    photoModels.add(photoModel);
                }
            }
            if(photoModels.size()>0){
                data.add(new PhotoGroupItem(title,photoModels));
            }

        }

        if (adapter == null) {
            adapter = new SelectPhotosAdapter(this, data, maxCount);
            rvContent.setAdapter(adapter);
            adapter.addHeader(header);
            return;
        }
        adapter.setListData(data);
        adapter.notifyDataSetChanged();
    }

    public void clickCategory(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (fragmentShow) {
            transaction.hide(fragment);
            fragmentShow = false;
        } else {
            if (fragment == null) {
                fragment = PhotoCategoryFragment.newInstance(buckets, curBucketId);
                transaction.add(R.id.fl_container, fragment);
            } else {
                transaction.show(fragment);
            }
            fragmentShow = true;
        }
        transaction.commit();
    }

    public void clickAlbumSelect(View view) {
        int index = (int) view.getTag(R.string.tag_index);
        curBucketId = buckets.get(index).getId();
        if (null != curBucketId) {
            reqDataFrom(curBucketId);
            tvCategory.setText(buckets.get(index).getName());
            fragment.clickAlbumSelect(view);
            if (fragmentShow) clickCategory(tvCategory);
        }
    }

    @Subscribe
    public void onEvent(PhotoSelectEvent event) {
        changeSelCount(event.count);
        if(event.count == maxCount && maxCount == 1){
            clickDone();
        }
    }

    @Subscribe
    public void onEvent(PicSaveCompleteEvent event) {
        if (loadingDialog != null) loadingDialog.dismiss();
        reqData();
        reqBucket();
    }

    private void changeSelCount(int count) {
        tvSelCount.setText(Html.fromHtml(count + "/" + maxCount));
    }

    public void clickPhotoView(View view) {
        PhotoModel imgObj = (PhotoModel) view.getTag(R.string.tag_obj);
        List<PhotoModel> allImg = new ArrayList<>(10);
        for (PhotoGroupItem item : adapter.getListData()) {
            allImg.addAll(item.getImgList());
        }

        PhotoDataSave.getInstance().setImgs(allImg);
        PhotoDataSave.getInstance().setSelImgs(adapter.getSelImgs());

        PhotoViewerActivity.open4result(this, allImg.indexOf(imgObj), maxCount, PHOTO_SELECT_PHOTO_VIEWER_REQUEST_CODE);
    }

    public void clickDone(View view) {
        if (forResult) {
            ArrayList<ImgObj> imgObjs = transToImgObj(adapter.getSelImgs());

            if (imgObjs.size() == 0) {
                ToastUtil.showToast("请选择图片");
                return;
            }
//            else{
//                Toast.makeText(SelectPhotoActivity.this,"正在压缩图片…",Toast.LENGTH_LONG).show();
//            }
//            for (ImgObj imgObj : imgObjs) {
//                String fileName = System.currentTimeMillis() + ".jpg";
//                File file = new File(ImageFactory.getSDPath(SelectPhotoActivity.this) + "/picture");
//                if (!file.exists()) {
//                    file.mkdirs();
//                }
//                File outDir = new File(file, fileName);//将要保存图片的路径，android推荐这种写法，将目录名和文件名分开，不然容易报错。
//                try {
//                    ImageFactory.getDefault().compressAndGenImage(imgObj.getLocalPath(), outDir.toString(), 100, false);
//                } catch (IOException e) {
//                    Log.e(TAG, "IOException:");
//                }
//                imgObj.setLocalPath(outDir.toString());
//                imgObj.setMd5();
//                imgObj.setUrl();
//                imgObj.setObjectKey();
//                Log.d(TAG, "压缩后的图片getLocalPath========" + imgObj.getLocalPath());
//                Log.d(TAG, "压缩后的图片getUrl========" + imgObj.getUrl());
//            }

            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra("result_select_image_list", imgObjs);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    public void clickDone(){
        if(forResult){
            ArrayList<ImgObj> imgObjs = transToImgObj(adapter.getSelImgs());
            if (imgObjs.size() == 0) {
                ToastUtil.showToast("请选择图片");
                return;
            }
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra("result_select_image_list", imgObjs);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private ArrayList<ImgObj> transToImgObj(List<PhotoModel> photoModels) {
        ArrayList<ImgObj> imgObjs = new ArrayList<>();
        for (PhotoModel photoModel : photoModels) {
            imgObjs.add(photoModel.getImgObj());
        }
        return imgObjs;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoDataSave.getInstance().clear();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_SELECT_PHOTO_VIEWER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        ArrayList<PhotoModel> selImgs = data.getParcelableArrayListExtra("result_select_image_list");
                        adapter.setSelImgs(selImgs);
                        adapter.notifyDataSetChanged();
                        changeSelCount(selImgs.size());
                        if (selImgs.size() == maxCount && maxCount == 1) {
                            clickDone();
                        }
                    }
                }
                break;
            case PHOTO_SELECT_CAMERA_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ImageUtil.scanMediaJpegFile(this, mPhotoFile, this);
                } else {
                    if (mPhotoFile != null) mPhotoFile.delete();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void setSelectedImg(ArrayList<PhotoModel> photoModels) {
        adapter.setSelImgs(photoModels);
        adapter.notifyDataSetChanged();
        changeSelCount(photoModels.size());
    }

    public void clickCamera(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }else{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mPhotoFile = StorageUtil.genSystemPhotoFile();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            startActivityForResult(takePictureIntent, PHOTO_SELECT_CAMERA_REQUEST_CODE);
        }
    }


    @Override
    public void onScanCompleted(String path, Uri uri) {
        Log.d(TAG, "onScanCompleted: " + path + "===" + uri.toString());
        if (!TextUtils.isEmpty(path)) {
            SavePicInfoService.open(this);
            reqData();
            reqBucket();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            setResult(RESULT_OK, getIntent());
            finish();
            return true;
        }else
        return super.onKeyDown(keyCode, event);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
            return;
        }
    }
}
