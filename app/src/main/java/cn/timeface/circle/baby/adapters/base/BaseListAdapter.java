package cn.timeface.circle.baby.adapters.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rayboot
 * @from 14-3-12 10:24
 * @TODO 基础adapter类，继承该类，子类专注实现getView方法
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    public Context mContext;
    public LayoutInflater mLayoutInflater;
    public List<T> listData;
    public int hashCode;

    public BaseListAdapter(Context context, List<T> listData) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.listData = listData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public List<T> getListData() {
        return listData == null ? new ArrayList<T>() : listData;
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listData == null ? 0 : listData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listData == null ? null : listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

}
