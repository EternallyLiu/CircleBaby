package cn.timeface.circle.baby.support.utils.glide.transformations;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

import cn.timeface.circle.baby.BuildConfig;

/**
 * Created by ray on 2016/10/18.
 */

public class TFStringUrlLoader extends BaseGlideUrlLoader<String> {
    public TFStringUrlLoader(Context context) {
        super(context);
    }

    public TFStringUrlLoader(ModelLoader<GlideUrl, InputStream> urlLoader) {
        super(urlLoader, null);
    }

    @Override
    protected String getUrl(String model, int width, int height) {
        String exConfig = "";
        String imageUrl = model;

        if (!imageUrl.contains("http")
                || imageUrl.contains("http://www.")
                //|| imageUrl.contains(TFApiServiceV2.IMAGE_BASE_URL)
//                || imageUrl.contains(TFApiServiceV2.OUR_SERVER_IMAGE_BASE_URL2)
                || imageUrl.contains("qzapp.qlogo.cn")) {
            return imageUrl;
        } else if (imageUrl.contains("http") && imageUrl.contains("@ex")) {
            exConfig = "_" + imageUrl.substring(imageUrl.lastIndexOf("@ex") + 3);
        }

        if (imageUrl.contains("@")) {
            imageUrl = imageUrl.substring(0, imageUrl.lastIndexOf("@"));
        }
        String ex = "@" + width * 2 + "w_1e_1l_2o" + exConfig + ".webp";
        if (BuildConfig.DEBUG) {
            Log.i("Glide", "getUrl " + imageUrl + ex);
        }
        return imageUrl + ex;
    }


    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        @Override
        public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new TFStringUrlLoader(factories.buildModelLoader(GlideUrl.class, InputStream.class));
        }

        @Override
        public void teardown() {
        }
    }
}
