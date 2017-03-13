package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.support.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.views.NoScrollListView;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class MyOrderConfirmAdapter extends BaseRecyclerAdapter<MyOrderBookItem> {

    private List<MyOrderPrintAdapter> adapterList;

    public MyOrderConfirmAdapter(Context context, List<MyOrderBookItem> listData) {
        super(context, listData);
        adapterList = new ArrayList<>();
        for (MyOrderBookItem item : listData) {
            MyOrderPrintAdapter adapter = new MyOrderPrintAdapter(mContext, item);
            adapterList.add(adapter);
        }
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_my_order_book_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.setIsRecyclable(true);
        return viewHolder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        MyOrderBookItem item = listData.get(position);

        if (item.getChildNum() > 0) {
            holder.tvBookTitle.setText(
                    Html.fromHtml(String.format(mContext.getResources().getString(R.string.html_content8),
                            item.getBookName(), " (已拆分为" + item.getChildNum() + "本)"))
            );
        } else {
            holder.tvBookTitle.setText(item.getBookName());
        }

        Drawable drawable;
//        switch (item.getBookType()) {
//            case TypeConstant.BOOK_TYPE_TIME:
//                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_flag_time_book);
//                break;
//            case TypeConstant.BOOK_TYPE_WECHAT:
//                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_flag_wechat_time_book);
//                break;
//            case TypeConstant.BOOK_TYPE_QQ:
//                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_flag_qq_time_book);
//                break;
//            case TypeConstant.BOOK_TYPE_BLOG:
//                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_flag_blog_time_book);
//                break;
//
//            case TypeConstant.BOOK_TYPE_DESK_CALENDAR:
//                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_flag_calendar_time_book);
//                break;
//            default:
                drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_flag_time_book);
//                break;
//        }

        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.tvBookTitle.setCompoundDrawables(drawable, null, null, null);

        if (adapterList != null && adapterList.size() > 0) {
            holder.listView.setAdapter(adapterList.get(position));
        }

        holder.dividerView.setVisibility(position != listData.size() - 1 ?
                View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    public void updateDataset(List<MyOrderBookItem> listData) {
        adapterList.clear();
        for (MyOrderBookItem item : listData) {
            MyOrderPrintAdapter adapter = new MyOrderPrintAdapter(mContext, item);
            adapterList.add(adapter);
        }
        notifyDataSetChanged();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_my_order_book_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_book_title)
        TextView tvBookTitle;
        @Bind(R.id.listView)
        NoScrollListView listView;
        @Bind(R.id.divider_view)
        View dividerView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
