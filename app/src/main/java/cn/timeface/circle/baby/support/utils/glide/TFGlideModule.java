package cn.timeface.circle.baby.support.utils.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.InputStream;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.glide.transformations.TFStringUrlLoader;


/**
 * author: rayboot  Created on 15/9/16.
 * email : sy0725work@gmail.com
 */
public class TFGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context));
        ViewTarget.setTagId(R.id.glide_tag_id);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(String.class, InputStream.class, new TFStringUrlLoader.Factory());
    }
}
