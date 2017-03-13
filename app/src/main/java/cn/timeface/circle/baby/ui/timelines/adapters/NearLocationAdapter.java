package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.timelines.beans.ContentType;
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
        switch (viewType) {
            case 0:
                return R.layout.location_near_item;
            case 1:
                return R.layout.location_search;
            default:
                return 0;
        }
    }

    @Override
    public int getViewType(int position) {
        ContentType item = getItem(position);
        return item.getType();
    }

    @Override
    public void initView(View contentView, int position) {
        ContentType item = getItem(position);
        if (item.getType() == 0) {
            TextView area = ViewHolder.getView(contentView, R.id.area);
            TextView detail = ViewHolder.getView(contentView, R.id.detail);
            SelectImageView ivSelect = ViewHolder.getView(contentView, R.id.iv_select);
            NearLocationObj locationObj = (NearLocationObj) item.getItem();
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
}
