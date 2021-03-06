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
import com.wechat.photopicker.endity.Photo;
import com.wechat.photopicker.endity.PhotoDirectory;
import com.wechat.photopicker.event.OnItemCheckListener;
import com.wechat.photopicker.event.OnPhotoClickListener;
import com.wechat.photopicker.event.Selectable;
import com.wechat.photopicker.event.SingeSelectable;
import com.wechat.photopicker.utils.MediaStoreHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;

/**
 * 选择图片adapter
 */
public class PhotoSelectorAdapter extends RecyclerView.Adapter<PhotoSelectorAdapter.ViewHolder> implements Selectable, SingeSelectable {
    public static final String TAG = "PhotoSelectorAdapter";
    private List<Photo> mPhotos;
    private List<PhotoDirectory> mDirectoryList;
    private Context mContext;
    private PhotoDirectory mDirectory;
    private List<Photo> mSelectorPhotos = new ArrayList<>();//选中图片集
    private List<String> mCurrentDirPhotoPaths = new ArrayList<>();
    private int mSelectorDirIndex;//选中文件夹索引
    private boolean hasCamera = true;
    private int mOptionalPhotoSize;
    public static final int ITEM_TYPE_PHOTO = 0;
    public static final int ITEM_TYPE_CAMERA = 1;

    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;
    private ArrayList<String> paths = new ArrayList<>(PickerPhotoActivity.MAX_SELECTOR_SIZE);

    public PhotoSelectorAdapter(List<PhotoDirectory> directoryList, Context context, int optionalPhotoSize) {
        mDirectoryList = directoryList;
        mContext = context;
        mOptionalPhotoSize = optionalPhotoSize;
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

        if (isShowCamera() == true && position == 0) {
            itemType = ITEM_TYPE_CAMERA;
        } else {
            itemType = ITEM_TYPE_PHOTO;
        }
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
        mDirectory = mDirectoryList.get(mSelectorDirIndex);
        mPhotos = mDirectory.getPhotos();
        Log.v("PhotoSelectorAdapter", "position ========= " + position);
        Log.v("PhotoSelectorAdapter", "mSelectorDirIndex ========= " + mSelectorDirIndex);
        Log.v("PhotoSelectorAdapter", "mPhotos ========= " + mPhotos.size());
        final Photo photo;

        int itemType = getItemViewType(position);

        if (itemType == ITEM_TYPE_PHOTO) {
            if (mSelectorDirIndex == MediaStoreHelper.INDEX_ALL_PHOTOS) {
                photo = mPhotos.get(position - 1);
            } else {
                photo = mPhotos.get(position);
            }
            final boolean isSelector = isSelected(photo);
            holder.mImageView.setSelected(isSelector);
            holder.mSelector.setSelected(isSelector);
            /**
             * load（new File(photo.getPath())）比load(photo.getPath())所用内存更低，
             * 加载速度更快
             */
            Glide.with(mContext)
                    .load(new File(photo.getPath()))
                    .centerCrop()
                    .thumbnail(0.1f)
                    .placeholder(R.mipmap.ic_photo_black_48dp)
                    .error(R.mipmap.ic_broken_image_black_48dp)
                    .into(holder.mImageView);
            Log.d(TAG, photo.getPath() + "----- position = " + position);
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
                        mSelectorPhotos.remove(photo);
                        notifyItemChanged(position);
                    } else if (selectedPhotosSize != mOptionalPhotoSize) {
                        Log.d(TAG, "add photo");
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
                        mSelectorPhotos.remove(photo);
                        notifyItemChanged(position);
                    } else if (selectedPhotosSize != mOptionalPhotoSize) {
                        Log.d(TAG, "add photo");
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
        if(mDirectoryList.size() == 0){
            PhotoDirectory photoDirectory = new PhotoDirectory();
            photoDirectory.setName("全部图片");
            mDirectoryList.add(photoDirectory);
        }
        count = mDirectoryList.size() == 0 ? 0 : mDirectoryList.get(mSelectorDirIndex).getPhotos().size();
        if (isShowCamera()) {
            count++;
        }
        return count;
    }

    @Override
    public boolean isSelected(Photo photo) {
        return mSelectorPhotos.contains(photo);
    }

    @Override
    public void toggleSelection(Photo photo) {
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
    public List<Photo> getSelectedPhotos() {
        return mSelectorPhotos;
    }

    public ArrayList<String> getSelectedPhotoPaths() {
        paths.clear();
        if (mSelectorPhotos.size() > 0 && mSelectorPhotos != null) {
            for (int i = 0; i < mSelectorPhotos.size(); i++) {
                paths.add(mSelectorPhotos.get(i).getPath());
            }
        }
        return paths;
    }

    public boolean isShowCamera() {
        return (hasCamera && mSelectorDirIndex == MediaStoreHelper.INDEX_ALL_PHOTOS) ? true : false;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
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
