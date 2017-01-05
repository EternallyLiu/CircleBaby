package cn.timeface.circle.baby.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.open.api.models.objs.TFOBookType;

public class HorizontalListViewAdapter3 extends BaseAdapter {
    private LayoutInflater mInflater;
    private int selectIndex = -1;
    private List<TFOBookType> list;

    public HorizontalListViewAdapter3(Context context, List<TFOBookType> list) {
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    public void setList(List<TFOBookType> list) {
        this.list = list;
    }

    public List<TFOBookType> getList() {
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
            convertView = mInflater.inflate(R.layout.item_selecttheme, parent, false);
            holder.mImage = (ImageView) convertView.findViewById(R.id.img_list_item);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == selectIndex) {
            convertView.setSelected(true);
            holder.ivSelect.setVisibility(View.VISIBLE);
        } else {
            convertView.setSelected(false);
            holder.ivSelect.setVisibility(View.GONE);
        }
        GlideUtil.displayImage(list.get(position).getTemplatePic(), holder.mImage);

        return convertView;
    }

    private static class ViewHolder {
        private ImageView ivSelect;
        private ImageView mImage;
    }

    public void setSelectIndex(int i) {
        selectIndex = i;
    }

}