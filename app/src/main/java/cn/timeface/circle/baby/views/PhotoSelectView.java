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
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import uk.co.senab.photoview.PhotoView;

/**
 * author: rayboot  Created on 16/3/15.
 * email : sy0725work@gmail.com
 */
public class PhotoSelectView extends RatioFrameLayout implements Checkable {
    @Bind(R.id.iv_photo)
    PhotoView ivPhoto;
    @Bind(R.id.cb_sel)
    CheckBox cbSel;

    public PhotoSelectView(Context context) {
        super(context);
        init();
    }

    public PhotoSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoSelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_photo_select, this);
        ButterKnife.bind(view);
    }

    public void setContent(ImgObj img) {
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

    public void canZoom(boolean zoom) {
        ivPhoto.setZoomable(zoom);
    }
}
