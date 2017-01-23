package cn.timeface.circle.baby.ui.timelines.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Checkable;

import cn.timeface.circle.baby.R;

/**
 * Created by wangshuai on 2017/1/12.
 */

public class SelectImageView extends ImageView implements Checkable {

    private boolean isSelected;
    private Drawable selectDrawable, unSelectDrawable;

    public SelectImageView(Context context) {
        this(context,null);
    }

    public SelectImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SelectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    public Drawable getSelectDrawable() {
        return selectDrawable;
    }

    public void setSelectDrawable(Drawable selectDrawable) {
        this.selectDrawable = selectDrawable;
    }

    public Drawable getUnSelectDrawable() {
        return unSelectDrawable;
    }

    public void setUnSelectDrawable(Drawable unSelectDrawable) {
        this.unSelectDrawable = unSelectDrawable;
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs==null)
            return;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SelectImageView);
        isSelected = typedArray.getBoolean(R.styleable.SelectImageView_isSelected, false);
        selectDrawable = typedArray.getDrawable(R.styleable.SelectImageView_selectDrawable);
        unSelectDrawable = typedArray.getDrawable(R.styleable.SelectImageView_unselectDrawable);
        typedArray.recycle();
        setChecked(isSelected);
    }

    @Override
    public void setChecked(boolean checked) {
        isSelected = checked;
        if (isChecked() && selectDrawable != null)
            setImageDrawable(selectDrawable);
        else if (!isChecked() && unSelectDrawable != null)
            setImageDrawable(unSelectDrawable);
    }

    @Override
    public boolean isChecked() {
        return isSelected;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }
}
