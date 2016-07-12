package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TimeLineDetailActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.objs.CommentObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.IconTextView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class TimeLineAdapter extends BaseRecyclerAdapter<TimeLineObj> {

    private View.OnClickListener onClickListener;
    int normalColor;
    public static Context context;
    private ViewHolder holder;
    public List<TimeLineObj> listData;
    private TimeLineObj item;
    private List<TimeLineGroupObj> allDetailsList;
    public int allDetailsListPosition;

    public TimeLineAdapter(Context mContext, List<TimeLineObj> listData, int position) {
        super(mContext, listData);

        allDetailsListPosition = position;
        this.listData = listData;
        this.context = mContext;
        normalColor = mContext.getResources().getColor(R.color.gray_normal);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_recode, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = ((ViewHolder) viewHolder);
        item = getItem(position);
        holder.timeLineObj = item;
        holder.tvContent.setText(item.getContent());
        holder.tvAuthor.setText(item.getAuthor().getRelationName());
        holder.tvDate.setText(DateUtil.getTime2(item.getDate()));
        holder.iconLike.setTextColor(item.getLike() == 1 ? Color.RED : normalColor);

        if (!TextUtils.isEmpty(item.getMilestone())) {
            holder.tvMilestone.setVisibility(View.VISIBLE);
            holder.tvMilestone.setText(item.getMilestone());
        }

        if (item.getMediaList().size() == 1) {
            holder.gv.setVisibility(View.GONE);
            holder.ivCover.setVisibility(View.VISIBLE);
            String url = item.getMediaList().get(0).getImgUrl();
            GlideUtil.displayImage(url, holder.ivCover);
        } else {
            holder.ivCover.setVisibility(View.GONE);
        }

        if (item.getMediaList().size() > 1) {
            holder.gv.setVisibility(View.VISIBLE);
            List<MediaObj> imgObjList = item.getMediaList();
            ArrayList<String> urls = new ArrayList<>();
            for (MediaObj mediaObj : imgObjList) {
                urls.add(mediaObj.getImgUrl());
            }
            MyAdapter myAdapter = new MyAdapter(context, urls);
            holder.gv.setAdapter(myAdapter);
            ViewGroup.LayoutParams layoutParams = holder.gv.getLayoutParams();
            layoutParams.height = Remember.getInt("width", 0);
            if (item.getMediaList().size() > 3) {
                layoutParams.height = Remember.getInt("width", 0) * 2;
            }
            if (item.getMediaList().size() > 6) {
                layoutParams.height = Remember.getInt("width", 0) * 3;
            }
            holder.gv.setLayoutParams(layoutParams);

        } else {
            holder.gv.setVisibility(View.GONE);
        }

        if (item.getCommentList().size() == 0 && item.getLikeList().size() == 0) {
            holder.llCommentLikeWrapper.setVisibility(View.GONE);
        } else {
            holder.llCommentLikeWrapper.setVisibility(View.VISIBLE);
        }

        if (item.getCommentList().size() > 0) {
            holder.llCommentWrapper.setVisibility(View.VISIBLE);
            holder.llCommentWrapper.removeAllViews();
            int comments = item.getCommentList().size() > 3 ? 3 : item.getCommentList().size();
            for (int i = 0; i < comments; i++) {
                CommentObj commentObj = item.getCommentList().get(i);
                holder.llCommentWrapper.addView(initCommentItemView(commentObj));
            }
        } else {
            holder.llCommentWrapper.setVisibility(View.GONE);
            holder.llCommentWrapper.removeAllViews();
        }
        if (item.getCommentList().size() > 3) {
            holder.tvMoreComment.setVisibility(View.VISIBLE);
        } else {
            holder.tvMoreComment.setVisibility(View.GONE);
        }

        if (item.getLikeCount() > 0) {
            holder.hsv.setVisibility(View.VISIBLE);
            holder.llGoodListUsersBar.removeAllViews();
            for (UserObj u : item.getLikeList()) {
                ImageView imageView = initPraiseItem();
                holder.llGoodListUsersBar.addView(imageView);
                GlideUtil.displayImage(u.getAvatar(), imageView);
            }
        } else {
            holder.hsv.setVisibility(View.GONE);
            holder.llGoodListUsersBar.removeAllViews();
        }

        if (item.getType() == 1) {
            holder.ivVideo.setVisibility(View.VISIBLE);
            int width = Remember.getInt("width", 0);
            ViewGroup.LayoutParams layoutParams = holder.ivCover.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            holder.ivCover.setLayoutParams(layoutParams);
//            holder.ivVideo.setLayoutParams(layoutParams);
            holder.ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        holder.position = position;
        holder.allDetailsPosition = allDetailsListPosition;
        holder.listData = listData;

    }


    private static ImageView initPraiseItem() {
        CircleImageView imageView = new CircleImageView(context);
        imageView.setImageResource(R.color.gray_pressed);
        int width = context.getResources().getDimensionPixelSize(R.dimen.size_36);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        int margin = context.getResources().getDimensionPixelSize(R.dimen.size_2);
        params.setMargins(margin, margin, margin, margin);
        imageView.setLayoutParams(params);
        return imageView;
    }

    private View initCommentItemView(CommentObj comment) {
        View view = mLayoutInflater.inflate(R.layout.view_comment, null);
        TextView tvRelation = (TextView) view.findViewById(R.id.tv_relation);
        TextView tvComment = (TextView) view.findViewById(R.id.tv_comment);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        if (comment.getToUserInfo().getRelationName() != null) {
            tvRelation.setText(comment.getUserInfo().getRelationName() + " 回复 " + comment.getToUserInfo().getRelationName());
        } else {
            tvRelation.setText(comment.getUserInfo().getRelationName());
        }
        tvComment.setText(comment.getContent());
        tvTime.setText(DateUtil.formatDate("MM-dd HH:mm", comment.getCommentDate()));
        return view;
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.gv)
        GridView gv;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.iv_video)
        ImageView ivVideo;
        @Bind(R.id.tv_milestone)
        TextView tvMilestone;
        @Bind(R.id.tv_author)
        TextView tvAuthor;
        @Bind(R.id.tv_date)
        TextView tvDate;
        @Bind(R.id.icon_like)
        IconTextView iconLike;
        @Bind(R.id.icon_comment)
        IconTextView iconComment;
        @Bind(R.id.ll_good_list_users_bar)
        LinearLayout llGoodListUsersBar;
        @Bind(R.id.hsv)
        HorizontalScrollView hsv;
        @Bind(R.id.ll_comment_wrapper)
        LinearLayout llCommentWrapper;
        @Bind(R.id.tv_more_comment)
        TextView tvMoreComment;
        @Bind(R.id.ll_recode)
        LinearLayout llRecode;
        List<TimeLineObj> listData;
        TimeLineObj timeLineObj;
        int position;
        int allDetailsPosition;
        @Bind(R.id.commentAnd)
        LinearLayout llCommentLikeWrapper;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            llRecode.setOnClickListener(this);
            iconLike.setOnClickListener(this);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    TimeLineDetailActivity.open(context, timeLineObj);
                    TimeLineDetailActivity.open(context, timeLineObj, allDetailsPosition, position);
                }
            });
        }

        public void setRecordObj(TimeLineObj timeLineObj) {
//            itemView.setTag(R.string.tag_ex, timeLineObj);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_recode:
//                    TimeLineDetailActivity.open(context, timeLineObj);
                    TimeLineDetailActivity.open(context, timeLineObj, allDetailsPosition, position);
                    break;
                case R.id.icon_like:
                    int p = iconLike.getCurrentTextColor() == Color.RED ? 0 : 1;
                    ApiFactory.getApi().getApiService().like(timeLineObj.getTimeId(), p)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                ToastUtil.showToast(response.getInfo());
                                if (response.success()) {
                                    boolean isContains = false;
                                    if (p == 1) {//之前没有点赞

                                        listData.get(position).setLike(1);
                                        int likeCount = listData.get(position).getLikeCount();
                                        listData.get(position).setLikeCount(likeCount + 1);
                                        for (UserObj u : listData.get(position).getLikeList()) {
                                            if (u.getUserId().equals(FastData.getUserInfo().getUserId())) {
                                                isContains = true;
                                                break;
                                            }
                                        }
                                        if (!isContains) {
                                            listData.get(position).getLikeList().add(FastData.getUserInfo());
                                            isContains = false;
                                        }

                                        iconLike.setTextColor(Color.RED);
                                        hsv.setVisibility(View.VISIBLE);

                                        ImageView imageView = initPraiseItem();
                                        llGoodListUsersBar.addView(imageView);
                                        GlideUtil.displayImage(FastData.getAvatar(), imageView);
                                    } else {

                                        listData.get(position).setLike(0);
                                        int likeCount = listData.get(position).getLikeCount();
                                        listData.get(position).setLikeCount(likeCount - 1);
                                        for (UserObj u : listData.get(position).getLikeList()) {
                                            if (u.getUserId().equals(FastData.getUserId())) {
                                                listData.get(position).getLikeList().remove(u);
                                            }
                                        }

                                        iconLike.setTextColor(context.getResources().getColor(R.color.gray_normal));
                                        llGoodListUsersBar.removeAllViews();
                                        if (timeLineObj.getLikeCount() == 0) {
                                            hsv.setVisibility(View.GONE);
                                            if (timeLineObj.getCommentCount() == 0) {
                                            }
                                        } else if (timeLineObj.getLikeCount() == 1 && timeLineObj.getLikeList().get(0).getUserId().equals(FastData.getUserId())) {
                                            hsv.setVisibility(View.GONE);
                                            if (timeLineObj.getCommentCount() == 0) {
                                            }
                                        } else {
                                            hsv.setVisibility(View.VISIBLE);
                                            for (UserObj u : timeLineObj.getLikeList()) {
                                                ImageView imageView = initPraiseItem();
                                                if (!u.getUserId().equals(FastData.getUserId())) {
                                                    llGoodListUsersBar.addView(imageView);
                                                }
                                                GlideUtil.displayImage(u.getAvatar(), imageView);
                                            }
                                        }
                                    }
                                }
                            }, error -> {
                                Log.e("RecoderAdapter", "like:");
                            });
                    break;

            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        ArrayList<String> urls;

        public MyAdapter(Context context, ArrayList<String> urls) {
            this.urls = urls;
        }

        @Override
        public int getCount() {
            return urls.size() > 9 ? 9 : urls.size();
        }

        @Override
        public Object getItem(int position) {
            return urls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.item_image, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
            int width = Remember.getInt("width", 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            iv.setLayoutParams(params);
            GlideUtil.displayImage(urls.get(position), iv);
//            iv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FragmentBridgeActivity.openBigimageFragment(v.getContext(), urls, position);
//                }
//            });
            return view;
        }
    }
}

