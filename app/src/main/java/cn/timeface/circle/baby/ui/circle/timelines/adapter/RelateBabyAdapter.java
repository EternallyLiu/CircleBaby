package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.babyInfo.beans.IconHistory;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.bean.RelateBabyObj;
import cn.timeface.circle.baby.ui.circle.timelines.bean.CircleBabyObj;
import cn.timeface.circle.baby.ui.circle.timelines.dialog.CircleBabyDialog;
import cn.timeface.circle.baby.ui.images.views.FlipImageView;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;

/**
 * author : wangshuai Created on 2017/3/20
 * email : wangs1992321@gmail.com
 */
public class RelateBabyAdapter extends BaseAdapter {
    public RelateBabyAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.baby_icon_history_item;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    public static final int STATUS_FINAL = R.drawable.ic_item_select_final;
    public static final int STATUS_SELECT = R.drawable.ic_item_select_sel;
    public static final int STATUS_NONE = R.drawable.ic_item_select_nor;

    @Override
    public void initView(View contentView, int position) {
        ImageView icon = ViewHolder.getView(contentView, R.id.icon);
        FlipImageView ivSelect = ViewHolder.getView(contentView, R.id.iv_select);
        TextView age = ViewHolder.getView(contentView, R.id.age);
        CircleBabyObj babyObj = getItem(position);
        GlideUtil.displayImageCircle(babyObj.getBabyAvatarUrl(), icon);
        age.setText(babyObj.getBabyName());
        if (babyObj.getSelectUserId() <= 0) {
            ivSelect.changeStatus(STATUS_NONE);
            ivSelect.setVisibility(View.GONE);
        } else {
            ivSelect.setVisibility(View.VISIBLE);
            if (GrowthCircleObj.getInstance().getJoinType() == 0 || FastData.getBabyId() == babyObj.getBabyId() || String.valueOf(babyObj.getSelectUserId()).equals(FastData.getUserId())) {
                ivSelect.changeStatus(STATUS_SELECT);
            } else ivSelect.changeStatus(STATUS_FINAL);
        }
        if (ivSelect.getStatus() == STATUS_FINAL)
            contentView.setOnClickListener(this);
    }

}
