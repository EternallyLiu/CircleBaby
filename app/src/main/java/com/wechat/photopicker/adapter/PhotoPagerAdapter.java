package com.wechat.photopicker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 显示大图的Viewpager适配器
 */
public class PhotoPagerAdapter extends PagerAdapter implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mPaths;
    private PhotoView imgView;

    public PhotoPagerAdapter(Context context, List<String> paths) {
        mContext = context;
        mPaths = paths;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_big_image, container, false);
        imgView = ButterKnife.findById(view, R.id.iv_big_image);
        imgView.setOnClickListener(this);
        GlideUtil.displayImage(mPaths.get(position), imgView, true);
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imgView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (getClickListener() != null)
                    getClickListener().imageClcik();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private ImageClickListener clickListener;

    public ImageClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ImageClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ImageClickListener {
        public void imageClcik();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_big_image:
                if (getClickListener() != null)
                    getClickListener().imageClcik();
                break;
        }

    }
}
