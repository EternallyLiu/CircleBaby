package cn.timeface.circle.baby.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;

import com.github.rayboot.widget.ratioview.RatioFrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.GlideUtil;

/**
 * Created by lidonglin on 2017/3/20.
 */
public class CirclePhotoImageView extends RatioFrameLayout{

    @Bind(R.id.iv_photo)
    ImageView ivPhoto;

    public CirclePhotoImageView(Context context) {
        super(context);
        init();
    }

    public CirclePhotoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclePhotoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CirclePhotoImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.image_view_circle_photo, this);
        ButterKnife.bind(view);
    }

    public void setContent(PhotoModel img) {
        GlideUtil.displayImagePick(!TextUtils.isEmpty(img.getLocalPath()) ? img.getLocalPath() : !TextUtils.isEmpty(img.getUrl()) ? img.getUrl() : null, ivPhoto);
    }

    public void setContent(MediaObj img) {
        GlideUtil.displayImagePick(img.getImgUrl(),ivPhoto);
    }

    public ImageView getIvPhoto() {
        return ivPhoto;
    }
}
