package cn.timeface.open.adapters.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;


/**
 * @author YW.SUN
 * @from 2015/8/4
 * @TODO
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<T> listData;
    private Context context;

    public BaseRecyclerAdapter(Context context, List<T> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void clearData() {
        this.listData.clear();
    }

    public void setListData(List<T> data) {
        clearData();
        this.listData.addAll(data);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
