package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.constants.MiPushConstant;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.bean.TeacherAuthObj;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CirclePassThroughMessageEvent;
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
        switch (item.getState()) {
            case 1:
                if (!FastData.getCircleUserInfo().isCreator())
                    ApiFactory.getApi().getApiService().checkTeacher(item.getTeacher().getCircleUserId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(baseResponse -> {
                                EventBus.getDefault().post(new CirclePassThroughMessageEvent(MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION));
                                if (baseResponse.success()) {
                                    deleteItem(postion);
                                }
                                else ToastUtil.showToast(context(), baseResponse.getInfo());
                            }, throwable -> {
                                EventBus.getDefault().post(new CirclePassThroughMessageEvent(MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION));
                                LogUtil.showError(throwable);
                            });
                else
                    ApiFactory.getApi().getApiService().cancelTeacher(FastData.getCircleId(), item.getTeacher().getCircleUserId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(baseResponse -> {
                                EventBus.getDefault().post(new CirclePassThroughMessageEvent(MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION));
                                if (baseResponse.success()) {
                                    deleteItem(postion);
                                }
                                else ToastUtil.showToast(context(), baseResponse.getInfo());
                            }, throwable -> {
                                EventBus.getDefault().post(new CirclePassThroughMessageEvent(MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION));
                                LogUtil.showError(throwable);
                            });
                break;
            case 3:
                if (FastData.getCircleUserInfo().isCreator())
                    ApiFactory.getApi().getApiService().start(0, FastData.getCircleId(), item.getTeacher().getCircleUserId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(baseResponse -> {
                                EventBus.getDefault().post(new CirclePassThroughMessageEvent(MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION));
                                if (baseResponse.success()) {
                                    deleteItem(postion);
                                }
                                else ToastUtil.showToast(context(), baseResponse.getInfo());
                            }, throwable -> {
                                EventBus.getDefault().post(new CirclePassThroughMessageEvent(MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION));
                                LogUtil.showError(throwable);
                            });
                break;
            default:
                if (FastData.getCircleUserInfo().isCreator())
                    ApiFactory.getApi().getApiService().cancelTeacher(FastData.getCircleId(), item.getTeacher().getCircleUserId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(baseResponse -> {
                                EventBus.getDefault().post(new CirclePassThroughMessageEvent(MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION));
                                if (baseResponse.success()) {
                                    deleteItem(postion);
                                }
                                else ToastUtil.showToast(context(), baseResponse.getInfo());
                            }, throwable -> {
                                EventBus.getDefault().post(new CirclePassThroughMessageEvent(MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION));
                                LogUtil.showError(throwable);
                            });
                break;
        }
    }

    @Override
    public void initView(View contentView, int position) {
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
                ImageView ivIcon = ViewHolder.getView(contentView, R.id.iv_icon);
                TextView tvMessage = ViewHolder.getView(contentView, R.id.tv_message);
                ImageView ivIcon1 = ViewHolder.getView(contentView, R.id.iv_icon_1);
                ImageView ivIcon2 = ViewHolder.getView(contentView, R.id.iv_icon_2);
                ImageView ivIcon3 = ViewHolder.getView(contentView, R.id.iv_icon_3);
                Button btnSubmit = ViewHolder.getView(contentView, R.id.btn_agree);
                btnSubmit.setOnClickListener(view -> agreeTearcher(position));
                btnSubmit.setEnabled(item.getState() == 1 ? true : false);
                btnSubmit.setText(item.getState() == 1 ? R.string.agree_teacher : item.getState() == 2 ? R.string.agreed : R.string.agree_complete);
                btnSubmit.setEnabled(FastData.getCircleUserInfo().isCreator() ? true : btnSubmit.isEnabled());
                btnSubmit.setText(FastData.getCircleUserInfo().isCreator() ? context().getString(R.string.cancel_agree) : btnSubmit.getText());
                GlideUtil.displayImageCircle(item.getTeacher().getCircleAvatarUrl(), ivIcon);
                tvMessage.setText(item.getMessage());
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
