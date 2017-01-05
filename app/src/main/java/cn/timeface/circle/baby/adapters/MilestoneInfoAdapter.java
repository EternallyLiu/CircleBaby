package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TimeLineDetailActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class MilestoneInfoAdapter extends BaseRecyclerAdapter<TimeLineObj> {

    private View.OnClickListener onClickListener;

    public MilestoneInfoAdapter(Context mContext, List<TimeLineObj> listData) {
        super(mContext, listData);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_milestoneinfo, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        TimeLineObj item = getItem(position);
        holder.timeLineObj = item;
        holder.context = mContext;
        int width = Remember.getInt("width", 0) * 3;
        ViewGroup.LayoutParams layoutParams = holder.ivCover.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = (int) (width * 0.6);
        holder.ivCover.setLayoutParams(layoutParams);
        holder.rlMilestone.setLayoutParams(layoutParams);
        holder.tvTime.setText(DateUtil.getYear2(item.getDate()));
        GlideUtil.displayImage(item.getMediaList().get(0).getImgUrl(), holder.ivCover);
        if(item.getType()==1){
            holder.ivVideo.setVisibility(View.VISIBLE);
        }else{
            holder.tvCount.setVisibility(View.VISIBLE);
            holder.tvCount.setText(item.getMediaList().size()+"");
        }

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
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.iv_video)
        ImageView ivVideo;
        @Bind(R.id.rl_milestone)
        RelativeLayout rlMilestone;
        @Bind(R.id.tv_count)
        TextView tvCount;
        TimeLineObj timeLineObj;
        Context context;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TimeLineDetailActivity.open(context,timeLineObj);
        }
    }
}
