package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.Msg;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class MessageAdapter extends BaseRecyclerAdapter<Msg> {

    private View.OnClickListener onClickListener;

    public MessageAdapter(Context mContext, List<Msg> listData) {
        super(mContext, listData);

    }

    public void setAllRead() {
        for (Msg msg : listData) {
            msg.setIsRead(1);
        }
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
        ViewHolder holder = (ViewHolder) viewHolder;
        Msg info = getItem(position);
        holder.onClickListener = onClickListener;
        holder.info = info;
        GlideUtil.displayImage(info.getUserInfo().getAvatar(), holder.ivAvatar,R.drawable.ic_launcher);
        if (info.getType() == 0) {
            holder.tvRelation.setText("系统消息");
        } else {
            holder.tvRelation.setText(FastData.getBabyNickName() + info.getUserInfo().getRelationName());
        }
        holder.tvTime.setText(DateUtil.formatDate("yyyy-MM-dd kk:mm", info.getTime()));
        if (info.getTimeInfo().getMediaList() == null || info.getTimeInfo().getMediaList().size() < 1) {
            holder.rlContent.setVisibility(View.GONE);
        } else {
            holder.rlContent.setVisibility(View.VISIBLE);
            GlideUtil.displayImage(info.getTimeInfo().getMediaList().get(0).getImgUrl(), holder.ivContent);
            if (info.getTimeInfo().getType() == 1) {
                holder.ivVideo.setVisibility(View.VISIBLE);
            }else{
                holder.ivVideo.setVisibility(View.GONE);
            }
        }
        if (info.getIsRead() == 0) {
            holder.ivDot.setVisibility(View.VISIBLE);
        } else {
            holder.ivDot.setVisibility(View.GONE);
        }
        String content = "";
        switch (info.getType()) {
            case 0:
                content = info.getContent();
                break;
            case 1:
                content = "赞了一下";
                break;
            case 2:
                content = "发布了新动态";
                break;
            case 3:
                content = info.getContent();
                break;
            case 4:
                content = "加入了宝宝印记，关注" + FastData.getBabyNickName() + "成长";
                break;
            default:
                content = info.getContent();
        }
        holder.tvContent.setText(content);
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
        RatioImageView ivContent;
        @Bind(R.id.rl_message)
        RelativeLayout rlMessage;
        @Bind(R.id.iv_dot)
        ImageView ivDot;
        @Bind(R.id.iv_video)
        ImageView ivVideo;
        @Bind(R.id.rl_content)
        RelativeLayout rlContent;

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
