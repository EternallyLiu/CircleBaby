package cn.timeface.open.utils.glide;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;

/**
 * author: shiyan  Created on 8/12/16.
 * email : sy0725work@gmail.com
 */
public class TFOBgUrlLoader extends BaseGlideUrlLoader<TFOBookContentModel> {
    public TFOBgUrlLoader(Context context) {
        super(context);
    }

    @Override
    protected String getUrl(TFOBookContentModel model, int width, int height) {
        if (TextUtils.isEmpty(model.getPageImage())) {
            return null;
        }

        Log.i("TFOBgUrlLoader", "getUrl: " + model.getPageImage() + "@" + width + "w.webp");
        return model.getPageImage() + "@" + width + "w.webp";
    }
}