package cn.timeface.circle.baby.ui.circle.groupmembers.section;

import com.chad.library.adapter.base.entity.SectionEntity;

import cn.timeface.circle.baby.ui.circle.groupmembers.bean.MenemberInfo;

/**
 * Created by wangwei on 2017/3/20.
 */

public class GroupMemberSection extends SectionEntity<MenemberInfo> {
    public GroupMemberSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public GroupMemberSection(MenemberInfo menemberInfo) {
        super(menemberInfo);
    }
}
