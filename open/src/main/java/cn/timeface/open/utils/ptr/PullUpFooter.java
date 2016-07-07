package cn.timeface.open.utils.ptr;

import android.content.Context;
import android.widget.LinearLayout;

import cn.timeface.open.R;


/**
 * @author SUN
 * @from 2015/2/9
 * @TODO
 */
public class PullUpFooter extends LinearLayout {
    public PullUpFooter(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.footer_pull_up, this);
    }
}
