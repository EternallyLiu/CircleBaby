package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TimeLineDetailActivity;
import cn.timeface.circle.baby.activities.VideoPlayActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.CommentObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.events.ActionCallBackEvent;
import cn.timeface.circle.baby.events.CommentSubmit;
import cn.timeface.circle.baby.events.IconCommentClickEvent;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Func1;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class TimeLineAdapter extends BaseRecyclerAdapter<TimeLineObj> {

    private View.OnClickListener onClickListener;
    int normalColor;
    public static Context context;
    private ViewHolder holder;
    public List<TimeLineObj> listData;
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
        holder.context = context;
        TimeLineObj item = getItem(position);
        holder.timeLineObj = item;
        holder.tvContent.setText(item.getContent());
        holder.tvAuthor.setText(item.getAuthor().getRelationName());
        holder.tvDate.setText(DateUtil.formatDate("MM-dd kk:mm",item.getDate()));
        holder.iconLike.setSelected(item.getLike() == 1 ? true : false);
        holder.tvCommentcount.setText(item.getCommentList().size() + "");
        holder.tvLikecount.setText(item.getLikeList().size() + "");

        if (!TextUtils.isEmpty(item.getMilestone())) {
            holder.tvMilestone.setVisibility(View.VISIBLE);
            holder.tvMilestone.setText(item.getMilestone());
        }

        if (item.getMediaList().size() == 1) {
            holder.gv.setVisibility(View.GONE);
            String url = item.getMediaList().get(0).getImgUrl();
            GlideUtil.displayImage(url, holder.ivCover);
        } else {
            holder.rlSingle.setVisibility(View.GONE);
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
                holder.llCommentWrapper.addView(holder.initCommentItemView(commentObj));
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
//            int width = Remember.getInt("width", 0);
//            ViewGroup.LayoutParams layoutParams = holder.ivCover.getLayoutParams();
//            layoutParams.width = width;
//            layoutParams.height = width;
//            holder.ivCover.setLayoutParams(layoutParams);
            holder.ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.rlSingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPlayActivity.open(context, item.getMediaList().get(0).getVideoUrl());
                }
            });
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

    /*private View initCommentItemView(CommentObj comment) {
        View view = mLayoutInflater.inflate(R.layout.view_comment, null);
        TextView tvComment = (TextView) view.findViewById(R.id.tv_comment);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        SpannableStringBuilder msb = new SpannableStringBuilder();
        msb.append(comment.getUserInfo().getRelationName())
                .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), 0, comment.getUserInfo().getRelationName().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//第一个人名
        if (comment.getToUserInfo() != null && !TextUtils.isEmpty(comment.getToUserInfo().getRelationName())) {
            msb.append("回复");
            msb.append(comment.getToUserInfo().getRelationName())
                    .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), msb.length() - comment.getToUserInfo().getRelationName().length(), msb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        msb.append("：").append(comment.getContent());
        tvComment.setText(msb);
        tvTime.setText(DateUtil.formatDate("MM-dd HH:mm", comment.getCommentDate()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context).setView(initCommentMenu(comment)).show();
                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                WindowManager m = window.getWindowManager();
                Display d = m.getDefaultDisplay();
                WindowManager.LayoutParams p = window.getAttributes();

                p.width = d.getWidth();
                window.setAttributes(p);
                window.setGravity(Gravity.BOTTOM);
                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
                window.setWindowAnimations(R.style.bottom_dialog_animation);
            }
        });

        return view;
    }

    public View initCommentMenu(CommentObj comment) {
        View view = View.inflate(context,R.layout.view_comment_menu, null);
        LinearLayout backView = (LinearLayout) view.findViewById(R.id.ll_publish_menu);
        backView.setBackgroundColor(context.getResources().getColor(R.color.trans));
        RelativeLayout tvAction = (RelativeLayout) view.findViewById(R.id.rl_action);
        TextView tv = (TextView) view.findViewById(R.id.tv_action);
        RelativeLayout tvCancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        if (comment.getUserInfo().getUserId().equals(FastData.getUserId())) {
            tv.setText("删除");
        }
        tvAction.setTag(R.string.tag_obj, comment);
        tvAction.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        return view;
    }*/

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        List<TimeLineObj> listData;
        TimeLineObj timeLineObj;
        int position;
        int allDetailsPosition;
        public Context context;

        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.gv)
        GridView gv;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.iv_video)
        ImageView ivVideo;
        @Bind(R.id.rl_single)
        RelativeLayout rlSingle;
        @Bind(R.id.tv_milestone)
        TextView tvMilestone;
        @Bind(R.id.tv_author)
        TextView tvAuthor;
        @Bind(R.id.tv_date)
        TextView tvDate;
        @Bind(R.id.icon_like)
        ImageView iconLike;
        @Bind(R.id.tv_likecount)
        TextView tvLikecount;
        @Bind(R.id.icon_comment)
        ImageView iconComment;
        @Bind(R.id.tv_commentcount)
        TextView tvCommentcount;
        @Bind(R.id.ll_good_list_users_bar)
        LinearLayout llGoodListUsersBar;
        @Bind(R.id.hsv)
        HorizontalScrollView hsv;
        @Bind(R.id.ll_comment_wrapper)
        LinearLayout llCommentWrapper;
        @Bind(R.id.tv_more_comment)
        TextView tvMoreComment;
        @Bind(R.id.commentAnd)
        LinearLayout llCommentLikeWrapper;
        @Bind(R.id.ll_recode)
        LinearLayout llRecode;
        private AlertDialog dialog;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            llRecode.setOnClickListener(this);
            iconLike.setOnClickListener(this);
            iconComment.setOnClickListener(this);
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
                    iconLike.setClickable(false);
                    int p = iconLike.isSelected() == true ? 0 : 1;
                    ApiFactory.getApi().getApiService().like(timeLineObj.getTimeId(), p)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                ToastUtil.showToast(response.getInfo());
                                if (response.success()) {
                                    boolean isContains = false;
                                    if (p == 1) {//之前没有点赞
                                        llCommentLikeWrapper.setVisibility(View.VISIBLE);
                                        listData.get(position).setLike(1);
                                        int likeCount = listData.get(position).getLikeList().size();
                                        listData.get(position).setLikeCount(likeCount + 1);
                                        tvLikecount.setText(likeCount + 1 + "");
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

                                        iconLike.setSelected(true);

                                        hsv.setVisibility(View.VISIBLE);

                                        ImageView imageView = initPraiseItem();
                                        llGoodListUsersBar.addView(imageView);
                                        GlideUtil.displayImage(FastData.getAvatar(), imageView);
                                    } else {

                                        listData.get(position).setLike(0);
                                        int likeCount = listData.get(position).getLikeList().size();
                                        listData.get(position).setLikeCount(likeCount - 1);
                                        tvLikecount.setText(likeCount - 1 + "");
                                        for (UserObj u : listData.get(position).getLikeList()) {
                                            if (u.getUserId().equals(FastData.getUserId())) {
                                                listData.get(position).getLikeList().remove(u);
                                            }
                                        }

                                        iconLike.setSelected(false);

                                        llGoodListUsersBar.removeAllViews();
                                        if (timeLineObj.getLikeCount() == 0) {
                                            hsv.setVisibility(View.GONE);
                                            if (timeLineObj.getCommentCount() == 0) {
                                                llCommentLikeWrapper.setVisibility(View.GONE);
                                            }
                                        } else if (timeLineObj.getLikeCount() == 1 && timeLineObj.getLikeList().get(0).getUserId().equals(FastData.getUserId())) {
                                            hsv.setVisibility(View.GONE);
                                            if (timeLineObj.getCommentCount() == 0) {
                                                llCommentLikeWrapper.setVisibility(View.GONE);
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
                                iconLike.setClickable(true);
                            }, error -> {
                                iconLike.setClickable(true);
                                Log.e("RecoderAdapter", "like:");
                            });
                    break;
                case R.id.icon_comment:
                    EventBus.getDefault().post(new IconCommentClickEvent(allDetailsPosition,position,timeLineObj));
                    break;
                case R.id.rl_action:

                    CommentObj commment = (CommentObj) v.getTag(R.string.tag_obj);
                    dialog.dismiss();
                    if (commment.getUserInfo().getUserId().equals(FastData.getUserId())) {
                        //删除评论操作
                        new AlertDialog.Builder(context)
                                .setTitle("确定删除这条评论吗?")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ApiFactory.getApi().getApiService().delComment(commment.getCommentId())
                                        .filter(new Func1<BaseResponse, Boolean>() {
                                            @Override
                                            public Boolean call(BaseResponse response) {
                                                return response.success();
                                            }
                                        })
                                        .flatMap(baseResponse -> ApiFactory.getApi().getApiService().queryBabyTimeDetail(timeLineObj.getTimeId()))
                                        .compose(SchedulersCompat.applyIoSchedulers())
                                        .subscribe(response -> {
                                            ToastUtil.showToast(response.getInfo());
                                            if (response.success()) {
                                                //重新加载评论列表
                                                timeLineObj = response.getTimeInfo();
                                                if (allDetailsPosition >= 0 && position >= 0) {
                                                    EventBus.getDefault().post(new CommentSubmit(allDetailsPosition, position, timeLineObj));
                                                }
                                            }
                                        }, error -> {
                                            Log.e("timeLineAdapter", "delComment:");
                                            error.printStackTrace();
                                        });
                            }
                        }).show();
                    } else {
                        //回复操作
                        EventBus.getDefault().post( new ActionCallBackEvent(commment,allDetailsPosition,position,timeLineObj));
                    }
                    break;
                case R.id.rl_cancel:
                    dialog.dismiss();
                    break;

            }
        }

        private View initCommentItemView(CommentObj comment) {
            View view = View.inflate(context,R.layout.view_comment, null);
            TextView tvComment = (TextView) view.findViewById(R.id.tv_comment);
            TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
            SpannableStringBuilder msb = new SpannableStringBuilder();
            msb.append(comment.getUserInfo().getRelationName())
                    .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), 0, comment.getUserInfo().getRelationName().length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//第一个人名
            if (comment.getToUserInfo() != null && !TextUtils.isEmpty(comment.getToUserInfo().getRelationName())) {
                msb.append("回复");
                msb.append(comment.getToUserInfo().getRelationName())
                        .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), msb.length() - comment.getToUserInfo().getRelationName().length(), msb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            msb.append("：").append(comment.getContent());
            tvComment.setText(msb);
            tvTime.setText(DateUtil.formatDate("MM-dd kk:mm", comment.getCommentDate()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new AlertDialog.Builder(context).setView(initCommentMenu(comment)).show();
                    dialog.setCanceledOnTouchOutside(true);
                    Window window = dialog.getWindow();
                    WindowManager m = window.getWindowManager();
                    Display d = m.getDefaultDisplay();
                    WindowManager.LayoutParams p = window.getAttributes();

                    p.width = d.getWidth();
                    window.setAttributes(p);
                    window.setGravity(Gravity.BOTTOM);
                    window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
                    window.setWindowAnimations(R.style.bottom_dialog_animation);
                }
            });

            return view;
        }

        public View initCommentMenu(CommentObj comment) {
            View view = View.inflate(context,R.layout.view_comment_menu, null);
            LinearLayout backView = (LinearLayout) view.findViewById(R.id.ll_publish_menu);
            backView.setBackgroundColor(context.getResources().getColor(R.color.trans));
            RelativeLayout tvAction = (RelativeLayout) view.findViewById(R.id.rl_action);
            TextView tv = (TextView) view.findViewById(R.id.tv_action);
            RelativeLayout tvCancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
            if (comment.getUserInfo().getUserId().equals(FastData.getUserId())) {
                tv.setText("删除");
            }
            tvAction.setTag(R.string.tag_obj, comment);
            tvAction.setOnClickListener(this);
            tvCancel.setOnClickListener(this);
            return view;
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
            View view = View.inflate(context, R.layout.item_image_and_count, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
            TextView tvCount = (TextView) view.findViewById(R.id.tv_count);
            if (position == 8 && urls.size() > 9) {
                tvCount.setVisibility(View.VISIBLE);
                tvCount.setText(urls.size() - 9 + "+");
            }
            int width = Remember.getInt("width", 0);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
            iv.setLayoutParams(params);
            tvCount.setLayoutParams(params);
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

