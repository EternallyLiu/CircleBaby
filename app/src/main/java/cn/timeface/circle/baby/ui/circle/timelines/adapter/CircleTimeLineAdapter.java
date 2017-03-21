package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseViewHolder;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineGroupListAdapter;
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

    private int paddingImage = 4;
    private int maxImageHeight = 240;

    public CircleTimeLineAdapter(Context activity) {
        super(activity);
        maxImageHeight = (int) (context().getResources().getDimension(R.dimen.size_120) * 1.5f);
        paddingImage = (int) (context().getResources().getDimension(R.dimen.size_2));
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
        doGrid(gv, timelineObj.getMediaList());
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

    private View getView(int index, LinearLayout rowLineaylayout, CircleMediaObj mediaObj) {
        View view = inflater.inflate(R.layout.time_line_list_image, rowLineaylayout, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rowLineaylayout.getLayoutParams();
        int width = (mediaObj.getW() * params.height) / mediaObj.getH();
        LinearLayout.LayoutParams itemParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (itemParams == null)
            itemParams = new LinearLayout.LayoutParams(width, params.height);
        else {
            itemParams.height = params.height;
            itemParams.width = width;
        }
        view.setLayoutParams(itemParams);
        imageView.setTag(R.id.recycler_item_click_tag, index);
        imageView.setOnClickListener(this);
        GlideUtil.displayImage(mediaObj.getLocalPath(), imageView);
        return view;
    }

    private int doGrid(LinearLayout gv, List<CircleMediaObj> list) {
        gv.setVisibility(View.GONE);
        if (gv.getChildCount() > 0)
            gv.removeAllViews();
        LinearLayout rowView = null;
        int height = maxImageHeight, width = 0;
        int count = 0;
        int startIndex = count;
        for (int i = 0; i < TimeLineGroupListAdapter.MAX_ROW_COUNT; i++) {
            if (count >= list.size()) {
                break;
            }
            width = 0;
            height = maxImageHeight;
            rowView = new LinearLayout(context());
            rowView.setOrientation(LinearLayout.HORIZONTAL);
            rowView.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int k = count; k < list.size(); k++) {
                count++;
                CircleMediaObj mediaObj = list.get(k);
                if (mediaObj.getH() <= 0 || mediaObj.getH() <= 0) {
                    continue;
                }
                if (mediaObj.getH() > height)
                    width += ((mediaObj.getW() * height) / mediaObj.getH());
                else {
                    if (width <= 0) {
                        height = mediaObj.getH();
                        width = mediaObj.getW();
                    } else if (mediaObj.getH() == height) {
                        height = mediaObj.getH();
                        width += (mediaObj.getW());
                    } else {
                        width = width * mediaObj.getH() / height;
                        height = mediaObj.getH();
                        width += (mediaObj.getW());
                    }
                }
                if (list.size() - count == 1) {
                    continue;
                } else {
                    if (width > App.mScreenWidth || count - startIndex >= TimeLineGroupListAdapter.ROW_MAX_COUNT) {
                        break;
                    } else continue;
                }
            }
            height = (App.mScreenWidth * height) / width;
            width = App.mScreenWidth;
            rowParams.height = height;
            rowView.setPadding(0, paddingImage, 0, 0);
            rowView.setLayoutParams(rowParams);
            for (int j = startIndex; j < count; j++) {
                View view = getView(j, rowView, list.get(j));
                if (j >= startIndex && j < count - 1) {
                    view.setPadding(0, 0, (startIndex + 1) == count ? 0 : paddingImage, 0);
                } else {
                    view.setPadding(0, 0, 0, 0);
                }
                rowView.addView(view);
            }
            startIndex = count;
            gv.addView(rowView);
        }
        if (gv.getChildCount() > 0) gv.setVisibility(View.VISIBLE);
        else gv.setVisibility(View.GONE);
        return count;
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
