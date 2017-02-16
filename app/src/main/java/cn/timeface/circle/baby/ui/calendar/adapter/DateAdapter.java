package cn.timeface.circle.baby.ui.calendar.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.ui.calendar.bean.DateObj;

/**
 * Created by JieGuo on 16/9/29.
 */

public class DateAdapter extends BaseRecyclerAdapter<DateObj> {

    private Action action;

    public DateAdapter(Context mContext, List<DateObj> listData) {
        super(mContext, listData);
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_commemoration_view, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.action = action;
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = ((ViewHolder) viewHolder);
        DateObj item = getItem(position);
        holder.setContent(item.getContent());
        holder.setDate(item.getDateStringMD());
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

        @Bind(R.id.tv_content)
        AppCompatTextView tvContent;
        @Bind(R.id.tv_date)
        AppCompatTextView tvDate;
        @Bind(R.id.ll_item)
        LinearLayout llItem;
        @Bind(R.id.icon_delete)
        TextView tvDelete;

        Action action;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            llItem.setOnClickListener(this);
            tvDelete.setOnClickListener(this);
        }

        public void setContent(String content) {
            tvContent.setText(content);
        }

        public void setDate(String date) {
            tvDate.setText(date);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_item:
                    if (action != null) {
                        action.update(getLayoutPosition());
                    }
                    break;

                case R.id.icon_delete:
                    if (action != null) {
                        action.delete(getLayoutPosition());
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public interface Action {

        void delete(int position);

        void update(int position);
    }
}
