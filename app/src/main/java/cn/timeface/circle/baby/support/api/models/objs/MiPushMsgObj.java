package cn.timeface.circle.baby.support.api.models.objs;

import java.io.Serializable;

/**
 * 小米推送 扩展数据Model
 */
public class MiPushMsgObj<T> implements Serializable {

    // 1：未读消息 2 ： 删除成员 3 ： 新的教师认证 4：新的作业
    // 5: 成为老师身份 6：取消老师身份 7 : 圈子解散 8 ：最近提交作业
    private int identifier; // 透传消息类型 参见MiPushConstant

    // 推送消息类型 参见MiPushConstant （服务端历史数据结构导致不能共用identifier字段）
    private int dataType;

    private T info;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

}
