package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.CardBookSizeObj;
import cn.timeface.circle.baby.managers.listeners.OnItemClickListener;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.views.dialog.LittleWindow;

/**
 * Created by lidonglin on 2016/7/7.
 */
public class BookSizeListAdapter extends BaseRecyclerAdapter<CardBookSizeObj> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;
    private OnItemClickListener onItemClickListener;

    public BookSizeListAdapter(Context mContext, List<CardBookSizeObj> listData) {
        super(mContext, listData);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_addbooklisttype, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (ViewHolder) viewHolder;
        CardBookSizeObj obj = getItem(position);
        holder.onClickListener = onClickListener;
        holder.obj = obj;
        holder.context = mContext;
        GlideUtil.displayImage(obj.getImgList().get(0).getImgUrl(), holder.iv);
        holder.tvTitle.setText(obj.getCoverTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.clickItem(obj.getBookSizeId());
                }
            }
        });
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        Context context;

        View.OnClickListener onClickListener = null;
        CardBookSizeObj obj;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
//            itemView.setOnClickListener(this);
        }

    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}