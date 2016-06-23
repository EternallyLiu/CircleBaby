package cn.timeface.circle.baby.api.models.base;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * @author YW.SUN
 * @from 2015/12/18
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class BasePrintProperty extends BaseModule {
    private String printId; //印刷编号
    private String size; //尺寸
    private int color; //印刷颜色
    private int pack; //装订方式
    private int paper; //纸张
    private int num; //印刷数量

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

    public int getPaper() {
        return paper;
    }

    public void setPaper(int paper) {
        this.paper = paper;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setData(String size, int color, int pack, int paper, int num) {
        this.size = size;
        this.color = color;
        this.pack = pack;
        this.paper = paper;
        this.num = num;
    }
}
