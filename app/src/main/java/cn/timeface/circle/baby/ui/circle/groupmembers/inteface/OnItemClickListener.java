package cn.timeface.circle.baby.ui.circle.groupmembers.inteface;

import android.view.View;

/**
 * Created by wangwei on 2017/3/20.
 */

public interface OnItemClickListener<T> {
    void onClickItem(View view, T item);
}
