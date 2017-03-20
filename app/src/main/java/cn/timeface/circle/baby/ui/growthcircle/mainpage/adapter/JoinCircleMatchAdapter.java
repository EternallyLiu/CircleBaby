package cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.views.CircleCardView;

public class JoinCircleMatchAdapter extends BaseRecyclerAdapter<GrowthCircleObj> {

    public JoinCircleMatchAdapter(Context mContext, List<GrowthCircleObj> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_circle_card_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        GrowthCircleObj item = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;

        holder.circleCardView.setShowJoinType(false);
        holder.circleCardView.setContent(item);
        holder.circleCardView.setTag(R.string.tag_obj, item);
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
        @Bind(R.id.circleCardView)
        CircleCardView circleCardView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
