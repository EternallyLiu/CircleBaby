package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TimeLineDetailActivity;
import cn.timeface.circle.baby.adapters.TimeLineAdapter;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.utils.DateUtil;

/**
 * Created by wangshuai on 2017/1/10.
 */
public class TimeLineDayAdapter extends BaseAdapter {
    private int largeSize, smailSize; //日历中两个字体大小
    private int dayColor;   //日历中几号的字体颜色

    public TimeLineDayAdapter(Context activity) {
        super(activity);
        largeSize = (int) activity.getResources().getDimension(R.dimen.text_large);
        smailSize = (int) activity.getResources().getDimension(R.dimen.text_small_10);
        dayColor = activity.getResources().getColor(R.color.sea_buckthorn);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.time_line_day_item;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_head:
                TimeLineObj timeLineObj = (TimeLineObj) v.getTag(R.id.ll_head);
                int tag = (int) v.getTag(R.id.recycler_item_click_tag);
                TimeLineDetailActivity.open(activity, timeLineObj, tag, 0);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void initView(View contentView, int position) {
        RecyclerView contentRecyclerView = ViewHolder.getView(contentView, R.id.content_recycler_view);
        LinearLayout llHead = ViewHolder.getView(contentView, R.id.ll_head);
        TextView calendar = ViewHolder.getView(contentView, R.id.calendar);
        TextView tvDateex=ViewHolder.getView(contentView,R.id.tv_dateex);
        TextView tvAge=ViewHolder.getView(contentView,R.id.tv_age);
        TimeLineGroupObj item = getItem(position);
        String month = DateUtil.getMonth(item.getDate());
        String day = DateUtil.getDay(item.getDate());
        String year = DateUtil.getDateYear(item.getDate());
        Log.i("test","year:"+year+"\nmonth:"+month+"\n day:"+day);

        //设置日历日期
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(day).append("\n").append(year).append(",").append(month);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(largeSize);
        builder.setSpan(sizeSpan,0,day.length()+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan=new ForegroundColorSpan(dayColor);
        builder.setSpan(colorSpan,0,day.length()+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        calendar.setText(builder);

        tvDateex.setText(item.getDateEx());
        tvAge.setText("宝宝"+item.getAge());

        List<TimeLineObj> timeLineList = item.getTimeLineList();
        TimeLineAdapter adapter = new TimeLineAdapter(activity, timeLineList, position);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        contentRecyclerView.setAdapter(adapter);
        llHead.setTag(R.id.recycler_item_click_tag, position);
        llHead.setTag(R.id.ll_head, timeLineList.get(0));
        llHead.setOnClickListener(this);
    }
}
