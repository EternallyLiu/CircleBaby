package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.Context;
import android.view.View;

/**
 * author : wangshuai Created on 2017/2/13
 * email : wangs1992321@gmail.com
 */
public class SendAdapter extends BaseAdapter {
    public SendAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return 0;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    @Override
    public void initView(View contentView, int position) {

    }
}
