package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.TimeLineDetailActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class TimeLineGroupAdapter extends BaseRecyclerAdapter<TimeLineGroupObj> implements View.OnClickListener{

    int normalColor;
    public static Context context;
    private ViewHolder holder;
    public List<TimeLineGroupObj> listData;

    public TimeLineGroupAdapter(Context mContext, List<TimeLineGroupObj> listData) {
        super(mContext, listData);
        this.listData = listData;
        this.context = mContext;
        normalColor = mContext.getResources().getColor(R.color.gray_normal);
        largeSize = (int) mContext.getResources().getDimension(R.dimen.text_large);
        smailSize = (int) mContext.getResources().getDimension(R.dimen.text_small_10);
        dayColor = mContext.getResources().getColor(R.color.sea_buckthorn);
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_timegroup_copy, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    private int largeSize, smailSize; //日历中两个字体大小
    private int dayColor;   //日历中几号的字体颜色


    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = ((ViewHolder) viewHolder);
//        holder.contentRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).color(Color.TRANSPARENT).sizeResId(R.dimen.view_space_normal).build());
        TimeLineGroupObj item = getItem(position);
        String month = DateUtil.getMonth(item.getDate());
        String day = DateUtil.getDay(item.getDate());
        String year = DateUtil.getDateYear(item.getDate());

        //设置日历日期
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(day).append("\n").append(year).append(",").append(month);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(largeSize);
        builder.setSpan(sizeSpan,0,day.length()+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan=new ForegroundColorSpan(dayColor);
        builder.setSpan(colorSpan,0,day.length()+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.calendar.setText(builder);

        holder.tvDateex.setText(item.getDateEx());
        holder.tvAge.setText("宝宝"+item.getAge());
        List<TimeLineObj> timeLineList = item.getTimeLineList();
        TimeLineAdapter adapter = new TimeLineAdapter(context, timeLineList, position);
        holder.contentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.contentRecyclerView.setAdapter(adapter);
        holder.llHead.setTag(R.id.recycler_item_click_tag, position);
        holder.llHead.setTag(R.id.ll_head, timeLineList.get(0));
        holder.llHead.setOnClickListener(this);
        holder.rlCalendar.setOnClickListener(this);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_head:
                TimeLineObj timeLineObj = (TimeLineObj) v.getTag(R.id.ll_head);
                int tag = (int) v.getTag(R.id.recycler_item_click_tag);
                TimeLineDetailActivity.open(mContext, timeLineObj, tag, 0);
                break;
            case R.id.rl_calendar:
                FragmentBridgeActivity.open(mContext,"TimeLineFragment");
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind((R.id.rl_calendar))
        RelativeLayout rlCalendar;
//        @Bind(R.id.iv_avatar)
//        CircleImageView ivAvatar;
        @Bind(R.id.calendar)
        TextView calendar;
        @Bind(R.id.tv_dateex)
        TextView tvDateex;
        @Bind(R.id.tv_age)
        TextView tvAge;
        @Bind(R.id.content_recycler_view)
        RecyclerView contentRecyclerView;
        @Bind(R.id.ll_head)
        LinearLayout llHead;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}

