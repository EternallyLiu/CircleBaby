package cn.timeface.circle.baby.ui.images.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.ImageView;

import cn.timeface.circle.baby.R;

/**
 * 这个和SelectImageView类似，不同在于没有实现Checkable接口，也就是用户点击操作刽修改图片状态
 * author : wangshuai Created on 2017/1/19
 * email : wangs1992321@gmail.com
 */
public class FlipImageView extends ImageView {

    private SparseArray<Drawable> drawableSparseArray = null;

    private Resources manager;

    private Drawable default_image;

    public FlipImageView(Context context) {
        this(context, null);
    }

    public FlipImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public FlipImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        drawableSparseArray = new SparseArray<>(0);
        manager = getContext().getResources();
        if (attrs == null)
            return;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FlipImageView);
        default_image = typedArray.getDrawable(R.styleable.FlipImageView_defaultImage);
        setImageDrawable(default_image);
    }

    public void changeStatus(@DrawableRes int drawableId) {
        setImageDrawable(getDrawable(drawableId));
    }

    private Drawable getDrawable(@DrawableRes int rid) {
        Drawable drawable =
                drawableSparseArray.get(rid);
        if (drawable == null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                drawable = manager.getDrawable(rid, getContext().getTheme());
            } else drawable = manager.getDrawable(rid);
            drawableSparseArray.put(rid, drawable);
        }
        return drawable;
    }

}
