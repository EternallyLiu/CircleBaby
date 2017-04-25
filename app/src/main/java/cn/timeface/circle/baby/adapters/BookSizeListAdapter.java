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
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.managers.listeners.OnItemClickListener;
import cn.timeface.circle.baby.support.api.models.objs.CardBookSizeObj;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;

/**
 * Created by lidonglin on 2016/7/7.
 */
public class BookSizeListAdapter extends BaseRecyclerAdapter<CardBookSizeObj> {

    private View.OnClickListener onClickListener;
    private OnItemClickListener<Integer> onItemClickListener;

    public BookSizeListAdapter(Context mContext, List<CardBookSizeObj> listData) {
        super(mContext, listData);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_booksize, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        CardBookSizeObj obj = getItem(position);
        holder.onClickListener = onClickListener;
        holder.obj = obj;
        holder.context = mContext;
        GlideUtil.displayImage(obj.getImgList().get(0).getImgUrl(), holder.iv);
        holder.tvTitle.setText(obj.getCoverTitle());
        holder.tvPagenum.setText("打印需要" + obj.getBookPage() + "张");
        holder.tvSize.setText("尺寸：" + obj.getHeight() + "×" + obj.getWidth() + "cm");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.clickItem(position);
                }
            }
        });

        int width = Remember.getInt("width", 0)*3;
        ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = (int) (width*0.58);
        holder.iv.setLayoutParams(layoutParams);
        holder.ivCover.setLayoutParams(layoutParams);
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
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_pagenum)
        TextView tvPagenum;
        @Bind(R.id.tv_size)
        TextView tvSize;
        Context context;

        View.OnClickListener onClickListener = null;
        CardBookSizeObj obj;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
//            itemView.setOnClickListener(this);
        }

    }


    public void setOnItemClickListener(OnItemClickListener<Integer> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
