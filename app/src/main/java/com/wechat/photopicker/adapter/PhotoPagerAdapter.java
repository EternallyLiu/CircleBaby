package com.wechat.photopicker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.views.widget.photoview.PhotoView;


/**
 * 显示大图的Viewpager适配器
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mPaths;

    public PhotoPagerAdapter(Context context, List<String> paths) {
        mContext = context;
        mPaths = paths;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.item_big_image, container, false);
        PhotoView imageView = (PhotoView) view.findViewById(R.id.iv_big_image);
        String path = mPaths.get(position);
        if (path.startsWith("http") || path.startsWith("www")) {
            /*if(path.endsWith("@.jpg")){
                path = path.replace("@.jpg","@600w_600h_1l_1o");
            }else{
                path = path + "@600w_600h_1l_1o";
            }*/
            Glide.with(mContext)
                    .load(path)
                    .thumbnail(0.1f)
                    .error(R.mipmap.ic_broken_image_black_48dp)
                    .fitCenter()
                    .into(imageView);
            container.addView(view);
        } else {
            Glide.with(mContext)
                    .load(new File(path))
                    .thumbnail(0.1f)
                    .error(R.mipmap.ic_broken_image_black_48dp)
                    .fitCenter()
                    .into(imageView);
            container.addView(view);
        }

//        imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(View view, float x, float y) {
//                finish();
//            }
//        });
//        imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
//            @Override
//            public void onViewTap(View view, float x, float y) {
//                finish();
//            }
//        });
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
