package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> implements ViewHolderInterface, View.OnClickListener {

    private RecyclerView.LayoutParams emptyLayoutParams = null;

    public static final int EMPTY_DATE_VIEW = -999999;

    /**
     * 给数据源中间添加数据
     */
    private static final int UPDATE_DATA_ADD_LIST_CENTER = 1101;

    private static final int DELETE_ALL = -100001;

    /**
     * 删除数据
     */
    private static final int UPDATE_DATA_DELETE_DATA = -1001;
    /**
     * 修改数据
     */
    private static final int UPDATE_DATA_UPDATE_DATA = 1002;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleMsg(msg);
        }
    };

    private void handleMsg(Message msg) {
        Log.i("test", "handleMsg:" + msg.what + "--" + msg.arg1 + "--" + msg.arg2);
        switch (msg.what) {
            case DELETE_ALL:
                if (list != null && list.size() > 0) {
                    list.clear();
                    notifyDataSetChanged();
                }
                break;
            case UPDATE_DATA_ADD_LIST_CENTER:
                if (msg.obj != null) {
                    if (msg.arg1 >= 0 && msg.arg1 <= getRealItemSize() && msg.arg2 > 0) {
                        if (msg.arg2 == 1) list.add(msg.arg1, msg.obj);
                        else list.addAll(msg.arg1, (Collection<Object>) msg.obj);
                        notifyItemRangeInserted(msg.arg1, msg.arg2);
                    }
                }
                break;
            case UPDATE_DATA_DELETE_DATA:
                if (msg.arg1 >= 0 && msg.arg1 < getRealItemSize()) {
                    for (int i = 0; i < msg.arg2; i++) {
                        list.remove(msg.arg1);
                        notifyItemRemoved(msg.arg1);
                    }
                }
                if (msg.arg1 != getRealItemSize())
                    notifyItemRangeChanged(msg.arg1, getRealItemSize() - msg.arg1);
                break;
            case UPDATE_DATA_UPDATE_DATA:
                if (msg.obj != null) {
                    list.remove(msg.arg1);
                    list.add(msg.arg1, msg.obj);
                }
                notifyItemChanged(msg.arg1);
                if (msg.arg1 != getRealItemSize())
                    notifyItemRangeChanged(msg.arg1, getRealItemSize() - msg.arg1);
                break;
        }
        if (getLoadDataFinish()!=null)
            getLoadDataFinish().loadfinish();
    }

    public boolean containObj(Object object){
        return list.contains(object);
    }

    public <T extends Object> T getObj(Object object){
        if (containObj(object))
            return (T) list.get(list.indexOf(object));
        else return null;
    }

    /**
     * 更新item
     *
     * @param itemId
     * @param object
     */
    public void updateItem(int itemId, Object object) {
        if (itemId < 0)
            itemId = 0;
        if (itemId >= getRealItemSize())
            itemId = getRealItemSize() - 1;
        handler.sendMessage(handler.obtainMessage(UPDATE_DATA_UPDATE_DATA, itemId, 0, object));
    }

    public void clearAll() {
        handler.sendMessage(handler.obtainMessage(DELETE_ALL));
    }

    public void addList(int position, List list) {
        if (list == null || list.size() <= 0) return;
        if (position < 0)
            position = 0;
        if (list.size() == 1)
            handler.sendMessage(handler.obtainMessage(UPDATE_DATA_ADD_LIST_CENTER, position, list.size(), list.get(0)));
        else
            handler.sendMessage(handler.obtainMessage(UPDATE_DATA_ADD_LIST_CENTER, position, list.size(), list));
    }

    public void addList(boolean isClear, List list) {
        if (isClear) clearAll();
        addList(isClear ? 0 : getRealItemSize(), list);
    }

    private View emptyDataView = null;

    public void addList(List list) {
        addList(false, list);
    }

    public void addListToBegin(List list) {
        addList(0, list);
    }

    public void deleteItem(int itemId) {
        deleteItem(itemId, 1);
    }

    public void deleteItem(int itemId, int count) {
        if (itemId < 0)
            itemId = 0;
        if (itemId >= getRealItemSize())
            itemId = getRealItemSize() - 1;
        if (count + itemId > 0 && count + itemId <= getRealItemSize())
            handler.sendMessage(handler.obtainMessage(UPDATE_DATA_DELETE_DATA, itemId, count, null));
    }

    private ArrayList list = new ArrayList<>(0);
    protected LayoutInflater inflater = null;

    protected Context activity;

    public BaseAdapter(Context activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
    }

    public int getRealItemSize() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemCount() {
        if (getEmptyDataView() != null)
            return list == null ? 1 : list.size() <= 0 ? 1 : list.size();
        else return getRealItemSize();
    }

    public <T extends Object> T getItem(int position) {
        if (position >= 0 && position < getRealItemSize())
            return (T) list.get(position);
        return null;
    }

    private OnItemClickLister itemClickLister;

    public OnItemClickLister getItemClickLister() {
        return itemClickLister;
    }

    public void setItemClickLister(OnItemClickLister itemClickLister) {
        this.itemClickLister = itemClickLister;
    }

    public View getEmptyDataView() {
        return emptyDataView;
    }

    public void setEmptyDataView(View emptyDataView) {
        this.emptyDataView = emptyDataView;
        if (emptyLayoutParams == null) {
            emptyLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        this.emptyDataView.setLayoutParams(emptyLayoutParams);
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag(R.id.recycler_item_click_tag);
        if (getItemClickLister() != null) {
            getItemClickLister().onItemClick(v, tag);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY_DATE_VIEW) {
            return new BaseViewHolder(getEmptyDataView(), this);
        } else
            return new BaseViewHolder(inflater.inflate(getViewLayoutID(viewType),
                    parent, false), this);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (getItem(position) != null)
            if (holder != null && holder.getListener() != null) {
                holder.getRootView().setTag(R.id.recycler_item_click_tag, position);
                holder.getRootView().setOnClickListener(this);
                holder.getListener().initView(holder.getRootView(), position);
            }

    }


    public abstract @LayoutRes int getViewLayoutID(int viewType);


    public abstract int getViewType(int position);
    @Override
    public int getItemViewType(int position) {
        if (getEmptyDataView() != null && getItem(position) == null)
            return EMPTY_DATE_VIEW;
        return getViewType(position);
    }

    public void error(){
        if (getLoadDataFinish()!=null)
            getLoadDataFinish().loadfinish();
    }

    @Override
    public abstract void initView(View contentView, int position);

    public LoadDataFinish getLoadDataFinish() {
        return loadDataFinish;
    }

    public void setLoadDataFinish(LoadDataFinish loadDataFinish) {
        this.loadDataFinish = loadDataFinish;
    }

    public interface OnItemClickLister {
        public void onItemClick(View view, int position);
    }

    private LoadDataFinish loadDataFinish;

    /**
     * 数据加载完毕之后回调改接口，为了处理数据加载完成之后的操作
     *
     */
    public interface LoadDataFinish {
        public void loadfinish();
    }
}
