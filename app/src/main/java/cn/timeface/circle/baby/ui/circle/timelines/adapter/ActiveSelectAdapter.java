package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
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
    public void initView(View contentView, int position) {
        switch (getItemViewType(position)) {
            case BaseEmptyAdapter.EMPTY_CODE:
                TFStateView tfStateView = ViewHolder.getView(contentView, R.id.tf_stateView);
                switch (getEmptyItem().getOperationType()) {
                    case -1:
                        tfStateView.showException(getEmptyItem().getThrowable());
                        break;
                    case 0:
                        tfStateView.empty(R.string.active_album_null);
                        break;
                    case 1:
                        tfStateView.loading();
                        break;
                    case 2:
                        tfStateView.finish();
                        break;
                }
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
