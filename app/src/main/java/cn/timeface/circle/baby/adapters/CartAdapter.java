package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.PrintCartItem;
import cn.timeface.circle.baby.constants.TypeConstant;

/**
 * Created by YW.SUN on 2015/5/17.
 */
public class CartAdapter extends BaseRecyclerAdapter<PrintCartItem> {
    private List<CartPrintPropertyAdapter> adapterList;
    private Map<String, Integer> stateMap = new HashMap<>();

    public CartAdapter(Context context, List<PrintCartItem> listData) {
        super(context, listData);
        adapterList = new ArrayList<>();
        setListData(listData);
    }

    @Override
    public void setListData(List<PrintCartItem> listData) {
        super.setListData(listData);

        Iterator iterator = adapterList.iterator();
        while (iterator.hasNext()) {
            CartPrintPropertyAdapter propertyAdapter = (CartPrintPropertyAdapter) iterator.next();
            stateMap.put(propertyAdapter.getBookId(),
                    propertyAdapter.getPropertyState());
        }
        adapterList.clear();
        for (PrintCartItem item : listData) {
            CartPrintPropertyAdapter adapter = new CartPrintPropertyAdapter(mContext,
                    item.getPrintList(),
                    listData.indexOf(item),
                    item);
            adapter.setPropertyState(stateMap.get(adapter.getBookId()) == null
                    ? CartPrintPropertyAdapter.PROPERTY_STATE_NOMAL
                    : stateMap.get(adapter.getBookId()));
            adapterList.add(adapter);
        }
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_print_cart, null);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final PrintCartItem item = listData.get(position);
        ViewHolder holder = ((ViewHolder) viewHolder);
        if (item.getChildNum() > 0) {
            holder.mTvBookTitle.setText(item.getTitle() + "(已拆分为" + item.getChildNum() + "本)");
        } else {
            holder.mTvBookTitle.setText(item.getTitle());
        }

        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_flag_time_book);

        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.mTvBookTitle.setCompoundDrawables(drawable, null, null, null);


        if (item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS
                || item.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE) {
            holder.mIvItemSelect.setSelected(false);
        } else {
            holder.mIvItemSelect.setSelected(item.isSelect());
        }
        holder.mIvItemSelect.setTag(R.string.tag_obj, item);
        holder.tvEdit.setText(
                adapterList.get(position).getPropertyState() == CartPrintPropertyAdapter.PROPERTY_STATE_EDIT
                        ? mContext.getString(R.string.finish)
                        : mContext.getString(R.string.edit));
        holder.tvEdit.setTag(R.string.tag_index, position);
        holder.tvEdit.setTag(R.string.tag_obj, item);

        if (adapterList.size() > 0) {
            //内层recylerview
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            holder.mLvRecycler.setHasFixedSize(true);
            holder.mLvRecycler.setLayoutManager(layoutManager);
            holder.mLvRecycler.setAdapter(adapterList.get(position));
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

    public void notifyDataDelete() {
        adapterList.clear();
        for (PrintCartItem item : listData) {
            CartPrintPropertyAdapter adapter = new CartPrintPropertyAdapter(mContext,
                    item.getPrintList(),
                    listData.indexOf(item),
                    item);
            adapterList.add(adapter);
        }
    }

    public CartPrintPropertyAdapter getPropertyAdapter(int position) {
        return adapterList.get(position);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_print_cart.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_book_title)
        TextView mTvBookTitle;
        //        @Bind(R.id.tv_print_limit_info) TextView mTvLimitInfo;
        @Bind(R.id.lv_recycler)
        RecyclerView mLvRecycler;
        @Bind(R.id.iv_item_select)
        ImageView mIvItemSelect;
        @Bind(R.id.tv_item_edit)
        TextView tvEdit;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
