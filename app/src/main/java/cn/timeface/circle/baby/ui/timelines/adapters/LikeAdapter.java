package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.utils.GlideUtil;

/**
 * author : wangshuai Created on 2017/2/7
 * email : wangs1992321@gmail.com
 */
public class LikeAdapter extends BaseAdapter {
    private int paddingSize = 20;

    public LikeAdapter(Context activity) {
        super(activity);
        paddingSize = (int) activity.getResources().getDimension(R.dimen.size_10);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.like_item;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    @Override
    public void initView(View contentView, int position) {
        ImageView icon = ViewHolder.getView(contentView, R.id.icon);
        UserObj userObj = getItem(position);
        if (userObj.getIsCreator() == -1) {
            icon.setImageResource(R.drawable.time_line_cool);
            icon.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
        } else {
            icon.setPadding(0, 0, 0, 0);
            GlideUtil.displayImageCircle(userObj.getAvatar(), icon);
        }
    }
}
