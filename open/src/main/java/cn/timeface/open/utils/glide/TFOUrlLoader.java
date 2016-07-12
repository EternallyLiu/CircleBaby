package cn.timeface.open.utils.glide;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import cn.timeface.open.api.models.objs.TFOBookElementModel;

/**
 * author: rayboot  Created on 16/7/11.
 * email : sy0725work@gmail.com
 */
public class TFOUrlLoader extends BaseGlideUrlLoader<TFOBookElementModel> {
    public TFOUrlLoader(Context context) {
        super(context);
    }

    @Override
    protected String getUrl(TFOBookElementModel model, int width, int height) {
        if (model.getImageContentExpand() == null || TextUtils.isEmpty(model.getImageContentExpand().getImageUrl())) {
            return null;
        }
        String imgUrl = model.getImageContentExpand().getImageUrl();
        Rect rect = model.getImageContentExpand().getOrgCropRect(model.getContentWidth(), model.getContentHeight());

        imgUrl += "@" + rect.left + "-" + rect.top + "-" + rect.width() + "-" + rect.height() + "a.webp";

        Log.v("image url", imgUrl);
        return imgUrl;
    }
}
