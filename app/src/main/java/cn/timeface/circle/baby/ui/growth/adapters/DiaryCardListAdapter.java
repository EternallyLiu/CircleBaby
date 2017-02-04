package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;
import com.github.rayboot.widget.ratioview.utils.RatioFixMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.DiaryCardObj;

/**
 * 日记卡片list adapter
 * author : YW.SUN Created on 2017/1/22
 * email : sunyw10@gmail.com
 */
public class DiaryCardListAdapter extends BaseRecyclerAdapter<DiaryCardObj> {

    public DiaryCardListAdapter(Context mContext, List<DiaryCardObj> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_card_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        DiaryCardListAdapter.ViewHolder holder = (DiaryCardListAdapter.ViewHolder) viewHolder;
        final CardObj cardObj = listData.get(position);
        holder.ivImg.setRatio(RatioFixMode.FIX_WIDTH, cardObj.getMedia().getW(), cardObj.getMedia().getH());
        Glide.with(mContext)
                .load(cardObj.getMedia().getImgUrl())
                .into(holder.ivImg);
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
