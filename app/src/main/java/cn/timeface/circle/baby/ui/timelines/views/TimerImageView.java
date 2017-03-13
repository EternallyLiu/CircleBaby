package cn.timeface.circle.baby.ui.timelines.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import rx.Observable;

/**
 * author : wangshuai Created on 2017/2/14
 * email : wangs1992321@gmail.com
 */
public class TimerImageView extends ImageView {
    private Handler handler;
    private List<String> list;
    private int index = 0;

    public List<String> getList() {
        return list;
    }

    private void showImage() {
        GlideUtil.displayImageCrossfade(list.get(index), this);
        Observable.interval(3, TimeUnit.SECONDS)
                .map(url -> list.get(index))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(url -> {
                    GlideUtil.displayImageCrossfade(url, this);
                    if (index < 0 || index >= list.size() - 1)
                        index = 0;
                    else index++;
                }, throwable -> {
                    LogUtil.showError(throwable);
                });


    }

    public void setList(List<String> list) {
        this.list = list;
        if (list != null && list.size() > 0) {
            showImage();
        }
    }

    public TimerImageView(Context context) {
        super(context);
    }

    public TimerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
