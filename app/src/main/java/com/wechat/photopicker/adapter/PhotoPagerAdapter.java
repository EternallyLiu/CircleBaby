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
import uk.co.senab.photoview.PhotoView;


/**
 * 显示大图的Viewpager适配器
 */
public class PhotoPagerAdapter extends PagerAdapter {

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
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_big_image, null);
        imgView = ButterKnife.findById(view, R.id.iv_big_image);
        Glide.with(mContext)
                .load(mPaths.get(position))
                .thumbnail(0.1f)
                .fitCenter()
                .into(imgView);
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
}
