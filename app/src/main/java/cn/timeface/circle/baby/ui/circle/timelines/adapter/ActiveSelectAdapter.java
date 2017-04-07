package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * author : wangshuai Created on 2017/3/16
 * email : wangs1992321@gmail.com
 */
public class ActiveSelectAdapter extends BaseEmptyAdapter {
    public ActiveSelectAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.circle_publish_select_active;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    @Override
    public void addEmpty() {
        if (getEmptyItem() != null && containObj(getEmptyItem())) {
            if (getRealItemSize() > 1) {
                deleteItem(getEmptyItem());

            }
        } else if (getEmptyItem() != null && !containObj(getEmptyItem())) {
            if (getRealItemSize() == 0) add(getEmptyItem());
        }
    }

    @Override
    public void initView(View contentView, int position) {
        LogUtil.showLog("view type==="+getItemViewType(position));
        switch (getItemViewType(position)) {
            case BaseEmptyAdapter.EMPTY_CODE:
                doEmpty(contentView);
                break;
            default:
                CircleActivityAlbumObj activityAlbumObj = getItem(position);
                TextView tvTip = ViewHolder.getView(contentView, R.id.tv_tip);
                TextView tvActive = ViewHolder.getView(contentView, R.id.tv_active);
                tvTip.setText(activityAlbumObj.getAlbumName());
                tvActive.setText(String.format(context().getString(R.string.select_active_media_count_tip), activityAlbumObj.getMediaCount()));
                break;
        }
    }
}
