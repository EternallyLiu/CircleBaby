package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
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
import cn.timeface.circle.baby.api.models.objs.OrderObj;
import cn.timeface.circle.baby.managers.listeners.OnItemClickListener;

/**
 * Created by zhsheng on 2016/6/20.
 */
public class OrderListAdapter extends BaseRecyclerAdapter<OrderObj> {


    private OnItemClickListener<OrderObj> itemClickListener;

    public OrderListAdapter(Context mContext, List<OrderObj> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_my_order, viewGroup, false);
        return new OrderListViewHolder(itemView);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        OrderListViewHolder orderHolder = (OrderListViewHolder) viewHolder;
        RecyclerView recyclerView = orderHolder.orderBookList;
        OrderObj orderObj = listData.get(position);
        orderHolder.setupData(orderObj);
        setupChildRecyclerView(recyclerView, orderObj.getBookList());
    }

    private void setupChildRecyclerView(RecyclerView recyclerView, List<MyOrderBookItem> orderBookItems) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyOrderBookAdapter(mContext, orderBookItems));
    }

    public void setOnItemClickListener(OnItemClickListener<OrderObj> listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    class OrderListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tv_order_number)
        TextView tvOrderNumber;
        @Bind(R.id.tv_order_status)
        TextView tvOrderStatus;
        @Bind(R.id.order_book_list)
        RecyclerView orderBookList;
        @Bind(R.id.tv_total_price)
        TextView tvTotalPrice;
        @Bind(R.id.tv_express_fee)
        TextView tvExpressFee;
        @Bind(R.id.ll_item_order)
        LinearLayout llItemOrder;
        private OrderObj orderObj;

        public OrderListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.clickItem(orderObj);
        }

        public void setupData(OrderObj orderObj) {
            this.orderObj = orderObj;


        }
    }
}
