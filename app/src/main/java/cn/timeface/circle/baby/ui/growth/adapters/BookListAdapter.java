package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioFrameLayout;
import com.github.rayboot.widget.ratioview.utils.RatioFixMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.DeviceUtil;

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

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                DeviceUtil.dpToPx(mContext.getResources(), 120),
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(DeviceUtil.dpToPx(mContext.getResources(), 16), 0, 0, 0);
        holder.flBookCover.setLayoutParams(lp);

        //精装照片书
        if(bookObj.getBookType() == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK){
            holder.flBookCover.setRatio(RatioFixMode.FIX_WIDTH, 1, 1);
        //成长纪念册
        } else if(bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK){
            lp.width = DeviceUtil.dpToPx(mContext.getResources(), 100);
            holder.flBookCover.setRatio(RatioFixMode.FIX_WIDTH, 344, 550);
        //成长语录
        } else if(bookObj.getBookType() == BookModel.BOOK_TYPE_GROWTH_QUOTATIONS){
            holder.flBookCover.setRatio(RatioFixMode.FIX_WIDTH, 1, 1);
        //绘画集
        } else if(bookObj.getBookType() == BookModel.BOOK_TYPE_PAINTING){
            lp.width = DeviceUtil.dpToPx(
                    mContext.getResources(),
                    150);
            holder.flBookCover.setRatio(RatioFixMode.FIX_WIDTH, 680, 524);
        } else {
            holder.flBookCover.setRatio(RatioFixMode.FIX_WIDTH, 1, 1);
        }

        Glide.with(mContext)
                .load(bookObj.getBookCover())
                .into(holder.ivBookCover);
        holder.tvTitle.setText(bookObj.getBookName());
        holder.tvPagenum.setText(String.valueOf(bookObj.getPageNum()));
        holder.tvCreattime.setText(DateUtil.getYear2(bookObj.getCreateTime()));
        if(clickListener != null ) {
            holder.ivMenu.setOnClickListener(clickListener);
            holder.tvPrint.setOnClickListener(clickListener);
            holder.flBookCover.setOnClickListener(clickListener);

            holder.flBookCover.setTag(R.string.tag_obj, bookObj);
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
