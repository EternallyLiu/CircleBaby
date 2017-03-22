package cn.timeface.circle.baby.ui.circle.groupmembers.adapter;

import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.MenemberInfo;
import cn.timeface.circle.baby.ui.circle.groupmembers.inteface.OnItemClickListener;
import cn.timeface.circle.baby.ui.circle.groupmembers.section.GroupMemberSection;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wangwei on 2017/3/20.
 */

public class GroupMemberAdapter extends BaseSectionQuickAdapter<GroupMemberSection, BaseViewHolder> {
    OnItemClickListener<GroupMemberSection> listener;
    boolean isExpand = false;

    public GroupMemberAdapter(int layoutResId, int sectionHeadResId, List<GroupMemberSection> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder baseViewHolder, GroupMemberSection groupMemberSection) {
        String[] split = groupMemberSection.header.split("&");
        if (isExpand) {
            baseViewHolder.setText(R.id.tv_expand, "收缩全部成员");
        } else {
            baseViewHolder.setText(R.id.tv_expand, "展开全部成员");
        }

        switch (split[0]) {
            case "1":
                baseViewHolder.setVisible(R.id.tv_expand, false);
                baseViewHolder.setText(R.id.tv_header, split[1]);
                baseViewHolder.setText(R.id.tv_count, split[2] + "人");
                break;
            case "2":
                baseViewHolder.setVisible(R.id.tv_expand, true);
                baseViewHolder.setText(R.id.tv_header, split[1]);
                baseViewHolder.setText(R.id.tv_count, split[2] + "人");
                break;
            case "3":
                baseViewHolder.setVisible(R.id.tv_expand, false);
                baseViewHolder.setText(R.id.tv_header, split[1]);
                baseViewHolder.setText(R.id.tv_count, split[2] + "人");
                break;
        }
        baseViewHolder.addOnClickListener(R.id.tv_expand);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, GroupMemberSection groupMemberSection) {
        MenemberInfo menemberInfo = groupMemberSection.t;
        if (menemberInfo.getCircleUserInfo() != null) {
            baseViewHolder.setText(R.id.tv_content, menemberInfo.getCircleUserInfo().getCircleNickName());
            CircleImageView view = baseViewHolder.getView(R.id.iv_content_img);
            CircleImageView viewChild = baseViewHolder.getView(R.id.iv_child_img);
            if (menemberInfo.getCircleUserInfo().getCircleUserType() == 4) {
                Glide.with(mContext)
                        .load(R.drawable.ic_add_member)
                        .into(view);
                viewChild.setVisibility(View.GONE);
            } else {
                viewChild.setVisibility(View.VISIBLE);
                if (menemberInfo.getCircleUserInfo() != null) {
                    Glide.with(mContext)
                            .load(menemberInfo.getCircleUserInfo().getCircleAvatarUrl())
                            .into(view);
                }

                Glide.with(mContext)
                        .load(menemberInfo.getBabyAvatarUrl())
                        .into(viewChild);
            }

            if (menemberInfo.getCircleUserInfo().getCircleUserType() == 1) {
                baseViewHolder.setVisible(R.id.iv_create_member, true);
            } else if (FastData.getCircleUserInfo().getCircleUserId() == menemberInfo.getCircleUserInfo().getCircleUserId()) {
                baseViewHolder.setVisible(R.id.iv_mine_self, true);
            } else {
                baseViewHolder.setVisible(R.id.iv_create_member, false);
                baseViewHolder.setVisible(R.id.iv_mine_self, false);
            }
        }
    }

    public void setIsExpand(boolean isExpand) {
        this.isExpand = isExpand;
        notifyDataSetChanged();
    }
}