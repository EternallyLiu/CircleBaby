package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocationObj;
import cn.timeface.circle.baby.ui.timelines.views.SelectImageView;

/**
 * author : wangshuai Created on 2017/2/9
 * email : wangs1992321@gmail.com
 */
public class NearLocationAdapter extends BaseAdapter {
    public NearLocationAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.location_near_item;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    @Override
    public void initView(View contentView, int position) {
        TextView area = ViewHolder.getView(contentView, R.id.area);
        TextView detail = ViewHolder.getView(contentView, R.id.detail);
        SelectImageView ivSelect = ViewHolder.getView(contentView, R.id.iv_select);
        NearLocationObj locationObj = getItem(position);
        area.setText(locationObj.getArea());
        if (locationObj.getLocation() == null) {
            ivSelect.setVisibility(View.VISIBLE);
            detail.setVisibility(View.GONE);
            ivSelect.setChecked(TextUtils.isEmpty(locationObj.getAreaDetail()) ? false : true);
        } else {
            detail.setVisibility(View.VISIBLE);
            ivSelect.setVisibility(View.GONE);
            detail.setText(locationObj.getAreaDetail());
        }
    }
}
