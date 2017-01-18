package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.ui.growth.beans.PrintGrowthHomeObj;

/**
 * 印成长首页adapter
 * author : YW.SUN Created on 2017/1/11
 * email : sunyw10@gmail.com
 */
public class PrintGrowthHomeAdapter extends BaseRecyclerAdapter<PrintGrowthHomeObj> {
    View.OnClickListener clickListener;

    public PrintGrowthHomeAdapter(Context mContext, List<PrintGrowthHomeObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_print_growth_home, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final PrintGrowthHomeObj printGrowthHomeObj = listData.get(position);
        Glide.with(mContext)
                .load(printGrowthHomeObj.getUrl())
                .placeholder(R.drawable.bg_default_holder_img)
                .centerCrop()
                .into(holder.ivBook);
        holder.tvDesc.setText(printGrowthHomeObj.getDesc());
        holder.tvSize.setText(printGrowthHomeObj.getSize());
        holder.tvTitle.setText(BookModel.getGrowthBookName(printGrowthHomeObj.getBookType()));
        holder.rlRoot.setTag(R.string.tag_obj, printGrowthHomeObj);
        if(clickListener != null){
            holder.rlRoot.setOnClickListener(clickListener);
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
        @Bind(R.id.iv_book)
        RatioImageView ivBook;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_desc)
        TextView tvDesc;
        @Bind(R.id.tv_size)
        TextView tvSize;
        @Bind(R.id.rl_root)
        RelativeLayout rlRoot;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
