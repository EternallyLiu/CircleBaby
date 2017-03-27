package cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

public class RecommendCoverAdapter extends BaseRecyclerAdapter<MediaObj> {

    public RecommendCoverAdapter(Context mContext, List<MediaObj> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_circle_recommend_cover, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        MediaObj item = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;

        Glide.with(mContext)
                .load(item.getImgUrl())
                .centerCrop()
                .into(holder.ivCover);

        holder.ivCover.setTag(R.string.tag_obj, item);
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
        @Bind(R.id.iv_cover)
        RatioImageView ivCover;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
