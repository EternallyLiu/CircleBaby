package cn.timeface.circle.baby.ui.images.adapters;

import android.content.Context;
import android.view.View;

import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;

/**
 * author : wangshuai Created on 2017/1/17
 * email : wangs1992321@gmail.com
 */
public class TagAdapter extends BaseAdapter {
    public TagAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void initView(View contentView, int position) {

    }
}
