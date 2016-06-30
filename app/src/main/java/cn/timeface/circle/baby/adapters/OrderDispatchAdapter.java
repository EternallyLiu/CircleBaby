package cn.timeface.circle.baby.adapters;

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
import cn.timeface.circle.baby.api.models.objs.PrintParamObj;

/**
 * @author YW.SUN
 * @from 2015/9/29
 * @TODO
 */
public class OrderDispatchAdapter extends RecyclerView.Adapter<OrderDispatchAdapter.ViewHolder> {

    private List<PrintParamObj> dataList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int dispatchPosition = 0;

    public OrderDispatchAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setDataList(List<PrintParamObj> list) {
        this.dataList = list;
    }

    public List<PrintParamObj> getDataList() {
        return dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_order_dispatch, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PrintParamObj obj = dataList.get(position);
        String[] showArray = obj.getShow().split(",");
        holder.mTvDispatch.setText(showArray[0] + ":");
        holder.mTvDispatchFee.setText(String.format(mContext.getString(R.string.total_price), Float.parseFloat(showArray[1])));
        holder.mLlDispatch.setTag(R.string.tag_index, position);
        holder.mLlDispatch.setTag(R.string.tag_ex, Float.parseFloat(showArray[1]));

        if (position == dispatchPosition) {
            holder.mLlDispatch.setBackgroundResource(R.drawable.shape_dash_orange_bg);
            holder.mTvDispatchFee.setSelected(true);
            holder.mTvDispatch.setSelected(true);
        } else {
            holder.mLlDispatch.setBackgroundResource(R.drawable.shape_grey_dash_border_bg);
            holder.mTvDispatchFee.setSelected(false);
            holder.mTvDispatch.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void notifyDate(int position) {
        this.dispatchPosition = position;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_dispatch)
        LinearLayout mLlDispatch;
        @Bind(R.id.tv_dispatch)
        TextView mTvDispatch;
        @Bind(R.id.tv_dispatch_fee)
        TextView mTvDispatchFee;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
