package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;

/**
 * 卡片 list adapter
 * author : YW.SUN Created on 2017/1/19
 * email : sunyw10@gmail.com
 */
public class CardListAdapter extends BaseRecyclerAdapter<KnowledgeCardObj> {

    public CardListAdapter(Context mContext, List<KnowledgeCardObj> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_card_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final KnowledgeCardObj cardObj = listData.get(position);

        holder.tvTitle.setText(cardObj.getMedia().getContent());
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
        @Bind(R.id.tv_title_pinyin)
        TextView tvTitlePinyin;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.iv_select)
        ImageView ivSelect;
        @Bind(R.id.iv_img)
        ImageView ivImg;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
