package cn.timeface.circle.baby.support.utils.ptr;

import android.content.Context;
import android.widget.LinearLayout;


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
        inflate(getContext(), cn.timeface.circle.baby.R.layout.footer_pull_up, this);
    }
}
