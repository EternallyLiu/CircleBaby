package cn.timeface.circle.baby.api.models.objs;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class PrintParamResponse extends BaseResponse {
    public static final String KEY_SIZE = "size";
    public static final String KEY_COLOR = "color";
    public static final String KEY_PACK = "pack";
    public static final String KEY_PAPER = "paper";
    private List<PrintParamObj> valueList; //返回数据集
    private String key; //键值
    private String name; //名称

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PrintParamObj> getValueList() {
        // 默认是未选中的状态（之前的操作会对选中状体造成影响）
        for (PrintParamObj paramObj : valueList) {
            paramObj.setIsSelect(false);
        }
        return valueList;
    }

    public void setValueList(List<PrintParamObj> valueList) {
        this.valueList = valueList;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
