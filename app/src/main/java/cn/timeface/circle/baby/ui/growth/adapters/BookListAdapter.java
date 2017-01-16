package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;

/**
 * book list adapter
 * author : YW.SUN Created on 2017/1/13
 * email : sunyw10@gmail.com
 */
public class BookListAdapter extends BaseRecyclerAdapter<BookObj> {
    public BookListAdapter(Context mContext, List<BookObj> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        return null;
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
}
