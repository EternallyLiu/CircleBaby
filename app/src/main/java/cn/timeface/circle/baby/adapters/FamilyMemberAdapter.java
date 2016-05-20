package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class FamilyMemberAdapter extends BaseRecyclerAdapter<FamilyMemberInfo> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;

    public FamilyMemberAdapter(Context mContext, List<FamilyMemberInfo> listData) {
        super(mContext, listData);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_family_member, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (ViewHolder) viewHolder;
        FamilyMemberInfo info = getItem(position);
        holder.onClickListener = onClickListener;
        holder.info = info;
        GlideUtil.displayImage(info.getUserInfo().getAvatar(),holder.ivAvatar);
        holder.tvName.setText(info.getUserInfo().getNickName());
        holder.tvCount.setText("来过" + info.getCount() + "次");
        holder.tvTime.setText("最近："+ DateUtil.formatDate(info.getTime()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBridgeActivity.openFamilyMemberInfoFragment(mContext,info.getUserInfo());
            }
        });
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
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_count)
        TextView tvCount;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.rl_familymember)
        RelativeLayout rlFamilymember;

        View.OnClickListener onClickListener = null;
        FamilyMemberInfo info;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            rlFamilymember.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            rlFamilymember.setTag(R.string.tag_ex, info);
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }
}
