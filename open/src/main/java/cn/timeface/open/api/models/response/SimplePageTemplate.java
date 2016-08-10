package cn.timeface.open.api.models.response;

/**
 * author: shiyan  Created on 8/10/16.
 * email : sy0725work@gmail.com
 */
public class SimplePageTemplate {
    int template_id;//
    String template_cover;//
    int template_singe_page;//

    public int getTemplateId() {
        return template_id;
    }

    public void setTemplateId(int template_id) {
        this.template_id = template_id;
    }

    public String getTemplateCover() {
        return template_cover;
    }

    public void setTemplateCover(String template_cover) {
        this.template_cover = template_cover;
    }

    public int getTemplateSingePage() {
        return template_singe_page;
    }

    public void setTemplateSingePage(int template_singe_page) {
        this.template_singe_page = template_singe_page;
    }
}
