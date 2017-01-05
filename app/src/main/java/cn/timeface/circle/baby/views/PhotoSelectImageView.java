package cn.timeface.circle.baby.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioFrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;

/**
 * author : YW.SUN Created on 2016/4/11
 * email : sunyw10@gmail.com
 */
public class PhotoSelectImageView extends RatioFrameLayout implements Checkable {

    @Bind(R.id.iv_photo)
    ImageView ivPhoto;
    @Bind(R.id.cb_sel)
    CheckBox cbSel;

    public PhotoSelectImageView(Context context) {
        super(context);
        init();
    }

    public PhotoSelectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoSelectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoSelectImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.image_view_photo_select, this);
        ButterKnife.bind(view);
    }

    public void setContent(PhotoModel img) {
        Glide.with(getContext())
                .load(img.getUri())
                .thumbnail(0.1f)
                .centerCrop()
                .into(ivPhoto);
    }

    @Override
    public void setChecked(boolean checked) {
        cbSel.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return cbSel.isChecked();
    }

    @Override
    public void toggle() {
        cbSel.toggle();
    }

    public void setOnCheckedListener(OnClickListener listener) {
        this.cbSel.setOnClickListener(listener);
    }

    public CheckBox getCbSel() {
        return cbSel;
    }

    public ImageView getIvPhoto() {
        return ivPhoto;
    }
}
