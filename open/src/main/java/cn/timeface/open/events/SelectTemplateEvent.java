package cn.timeface.open.events;

/**
 * Created by zhsheng on 2016/7/11.
 */
public class SelectTemplateEvent {
    public SelectTemplateEvent(int templateId) {
        this.templateId = templateId;
    }

    private int templateId;

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }
}
