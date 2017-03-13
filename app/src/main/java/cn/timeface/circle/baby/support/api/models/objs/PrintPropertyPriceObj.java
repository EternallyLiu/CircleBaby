package cn.timeface.circle.baby.support.api.models.objs;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BasePrintProperty;

/**
 * Created by zhsheng on 2016/6/21.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PrintPropertyPriceObj extends BasePrintProperty {
    private float price; //单价
    private String date; //加入印刷车时间
    private String calendar;
    private boolean isSelect = true;

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setData(String size, int color, int pack, int paper, int num, float price) {
        super.setData(size, color, pack, paper, num);
        this.price = price;
    }
}
