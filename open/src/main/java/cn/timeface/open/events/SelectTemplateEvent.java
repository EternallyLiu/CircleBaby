package cn.timeface.open.events;

/**
 * Created by zhsheng on 2016/7/11.
 */
public class SelectTemplateEvent {
    public SelectTemplateEvent(String templateId) {
        this.templateId = templateId;
    }

    private String templateId;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
