package cn.timeface.circle.baby.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.timeface.circle.baby.R;
import cn.timeface.common.utils.StringUtil;


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
            imageView.setImageResource(R.drawable.ic_log);
            return;
        }
        Glide.with(context).load(url).into(imageView);
    }

    public static void displayImage(String url, ImageView imageView,int id) {
        if (TextUtils.isEmpty(url) || imageView == null) {
            imageView.setImageResource(id);
            return;
        }
        Glide.with(context).load(url).into(imageView);
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

}
