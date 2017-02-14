package cn.timeface.circle.baby.ui.timelines.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * author : wangshuai Created on 2017/2/14
 * email : wangs1992321@gmail.com
 */
public class TimerImageView extends ImageView implements Runnable {
    private Handler handler;
    private List<String> list;
    private int index = 0;

    public List<String> getList() {
        return list;
    }

    private void showImage() {
        Glide.with(getContext()).load(list.get(index)).crossFade(300).into(this);
        if (index < 0 || index >= list.size() - 1)
            index = 0;
        else index++;

        if (handler == null) handler = new Handler();
        handler.postDelayed(this, 3000);
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

    @Override
    public void run() {
        showImage();
    }
}
