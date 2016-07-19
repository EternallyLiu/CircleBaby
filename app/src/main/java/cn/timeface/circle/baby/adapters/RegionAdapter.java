package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.DistrictModel;

/**
 * @author SunYanwei (QQ:707831837)
 * @from 2014-4-16下午4:43:06
 * @TODO 选择地区adapter
 */
public class RegionAdapter extends BaseAdapter {
    private List<DistrictModel> regionList;
    private LayoutInflater infalter;

    public RegionAdapter(List<DistrictModel> regionList, Context context) {
        this.regionList = regionList;
        infalter = LayoutInflater.from(context);
    }

    /**
     * 获取总数量
     */
    @Override
    public int getCount() {
        return regionList.size();
    }

    /**
     * 获取单个条目
     */
    @Override
    public Object getItem(int position) {
        return regionList.get(position);
    }

    /**
     * 获取单个条目id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 取得view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.item_region, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.regionText.setText(regionList.get(position).getLocationName());

        return convertView;
    }

    /**
     * viewholder
     */
    static class ViewHolder {
        @Bind(R.id.region_txt)
        TextView regionText;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}