package cn.timeface.circle.baby.ui.babyInfo.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.babyInfo.beans.IconHistory;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;

/**
 * Created by wangshuai on 2017/1/12.
 */

public class IconHisAdapter extends BaseAdapter {
    public IconHisAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.baby_icon_history_item;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    @Override
    public void initView(View contentView, int position) {
        ImageView icon= ViewHolder.getView(contentView,R.id.icon);
        TextView age=ViewHolder.getView(contentView,R.id.age);
        IconHistory iconHistory=getItem(position);
        GlideUtil.displayImageCircle(iconHistory.getAvatar(),icon);
        age.setText(iconHistory.getAge());
    }
}
