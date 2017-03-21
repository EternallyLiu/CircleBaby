package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseViewHolder;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.ui.timelines.views.SelectImageView;
import cn.timeface.circle.baby.ui.timelines.views.TimeLineMarker;

/**
 * author : wangshuai Created on 2017/3/17
 * email : wangs1992321@gmail.com
 */
public class CircleTimeLineAdapter extends BaseAdapter {
    private boolean hasWork = false;
    private List<View> mHeadView = new ArrayList<>(0);
    private List<View> mFooter = new ArrayList<>(0);
    private static final int VIEW_TYPE_HEADER = -49999;
    private static final int VIEW_TYPE_FOOTER = -39999;

    public CircleTimeLineAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getItemCount() {
        return mHeadView.size() + getRealItemSize() + mFooter.size();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return super.onCreateViewHolder(parent, viewType);
        } else if (viewType < VIEW_TYPE_FOOTER) {
            View headerView = mHeadView.get(viewType - VIEW_TYPE_HEADER);
            return new BaseViewHolder(headerView, null);
        } else if (viewType >= VIEW_TYPE_FOOTER && viewType < 0) {
            View footer = mFooter.get(viewType - VIEW_TYPE_FOOTER);
            return new BaseViewHolder(footer, null);
        } else return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (position < mHeadView.size()) {

        } else if (position < (mHeadView.size() + getRealItemSize())) {
            super.onBindViewHolder(holder, position);

        } else {
        }

    }

    public void addHeader(View view) {
        if (!mHeadView.contains(view)) {
            mHeadView.add(view);
            notifyDataSetChanged();
        }
    }

    public void addFooter(View view) {
        if (!mFooter.contains(view)) {
            mFooter.add(view);
            notifyDataSetChanged();
        }
    }

    public void removeHeader(int index) {
        if (index < mHeadView.size() && index >= 0) {
            mHeadView.remove(index);
            notifyDataSetChanged();
        }
    }

    public void removeHeader(View view) {
        if (mHeadView.contains(view)) {
            removeHeader(mHeadView.indexOf(view));
        }
    }

    public void removeFooter(int index) {
        if (index >= 0 && index < mFooter.size()) {
            mFooter.remove(index);
            notifyDataSetChanged();
        }
    }

    public void removeFooter(View view) {
        if (mFooter.contains(view)) {
            removeFooter(mFooter.indexOf(view));
        }
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.circle_time_line_list_layout;
    }

    @Override
    public int getViewType(int position) {
        if (position >= 0 && position < mHeadView.size()) {
            return VIEW_TYPE_HEADER + position;
        } else if (position >= 0 && position >= mHeadView.size() + getRealItemSize()) {
            return VIEW_TYPE_FOOTER + position;
        }
        return 1;
    }

    private void doTimeLine(View contentView, int position, CircleTimelineObj timelineObj) {
        TextView tvDateTime = ViewHolder.getView(contentView, R.id.tv_date_time);
        TextView tvTitle = ViewHolder.getView(contentView, R.id.tv_title);
        TextView tvDetail = ViewHolder.getView(contentView, R.id.tv_detail);
        ImageView ivIcon = ViewHolder.getView(contentView, R.id.iv_icon);
        TextView tvName = ViewHolder.getView(contentView, R.id.tv_name);
        TextView tvDelete = ViewHolder.getView(contentView, R.id.tv_delete);
        ImageView ivMessage = ViewHolder.getView(contentView, R.id.iv_message);
        SelectImageView ivLike = ViewHolder.getView(contentView, R.id.iv_like);
        LinearLayout gv = ViewHolder.getView(contentView, R.id.ll_gv);
        TimeLineMarker marker = ViewHolder.getView(contentView, R.id.line);

        tvDateTime.setText(DateUtil.formatDate("yyyy年MM月dd日 HH时mm分 E", timelineObj.getCreateDate()));
        tvTitle.setText(timelineObj.getTitle());
        tvDetail.setText(timelineObj.getContent());
        tvName.setText(timelineObj.getPublisher().getCircleNickName());
        GlideUtil.displayImageCircle(timelineObj.getPublisher().getCircleAvatarUrl(), ivIcon);
        ivLike.setChecked(timelineObj.getLike() % 2 == 0 ? false : true);

        if (position == 0 && isHasWork()) {
            marker.setDrawBegin(true);
            marker.setDrawEnd(true);
        } else if (position == getItemCount() - 1) {
            marker.setDrawBegin(true);
            marker.setDrawEnd(false);
        } else {
            marker.setDrawBegin(true);
            marker.setDrawEnd(true);
        }
    }

    @Override
    public void initView(View contentView, int position) {

        if (position < mHeadView.size()) {

        } else if (position < (mHeadView.size() + getRealItemSize())) {
            doTimeLine(contentView, position - mHeadView.size(), getItem(position - mHeadView.size()));
        } else {
        }

    }

    public boolean isHasWork() {
        return hasWork;
    }

    public void setHasWork(boolean hasWork) {
        this.hasWork = hasWork;
    }
}
