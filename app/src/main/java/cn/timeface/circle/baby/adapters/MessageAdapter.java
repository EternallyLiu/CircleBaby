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
import cn.timeface.circle.baby.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.api.models.objs.Msg;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class MessageAdapter extends BaseRecyclerAdapter<Msg> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;

    public MessageAdapter(Context mContext, List<Msg> listData) {
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
        Msg info = getItem(position);
        holder.onClickListener = onClickListener;
        holder.info = info;
        GlideUtil.displayImage(info.getUserInfo().getAvatar(), holder.ivAvatar);
        holder.tvRelation.setText(info.getUserInfo().getRelationName());
        holder.tvTime.setText(DateUtil.getDisTime(info.getTime()));
        if (info.getTimeInfo().getMediaList() == null || info.getTimeInfo().getMediaList().size() < 1) {
            holder.ivContent.setVisibility(View.GONE);
        } else {
            holder.ivContent.setVisibility(View.VISIBLE);
            GlideUtil.displayImage(info.getTimeInfo().getMediaList().get(0).getImgUrl(), holder.ivContent);
        }
        switch (info.getType()) {
            case 0:
                break;
            case 1:
                holder.tvContent.setText("赞了一下");
                break;
            case 2:
                holder.tvContent.setText("添加了" + info.getUserInfo().getBabyObj().getName() + "的新动态");
                break;
            case 3:
                holder.tvContent.setText("评论：" + info.getContent());
                break;
            case 4:
                holder.tvContent.setText("新加入宝宝时光，关注" + info.getUserInfo().getBabyObj().getName() + "的成长");
                break;

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

        View.OnClickListener onClickListener = null;
        Msg info;

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
