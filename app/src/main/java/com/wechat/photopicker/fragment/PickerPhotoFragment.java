package com.wechat.photopicker.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.wechat.photopicker.MainActivity;
import com.wechat.photopicker.adapter.DirectoryListAdapter;
import com.wechat.photopicker.adapter.PhotoSelectorAdapter;
import com.wechat.photopicker.endity.Photo;
import com.wechat.photopicker.endity.PhotoDirectory;
import com.wechat.photopicker.utils.ImageCaptureManager;
import com.wechat.photopicker.utils.MediaStoreHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;

import static android.app.Activity.RESULT_OK;
import static com.wechat.photopicker.PickerPhotoActivity.MAX_SELECTOR_SIZE;
import static com.wechat.photopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * 选择图片Fragment
 */
public class PickerPhotoFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "PickerPhotoFragment";
    private ImageCaptureManager mImageCaptureManager;
    private PhotoSelectorAdapter mPhotoSelectorAdapter;
    private RecyclerView mRecyclerView;
    private MediaStoreHelper mMediaStoreHelper;
    private static final int CAMERA_REQUEST_CODE = 51;

    private DirectoryListAdapter mDirectoryListAdapter;
    private List<PhotoDirectory> mPhotoDirectories;
    private ListPopupWindow mListPopupWindow;
    private Button btSwitchPopupWindow;
    private Button btPreview;
    private View rootView;
    private ListView mDirListView;//popupWindow里的listView
    private int scrollPos = 0;
    private int scrollTop = 0;
    private int mOptionalPhotoSize;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mOptionalPhotoSize = MAX_SELECTOR_SIZE - intent.getIntExtra(MainActivity.KEY_SELECTED_PHOTO_SIZE, 0);
        mPhotoDirectories = new ArrayList<>();
        mImageCaptureManager = new ImageCaptureManager(getActivity());
        //初始化图片数据
        mMediaStoreHelper.getPhotoDirs(getActivity(), savedInstanceState, new MediaStoreHelper.DirectoryResultCallback() {
            @Override
            public void onResultCallback(List<PhotoDirectory> directories) {
                mPhotoDirectories.clear();
                mPhotoDirectories.addAll(directories);
                mPhotoSelectorAdapter.notifyDataSetChanged();
                mDirectoryListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        rootView = inflater.inflate(R.layout.fragment_picker_photo, container, false);

        btSwitchPopupWindow = (Button) rootView.findViewById(R.id.bt_directory);
        btSwitchPopupWindow.setOnClickListener(this);
        btPreview = (Button) rootView.findViewById(R.id.bt_preview);
        btPreview.setOnClickListener(this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
        mDirectoryListAdapter = new DirectoryListAdapter(mPhotoDirectories, getActivity());
        mPhotoSelectorAdapter = new PhotoSelectorAdapter(mPhotoDirectories, getActivity(), mOptionalPhotoSize);
        //初始显示第一个文件夹内的图片（全图图片）
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mPhotoSelectorAdapter);

        mListPopupWindow = new ListPopupWindow(getActivity());
        mListPopupWindow.setAnchorView(btSwitchPopupWindow);
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setModal(true);
        mListPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        mListPopupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
        mListPopupWindow.setAdapter(mDirectoryListAdapter);

        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //通过被选中item的position，控制显示内容
                mListPopupWindow.dismiss();
                mPhotoSelectorAdapter.setSelectableIndex(position);
                mDirectoryListAdapter.setSelectableIndex(position);
                mPhotoSelectorAdapter.notifyDataSetChanged();
                mDirectoryListAdapter.notifyDataSetChanged();
                btSwitchPopupWindow.setText(mPhotoDirectories.get(position).getName());
            }
        });
        /**
         * PhotoAdaptet里控件的点击回调函数
         */
        mPhotoSelectorAdapter.setOnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "------CLICK CAMERA------");
                try {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST_CODE);
                    }else{
                        Intent intent = mImageCaptureManager.dispatchTakePictureIntent();
                        startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * PhotoAdapter点击图片跳转到BigImageActivity显示大图
         */
//        mPhotoSelectorAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
//            @Override
//            public void onClick(View view, int position, boolean showCamera) {
//                BigImageShowIntent bigImageShowIntent = new BigImageShowIntent(getActivity());
//                bigImageShowIntent.setPhotoPaths(mPhotoSelectorAdapter.getCurrentDirPhotoPaths());
//                bigImageShowIntent.setSelectorPosition(showCamera ? position -1 :position);
//                startActivity(bigImageShowIntent);
//                Log.d(TAG,mPhotoSelectorAdapter.getCurrentDirPhotoPaths().size()+"");
//            }
//        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mImageCaptureManager.galleryAddPic();
            if (mPhotoDirectories.size() > 0) {
                String path = mImageCaptureManager.getCurrentPhotoPath();
                PhotoDirectory directory = mPhotoDirectories.get(INDEX_ALL_PHOTOS);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
                directory.setCoverPath(path);
                mPhotoSelectorAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_directory) {
            if (mListPopupWindow.isShowing()) {
                mListPopupWindow.dismiss();
            } else if (mPhotoDirectories.size() > 1) {
                if (!getActivity().isFinishing()) {
                    mListPopupWindow.setHeight(Math.round(rootView.getHeight() * 0.8f));
                    mListPopupWindow.show();
                    if (mListPopupWindow.isShowing()) {
                        mDirListView = mListPopupWindow.getListView();
                        mDirListView.setSelectionFromTop(scrollPos, scrollTop);
                    }
                    mDirListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                                // scrollPos记录当前可见的List顶端的一行的位置
                                scrollPos = mDirListView.getFirstVisiblePosition();
                            }
                            View v = mDirListView.getChildAt(0);
                            scrollTop = (v == null) ? 0 : v.getTop();
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                        }
                    });
                }
            }

        } else if (i == R.id.bt_preview) {
//            BigImageShowIntent bigImageShowIntent = new BigImageShowIntent(getActivity());
//            bigImageShowIntent.setPhotoPaths(mPhotoSelectorAdapter.getSelectedPhotoPaths());
//            startActivity(bigImageShowIntent);
            FragmentBridgeActivity.openBigimageFragment(v.getContext(), mPhotoSelectorAdapter.getSelectedPhotoPaths(), 0, false, false);
        }
    }

    public PhotoSelectorAdapter getPhotoSelectorAdapter() {
        return mPhotoSelectorAdapter;
    }

}
