package cn.timeface.circle.baby.ui.circle.groupmembers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wechat.photopicker.event.OnPhotoClickListener;

import java.util.ArrayList;

import cn.timeface.circle.baby.R;

/**
 * Created by wangwei on 2017/3/22.
 */

public class PhotosShowAdapter extends RecyclerView.Adapter<PhotosShowAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mPathList;
    private int count;

    private OnPhotoClickListener mOnPhotoClickListener = null;
    public PhotosShowAdapter(Context context, ArrayList<String> mList){
        mContext = context;
        mPathList = mList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_show,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String path =mPathList.get(position);
        Glide.with(mContext)
                .load(path)
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.mipmap.ic_photo_black_48dp)
                .error(R.mipmap.ic_broken_image_black_48dp)
                .into(holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPhotoClickListener.onClick(v,position,false);
            }
        });
    }
    public void addAllDate(ArrayList<String> mPathList){
        this.mPathList.addAll(mPathList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        count = mPathList.size();
        return count == 0?0:count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View view){
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.iv_photo);
        }
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener){
        mOnPhotoClickListener = onPhotoClickListener;
    }
}
