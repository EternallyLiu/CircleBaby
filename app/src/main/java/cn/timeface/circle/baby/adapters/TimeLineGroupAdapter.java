package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.utils.DateUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class TimeLineGroupAdapter extends BaseRecyclerAdapter<TimeLineGroupObj> {

    private View.OnClickListener onClickListener;
    int normalColor;
    public static Context context;
    private ViewHolder holder;
    public List<TimeLineGroupObj> listData;

    public TimeLineGroupAdapter(Context mContext, List<TimeLineGroupObj> listData) {
        super(mContext, listData);
        this.listData = listData;
        this.context = mContext;
        normalColor = mContext.getResources().getColor(R.color.gray_normal);
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_timelinegroup, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = ((ViewHolder) viewHolder);
        holder.contentRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).color(Color.TRANSPARENT).sizeResId(R.dimen.view_space_normal).build());
        TimeLineGroupObj item = getItem(position);
        String month = DateUtil.getMonth(item.getDate());
        String day = DateUtil.getDay(item.getDate());
        holder.tvMonth.setText(month);
        holder.tvDay.setText(day);
        holder.tvDateex.setText(item.getDateEx());
        holder.tvAge.setText(item.getAge());
        List<TimeLineObj> timeLineList = item.getTimeLineList();
        TimeLineAdapter adapter = new TimeLineAdapter(context, timeLineList, position);
        holder.contentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.contentRecyclerView.setAdapter(adapter);

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

        @Bind(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @Bind(R.id.tv_day)
        TextView tvDay;
        @Bind(R.id.tv_month)
        TextView tvMonth;
        @Bind(R.id.tv_dateex)
        TextView tvDateex;
        @Bind(R.id.tv_age)
        TextView tvAge;
        @Bind(R.id.content_recycler_view)
        RecyclerView contentRecyclerView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}

