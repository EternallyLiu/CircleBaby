package cn.timeface.circle.baby.ui.timelines.adapters;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by wang on 2016/9/5.
 */
public class ViewHolder {

    public static <T extends View> T getView(View view, int id) {
        SparseArray array = (SparseArray) view.getTag();
        if (array == null) {
            array = new SparseArray();
            view.setTag(array);
        }
        View childView = (View) array.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            array.put(id, childView);
        }
        return (T) childView;

    }

}
