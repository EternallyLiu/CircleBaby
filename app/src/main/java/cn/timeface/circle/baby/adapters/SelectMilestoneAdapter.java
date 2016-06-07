package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.api.models.objs.Relationship;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class SelectMilestoneAdapter extends BaseRecyclerAdapter<Milestone> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;

    public SelectMilestoneAdapter(Context mContext, List<Milestone> listData) {
        super(mContext, listData);

    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_relationship, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (ViewHolder) viewHolder;
        Milestone item = getItem(position);
        holder.tvRelationship.setText(item.getMilestone());
        holder.onClickListener = onClickListener;
        holder.milestone = item;


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
        @Bind(R.id.tv_relationship)
        TextView tvRelationship;
        View.OnClickListener onClickListener = null;
        Milestone milestone;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            tvRelationship.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            tvRelationship.setTag(R.string.tag_ex, milestone);
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }
}
