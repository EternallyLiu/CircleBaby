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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.mvp.model.CalendarModel;
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

        int imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 120);
        switch (bookObj.getBookType()) {
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK://精装照片书
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS://成长语录
                imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 120);
                break;
            case BookModel.BOOK_TYPE_PAINTING://绘画集
                imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 130);
                break;
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK://成长纪念册
                imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 100);
                break;
            case BookModel.BOOK_TYPE_CALENDAR://台历
                imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 100);
                break;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                imageWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        holder.ivBookCover.setLayoutParams(lp);
        holder.ivBookCover.setMaxWidth(imageWidth);


        if (bookObj.getBookType() == BookModel.BOOK_TYPE_CALENDAR) {
            holder.ivBookCover.setImageResource(bookObj.getOpenBookType() == CalendarModel.BOOK_TYPE_CALENDAR_HORIZONTAL
                    ? R.drawable.bg_calendar_horizontal : R.drawable.bg_calendar_vertical);

            holder.ivMask.setVisibility(View.GONE);
            holder.tvPagenum.setVisibility(View.GONE);
            holder.tvAuthor.setVisibility(View.GONE);
            holder.tvCreattime.setText("最后编辑: " + DateUtil.getYear2(bookObj.getCreateTime()));
        } else {
            Glide.with(mContext)
                    .load(bookObj.getBookCover())
                    .into(holder.ivBookCover);
            holder.ivMask.setVisibility(View.VISIBLE);
            holder.tvPagenum.setVisibility(View.VISIBLE);
            holder.tvAuthor.setVisibility(bookObj.showAuthor() ? View.VISIBLE : View.GONE);
            holder.tvCreattime.setText("创建时间: " + DateUtil.getYear2(bookObj.getCreateTime()));
        }

        holder.tvTitle.setText(bookObj.getBookName());
        holder.tvPagenum.setText("页      数: " + String.valueOf(bookObj.getPageNum()));
        holder.tvAuthor.setText("作      者: " + (bookObj.getAuthor() != null ? bookObj.getAuthor().getNickName() : ""));

        if (clickListener != null) {
            holder.ivMenu.setOnClickListener(clickListener);
            holder.tvPrint.setOnClickListener(clickListener);
            holder.rlBookCover.setOnClickListener(clickListener);
            holder.tvEdit.setOnClickListener(clickListener);

            holder.rlBookCover.setTag(R.string.tag_obj, bookObj);
            holder.ivMenu.setTag(R.string.tag_obj, bookObj);
            holder.tvPrint.setTag(R.string.tag_obj, bookObj);
            holder.tvEdit.setTag(R.string.tag_obj, bookObj);
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
        @Bind(R.id.tv_author)
        TextView tvAuthor;
        @Bind(R.id.tv_creattime)
        TextView tvCreattime;
        @Bind(R.id.tv_edit)
        TextView tvEdit;
        @Bind(R.id.tv_print)
        TextView tvPrint;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.rl_book_cover)
        RelativeLayout rlBookCover;
        @Bind(R.id.iv_front_mask)
        ImageView ivMask;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
