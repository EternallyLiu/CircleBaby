package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.babyInfo.beans.IconHistory;
import cn.timeface.circle.baby.ui.circle.bean.GetCircleAllBabyObj;
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
    public static final int STATUS_SELECT = R.drawable.selected;
    public static final int STATUS_NONE = R.drawable.ic_item_select_nor;

    private GetCircleAllBabyObj initNewBaby() {
        GetCircleAllBabyObj getCircleAllBabyObj = new GetCircleAllBabyObj();
        getCircleAllBabyObj.setBaseType(-11);
        getCircleAllBabyObj.setBabyName("新增成员");
        getCircleAllBabyObj.setSelected(0);
        return getCircleAllBabyObj;
    }

    public ArrayList<GetCircleAllBabyObj> getDataBaby() {
        ArrayList<GetCircleAllBabyObj> arrayList = new ArrayList<GetCircleAllBabyObj>();
        for (int i = 0; i < getItemCount(); i++) {
            GetCircleAllBabyObj babyObj = getItem(i);
            if (babyObj.getBaseType() == 0)
                arrayList.add(babyObj);
        }
        return arrayList;
    }

    @Override
    public void addList(boolean isClear, List list) {
        list.add(initNewBaby());
        super.addList(isClear, list);
    }

    @Override
    public void initView(View contentView, int position) {
        ImageView icon = ViewHolder.getView(contentView, R.id.icon);
        FlipImageView ivSelect = ViewHolder.getView(contentView, R.id.iv_select);
        TextView age = ViewHolder.getView(contentView, R.id.age);
        GetCircleAllBabyObj babyObj = getItem(position);
        if (babyObj.getBaseType() == -11) {
            GlideUtil.displayImageCircle(R.drawable.circlemanage_add, icon);
        } else {
            GlideUtil.displayImageCircle(babyObj.getBabyAvatarUrl(), icon);
        }
        age.setText(babyObj.getBabyName());

        if (babyObj.getSelected() == 1) {
            ivSelect.setVisibility(View.VISIBLE);
            if (babyObj.getSelectUserId() <= 0 || babyObj.getSelectUserId() == FastData.getCircleUserId() || babyObj.getIsCurrUserBaby() == 1) {
                ivSelect.changeStatus(STATUS_SELECT);
            } else ivSelect.changeStatus(STATUS_FINAL);
        } else {
            ivSelect.setVisibility(View.GONE);
        }

    }

}
