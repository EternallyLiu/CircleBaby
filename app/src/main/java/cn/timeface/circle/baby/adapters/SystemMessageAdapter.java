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
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.SystemMsg;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class SystemMessageAdapter extends BaseRecyclerAdapter<SystemMsg> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;

    public SystemMessageAdapter(Context mContext, List<SystemMsg> listData) {
        super(mContext, listData);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_message, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (ViewHolder) viewHolder;
        SystemMsg info = getItem(position);
        holder.onClickListener = onClickListener;
        holder.info = info;
        GlideUtil.displayImage(info.getAvatar(), holder.ivAvatar);
        holder.tvRelation.setVisibility(View.GONE);
        holder.tvTime.setText(DateUtil.getDisTime(info.getTime()));
        holder.tvContent.setText(info.getContent());
        holder.ivContent.setVisibility(View.GONE);

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
        @Bind(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @Bind(R.id.tv_relation)
        TextView tvRelation;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.iv_content)
        ImageView ivContent;
        @Bind(R.id.rl_message)
        RelativeLayout rlMessage;
        @Bind(R.id.iv_dot)
        ImageView ivDot;

        View.OnClickListener onClickListener = null;
        SystemMsg info;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            rlMessage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            rlMessage.setTag(R.string.tag_ex, info);
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }
}
