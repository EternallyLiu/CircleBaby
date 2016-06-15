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
import cn.timeface.circle.baby.api.models.objs.Msg;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class AddBookListAdapter extends BaseRecyclerAdapter<BookTypeListObj> {

    private ViewHolder holder;
    private View.OnClickListener onClickListener;

    public AddBookListAdapter(Context mContext, List<BookTypeListObj> listData) {
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
        BookTypeListObj obj = getItem(position);
        holder.onClickListener = onClickListener;
        holder.obj = obj;
        holder.context = mContext;
        GlideUtil.displayImage(obj.getImgList().get(0).getImgUrl(), holder.iv);
        holder.tvTitle.setText(obj.getCoverTitle());

    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        Context context;

        View.OnClickListener onClickListener = null;
        BookTypeListObj obj;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FragmentBridgeActivity.openAddBookFragment(context, obj);
        }
    }
}
