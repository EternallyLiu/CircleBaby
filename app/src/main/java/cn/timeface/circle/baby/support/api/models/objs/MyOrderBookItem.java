package cn.timeface.circle.baby.support.api.models.objs;

import android.text.TextUtils;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by zhsheng on 2016/6/21.
 */
public class MyOrderBookItem extends BaseObj {
    private String bookId; //时光书编号
    private String bookName; //时光书名
    private int bookType; //时光书类型 0 时光书（默认） 1 微信书 2 时光圈时光书 3 时光圈通讯录 4 QQ书
    private long lastdate; //时光书更新时间，时间戳
    private String coverImage; // 封面图片
    private float price; // 价格
    private int childNum;//拆分书本数，默认0

    private List<PrintParamResponse> paramList; //返回数据集
    private List<PrintPropertyPriceObj> printList; //印刷数据列表

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public long getLastdate() {
        return lastdate;
    }

    public void setLastdate(long lastdate) {
        this.lastdate = lastdate;
    }

    public String getCoverImage() {
        return TextUtils.isEmpty(coverImage) ? null : coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<PrintParamResponse> getParamList() {
        return paramList;
    }

    public void setParamList(List<PrintParamResponse> paramList) {
        this.paramList = paramList;
    }

    public List<PrintPropertyPriceObj> getPrintList() {
        return printList;
    }

    public void setPrintList(List<PrintPropertyPriceObj> printList) {
        this.printList = printList;
    }

    public int getChildNum() {
        return childNum;
    }

    public void setChildNum(int childNum) {
        this.childNum = childNum;
    }

    /**
     * 获取书名（带拆分信息）
     */
    public String getBookNameAndChildNum() {
        return childNum > 0 ? bookName + "（已拆分为" + childNum + "本）" : bookName;
    }

    public String getPropertyShow(String key, String value) {
        if (paramList != null) {
            for (PrintParamResponse printParam : paramList) {
                if (key.equals(printParam.getKey())) {
                    for (PrintParamObj obj : printParam.getValueList()) {
                        if (value.equals(obj.getValue())) {
                            return obj.getShow();
                        }
                    }
                }
            }
        }
        return "";
    }
}
