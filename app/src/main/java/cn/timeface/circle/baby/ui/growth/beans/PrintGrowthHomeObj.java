package cn.timeface.circle.baby.ui.growth.beans;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 首页数据obj
 * author : YW.SUN Created on 2017/1/11
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PrintGrowthHomeObj {
    private int bookType;//书本类型
    private String desc;//模块的描述
    private String size;//书本打印后的大小
    private String url;

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
