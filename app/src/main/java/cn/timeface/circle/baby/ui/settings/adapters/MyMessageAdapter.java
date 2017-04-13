package cn.timeface.circle.baby.ui.settings.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.settings.beans.MessageBean;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;

/**
 * author : wangshuai Created on 2017/4/13
 * email : wangs1992321@gmail.com
 */
public class MyMessageAdapter extends BaseEmptyAdapter {
    public MyMessageAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.message_list_item;
    }

    @Override
    public int getViewType(int position) {
        return 1001;
    }

    @Override
    public void initView(View contentView, int position) {
        switch (getItemViewType(position)) {
            case EMPTY_CODE:
                doEmpty(contentView);
                break;
            default:
                doMessage(contentView, position);
                break;
        }
    }

    private void doMessage(View contentView, int position) {
        MessageBean item = getItem(position);
        ImageView ivAvatar= ViewHolder.getView(contentView,R.id.iv_avatar);
        ImageView ivDot=ViewHolder.getView(contentView,R.id.iv_dot);
        TextView tvRelative=ViewHolder.getView(contentView,R.id.tv_relation);
        TextView tvContent=ViewHolder.getView(contentView,R.id.tv_content);
        TextView tvTime=ViewHolder.getView(contentView,R.id.tv_time);
        RelativeLayout rlContent=ViewHolder.getView(contentView,R.id.rl_content);
        ImageView ivContent=ViewHolder.getView(contentView,R.id.iv_content);
        ImageView ivVedio=ViewHolder.getView(contentView,R.id.iv_video);
        tvRelative.setVisibility(TextUtils.isEmpty(item.getTitle())?View.GONE:View.VISIBLE);
        tvRelative.setText(item.getTitle());
        tvContent.setText(item.getContent());
        GlideUtil.displayImageCircle(item.getImgUrl(),R.drawable.ic_launcher,ivAvatar);
        tvTime.setText(DateUtil.formatDate("MM-dd HH:mm",item.getTime()));
        rlContent.setVisibility(View.GONE);
    }
}
