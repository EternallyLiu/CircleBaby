package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.utils.GlideUtil;

/**
 * Created by lidonglin on 2016/5/12.
 */
public class PhotoGridAdapter extends BaseAdapter {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_BODY = 2;
    private Context context;

    List<String> data = new ArrayList<>();

    public PhotoGridAdapter(Context context) {
        this.context = context;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_BODY;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (getItemViewType(position) == TYPE_HEADER) {
            view = View.inflate(context, R.layout.item_record_add_photo, null);
        } else {
            view = View.inflate(context, R.layout.item_record_photo, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_cover);
            GlideUtil.displayImage(data.get(position - 1), imageView);
        }
        return view;
    }
}
