package cn.timeface.circle.baby.events;

        import cn.timeface.circle.baby.ui.circle.groupmembers.bean.MenemberInfo;

/**
 * Created by wangwei on 2017/3/22.
 */

public class UpdateMemberDetailEvent {
    private MenemberInfo menemberInfo;

    public MenemberInfo getMenemberInfo() {
        return menemberInfo;
    }

    public void setMenemberInfo(MenemberInfo menemberInfo) {
        this.menemberInfo = menemberInfo;
    }

    public UpdateMemberDetailEvent(MenemberInfo menemberInfo) {
        this.menemberInfo = menemberInfo;

    }
}
