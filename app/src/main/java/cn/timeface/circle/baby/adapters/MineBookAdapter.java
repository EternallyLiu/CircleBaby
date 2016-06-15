package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class MineBookAdapter extends BaseRecyclerAdapter<MineBookObj> {


    private ViewHolder holder;
    private View.OnClickListener onClickListener;

    public MineBookAdapter(Context mContext, List<MineBookObj> listData) {
        super(mContext, listData);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_minebook, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = (ViewHolder) viewHolder;
        MineBookObj obj = getItem(position);
        holder.onClickListener = onClickListener;
        holder.obj = obj;
        holder.context = mContext;
        GlideUtil.displayImage(obj.getCoverImage(), holder.ivBook);
        holder.tvTitle.setText(obj.getTitle());
        holder.tvPagecount.setText(obj.getPageCount());
        holder.tvCreattime.setText(DateUtil.getYear2(obj.getCreateTime()));

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
        @Bind(R.id.iv_book)
        ImageView ivBook;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_pagecount)
        TextView tvPagecount;
        @Bind(R.id.tv_creattime)
        TextView tvCreattime;
        @Bind(R.id.tv_edit)
        TextView tvEdit;
        @Bind(R.id.tv_print)
        TextView tvPrint;
        @Bind(R.id.ll_menu)
        LinearLayout llMenu;
        Context context;

        View.OnClickListener onClickListener = null;
        MineBookObj obj;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
            llMenu.setOnClickListener(this);
            tvEdit.setOnClickListener(this);
            tvPrint.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_menu:
                    llMenu.getChildAt(0).setPressed(true);
                    llMenu.getChildAt(1).setPressed(true);
                    llMenu.getChildAt(2).setPressed(true);

                    View view = View.inflate(context, R.layout.view_popwindow, null);
                    TextView tvShare = (TextView) view.findViewById(R.id.tv_share);
                    TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
                    tvShare.setOnClickListener(this);
                    tvDelete.setOnClickListener(this);
                    PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
                    popupWindow.showAtLocation(itemView, Gravity.RIGHT|Gravity.CENTER_VERTICAL,0,0);
                    break;
                case R.id.tv_edit:

                    break;
                case R.id.tv_print:

                    break;
                case R.id.tv_share:

                    break;
                case R.id.tv_delete:

                    break;
            }
        }
    }
}
