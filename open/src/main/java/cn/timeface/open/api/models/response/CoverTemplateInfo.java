package cn.timeface.open.api.models.response;

import java.util.List;

import cn.timeface.open.api.models.objs.TFOBookContentModel;

/**
 * Created by zhsheng on 2016/7/13.
 */
public class CoverTemplateInfo {
    private List<TFOBookContentModel> content_list;


    public List<TFOBookContentModel> getContent_list() {
        return content_list;
    }

    public void setContent_list(List<TFOBookContentModel> content_list) {
        this.content_list = content_list;
    }
}
