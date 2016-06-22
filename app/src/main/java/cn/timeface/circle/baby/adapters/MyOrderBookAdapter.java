package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.views.PrintItemView;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class MyOrderBookAdapter extends BaseRecyclerAdapter<MyOrderBookItem> {


    public MyOrderBookAdapter(Context context, List<MyOrderBookItem> orderBookItems) {
        super(context, orderBookItems);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_order_book_list, viewGroup, false);
        return new OrderBookItemHolder(itemView);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        OrderBookItemHolder itemHolder = (OrderBookItemHolder) viewHolder;
        MyOrderBookItem myOrderBookItem = listData.get(position);
        itemHolder.tvBookTitle.setText(myOrderBookItem.getBookName());
        List<PrintPropertyPriceObj> printList = myOrderBookItem.getPrintList();
        for (PrintPropertyPriceObj priceObj : printList) {
            View printItemView = getPrintItemView(myOrderBookItem, priceObj);
            itemHolder.flPrintContainer.addView(printItemView);
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

    private View getPrintItemView(MyOrderBookItem orderBookItem, PrintPropertyPriceObj propertyPriceObj) {
        PrintItemView printItemView = new PrintItemView(mContext);
        printItemView.setupViewData(orderBookItem, propertyPriceObj);
        return printItemView;
    }


    class OrderBookItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_book_title)
        TextView tvBookTitle;
        @Bind(R.id.fl_print_container)
        LinearLayout flPrintContainer;

        public OrderBookItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
