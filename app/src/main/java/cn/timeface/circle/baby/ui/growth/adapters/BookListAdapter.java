package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioFrameLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.utils.DateUtil;

/**
 * book list adapter
 * author : YW.SUN Created on 2017/1/13
 * email : sunyw10@gmail.com
 */
public class BookListAdapter extends BaseRecyclerAdapter<BookObj> {
    View.OnClickListener clickListener;

    public BookListAdapter(Context mContext, List<BookObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_book_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final BookObj bookObj = listData.get(position);

        Glide.with(mContext)
                .load(bookObj.getBookCover())
                .into(holder.ivBookCover);
        holder.tvTitle.setText(bookObj.getBookName());
        holder.tvPagenum.setText(String.valueOf(bookObj.getPageNum()));
        holder.tvCreattime.setText(DateUtil.getYear2(bookObj.getCreateTime()));
        if(clickListener != null ) {
            holder.ivMenu.setOnClickListener(clickListener);
            holder.tvPrint.setOnClickListener(clickListener);
            holder.ivMenu.setTag(R.string.tag_obj, bookObj);
            holder.tvPrint.setTag(R.string.tag_obj, bookObj);
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
        @Bind(R.id.iv_book_cover)
        ImageView ivBookCover;
        @Bind(R.id.iv_menu)
        ImageView ivMenu;
        @Bind(R.id.tv_pagenum)
        TextView tvPagenum;
        @Bind(R.id.tv_creattime)
        TextView tvCreattime;
        @Bind(R.id.tv_edit)
        TextView tvEdit;
        @Bind(R.id.tv_print)
        TextView tvPrint;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.fl_book_cover)
        RatioFrameLayout flBookCover;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
