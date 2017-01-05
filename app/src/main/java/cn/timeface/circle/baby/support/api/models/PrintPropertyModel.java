package cn.timeface.circle.baby.support.api.models;

import com.activeandroid.annotation.Column;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * @author YW.SUN
 * @from 2015/6/5
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PrintPropertyModel extends BaseObj {
    @Column(name = "print_id")
    public String printId; //印刷编号

    @Column(name = "print_size")
    public String size; //尺寸

    @Column(name = "print_color")
    public int color; //印刷颜色

    @Column(name = "print_pack")
    public int pack; //装订方式

    @Column(name = "print_num")
    public int num; //印刷数量

    @Column(name = "print_price")
    public float price; //单价

    @Column(name = "is_select")
    public int isSelect;

    public PrintPropertyModel() {
        super();
    }

    public PrintPropertyModel(String printId, String size, int color, int pack, int num, float price,
                              int isSelect) {
        super();
        this.isSelect = isSelect;
        this.printId = printId;
        this.size = size;
        this.color = color;
        this.pack = pack;
        this.num = num;
        this.price = price;

    }

    public boolean isSelect() {
        return isSelect == 1;
    }

    public void setIsSelect(int isSelect) {
        this.isSelect = isSelect;
    }

    public String getPrintId() {
        return printId;
    }

    public void setPrintId(String printId) {
        this.printId = printId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getPack() {
        return pack;
    }

    public void setPack(int pack) {
        this.pack = pack;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
