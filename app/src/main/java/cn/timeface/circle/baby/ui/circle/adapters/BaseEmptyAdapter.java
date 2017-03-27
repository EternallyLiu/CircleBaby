package cn.timeface.circle.baby.ui.circle.adapters;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseViewHolder;
import cn.timeface.circle.baby.ui.timelines.adapters.EmptyItem;

/**
 * author : wangshuai Created on 2017/3/27
 * email : wangs1992321@gmail.com
 */
public abstract class BaseEmptyAdapter extends BaseAdapter {
    public static final int EMPTY_CODE = -0xeeffffee;

    private EmptyItem emptyItem;

    public BaseEmptyAdapter(Context activity) {
        super(activity);
    }

    protected void handleMsg(Message msg) {
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
                        if (msg.arg1 != getRealItemSize())
                            notifyItemRangeChanged(msg.arg1, getRealItemSize() - msg.arg1);
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
//                if (msg.arg1 != getRealItemSize())
//                    notifyItemRangeChanged(msg.arg1, getRealItemSize() - msg.arg1);
                break;
        }
        addEmpty();
        if (getLoadDataFinish() != null)
            getLoadDataFinish().loadFinish(msg.what);
    }

    public void addEmpty() {
        addEmpty(0);
    }

    private void addEmpty(int size) {
        if (getEmptyItem() != null && containObj(getEmptyItem())) {
            if (getRealItemSize() > size + 1) deleteItem(getEmptyItem());
        } else if (getEmptyItem() != null && !containObj(getEmptyItem())) {
            if (getRealItemSize() <= size) add(getEmptyItem());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getEmptyItem() != null && getItem(position) == getEmptyItem()) {
            return EMPTY_CODE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY_CODE) {
            return new BaseViewHolder(inflater.inflate(R.layout.footer_circle_dynamic_list,
                    parent, false), this);
        } else
            return new BaseViewHolder(inflater.inflate(getViewLayoutID(viewType),
                    parent, false), this);
    }

    public EmptyItem getEmptyItem() {
        return emptyItem;
    }

    public void setEmptyItem(EmptyItem emptyItem) {
        this.emptyItem = emptyItem;
        add(this.emptyItem);
    }
}