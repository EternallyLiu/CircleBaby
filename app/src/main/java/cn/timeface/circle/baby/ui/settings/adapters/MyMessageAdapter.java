package cn.timeface.circle.baby.ui.settings.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.settings.beans.MessageBean;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * author : wangshuai Created on 2017/4/13
 * email : wangs1992321@gmail.com
 */
public class MyMessageAdapter extends BaseEmptyAdapter {
    public MyMessageAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        return R.layout.message_list_item;
    }

    @Override
    public int getViewType(int position) {
        return 1001;
    }

    @Override
    public void initView(View contentView, int position) {
        switch (getItemViewType(position)) {
            case EMPTY_CODE:
                doEmpty(contentView);
                break;
            default:
                doMessage(contentView, position);
                break;
        }
    }

    protected void doEmpty(View contentView) {
        TFStateView tfStateView = ViewHolder.getView(contentView, R.id.tf_stateView);
        emptyLayoutParams = (RecyclerView.LayoutParams) contentView.getLayoutParams();
        if (emptyLayoutParams == null) {
            emptyLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (getRetryListener() != null)
            tfStateView.setOnRetryListener(getRetryListener());
        switch (getEmptyItem().getOperationType()) {
            case -1:
                emptyLayoutParams.height = RecyclerView.LayoutParams.MATCH_PARENT;
                tfStateView.showException(getEmptyItem().getThrowable());
                break;
            case 0:
                emptyLayoutParams.height = RecyclerView.LayoutParams.MATCH_PARENT;
                tfStateView.empty(R.string.message_no_data);
                break;
            case 1:
                emptyLayoutParams.height = RecyclerView.LayoutParams.MATCH_PARENT;
                tfStateView.loading();
                break;
            case 2:
                emptyLayoutParams.height = RecyclerView.LayoutParams.MATCH_PARENT;
                tfStateView.finish();
                break;
        }
        contentView.setLayoutParams(emptyLayoutParams);
    }

    private void doMessage(View contentView, int position) {
        MessageBean item = getItem(position);
        ImageView ivAvatar = ViewHolder.getView(contentView, R.id.iv_avatar);
        ImageView ivDot = ViewHolder.getView(contentView, R.id.iv_dot);
        TextView tvRelative = ViewHolder.getView(contentView, R.id.tv_relation);
        TextView tvContent = ViewHolder.getView(contentView, R.id.tv_content);
        TextView tvTime = ViewHolder.getView(contentView, R.id.tv_time);
        RelativeLayout rlContent = ViewHolder.getView(contentView, R.id.rl_content);
        ImageView ivContent = ViewHolder.getView(contentView, R.id.iv_content);
        ImageView ivVedio = ViewHolder.getView(contentView, R.id.iv_video);
        GlideUtil.displayImageCircle(item.getImgUrl(), R.drawable.ic_launcher, ivAvatar);
        tvTime.setText(DateUtil.formatDate("MM-dd HH:mm", item.getTime()));
        rlContent.setVisibility(View.GONE);

        ivDot.setVisibility(item.getIsRead() == 1 ? View.GONE : View.VISIBLE);

        if (!TextUtils.isEmpty(item.getInfoValue("mediaUrl"))) {
            rlContent.setVisibility(View.VISIBLE);
            GlideUtil.displayImage(item.getInfoValue("mediaUrl"), ivContent);
            if (!TextUtils.isEmpty(item.getInfoValue("mediaType"))) {
                ivVedio.setVisibility(Integer.valueOf(item.getInfoValue("mediaType")) == 1 ? View.VISIBLE : View.GONE);
            }
        }
        if (item.getIdentifier() >= 1000 && item.getIdentifier() < 2000) {
            tvRelative.setVisibility(TextUtils.isEmpty(item.getTitle()) ? View.GONE : View.VISIBLE);
            tvRelative.setText(item.getTitle());
            tvContent.setText(item.getContent());
        } else if (item.getIdentifier() >= 2000 && item.getIdentifier() < 3000) {
            tvRelative.setVisibility(View.GONE);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString spannableString = null;
            if (!TextUtils.isEmpty(item.getInfoValue("beginString"))) {
                spannableString = new SpannableString(String.format("【%s】", item.getInfoValue("beginString")));
            }
            if (spannableString != null) builder.append(spannableString);
            builder.append(item.getContent());
            if (spannableString != null)
                builder.setSpan(SpannableUtils.getTextColor(context(), R.color.sea_buckthorn), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tvContent.setText(builder);
        } else if (item.getIdentifier() >= 3000 && item.getIdentifier() < 4000) {
            tvRelative.setTextColor(context().getResources().getColor(R.color.dodger_blue));
            tvRelative.setVisibility(TextUtils.isEmpty(item.getTitle()) ? View.GONE : View.VISIBLE);
            tvRelative.setText(item.getTitle());
            tvContent.setText(item.getContent());
        }

    }
}
