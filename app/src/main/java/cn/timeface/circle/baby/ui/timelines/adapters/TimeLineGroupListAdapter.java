package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.activities.VideoPlayActivity;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.beans.TimeGroupSimpleBean;
import cn.timeface.circle.baby.ui.timelines.fragments.TimeFaceDetailFragment;

/**
 * author : wangshuai Created on 2017/2/16
 * email : wangs1992321@gmail.com
 */
public class TimeLineGroupListAdapter extends BaseAdapter {
    private static final int HEADER_VIEW_TYPE = -1000;// [-1000 ,- 0)代表的是header
    private static final int FOOTER_VIEW_TYPE = -2000;//[-2000, -1000)代表的是footer
    private final List<View> mHeaders = new ArrayList<>();
    private final List<View> mFooters = new ArrayList<>();
    private int largeSize, smailSize; //日历中两个字体大小
    private int dayColor;   //日历中几号的字体颜色
    private int maxImageHeight = 240;
    private int paddingImage = 4;
    private static final int MAX_ROW_COUNT = 2;
    private int lastPadding = 0;

    public TimeLineGroupListAdapter(Context activity) {
        super(activity);
        largeSize = (int) context().getResources().getDimension(R.dimen.text_large);
        smailSize = (int) context().getResources().getDimension(R.dimen.text_small_10);
        dayColor = context().getResources().getColor(R.color.sea_buckthorn);
        maxImageHeight = (int) (context().getResources().getDimension(R.dimen.size_120) * 1.5f);
        paddingImage = (int) (context().getResources().getDimension(R.dimen.size_2));
        if (activity instanceof TabMainActivity)
            lastPadding = (int) context().getResources().getDimension(R.dimen.size_56);
    }

    @Override
    public int getItemCount() {
        return mHeaders.size() + getCount() + mFooters.size();
    }

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case 1:
                return R.layout.time_line_list_group;
            case 0:
                return R.layout.time_line_list_item;
        }
        return 0;
    }

    private int getCount() {

        return super.getRealItemSize();
    }

    @Override
    public int getViewType(int position) {
        if (position < mHeaders.size()) {
            return HEADER_VIEW_TYPE + position;

        } else if (position < (mHeaders.size() + getCount())) {
            BaseObj item = getItem(position - mHeaders.size());
            return item.getBaseType();

        } else {
            return FOOTER_VIEW_TYPE + position - mHeaders.size() - getCount();
        }
    }

    public void deleteTimeLine(int timeId) {
        if (timeId <= 0) return;
        TimeLineObj timeLineObj = new TimeLineObj();
        timeLineObj.setTimeId(timeId);
        deleteItem(timeLineObj);
    }

    public int findPosition(int timeId) {
        if (timeId <= 0) return 0;
        TimeLineObj timeLineObj = new TimeLineObj();
        timeLineObj.setTimeId(timeId);
        return mHeaders.size() + getPosition(timeLineObj);
    }

    private boolean isHeader(int viewType) {
        return viewType >= HEADER_VIEW_TYPE && viewType < (HEADER_VIEW_TYPE + mHeaders.size());
    }

    private boolean isFooter(int viewType) {
        return viewType >= FOOTER_VIEW_TYPE && viewType < (FOOTER_VIEW_TYPE + mFooters.size());
    }

    public void removeHeader(View view) {
        if (mHeaders.contains(view)) {
            mHeaders.remove(view);
        }
    }

    public void removeFooter(View view) {
        if (mFooters.contains(view)) {
            mFooters.remove(view);
        }
    }

    public void addHeader(@NonNull View view) {
        mHeaders.add(view);
    }

    public void addFooter(@NonNull View view) {
        mFooters.add(view);
    }

    public int getHeaderCount() {
        return mHeaders.size();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeader(viewType)) {
            int whichHeader = Math.abs(viewType - HEADER_VIEW_TYPE);
            View headerView = mHeaders.get(whichHeader);
            return new BaseViewHolder(headerView, null);
//            return new RecyclerView.ViewHolder(headerView) {
//            };
        } else if (isFooter(viewType)) {
            int whichFooter = Math.abs(viewType - FOOTER_VIEW_TYPE);
            View footerView = mFooters.get(whichFooter);
            return new BaseViewHolder(footerView, null);
//            return new RecyclerView.ViewHolder(footerView) {
//            };
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (position < mHeaders.size()) {

        } else if (position < (mHeaders.size() + getCount())) {
            super.onBindViewHolder(holder, position - mHeaders.size());

        } else {
        }
    }

    @Override
    public void initView(View contentView, int position) {
        if (contentView != null && position == getItemCount() - 1)
            contentView.setPadding(0, 0, 0, lastPadding);
        if (position < mHeaders.size()) {

        } else if (position < (mHeaders.size() + getCount())) {
            BaseObj item = getItem(position);
            if (item == null)
                return;
            switch (item.getBaseType()) {
                case 1:
                    doGroup(contentView, position, (TimeGroupSimpleBean) item);
                    break;
                case 0:
                    doTimeLine(contentView, position, (TimeLineObj) item);
            }
        } else {

        }

    }

    @Override
    public <T> T getItem(int position) {
        return super.getItem(position);
    }

    /**
     * 处理时光内容展示
     *
     * @param contentView
     * @param position
     * @param item
     */
    private void doTimeLine(View contentView, int position, TimeLineObj item) {
        TextView tvContent = ViewHolder.getView(contentView, R.id.tv_content);
        LinearLayout gv = ViewHolder.getView(contentView, R.id.gv);
        ImageView ivCover = ViewHolder.getView(contentView, R.id.iv_cover);
        ImageView ivVideo = ViewHolder.getView(contentView, R.id.iv_video);
        RelativeLayout rlSingle = ViewHolder.getView(contentView, R.id.rl_single);
//        TextView tvMilestone = ViewHolder.getView(contentView, R.id.tv_milestone);
        TextView tvAuthor = ViewHolder.getView(contentView, R.id.tv_author);
        TextView tvDate = ViewHolder.getView(contentView, R.id.tv_date);
        ImageView iconLike = ViewHolder.getView(contentView, R.id.icon_like);
        TextView tvLikecount = ViewHolder.getView(contentView, R.id.tv_likecount);
        ImageView iconComment = ViewHolder.getView(contentView, R.id.icon_comment);
        TextView tvCommentcount = ViewHolder.getView(contentView, R.id.tv_commentcount);
        LinearLayout llRecode = ViewHolder.getView(contentView, R.id.ll_recode);
        TextView picCount = ViewHolder.getView(contentView, R.id.pic_count);

        //设置基本信息 内容 作者 时间 是否点赞 评论个数 点赞个数
        if (TextUtils.isEmpty(item.getContent()))
            tvContent.setVisibility(View.GONE);
        else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(item.getContent());
        }
        tvAuthor.setText(item.getAuthor().getRelationName());
        tvDate.setText(DateUtil.formatDate("MM-dd kk:mm", item.getDate()));
        iconLike.setImageResource(item.getLike() == 1 ? R.drawable.time_line_cool : R.drawable.time_line_cool_no);
        tvCommentcount.setText(item.getCommentCount() + "");
        tvLikecount.setText(item.getLikeCount() + "");
        iconLike.setTag(R.id.icon_like, position);
        iconLike.setOnClickListener(this);
        tvLikecount.setOnClickListener(this);

        iconComment.setTag(R.id.recycler_item_click_tag, position);
        iconComment.setOnClickListener(this);

        //处理图片
        rlSingle.setVisibility(View.GONE);
        if (gv.getChildCount() > 0)
            gv.removeAllViews();
        gv.setVisibility(View.GONE);
        if (item.getType() != 1) {
            LinearLayout rowView = null;
            int height = maxImageHeight, width = 0;
            int count = 0;
            int startIndex = count;
            for (int i = 0; i < MAX_ROW_COUNT; i++) {
                if (count >= item.getMediaList().size()) {
                    break;
                }
                width = 0;
                height = maxImageHeight;
                rowView = new LinearLayout(context());
                rowView.setOrientation(LinearLayout.HORIZONTAL);
                rowView.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                for (int k = count; k < item.getMediaList().size(); k++) {
                    count++;
                    MediaObj mediaObj = item.getMediaList().get(k);
                    if (mediaObj.getH() <= 0 || mediaObj.getW() <= 0)
                        continue;
                    if (mediaObj.getH() > height)
                        width += ((mediaObj.getW() * height) / mediaObj.getH());
                    else {
                        if (width <= 0) {
                            height = mediaObj.getH();
                            width += (mediaObj.getW());
                        } else {
                            width = (width * mediaObj.getH()) / width;
                            height = mediaObj.getH();
                            width += (mediaObj.getW());
                        }
                    }
                    if (width > App.mScreenWidth)
                        break;
                    else continue;
                }
                height = (App.mScreenWidth * height) / width;
                width = App.mScreenWidth;
                rowParams.height = height;
                rowView.setLayoutParams(rowParams);
                for (int j = startIndex; j < count; j++) {
                    View view = getView(position, j, rowView, item.getMediaList().get(j));
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
            if (count < item.getMediaList().size()) {
                picCount.setVisibility(View.VISIBLE);
                picCount.setText(String.format("共%d张图片  ", item.getMediaList().size()));
            } else picCount.setVisibility(View.GONE);
            if (gv.getChildCount() > 0) gv.setVisibility(View.VISIBLE);
            else gv.setVisibility(View.GONE);
        } else {
            rlSingle.setVisibility(View.VISIBLE);
            int width = Remember.getInt("width", 0) * 3;
            ViewGroup.LayoutParams layoutParams = ivCover.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = (int) (width * 0.5);
            gv.setVisibility(View.GONE);
            ivVideo.setVisibility(View.VISIBLE);
            ivCover.setLayoutParams(layoutParams);
            ivVideo.setLayoutParams(layoutParams);
            GlideUtil.displayImage(item.getMediaList().get(0).getImgUrl(), ivCover, true);
            rlSingle.setTag(R.id.rl_single, item.getMediaList().get(0).getVideoUrl());
            rlSingle.setOnClickListener(this);
        }


    }

    /**
     * 获取图片视图
     *
     * @param groupIndex
     * @param index
     * @param rowLineaylayout
     * @param mediaObj
     * @return
     */
    private View getView(int groupIndex, int index, LinearLayout rowLineaylayout, MediaObj mediaObj) {
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
        imageView.setTag(R.id.icon, groupIndex);
        imageView.setTag(R.id.recycler_item_click_tag, index);
        imageView.setOnClickListener(this);
        GlideUtil.displayImage(mediaObj.getImgUrl(), imageView);
        return view;
    }

    /**
     * 处理时光列表组内容展示
     *
     * @param contentView
     * @param position
     * @param item
     */
    private void doGroup(View contentView, int position, TimeGroupSimpleBean item) {
        TextView calendar = ViewHolder.getView(contentView, R.id.calendar);
        TextView tvDateex = ViewHolder.getView(contentView, R.id.tv_dateex);
        TextView tvAge = ViewHolder.getView(contentView, R.id.tv_age);
        String month = DateUtil.getMonth(item.getDate());
        String day = DateUtil.getDay(item.getDate());
        String year = DateUtil.getDateYear(item.getDate());

        //设置日历日期
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(day).append("\n").append(year).append(".").append(month);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(largeSize);
        builder.setSpan(sizeSpan, 0, day.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(dayColor);
        builder.setSpan(colorSpan, 0, day.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        calendar.setText(builder);

        tvDateex.setText(item.getDateEx());
        tvAge.setText("宝宝" + item.getAge());
        calendar.setOnClickListener(this);
        contentView.setTag(R.id.recycler_item_click_tag, position + 1);
    }

    private void like(int postion, View v) {
        TimeLineObj timeLineObj = getItem(postion);
        ApiFactory.getApi().getApiService().like(timeLineObj.getTimeId(), timeLineObj.getLike() % 2 == 1 ? 0 : 1)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        timeLineObj.setLike(timeLineObj.getLike() % 2 == 1 ? 0 : 1);
                        int count = timeLineObj.getLikeCount();
                        if (timeLineObj.getLike() % 2 == 1) {
                            count++;
                        } else count--;
                        timeLineObj.setLikeCount(count);
                        updateItem(timeLineObj);
                    }
                    v.setClickable(true);
                }, throwable -> {
                });
    }

    @Override
    public void onClick(View v) {
        TimeLineObj timeLineObj = null;
        switch (v.getId()) {
            case R.id.icon_like:
            case R.id.tv_likecount:
                v.setClickable(false);
                like((Integer) v.getTag(R.id.icon_like), v);
                break;
            case R.id.icon:
                int allDetailsListPosition = (int) v.getTag(R.id.icon);
                int position = (int) v.getTag(R.id.recycler_item_click_tag);
                timeLineObj = getItem(allDetailsListPosition);
                if (timeLineObj != null)
                    FragmentBridgeActivity.openBigimageFragment(v.getContext(), allDetailsListPosition, timeLineObj.getMediaArray(), timeLineObj.getUrls(), position, true, false);
                break;
            case R.id.rl_single:
                VideoPlayActivity.open(context(), v.getTag(R.id.rl_single).toString());
                break;
            case R.id.calendar:
                FragmentBridgeActivity.open(context(), "TimeLineFragment");
                break;
            case R.id.icon_comment:

                int index = (int) v.getTag(R.id.recycler_item_click_tag);
                timeLineObj = getItem(index);
                TimeFaceDetailFragment.open(context(), index, true, timeLineObj);
                break;
            default:
                int tag = (int) v.getTag(R.id.recycler_item_click_tag);
                timeLineObj = getItem(tag);
                if (getItemClickLister() == null) {
                    if (timeLineObj != null) {
                        TimeFaceDetailFragment.open(context(), tag, timeLineObj);
                    }
                } else super.onClick(v);
                break;
        }
    }
}
