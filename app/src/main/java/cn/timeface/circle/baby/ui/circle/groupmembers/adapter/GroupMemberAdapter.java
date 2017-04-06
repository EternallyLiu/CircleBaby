package cn.timeface.circle.baby.ui.circle.groupmembers.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
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
            baseViewHolder.setText(R.id.tv_expand, "收起");
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
                baseViewHolder.setText(R.id.tv_header, split[1]);
                baseViewHolder.setText(R.id.tv_count, split[2] + "人");
                if (Integer.parseInt(split[2]) > 9) {
                    baseViewHolder.setVisible(R.id.tv_expand, true);
                } else {
                    baseViewHolder.setVisible(R.id.tv_expand, false);
                }
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
        if (menemberInfo.getUserInfo() != null) {
            baseViewHolder.setText(R.id.tv_content, menemberInfo.getUserInfo().getCircleNickName());
            CircleImageView view = baseViewHolder.getView(R.id.iv_content_img);
            CircleImageView viewChild = baseViewHolder.getView(R.id.iv_child_img);
            if (menemberInfo.getUserInfo().getCircleUserType() == 4) {
                GlideUtil.displayImage(null,view,R.drawable.ic_add_member);
//                Glide.with(mContext)
//                        .load(R.drawable.ic_add_member)
//                        .into(view);
                viewChild.setVisibility(View.GONE);
            } else {
                viewChild.setVisibility(View.VISIBLE);
                if (menemberInfo.getUserInfo() != null) {
                    GlideUtil.displayImage(menemberInfo.getUserInfo().getCircleAvatarUrl(),view);
//                    Glide.with(mContext)
//                            .load(menemberInfo.getUserInfo().getCircleAvatarUrl())
//                            .into(view);
                }
                GlideUtil.displayImage(menemberInfo.getBabyBrief().getBabyAvatarUrl(),viewChild);
//                Glide.with(mContext)
//                        .load(menemberInfo.getBabyBrief().getBabyAvatarUrl())
//                        .into(viewChild);
            }

            if (menemberInfo.getUserInfo().getCircleUserId() == GrowthCircleObj.getInstance().getCircleCreateUserId()) {
                baseViewHolder.setVisible(R.id.iv_create_member, true);
            } else if (FastData.getCircleUserInfo().getCircleUserId() == menemberInfo.getUserInfo().getCircleUserId()) {
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