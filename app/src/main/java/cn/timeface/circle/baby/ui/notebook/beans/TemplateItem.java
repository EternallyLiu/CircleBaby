package cn.timeface.circle.baby.ui.notebook.beans;

import java.io.Serializable;

import cn.timeface.open.api.bean.obj.TFOSimpleTemplate;

/**
 * Created by JieGuo on 16/11/21.
 */

public class TemplateItem implements Serializable {
    private static long serialVersionUID = 10L;

    public TFOSimpleTemplate template;

    public boolean isSelected = false;
}
