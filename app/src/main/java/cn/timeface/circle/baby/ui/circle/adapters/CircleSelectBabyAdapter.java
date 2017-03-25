package cn.timeface.circle.baby.ui.circle.adapters;

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
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.bean.GetCircleAllBabyObj;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleBabyBriefObj;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 圈作品选择作业adapter
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */

public class CircleSelectBabyAdapter extends BaseRecyclerAdapter<CircleBabyBriefObj> {
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_select_count)
    TextView tvSelectCount;

    View.OnClickListener onClickListener;

    public CircleSelectBabyAdapter(Context mContext, List<CircleBabyBriefObj> listData, View.OnClickListener onClickListener) {
        super(mContext, listData);
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_circle_select_baby, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final CircleBabyBriefObj babyObj = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tvName.setText(babyObj.getBabyName());
        holder.tvSelectCount.setText("已选择10条记录");
        if(onClickListener != null)holder.rlRoot.setOnClickListener(onClickListener);
        holder.rlRoot.setTag(R.string.tag_obj, babyObj);
        GlideUtil.displayImage(babyObj.getBabyAvatarUrl(), holder.ivAvatar);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_select_count)
        TextView tvSelectCount;
        @Bind(R.id.rl_root)
        RelativeLayout rlRoot;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
