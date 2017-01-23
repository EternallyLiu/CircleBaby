package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.timelines.beans.MonthRecord;
import cn.timeface.circle.baby.ui.timelines.beans.TimeAxisObj;
import cn.timeface.circle.baby.ui.timelines.views.SelectImageView;
import cn.timeface.circle.baby.ui.timelines.views.TimeLineMarker;

/**
 * Created by wangshuai on 2017/1/10.
 */

public class TimeLineSelectAdapter extends BaseAdapter {
    public TimeLineSelectAdapter(Context activity) {
        super(activity);
        resources=activity.getResources();
    }

    private Drawable lineDrawable, selectDrawable, noSelectDrawable;

    @Override
    public void addList(boolean isClear, List list) {
        super.addList(isClear, list);
    }

    private Resources resources;

    @Override
    public @LayoutRes int getViewLayoutID(int viewType) {
        switch (viewType) {
            case 1:
                return R.layout.time_line_year;
            default:
                return R.layout.time_line_month;
        }
    }

    @Override
    public int getViewType(int position) {
        if (getItem(position) instanceof TimeAxisObj)
            return 1;
        else
            return 0;
    }

    private Drawable getNoSelectDrawable() {
        if (noSelectDrawable == null)
            noSelectDrawable = resources.getDrawable(R.drawable.time_line_marker_no);
        return noSelectDrawable;
    }

    private Drawable getSelectDrawable() {
        if (selectDrawable == null)
            selectDrawable = resources.getDrawable(R.drawable.time_line_marker);
        return selectDrawable;
    }

//    private Drawable getLineDrawable() {
//        return resources.getDrawable(R.drawable.time_line_draw_line);
//    }

    private void doYearView(View view, TimeAxisObj item, int position) {
        TimeLineMarker marker = ViewHolder.getView(view, R.id.line);
        TextView operationTip=ViewHolder.getView(view,R.id.operation_tip);
        SelectImageView selectImageView=ViewHolder.getView(view,R.id.open);
        selectImageView.setChecked(item.isSelected());
        if (item.isSelected()) {
            operationTip.setText(R.string.time_line_select_up);
            marker.setMarkerDrawable(getSelectDrawable());
        } else {
            marker.setMarkerDrawable(getNoSelectDrawable());
            operationTip.setText(R.string.time_line_select_down);
        }

        TextView year = ViewHolder.getView(view, R.id.year);
        year.setText(item.getYear() + "年");

        Log.i("test","position:"+position);
        if (getItemCount() <= 1) {
            Log.i("test","1");
            marker.setDrawBegin(false);
            marker.setDrawEnd(false);
        } else if (position == 0) {
            Log.i("test","2");
            marker.setDrawBegin(false);
            marker.setDrawEnd(true);
        } else if (position == getItemCount() - 1) {
            Log.i("test","3");
            marker.setDrawBegin(true);
            marker.setDrawEnd(false);
        } else {
            Log.i("test","4");
            marker.setDrawBegin(true);
            marker.setDrawEnd(true);
        }
    }

    private void doMonthView(View view, int position) {
        TextView month = ViewHolder.getView(view, R.id.month);
        TextView babyAge = ViewHolder.getView(view, R.id.baby_age);
        TextView count = ViewHolder.getView(view, R.id.count);
        ImageView image1 = ViewHolder.getView(view, R.id.image_1);
        ImageView image2 = ViewHolder.getView(view, R.id.image_2);
        MonthRecord item = getItem(position);
        month.setText(item.getMoth() + "月份");
        babyAge.setText(item.getBabyAge());
        count.setText("共" + item.getRecordcount() + "条");
        if (item.getMedias() == null || item.getMedias().size() <= 0) {
            image1.setVisibility(View.GONE);
            image2.setVisibility(View.GONE);
        } else if (item.getMedias().size() == 1) {
            image1.setVisibility(View.VISIBLE);
            GlideUtil.displayImage(item.getMedias().get(0).getImgUrl(), image1);
            image2.setVisibility(View.GONE);
        } else {
            image1.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            GlideUtil.displayImage(item.getMedias().get(0).getImgUrl(), image1);
            GlideUtil.displayImage(item.getMedias().get(1).getImgUrl(), image2);

        }
    }

    @Override
    public void initView(View contentView, int position) {
        Log.d("test", "position==" + position);
        BaseObj item = getItem(position);
        if (item instanceof TimeAxisObj)
            doYearView(contentView, (TimeAxisObj) item, position);
        else if (item instanceof MonthRecord) {
            doMonthView(contentView, position);
        }
    }
}
