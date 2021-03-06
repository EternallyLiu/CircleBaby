package cn.timeface.circle.baby.support.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.glide.transformations.TFStringUrlLoader;
import cn.timeface.circle.baby.ui.babyInfo.views.CircleTransform;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import rx.Observable;


/**
 * Created by JieGuo on 2/26/16.
 */
public class GlideUtil {

    private static Context context;

    GlideUtil() {

    }

    public static void init(Context context) {
        GlideUtil.context = context;
    }

    public static void displayImage(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url) || imageView == null) {
            imageView.setImageResource(R.drawable.ic_launcher);
            return;
        }
        if (url.startsWith("http") || url.startsWith("www")) {
            Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).asBitmap().error(R.drawable.bg_default_holder_img).placeholder(R.drawable.bg_default_holder_img).into(imageView);
        } else
            Glide.with(imageView.getContext()).load(url).asBitmap().error(R.drawable.ic_launcher).placeholder(R.drawable.ic_launcher).into(imageView);
    }

    public static void displayImageCrossfade(String url, ImageView imageView) {
        if (url.startsWith("http"))
            Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).crossFade(300).into(imageView);
        else Glide.with(imageView.getContext()).load(url).crossFade(300).into(imageView);
    }

    public static void displayImageCircle(String url, ImageView imageView) {
        displayImageCircle(url, R.drawable.ic_launcher, imageView);
    }

    public static void displayImageCircle(@DrawableRes int resurceId, ImageView imageView) {
        displayImageCircle(null, resurceId, imageView);
    }

    public static void displayImageCircle(String url, @DrawableRes int rid, ImageView imageView) {
        if (imageView == null) return;
        if (TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext()).load(rid).asBitmap().transform(new CircleTransform(context)).into(imageView);
            return;
        }
        Observable.defer(() -> Observable.just(url)).filter(s -> !TextUtils.isEmpty(s)).filter(s -> imageView != null).map(s -> url.startsWith("http") || url.startsWith("www")).subscribe(aBoolean -> {
            if (aBoolean)
                Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).asBitmap().error(rid).placeholder(rid).transform(new CircleTransform(context)).into(imageView);
            else
                Glide.with(imageView.getContext()).load(url).asBitmap().error(rid).placeholder(rid).transform(new CircleTransform(context)).into(imageView);
        }, throwable -> LogUtil.showError(throwable));
    }

    public static void displayImage(String url, ImageView imageView, int id) {
        if (TextUtils.isEmpty(url) || imageView == null) {
            Glide.with(imageView.getContext()).load(id).into(imageView);
            return;
        }
        displayImage(url, imageView);
    }

    public static void setImage(String url, ImageView imageView, int id) {
        if (TextUtils.isEmpty(url) || imageView == null) {
            imageView.setImageResource(id);
            return;
        }
        if (url.startsWith("http")) {

            Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).error(id).crossFade().placeholder(id).into(imageView);
        } else {
            Glide.with(imageView.getContext()).load(url).error(id).crossFade().placeholder(id).into(imageView);
        }
    }

    public static void setImage(String url, ImageView imageView, int id, boolean fitCenter) {
        if (TextUtils.isEmpty(url) || imageView == null) {
            imageView.setImageResource(id);
            return;
        }
        if (url.startsWith("http")) {
            if (fitCenter) {
                Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).error(id).crossFade().placeholder(id).fitCenter().into(imageView);

            } else {
                Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).error(id).crossFade().placeholder(id).centerCrop().into(imageView);

            }
        } else {
            if (fitCenter)
                Glide.with(imageView.getContext()).load(url).error(id).crossFade().placeholder(id).fitCenter().into(imageView);
            else
                Glide.with(imageView.getContext()).load(url).error(id).crossFade().placeholder(id).centerCrop().into(imageView);
        }
    }

    public static String getCacheDir() {
        return Glide.getPhotoCacheDir(context).toString();
    }


    public static void initImage(String path, ImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        int height = params.height;
        int width = params.width;
        int scale = 1;
        int x = outWidth / width;
        int y = outHeight / height;
        scale = x > y ? x : y;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        imageView.setImageBitmap(bitmap);
    }

    public static void displayImagePick(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url) || imageView == null) {
            imageView.setImageResource(R.drawable.ic_launcher);
            return;
        }
        Observable.defer(() -> Observable.just(url))
                .filter(s -> !TextUtils.isEmpty(s))
                .filter(s -> imageView != null)
                .map(s -> s.startsWith("http") || s.startsWith("www"))
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).thumbnail(0.1f).centerCrop().crossFade().into(imageView);
                    } else
                        Glide.with(imageView.getContext()).load(url).thumbnail(0.1f).centerCrop().crossFade().into(imageView);

                }, throwable -> LogUtil.showError(throwable));
    }

    public static void displayImage(String url, ImageView imageView, boolean fitCenter) {
        if (url.startsWith("http")) {
            if (fitCenter)
                Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).thumbnail(0.1f).fitCenter().crossFade().into(imageView);
            else
                Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).thumbnail(0.1f).centerCrop().crossFade().into(imageView);
        } else {
            if (fitCenter)
                Glide.with(imageView.getContext()).load(url).thumbnail(0.1f).fitCenter().crossFade().into(imageView);
            else
                Glide.with(imageView.getContext()).load(url).thumbnail(0.1f).centerCrop().crossFade().into(imageView);
        }
    }

    public static void displayImage(String url, ImageView imageView, int rid, boolean fitCenter) {
        if (url.startsWith("http")) {
            if (fitCenter)
                Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).thumbnail(0.1f).placeholder(rid).error(rid).fitCenter().crossFade().into(imageView);
            else
                Glide.with(imageView.getContext()).using(new TFStringUrlLoader(imageView.getContext())).load(url).thumbnail(0.1f).placeholder(rid).error(rid).centerCrop().crossFade().into(imageView);
        } else {
            if (fitCenter)
                Glide.with(imageView.getContext()).load(url).thumbnail(0.1f).fitCenter().placeholder(rid).error(rid).crossFade().into(imageView);
            else
                Glide.with(imageView.getContext()).load(url).thumbnail(0.1f).centerCrop().placeholder(rid).error(rid).crossFade().into(imageView);
        }
    }


}
