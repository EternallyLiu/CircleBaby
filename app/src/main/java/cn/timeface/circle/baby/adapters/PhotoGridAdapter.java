package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;

/**
 * Created by lidonglin on 2016/5/12.
 */
public class PhotoGridAdapter extends BaseAdapter {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_BODY = 2;
    private Context context;
    private View.OnClickListener listener;
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
        return position == data.size() ? TYPE_HEADER : TYPE_BODY;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        int width = (int) (Remember.getInt("width", 0) * 0.75);
        if (getItemViewType(position) == TYPE_HEADER) {
            view = View.inflate(context, R.layout.item_timeline_add, null);
            ImageView ivAdd = (ImageView) view.findViewById(R.id.iv_add);
            ivAdd.setLayoutParams(new FrameLayout.LayoutParams(width,width));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        listener.onClick(v);
                    }
                }
            });
        } else {
            view = View.inflate(context, R.layout.item_image, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            imageView.setLayoutParams(params);
            GlideUtil.displayImage(data.get(position), imageView);
        }
        return view;
    }

    public void setOnAddClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
}
