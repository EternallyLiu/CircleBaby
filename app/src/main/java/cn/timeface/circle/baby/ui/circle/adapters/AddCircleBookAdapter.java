package cn.timeface.circle.baby.ui.circle.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.ui.circle.photo.bean.CircleBookTypeObj;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 新加圈作品adapter
 * Created by lidonglin on 2017/3/22.
 */
public class AddCircleBookAdapter extends BaseRecyclerAdapter<CircleBookTypeObj> {
    View.OnClickListener clickListener;

    public AddCircleBookAdapter(Context mContext, List<CircleBookTypeObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_add_circle_book, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final CircleBookTypeObj obj = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tvTitle.setText(obj.getTitle());
        holder.tvDesc.setText(obj.getDescription());
        Glide.with(mContext)
                .load(obj.getIconUrl())
                .into(holder.ivBookCover);
        holder.tvCreat.setTag(R.string.tag_obj, obj);
        holder.tvDetail.setTag(R.string.tag_obj, obj);
        if (clickListener != null) holder.tvCreat.setOnClickListener(clickListener);
        if (clickListener != null) holder.tvDetail.setOnClickListener(clickListener);
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
        @Bind(R.id.iv_book_cover)
        ImageView ivBookCover;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_desc)
        TextView tvDesc;
        @Bind(R.id.tv_creat)
        TextView tvCreat;
        @Bind(R.id.tv_detail)
        TextView tvDetail;
        @Bind(R.id.ll_content)
        LinearLayout llContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
