package cn.timeface.circle.baby.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import cn.timeface.circle.baby.R;

public class VideoEditAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private int selectIndex = -1;
    private List<Bitmap> list;

    public VideoEditAdapter(Context context, List<Bitmap> list) {
        this.mContext = context;
        this.list = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public void setList(List<Bitmap> list) {
        this.list = list;
    }

    public List<Bitmap> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_videobitmap, parent, false);
            holder.mImage = (ImageView) convertView.findViewById(R.id.iv_videobitmap);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == selectIndex) {
            convertView.setSelected(true);
        } else {
            convertView.setSelected(false);
        }
        holder.mImage.setImageBitmap(list.get(position));
        return convertView;
    }

    private static class ViewHolder {
        private ImageView mImage;
    }

    public void setSelectIndex(int i) {
        selectIndex = i;
    }
}