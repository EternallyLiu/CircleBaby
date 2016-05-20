package cn.timeface.circle.baby.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.Paper;
import cn.timeface.circle.baby.utils.BitmapUtil;
import cn.timeface.circle.baby.utils.GlideUtil;

public class HorizontalListViewAdapter2 extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    Bitmap iconBitmap;
    private int selectIndex = -1;
    private List<Paper> list;

    public HorizontalListViewAdapter2(Context context, List<Paper> list) {
        this.mContext = context;
        this.list = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public void setList(List<Paper> list) {
        this.list = list;
    }

    public List<Paper> getList() {
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
            convertView = mInflater.inflate(R.layout.item_horizontal_listview, null);
            holder.mImage = (ImageView) convertView.findViewById(R.id.img_list_item);
            holder.mTitle = (TextView) convertView.findViewById(R.id.text_list_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == selectIndex) {
            convertView.setSelected(true);
        } else {
            convertView.setSelected(false);
        }

        holder.mTitle.setText(list.get(position).getPaperName());
        GlideUtil.displayImage(list.get(position).getPaperUrl(), holder.mImage);

        return convertView;
    }

    private static class ViewHolder {
        private TextView mTitle;
        private ImageView mImage;
    }

    public void setSelectIndex(int i) {
        selectIndex = i;
    }
}