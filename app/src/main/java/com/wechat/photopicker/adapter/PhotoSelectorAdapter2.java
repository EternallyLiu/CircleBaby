package com.wechat.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wechat.photopicker.PickerPhotoActivity;
import com.wechat.photopicker.endity.PhotoDirectory;
import com.wechat.photopicker.event.OnItemCheckListener2;
import com.wechat.photopicker.event.OnPhotoClickListener;
import com.wechat.photopicker.event.Selectable2;
import com.wechat.photopicker.event.SingeSelectable;
import com.wechat.photopicker.utils.MediaStoreHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.ImageInfoListObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;

/**
 * 选择图片adapter
 */
public class PhotoSelectorAdapter2 extends RecyclerView.Adapter<PhotoSelectorAdapter2.ViewHolder> implements Selectable2, SingeSelectable {
    public static final String TAG = "PhotoSelectorAdapter2";
    private List<MediaObj> mPhotos;
    private List<ImageInfoListObj> dataList;
    private List<Integer> timeIds = new ArrayList<>();
    private Context mContext;
    private PhotoDirectory mDirectory;
    private List<MediaObj> mSelectorPhotos = new ArrayList<>();//选中图片集
    private List<String> mCurrentDirPhotoPaths = new ArrayList<>();
    private int mSelectorDirIndex;//选中文件夹索引
    private boolean hasCamera = true;
    private int mOptionalPhotoSize;
    public static final int ITEM_TYPE_PHOTO = 0;
    public static final int ITEM_TYPE_CAMERA = 1;

    private OnItemCheckListener2 onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;
    private ArrayList<String> paths = new ArrayList<>(PickerPhotoActivity.MAX_SELECTOR_SIZE);

    public PhotoSelectorAdapter2(List<ImageInfoListObj> dataList, Context context, int optionalPhotoSize) {
        this.dataList = dataList;

        mContext = context;
        mOptionalPhotoSize = optionalPhotoSize;
        mPhotos = new ArrayList<>();
        for (ImageInfoListObj obj : dataList) {
            mPhotos.addAll(obj.getMediaList());
            for (MediaObj media : obj.getMediaList()){
                if(media.getSelected() == 1){
                    mSelectorPhotos.add(media);
                }
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public View mSelector;
        private View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
            mSelector = itemView.findViewById(R.id.iv_photo_selector);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    @Override
    public void setSelectableIndex(int index) {
        mSelectorDirIndex = index;
    }


    /**
     * 返回itemType，position==0的时候返回1（ITEM_TYPE_CAMERA），其他返回0（ITEM_TYPE_PHOTO）；
     * 不采用三元运算符的原因：方便以后扩展功能时，减少所要写的代码量
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int itemType;

//        if ( isShowCamera() == true && position == 0 ){
//            itemType = ITEM_TYPE_CAMERA;
//        } else {
        itemType = ITEM_TYPE_PHOTO;
//        }
        return itemType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MediaObj photo;

        int itemType = getItemViewType(position);

        if (itemType == ITEM_TYPE_PHOTO) {
            photo = mPhotos.get(position);
            final boolean isSelector = isSelected(photo);
            holder.mImageView.setSelected(isSelector);
            holder.mSelector.setSelected(isSelector);
            /**
             * load（new File(photo.getPath())）比load(photo.getPath())所用内存更低，
             * 加载速度更快
             */
            Glide.with(mContext)
                    .load(photo.getImgUrl())
                    .centerCrop()
                    .thumbnail(0.1f)
                    .placeholder(R.mipmap.ic_photo_black_48dp)
                    .error(R.mipmap.ic_broken_image_black_48dp)
                    .into(holder.mImageView);
            Log.d(TAG, photo.getImgUrl() + "----- position = " + position);
//            holder.mImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onPhotoClickListener.onClick(v,position,isShowCamera());
//                }
//            });

            holder.mSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "------CLICK SELECTOR");
                    int selectedPhotosSize = mSelectorPhotos.size();
                    if (mSelectorPhotos.contains(photo)) {
                        Log.d(TAG, "remove photo");
                        photo.setSelected(0);
                        timeIds.remove(Integer.valueOf(photo.getTimeId()));
                        mPhotos.set(position, photo);
                        mSelectorPhotos.remove(photo);
                        notifyItemChanged(position);
                    } else if (selectedPhotosSize != mOptionalPhotoSize) {
                        Log.d(TAG, "add photo");
                        photo.setSelected(1);
                        timeIds.add(photo.getTimeId());
                        mPhotos.set(position, photo);
                        mSelectorPhotos.add(photo);
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(mContext, "你最多只能选择" + mOptionalPhotoSize + "张照片", Toast.LENGTH_SHORT).show();
                    }
                    if (onItemCheckListener != null) {
                        onItemCheckListener.OnItemCheck(position, photo, isSelector, mSelectorPhotos.size());
                    }
                }
            });

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "------CLICK SELECTOR");
                    int selectedPhotosSize = mSelectorPhotos.size();
                    if (mSelectorPhotos.contains(photo)) {
                        Log.d(TAG, "remove photo");
                        photo.setSelected(0);
                        timeIds.remove(Integer.valueOf(photo.getTimeId()));
                        mPhotos.set(position, photo);
                        mSelectorPhotos.remove(photo);
                        notifyItemChanged(position);
                    } else if (selectedPhotosSize != mOptionalPhotoSize) {
                        Log.d(TAG, "add photo");
                        photo.setSelected(1);
                        timeIds.add(photo.getTimeId());
                        mPhotos.set(position, photo);
                        mSelectorPhotos.add(photo);
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(mContext, "你最多只能选择" + mOptionalPhotoSize + "张照片", Toast.LENGTH_SHORT).show();
                    }
                    if (onItemCheckListener != null) {
                        onItemCheckListener.OnItemCheck(position, photo, isSelector, mSelectorPhotos.size());
                    }

                }
            });
        } else if (itemType == ITEM_TYPE_CAMERA) {
            holder.mImageView.setImageResource(R.drawable.camera);
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER);
            holder.mSelector.setVisibility(View.GONE);
            if (isShowCamera()) {
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCameraClickListener != null) {
                            Log.d(TAG, "------CLICK CAMERA------");
                            onCameraClickListener.onClick(v);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        int count;
        count = mPhotos.size();
        return count;
    }

    @Override
    public boolean isSelected(MediaObj photo) {
        return mSelectorPhotos.contains(photo);
    }

    @Override
    public void toggleSelection(MediaObj photo) {
        if (mSelectorPhotos.contains(photo)) {
            mSelectorPhotos.remove(photo);
        } else {
            mSelectorPhotos.add(photo);
        }
    }

    @Override
    public void clearSelection() {
        mSelectorPhotos.clear();
    }

    @Override
    public int getSelectedItemCount() {
        return mSelectorPhotos.size();
    }

    @Override
    public List<MediaObj> getSelectedPhotos() {
        return mSelectorPhotos;
    }

    public List<Integer> getTimeIds(){
        return timeIds;
    }

    public List<ImageInfoListObj> getDataList() {
        return dataList;
    }

    public List<MediaObj> getPhotos() {
        return mPhotos;
    }

    public ArrayList<String> getSelectedPhotoPaths() {
        paths.clear();
        if (mSelectorPhotos.size() > 0 && mSelectorPhotos != null) {
            for (int i = 0; i < mSelectorPhotos.size(); i++) {
                paths.add(mSelectorPhotos.get(i).getImgUrl());
            }
        }
        return paths;
    }

    public boolean isShowCamera() {
        return (hasCamera && mSelectorDirIndex == MediaStoreHelper.INDEX_ALL_PHOTOS) ? true : false;
    }

    public void setOnItemCheckListener(OnItemCheckListener2 onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    public ArrayList<String> getCurrentDirPhotoPaths() {
        return mDirectory.getPhotoPaths();
    }
}
