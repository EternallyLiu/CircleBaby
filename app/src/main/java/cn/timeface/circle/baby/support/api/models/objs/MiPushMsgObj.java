package cn.timeface.circle.baby.support.api.models.objs;

import java.io.Serializable;

/**
 * 小米推送 扩展数据Model
 */
public class MiPushMsgObj<T> implements Serializable {

    // 推送/透传消息类型 参见MiPushConstant
    private int identifier;

    private T info;

    private String title; // 宝宝名+关系

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
