package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.bean.TeacherAuthObj;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * author : wangshuai Created on 2017/3/25
 * email : wangs1992321@gmail.com
 */
public class TearcherOpproverAdapter extends BaseEmptyAdapter {

    private RecyclerView.LayoutParams emptyLayoutParams;

    public TearcherOpproverAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.teacher_opprover_item;
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    @Override
    public void addEmpty() {
        if (getEmptyItem() != null && containObj(getEmptyItem())) {
            if (getRealItemSize() > 1) deleteItem(getEmptyItem());
        } else if (getEmptyItem() != null && !containObj(getEmptyItem())) {
            if (getRealItemSize() <= 0) add(getEmptyItem());
        }
    }

    private void agreeTearcher(int postion) {
        TeacherAuthObj item = getItem(postion);
        ApiFactory.getApi().getApiService().checkTeacher(item.getTeacher().getCircleUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(baseResponse -> {
                    if (baseResponse.success()) deleteItem(postion);
                    else ToastUtil.showToast(context(), baseResponse.getInfo());
                }, throwable -> LogUtil.showError(throwable));
    }

    @Override
    public void initView(View contentView, int position) {
        LogUtil.showLog("position===" + position + "---type==" + getItemViewType(position));
        switch (getItemViewType(position)) {
            case BaseEmptyAdapter.EMPTY_CODE:
                TFStateView tfStateView = ViewHolder.getView(contentView, R.id.tf_stateView);
                emptyLayoutParams = (RecyclerView.LayoutParams) contentView.getLayoutParams();
                if (emptyLayoutParams == null) {
                    emptyLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                contentView.setLayoutParams(emptyLayoutParams);
                switch (getEmptyItem().getOperationType()) {
                    case -1:
                        tfStateView.showException(getEmptyItem().getThrowable());
                        break;
                    case 0:
                        tfStateView.empty(R.string.teacher_empty_message);
                        break;
                    case 1:
                        tfStateView.loading();
                        break;
                    case 2:
                        tfStateView.finish();
                        break;
                }
                break;
            case 0:
                TeacherAuthObj item = getItem(position);
                LogUtil.showLog("item:" + JSONUtils.parse2JSONString(item));
                ImageView ivIcon = ViewHolder.getView(contentView, R.id.iv_icon);
                TextView tvMessage = ViewHolder.getView(contentView, R.id.tv_message);
                ImageView ivIcon1 = ViewHolder.getView(contentView, R.id.iv_icon_1);
                ImageView ivIcon2 = ViewHolder.getView(contentView, R.id.iv_icon_2);
                ImageView ivIcon3 = ViewHolder.getView(contentView, R.id.iv_icon_3);
                Button btnSubmit = ViewHolder.getView(contentView, R.id.btn_agree);
                btnSubmit.setOnClickListener(view -> agreeTearcher(position));
                GlideUtil.displayImageCircle(item.getTeacher().getCircleAvatarUrl(), ivIcon);
                tvMessage.setText(String.format("%s 老师申请入驻%s 已通过家长%d/3", item.getTeacher().getCircleNickName(), GrowthCircleObj.getInstance().getCircleName(), item.getAgreeUserList().size()));
                switch (item.getAgreeUserList().size()) {
                    case 0:
                        ivIcon1.setVisibility(View.GONE);
                        ivIcon2.setVisibility(View.GONE);
                        ivIcon3.setVisibility(View.GONE);
                        break;
                    case 1:
                        GlideUtil.displayImageCircle(item.getAgreeUserList().get(0).getCircleAvatarUrl(), ivIcon1);
                        ivIcon1.setVisibility(View.VISIBLE);
                        ivIcon2.setVisibility(View.GONE);
                        ivIcon3.setVisibility(View.GONE);
                        break;
                    case 2:
                        GlideUtil.displayImageCircle(item.getAgreeUserList().get(0).getCircleAvatarUrl(), ivIcon1);
                        GlideUtil.displayImageCircle(item.getAgreeUserList().get(1).getCircleAvatarUrl(), ivIcon2);
                        ivIcon1.setVisibility(View.VISIBLE);
                        ivIcon2.setVisibility(View.VISIBLE);
                        ivIcon3.setVisibility(View.GONE);
                        break;
                    case 3:
                        GlideUtil.displayImageCircle(item.getAgreeUserList().get(0).getCircleAvatarUrl(), ivIcon1);
                        GlideUtil.displayImageCircle(item.getAgreeUserList().get(1).getCircleAvatarUrl(), ivIcon2);
                        GlideUtil.displayImageCircle(item.getAgreeUserList().get(2).getCircleAvatarUrl(), ivIcon3);
                        ivIcon1.setVisibility(View.VISIBLE);
                        ivIcon2.setVisibility(View.VISIBLE);
                        ivIcon3.setVisibility(View.VISIBLE);
                        break;
                }
                break;
        }
    }
}
