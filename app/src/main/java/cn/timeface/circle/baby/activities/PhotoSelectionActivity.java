package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.CircleSelectPhotosAdapter;
import cn.timeface.circle.baby.api.models.PhotoDataResult;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.PhotoGroupItem;
import cn.timeface.circle.baby.events.PhotoSelectEvent;
import cn.timeface.circle.baby.events.PhotoSelectionAddedEvent;
import cn.timeface.circle.baby.events.PhotoSelectionRemovedEvent;
import cn.timeface.circle.baby.fragments.PhotoCategoryFragment;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.ImageUtil;
import cn.timeface.circle.baby.utils.mediastore.MediaStoreBucket;
import cn.timeface.circle.baby.utils.mediastore.MediaStoreBucketsAsyncTask;
import cn.timeface.circle.baby.utils.mediastore.MediaStoreCursorHelper;
import cn.timeface.circle.baby.utils.mediastore.PhotoCursorLoader;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.StorageUtil;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by rayboot on 15/4/16.
 */
public class PhotoSelectionActivity extends BaseAppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, MediaStoreBucketsAsyncTask.MediaStoreBucketsResultListener,
        MediaScannerConnection.OnScanCompletedListener, IEventBus {
    static final int PHOTO_SELECT_CAMERA_REQUEST_CODE = 101;
    static final String LOADER_PHOTOS_BUCKETS_PARAM = "bucket_id";
    static final int LOADER_USER_PHOTOS_EXTERNAL = 0x01;
    public static MediaStoreBucket curBucket;
    final int PHOTO_SELECT_PHOTO_VIEWER_REQUEST_CODE = 102;
    final int PHOTO_SELECT_PHOTO_SCAN_REQUEST_CODE = 103;
    boolean isDirectReturn = false;
    boolean takePhoto = false;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.appbar)
    AppBarLayout mAppbar;
    @Bind(R.id.tv_category)
    TextView tvCategory;
    private CircleSelectPhotosAdapter mPhotoAdapter;
    private RecyclerView mPhotoGrid;
    private ArrayList<MediaStoreBucket> mBuckets = new ArrayList<>();
    private File mPhotoFile;
    private File mSelectCameraFile;
    private List<ImgObj> selImgs = new ArrayList<>(10);

    private int maxCount;
    private View header;
    PhotoCategoryFragment fragment;
    boolean fragmentShow = false;
    TFProgressDialog tfProgressDialog;
    MenuItem okMenuItem;
    List<PhotoGroupItem> groupItems;

    public static void openForResult(Context context, String titleName,
                                     List<ImgObj> selImages, int maxCount, boolean isDirectReturn,
                                     int requestCode, boolean showCountFlag) {
        openForResult(context, titleName, selImages, maxCount, isDirectReturn, requestCode, false, showCountFlag);
    }

    public static void openForResult(Context context, String titleName,
                                     List<ImgObj> selImages, int maxCount, boolean isDirectReturn,
                                     int requestCode, boolean takePhoto, boolean showCountFlag) {
        Intent intent = new Intent(context, PhotoSelectionActivity.class);
        intent.putExtra("title_name", titleName);
        intent.putParcelableArrayListExtra("sel_image_list", (ArrayList<? extends Parcelable>) selImages);
        intent.putExtra("max_count", maxCount);
        intent.putExtra("direct_return", isDirectReturn);
        intent.putExtra("take_photo", takePhoto);
        intent.putExtra("show_count_flag", showCountFlag);
        ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_photo_selection);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title_name"));

        header = getLayoutInflater().inflate(R.layout.header_photo_selection, null);

        selImgs = getIntent().getParcelableArrayListExtra("sel_image_list");
        this.isDirectReturn = getIntent().getBooleanExtra("direct_return", false);
        this.takePhoto = getIntent().getBooleanExtra("take_photo", false);
        this.maxCount = getIntent().getIntExtra("max_count", 9);

        if (takePhoto) {
            takePhoto();
        }
        mPhotoGrid = (RecyclerView) findViewById(R.id.gv_photos);
        setSelectCount();
        mPhotoGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (Math.abs(dy) > 5 && fragmentShow) {
                    clickCategory(tvCategory);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        tfProgressDialog = new TFProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_photo_slection, menu);
        okMenuItem = menu.findItem(R.id.finish_title);
        okMenuItem.setTitle("完成(" + selImgs.size() + "/" + maxCount + ")");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(takePhoto || groupItems == null || groupItems.size() <= 0){
            // Load buckets
            MediaStoreBucketsAsyncTask.execute(this, this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_SELECT_CAMERA_REQUEST_CODE:
                if (null != mPhotoFile) {
                    if (resultCode == Activity.RESULT_OK) {
                        ImageUtil.scanMediaJpegFile(this, mPhotoFile, this);
                        mSelectCameraFile = mPhotoFile;
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d("UserPhotosFragment", "Deleting Photo File");
                        }
                        mPhotoFile.delete();
                        if (takePhoto) finish();
                    }
                    mPhotoFile = null;
                }
                break;
            case PHOTO_SELECT_PHOTO_VIEWER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        selImgs = data.getParcelableArrayListExtra("result_select_image_list");
                        mPhotoAdapter.setSelImgs(selImgs);
                        mPhotoAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case PHOTO_SELECT_PHOTO_SCAN_REQUEST_CODE:
                if (data == null) return;
                File scanFile = (File) data.getSerializableExtra("result_scan_file");
                groupItems.get(0).getImgObjList().add(0, ImgObj.getSelection(Uri.fromFile(scanFile)));
                selImgs.add(ImgObj.getSelection(Uri.fromFile(scanFile)));
                mPhotoAdapter.notifyDataSetChanged();
                okMenuItem.setTitle("完成(" + (mPhotoAdapter.getSelImgs().size()) + "/" + maxCount + ")");
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader cursorLoader = null;

        switch (id) {
            case LOADER_USER_PHOTOS_EXTERNAL:
                String selection = null;
                String[] selectionArgs = null;

                if (null != bundle && bundle.containsKey(LOADER_PHOTOS_BUCKETS_PARAM)) {
                    selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
                    selectionArgs = new String[]{bundle.getString(LOADER_PHOTOS_BUCKETS_PARAM)};
                }

                cursorLoader = new PhotoCursorLoader(this,
                        MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI,
                        MediaStoreCursorHelper.PHOTOS_PROJECTION, selection, selectionArgs,
                        MediaStoreCursorHelper.PHOTOS_ORDER_BY, false);
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_USER_PHOTOS_EXTERNAL:
                //选中拍照后的图片
                if (mSelectCameraFile != null) {
                    Uri uri = ImageUtil.getImageContentUri(this, mSelectCameraFile);
                    if (uri != null) {
                        if (maxCount == 1) {
                            selImgs.clear();
                        }
                        selImgs.add(ImgObj.getSelection(uri));
                    }
                    mSelectCameraFile = null;
                }
                setData(data);
                break;
        }
    }

    private void setData(Cursor cursor) {
        tfProgressDialog.show();
        Observable.create(
                new Observable.OnSubscribe<TreeMap<String, List<ImgObj>>>() {
                    @Override
                    public void call(Subscriber<? super TreeMap<String, List<ImgObj>>> subscriber) {
                        subscriber.onNext(MediaStoreCursorHelper.photoCursorSelectionMap(
                                MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI,
                                cursor));
                        subscriber.onCompleted();
                    }
                })
                .compose(SchedulersCompat.applyIoSchedulers())
                .map(
                        stringListHashMap -> {
                            List<PhotoGroupItem> groupItems = new ArrayList<>();
                            Iterator iterator = stringListHashMap.entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry entry = (Map.Entry) iterator.next();
                                PhotoGroupItem item = new PhotoGroupItem();
                                item.setTitle(entry.getKey().toString());
                                item.setImgObjList((List<ImgObj>) entry.getValue());
                                groupItems.add(item);
                            }
                            return groupItems;
                        })
                .subscribe(
                        photoGroupItems -> {
                            this.groupItems = photoGroupItems;
                            mPhotoGrid.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                            mPhotoAdapter = new CircleSelectPhotosAdapter(this, photoGroupItems, maxCount);
                            mPhotoAdapter.addHeader(header);
                            mPhotoAdapter.setSelImgs(selImgs);
                            mPhotoGrid.setAdapter(mPhotoAdapter);
                            mPhotoAdapter.notifyDataSetChanged();
                            tfProgressDialog.dismiss();
                            okMenuItem.setTitle("完成(" + (mPhotoAdapter.getSelImgs().size()) + "/" + maxCount + ")");
                        },
                        throwable -> {
                            tfProgressDialog.dismiss();
                        }
                );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_USER_PHOTOS_EXTERNAL:
                break;
        }
    }

    @Override
    public void onBucketsLoaded(List<MediaStoreBucket> buckets) {
        if (null != buckets && !buckets.isEmpty()) {
            mBuckets.clear();
            mBuckets.addAll(buckets);
            curBucket = getSelectedBucket();
            loadBucketId(curBucket.getId());
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        runOnUiThread(new Runnable() {
            public void run() {
                MediaStoreBucket bucket = getSelectedBucket();
                if (null != bucket) {
                    loadBucketId(bucket.getId());
                }
            }
        });
    }

    private MediaStoreBucket getSelectedBucket() {
        if (null != curBucket) {
            return curBucket;
        }
        return mBuckets != null && mBuckets.size() > 0 ? mBuckets.get(0) : null;
    }

    private void loadBucketId(String id) {
//        if (isAdded()) {
        Bundle bundle = new Bundle();
        if (null != id) {
            bundle.putString(LOADER_PHOTOS_BUCKETS_PARAM, id);
        }
        try {
            getLoaderManager().restartLoader(LOADER_USER_PHOTOS_EXTERNAL, bundle, this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            // Can sometimes catch with: Fragment not attached to Activity.
            // Not much we can do to recover
        }
//        }
    }

    private void takePhoto() {
        if (null == mPhotoFile) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mPhotoFile = StorageUtil.genSystemPhotoFile();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            startActivityForResult(takePictureIntent, PHOTO_SELECT_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setSelectCount() {
    }

    public void clickOK() {
        Intent data = new Intent();
//        data.putParcelableArrayListExtra("result_select_image_list", (ArrayList<? extends Parcelable>) mPhotoAdapter.getSelImgs());
        data.putStringArrayListExtra("result_select_image_list", mPhotoAdapter.getSelImgPaths());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                return true;

            case R.id.finish_title:
                clickOK();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clickAlbumSelect(View view) {
        int index = (int) view.getTag(R.string.tag_index);
        curBucket = mBuckets.get(index);
        if (null != curBucket) {
            loadBucketId(curBucket.getId());
            tvCategory.setText(curBucket.getName());
            fragment.clickAlbumSelect(view);
            if (fragmentShow) clickCategory(tvCategory);
        }
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
                fragment = PhotoCategoryFragment.newInstance(mBuckets);
                transaction.add(R.id.fl_container, fragment);
            } else {
                transaction.show(fragment);
            }
            fragmentShow = true;
        }
        transaction.commit();
    }

    /**
     * click 相机
     *
     * @param view
     */
    public void clickCamera(View view) {
        takePhoto();
    }

    /**
     * click 扫描仪
     *
     * @param view
     */
//    public void clickScan(View view) {
//        ScanActivity.openForResult(this, PHOTO_SELECT_PHOTO_SCAN_REQUEST_CODE);
//    }

    /**
     * 大图预览
     */
    public void clickPhotoView(View view) {
        ImgObj imgObj = (ImgObj) view.getTag(R.string.tag_obj);

        List<ImgObj> imgObjs = new ArrayList<>();
        for (PhotoGroupItem groupItem : groupItems) {
            imgObjs.addAll(groupItem.getImgObjList());
        }

        PhotoDataResult photoDataResult = PhotoDataResult.getInstance();
        photoDataResult.setSelImgObjs(selImgs);
        photoDataResult.setImgObjs(imgObjs);
        PhotoViewerNewActivity.openForResult(this, imgObjs.indexOf(imgObj), maxCount, PHOTO_SELECT_PHOTO_VIEWER_REQUEST_CODE);
    }

    @Subscribe
    public void onEvent(PhotoSelectionAddedEvent event) {
        setSelectCount();
    }

    @Subscribe
    public void onEvent(PhotoSelectionRemovedEvent event) {
        setSelectCount();
    }

    @Subscribe
    public void onEvent(PhotoSelectEvent event) {
        okMenuItem.setTitle("完成(" + (mPhotoAdapter.getSelImgs().size()) + "/" + maxCount + ")");
    }

    public void close() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        close();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Bundle bundle = new Bundle();
        bundle.putString("mPhotoFile_path", mPhotoFile == null ? "" : mPhotoFile.getAbsolutePath());
        bundle.putString("mSelectCameraFile_path", mSelectCameraFile == null ? "" : mSelectCameraFile.getAbsolutePath());
        outState.putBundle("bundle", bundle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle b = savedInstanceState.getBundle("bundle");
        mPhotoFile = TextUtils.isEmpty(b.getString("mPhotoFile_path")) ? null : new File(b.getString("mPhotoFile_path"));
        mSelectCameraFile = TextUtils.isEmpty(b.getString("mSelectCameraFile_path")) ? null : new File(b.getString("mSelectCameraFile_path"));
    }

    @Override
    public void onEvent(Object event) {

    }
}
