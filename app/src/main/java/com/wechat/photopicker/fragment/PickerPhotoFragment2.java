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
import android.widget.TextView;

import com.wechat.photopicker.adapter.PhotoSelectorAdapter2;
import com.wechat.photopicker.event.OnPhotoClickListener;
import com.wechat.photopicker.utils.ImageCaptureManager;
import com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CardPublishActivity;
import cn.timeface.circle.baby.activities.DiaryPublishActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.NewPickerAdapterEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

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
    private List<MediaObj> mediaobjs;
    private List<ImageInfoListObj> dataList;
    private TextView tvCreat;
    private int bookType;
    private int bookPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        dataList = intent.getParcelableArrayListExtra("dataList");
        bookType = intent.getIntExtra("bookType", 0);
        bookPage = intent.getIntExtra("bookPage", MAX_SELECTOR_SIZE);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        rootView = inflater.inflate(R.layout.fragment_picker_photo2,container,false);

        btPreview = (Button) rootView.findViewById(R.id.bt_preview);
        tvCreat = (TextView) rootView.findViewById(R.id.tv_creat);
        tvCreat.setOnClickListener(this);
        btPreview.setOnClickListener(this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
        mPhotoSelectorAdapter = new PhotoSelectorAdapter2(dataList,getActivity(),bookPage);
        //初始显示第一个文件夹内的图片（全图图片）
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mPhotoSelectorAdapter);


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

        }else if(i == R.id.tv_creat){
            if(bookType==2){
                //日记卡片
                DiaryPublishActivity.open(getContext());
//                PublishActivity.open(getContext(), PublishActivity.DIALY);
            }else if(bookType == 3){
                //识图卡片
                CardPublishActivity.open(getContext());
//                PublishActivity.open(getContext(), PublishActivity.CARD);
            }
        }
    }
    public PhotoSelectorAdapter2 getPhotoSelectorAdapter(){
        return mPhotoSelectorAdapter;
    }

    public void reqData() {
        System.out.println("PickerPhotoFragment2============HomeRefreshEvent");
        ApiFactory.getApi().getApiService().queryImageInfoList("", bookType)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        List<ImageInfoListObj> dataList = response.getDataList();
                        List<ImageInfoListObj> dataList1 = mPhotoSelectorAdapter.getDataList();
                        dataList1.add(0,dataList.get(0));
                        mPhotoSelectorAdapter = new PhotoSelectorAdapter2(dataList1,getActivity(),bookPage);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
                        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(mPhotoSelectorAdapter);
                        EventBus.getDefault().post(new NewPickerAdapterEvent(mPhotoSelectorAdapter));
                    }
                }, error -> {
                    Log.e(TAG, "queryImageInfoList:");
                });
    }
}
