package com.wechat.photopicker.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wechat.photopicker.adapter.PhotoSelectorAdapter2;
import com.wechat.photopicker.event.OnPhotoClickListener;
import com.wechat.photopicker.utils.ImageCaptureManager;
import com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;

import static com.wechat.photopicker.PickerPhotoActivity2.MAX_SELECTOR_SIZE;

/**
 * 选择图片Fragment
 */
public class PickerPhotoFragment2 extends Fragment implements View.OnClickListener{
    public static final String TAG = "PickerPhotoFragment2";
    private ImageCaptureManager mImageCaptureManager;
    private PhotoSelectorAdapter2 mPhotoSelectorAdapter;
    private RecyclerView mRecyclerView;

    private Button btPreview;
    private View rootView;
    private int mOptionalPhotoSize;
    private List<MediaObj> mediaobjs;
    private List<ImageInfoListObj> dataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        dataList = intent.getParcelableArrayListExtra("dataList");
        mOptionalPhotoSize = MAX_SELECTOR_SIZE;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        rootView = inflater.inflate(R.layout.fragment_picker_photo2,container,false);

        btPreview = (Button) rootView.findViewById(R.id.bt_preview);
        btPreview.setOnClickListener(this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
        mPhotoSelectorAdapter = new PhotoSelectorAdapter2(dataList,getActivity(),mOptionalPhotoSize);
        //初始显示第一个文件夹内的图片（全图图片）
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mPhotoSelectorAdapter);


        /**
         * PhotoAdapter点击图片跳转到BigImageActivity显示大图
         */
        mPhotoSelectorAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View view, int position, boolean showCamera) {
                BigImageShowIntent bigImageShowIntent = new BigImageShowIntent(getActivity());
                bigImageShowIntent.setPhotoPaths(mPhotoSelectorAdapter.getCurrentDirPhotoPaths());
                bigImageShowIntent.setSelectorPosition(showCamera ? position -1 :position);
                startActivity(bigImageShowIntent);
                Log.d(TAG,mPhotoSelectorAdapter.getCurrentDirPhotoPaths().size()+"");
            }
        });
        return rootView;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
//            mImageCaptureManager.galleryAddPic();
//            if (mPhotoDirectories.size() > 0) {
//                String path = mImageCaptureManager.getCurrentPhotoPath();
//                PhotoDirectory directory = mPhotoDirectories.get(INDEX_ALL_PHOTOS);
//                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
//                directory.setCoverPath(path);
//                mPhotoSelectorAdapter.notifyDataSetChanged();
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_preview) {
            BigImageShowIntent bigImageShowIntent = new BigImageShowIntent(getActivity());
            bigImageShowIntent.setPhotoPaths(mPhotoSelectorAdapter.getSelectedPhotoPaths());
            startActivity(bigImageShowIntent);

        }
    }
    public PhotoSelectorAdapter2 getPhotoSelectorAdapter(){
        return mPhotoSelectorAdapter;
    }
}
