package cn.timeface.circle.baby.ui.circle.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;

/**
 * 圈作品选择task list 界面
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectSchoolTaskAdapter extends BaseRecyclerAdapter<CircleSchoolTaskObj> {
    View.OnClickListener onClickListener;

    public CircleSelectSchoolTaskAdapter(Context mContext, List<CircleSchoolTaskObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.onClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_circle_select_school_task, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final CircleSchoolTaskObj schoolTaskObj = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tvTitle.setText(schoolTaskObj.getTitle());
        if(schoolTaskObj.getSelectCount() > 0){
            holder.tvSelectCount.setVisibility(View.VISIBLE);
            holder.tvSelectCount.setText("已选择" + schoolTaskObj.getSelectCount() + "条记录");
        } else {
            holder.tvSelectCount.setVisibility(View.GONE);
        }

        if(onClickListener != null)holder.rlRoot.setOnClickListener(onClickListener);
        holder.rlRoot.setTag(R.string.tag_obj, schoolTaskObj);
        holder.rlRoot.setTag(R.string.tag_index, position);
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
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_select_count)
        TextView tvSelectCount;
        @Bind(R.id.rl_root)
        RelativeLayout rlRoot;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
