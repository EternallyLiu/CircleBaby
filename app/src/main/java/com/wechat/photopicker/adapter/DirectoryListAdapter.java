package com.wechat.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wechat.photopicker.endity.PhotoDirectory;
import com.wechat.photopicker.event.SingeSelectable;

import java.io.File;
import java.util.List;

import cn.timeface.circle.baby.R;

/**
 * 类描述：图片文件夹ListViewAdapter
 */
public class DirectoryListAdapter extends BaseAdapter implements SingeSelectable {
    public static final String TAG = "DirectoryListAdapter";
    private List<PhotoDirectory> mPhotoDirectories;
    private Context mContext;
    private LayoutInflater mInflater;
    private int selectableIndex = 0;
    public DirectoryListAdapter(List<PhotoDirectory> photoDirectories,Context context){
        mPhotoDirectories = photoDirectories;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    public int getSelectableIndex(){
        return selectableIndex;
    }
    @Override
    public int getCount() {
        int size = mPhotoDirectories.size();
        return size == 0 ? 0 : size;
    }

    @Override
    public Object getItem(int position) {
        return mPhotoDirectories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setSelectableIndex(int index) {
        selectableIndex = index;
    }

    private class ViewHolder {
        ImageView cover;
        TextView name;
        TextView count;
        ImageView dirSelector;
        public ViewHolder(View rootView){
            cover = (ImageView) rootView.findViewById(R.id.iv_dir_item_image);
            name = (TextView) rootView.findViewById(R.id.tv_dir_item_name);
            count = (TextView) rootView.findViewById(R.id.tv_dir_item_count);
            dirSelector = (ImageView) rootView.findViewById(R.id.iv_dir_selector);
        }
        public void bindData(PhotoDirectory photoDirectory,int position){
            if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
                return;
            }
            Glide.with(mContext)
                 .load(new File(photoDirectory.getCoverPath()))
                 .dontAnimate()
                 .thumbnail(0.1f)
                 .into(cover);
            name.setText(photoDirectory.getName());
            count.setText(mContext.getString(R.string.photo_count, photoDirectory.getPhotos().size()));

            if (position == selectableIndex ){
                Log.d(TAG,"selectableIndex" + selectableIndex);
                dirSelector.setSelected(true);
            } else {
                dirSelector.setSelected(false);
            }

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_directory,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bindData(mPhotoDirectories.get(position),position);
        return convertView;
    }
}
