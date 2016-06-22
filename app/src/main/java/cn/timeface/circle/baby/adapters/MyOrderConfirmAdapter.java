package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.MyOrderBookItem;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class MyOrderConfirmAdapter extends BaseRecyclerAdapter<MyOrderBookItem> {
    public MyOrderConfirmAdapter(Context mContext, List<MyOrderBookItem> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_my_order_book_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {

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
        @Bind(R.id.tv_book_title)
        TextView tvBookTitle;
        @Bind(R.id.recyclerViewBook)
        RecyclerView listView;
        @Bind(R.id.divider_view)
        View dividerView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
