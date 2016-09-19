package cn.timeface.circle.baby.api.models.objs;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/27.
 */
public class TemplateObj extends BaseObj {
    int paperHeight;
    int paperWidth;
    int paperId;
    String paperUrl;
    String paperName;
    List<TemplateAreaObj> templateList;

    public TemplateObj(int paperHeight, int paperWidth, int paperId, String paperUrl, String paperName, List<TemplateAreaObj> templateList) {
        this.paperHeight = paperHeight;
        this.paperWidth = paperWidth;
        this.paperId = paperId;
        this.paperUrl = paperUrl;
        this.paperName = paperName;
        this.templateList = templateList;
    }

    public int getPaperHeight() {
        return paperHeight;
    }

    public void setPaperHeight(int paperHeight) {
        this.paperHeight = paperHeight;
    }

    public int getPaperWidth() {
        return paperWidth;
    }

    public void setPaperWidth(int paperWidth) {
        this.paperWidth = paperWidth;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public String getPaperUrl() {
        return paperUrl;
    }

    public void setPaperUrl(String paperUrl) {
        this.paperUrl = paperUrl;
    }

    public String getPaperName() {
        return paperName;
    }

    public String getShortPaperName() {
        return paperName.substring(paperName.lastIndexOf("-") + 1);
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public List<TemplateAreaObj> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<TemplateAreaObj> templateList) {
        this.templateList = templateList;
    }
}
