package cn.timeface.circle.baby.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.TemplateObj;
import cn.timeface.circle.baby.utils.GlideUtil;

public class HorizontalListViewAdapter2 extends BaseAdapter {
    private LayoutInflater mInflater;
    private int selectIndex = -1;
    private List<TemplateObj> list;

    public HorizontalListViewAdapter2(Context context, List<TemplateObj> list) {
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public void setList(List<TemplateObj> list) {
        this.list = list;
    }

    public List<TemplateObj> getList() {
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
            convertView = mInflater.inflate(R.layout.item_horizontal_listview, parent, false);
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

        holder.mTitle.setText(list.get(position).getShortPaperName());
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