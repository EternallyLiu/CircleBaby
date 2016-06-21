package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.MyOrderBookItem;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class MyOrderBookAdapter extends RecyclerView.Adapter {
    private final List<MyOrderBookItem> orderBookItems;
    private final Context context;


    public MyOrderBookAdapter(Context context, List<MyOrderBookItem> orderBookItems) {
        this.context = context;
        this.orderBookItems = orderBookItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_order_book_list, parent, false);
        return new OrderBookItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderBookItemHolder itemHolder = (OrderBookItemHolder) holder;
        MyOrderBookItem myOrderBookItem = orderBookItems.get(position);
    }

    @Override
    public int getItemCount() {
        return orderBookItems.size();
    }

    class OrderBookItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_book_title)
        TextView tvBookTitle;
        @Bind(R.id.iv_book_cover)
        RatioImageView ivBookCover;
        @Bind(R.id.fl_cover)
        FrameLayout flCover;
        @Bind(R.id.tv_size)
        TextView tvSize;
        @Bind(R.id.tv_color)
        TextView tvColor;
        @Bind(R.id.tv_paper)
        TextView tvPaper;
        @Bind(R.id.tv_pack)
        TextView tvPack;
        @Bind(R.id.tv_price)
        TextView tvPrice;
        @Bind(R.id.tv_number)
        TextView tvNumber;
        @Bind(R.id.ll_price_number)
        LinearLayout llPriceNumber;
        @Bind(R.id.ll_root)
        LinearLayout llRoot;

        public OrderBookItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
