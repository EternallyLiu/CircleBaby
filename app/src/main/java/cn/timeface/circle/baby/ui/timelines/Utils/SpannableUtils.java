package cn.timeface.circle.baby.ui.timelines.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.UUID;

import cn.timeface.circle.baby.R;

/**
 * author : wangshuai Created on 2017/2/22
 * email : wangs1992321@gmail.com
 */
public class SpannableUtils {

    /**
     * 获取字体样式 Typeface.BOLD加粗
     *
     * @param type
     * @return
     */
    public static StyleSpan getTextStyle(int type) {
        return new StyleSpan(type);
    }

    /**
     * 获取字体颜色对象
     *
     * @param color
     * @return
     */
    public static ForegroundColorSpan getTextColor(@ColorInt int color) {
        return new ForegroundColorSpan(color);
    }

    /**
     * 获取字体颜色对象
     *
     * @param color
     * @return
     */
    public static ForegroundColorSpan getTextColor(Context context, @ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new ForegroundColorSpan(context.getColor(colorId));
        } else {
            return new ForegroundColorSpan(context.getResources().getColor(colorId));
        }
    }

    /**
     * 获取字体大小对象
     *
     * @param context 上下文
     * @param size    大小尺寸id
     * @return
     */
    public static AbsoluteSizeSpan getTextSize(Context context, @DimenRes int size) {
        return new AbsoluteSizeSpan((int) context.getResources().getDimension(size));
    }

}
