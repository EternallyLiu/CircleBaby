package cn.timeface.circle.baby.ui.kiths.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.support.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.support.api.models.responses.FamilyListResponse;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;

/**
 * author : wangshuai Created on 2017/1/19
 * email : wangs1992321@gmail.com
 */
public class KithsAdapter extends BaseAdapter implements View.OnClickListener {

    private int largeSize = 0;
    private Resources resources;

    public KithsAdapter(Context activity) {
        super(activity);
        resources = activity.getResources();
        largeSize = (int) resources.getDimension(R.dimen.text_normal);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case 1:
            case 2:
                return R.layout.kith_list_item;
            default:
                return R.layout.kith_list_item_unjoin;
        }
    }

    @Override
    public int getViewType(int position) {
        FamilyMemberInfo info = getItem(position);
        return info.getAttention();
    }

    private void unJoin(View view, FamilyMemberInfo info) {
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(largeSize);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
        SpannableStringBuilder builder = new SpannableStringBuilder();
        ImageView icon = ViewHolder.getView(view, R.id.icon);
        TextView content = ViewHolder.getView(view, R.id.content);
        Button invite = ViewHolder.getView(view, R.id.invite);
        GlideUtil.displayImageCircle(R.drawable.baby_avater, icon);
        int nameLength = info.getUserInfo().getRelationName().length() + 1;
        builder.append(info.getUserInfo().getRelationName()).append("\n").append("未加入");
        builder.setSpan(sizeSpan, 0, nameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(colorSpan, 0, nameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        content.setText(builder);


        invite.setTag(info.getUserInfo().getRelationName().equals("其他成员") ? "" : info.getUserInfo().getRelationName());
        invite.setOnClickListener(this);
    }

    private void join(View view, FamilyMemberInfo info) {
        ImageView createState = ViewHolder.getView(view, R.id.create_state);
        ImageView icon = ViewHolder.getView(view, R.id.icon);
        TextView content = ViewHolder.getView(view, R.id.content);
        ImageView familyMe = ViewHolder.getView(view, R.id.family_me);
        TextView message = ViewHolder.getView(view, R.id.message);
        TextView sendPicCount = ViewHolder.getView(view, R.id.send_pic_count);
        createState.setVisibility(info.getUserInfo().getIsCreator() == 1 ? View.VISIBLE : View.GONE);
        GlideUtil.displayImageCircle(info.getUserInfo().getAvatar(), icon);
        familyMe.setVisibility(info.getUserInfo().getUserId().equals(FastData.getUserId()) ? View.VISIBLE : View.GONE);
        SpannableString ss = new SpannableString(info.getUserInfo().getRelationName());
        content.setText(ss);

        message.setText(String.format("来过%d次  最近：%s", info.getCount(), DateUtil.formatDate(info.getTime())));
        sendPicCount.setText(String.format("已上传%d张照片", info.getPiccount()));
    }

    @Override
    public void initView(View contentView, int position) {
        FamilyMemberInfo info = getItem(position);
        switch (info.getAttention()) {
            case 1:
            case 2:
                join(contentView, info);
                break;
            default:
                unJoin(contentView, info);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invite:
                FragmentBridgeActivity.openInviteFragment(activity, v.getTag().toString());
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
