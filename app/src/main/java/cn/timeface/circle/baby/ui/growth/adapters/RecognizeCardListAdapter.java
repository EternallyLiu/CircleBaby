package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;
import com.github.rayboot.widget.ratioview.utils.RatioFixMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;

/**
 * 识图卡片list adapter
 * author : YW.SUN Created on 2017/1/22
 * email : sunyw10@gmail.com
 */
public class RecognizeCardListAdapter extends BaseRecyclerAdapter<KnowledgeCardObj> {
    View.OnClickListener clickListener;
    List<CardObj> selectCards;

    public RecognizeCardListAdapter(Context mContext, List<KnowledgeCardObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_card_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        RecognizeCardListAdapter.ViewHolder holder = (RecognizeCardListAdapter.ViewHolder) viewHolder;
        final CardObj cardObj = listData.get(position);
        holder.ivImg.setRatio(RatioFixMode.FIX_WIDTH, 1, 1);
        Glide.with(mContext)
                .load(cardObj.getMedia().getImgUrl())
                .into(holder.ivImg);
        holder.ivSelect.setSelected(cardObj.select());
        holder.ivSelect.setTag(R.string.tag_obj, cardObj);
        holder.ivSelect.setTag(R.string.tag_index, position);
        holder.flRoot.setTag(R.string.tag_obj, cardObj);
        if(clickListener != null) {
            holder.ivSelect.setOnClickListener(clickListener);
            holder.flRoot.setOnClickListener(clickListener);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_img)
        RatioImageView ivImg;
        @Bind(R.id.iv_select)
        ImageView ivSelect;
        @Bind(R.id.fl_root)
        FrameLayout flRoot;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
