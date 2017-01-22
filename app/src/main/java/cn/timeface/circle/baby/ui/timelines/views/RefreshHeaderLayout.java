package cn.timeface.circle.baby.ui.timelines.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.FastData;

/**
 * author : wangshuai Created on 2017/1/22
 * email : wangs1992321@gmail.com
 */
public class RefreshHeaderLayout {


    private View rootView;
    private ImageView imageView;
    private TextView title;
    private Context context;
    private ObjectAnimator oa;

    public RefreshHeaderLayout(Context context) {
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.refresh_pull_down_layout, null);
        imageView = (ImageView) rootView.findViewById(R.id.image);
        title = (TextView) rootView.findViewById(R.id.title);
    }

    public float getRotationValue(int height) {
        int maxHeight = 960;
        if (App.mScreenHeight > 0)
            maxHeight = App.mScreenHeight / 2;
        return height * ((float) maxHeight / (float) 360);
    }

    public void setRotation(float x) {
        imageView.setRotation(x);
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void startAmination() {
        oa = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        oa.setDuration(1000);
        oa.setRepeatCount(-1);
        oa.start();
    }

    public void stopAnimation() {
        if (oa != null && oa.isRunning())
            oa.end();
        oa = null;
    }

}
