package cn.timeface.circle.baby.ui.calendar.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseListAdapter;

/**
 * Created by JieGuo on 16/10/8.
 */

public class DateSpinnerAdapter extends BaseListAdapter<Long> {

    private Calendar calendar = Calendar.getInstance();

    public DateSpinnerAdapter(Context context, List<Long> listData) {
        super(context, listData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AppCompatTextView textView = null;
        if (convertView == null) {
            textView = new AppCompatTextView(mContext);
            int height = mContext.getResources().getDimensionPixelSize(R.dimen.size_48);
            //int width = mContext.getResources().getDimensionPixelSize(R.dimen.size_160);
            textView.setLayoutParams(
                    new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height)
            );
            textView.setGravity(Gravity.CENTER);
            convertView = textView;
        } else {
            textView = ((AppCompatTextView) convertView);
        }

        textView.setText(getItemString(position));
        return convertView;
    }

    @Override
    public Long getItem(int position) {
        return getListData().get(position);
    }

    public String getItemString(int position) {
        return String.format(Locale.CHINESE,
                "%s年%s月%s日",
                getYear(position)
                , getMonth(position),
                getDay(position)
        );
    }

    public String getYear(int position) {
        calendar.setTimeInMillis(getItem(position));
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public String getMonth(int position) {
        calendar.setTimeInMillis(getItem(position));
        int month = calendar.get(Calendar.MONTH) + 1;
        return String.valueOf(month);
    }

    public String getDay(int position) {
        calendar.setTimeInMillis(getItem(position));
        return String.valueOf(calendar.get(Calendar.DATE));
    }
}
