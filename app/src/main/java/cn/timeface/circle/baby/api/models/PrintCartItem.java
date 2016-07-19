package cn.timeface.circle.baby.api.models;

import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseModule;
import cn.timeface.circle.baby.api.models.objs.PrintParamObj;
import cn.timeface.circle.baby.api.models.objs.PrintParamResponse;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.constants.TypeConstant;

/**
 * @author YW.SUN
 * @from 2015/5/18
 * @TODO 印刷车item
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PrintCartItem extends BaseModule {
    private String bookId; //时光书编号
    private String title; //标题
    private UserObj author; //用户编号
    private String summary; //描述
    private String tagName; //分类
    private String date; //创建时间，时间戳
    private String coverImage; //封面图片
    private int totalPage; //总页数
    private String authorName; //作者名
    private int bookType; //0 时光书（默认）1 微信书 2 时光圈时光书 3 时光圈通讯录 4 QQ书
    private String printDate; //最新加入印刷车的时间，时间戳
    private int printCode; //8800可印刷 8801可印刷，限软装（4-48页） 8802少于4页，不可印刷 8803超出255页，不可印刷
    private int childNum; //拆分书本数，printCode有错误时，该字段为0
    private List<PrintParamResponse> paramList; //返回数据集
    private List<PrintPropertyPriceObj> printList; //印刷数据列表
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        for (PrintPropertyPriceObj obj : printList) {
            if (printCode == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE
                    || printCode == TypeConstant.PRINT_CODE_LIMIT_MORE
                    || printCode == TypeConstant.PRINT_CODE_LIMIT_LESS) {
                obj.setIsSelect(false);
            } else {
                obj.setIsSelect(isSelect);
            }
        }
        checkAllSelect();
    }

    public boolean checkAllSelect() {
        for (PrintPropertyPriceObj propertyObj : printList) {
            this.isSelect = true;
            if (!propertyObj.isSelect()) {
                this.isSelect = false;
                break;
            }
        }

        return isSelect;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserObj getAuthor() {
        return author;
    }

    public void setAuthor(UserObj author) {
        this.author = author;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCoverImage() {
        return TextUtils.isEmpty(coverImage) ? null : coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getPrintDate() {
        return printDate;
    }

    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }

    public int getPrintCode() {
        return printCode;
    }

    public void setPrintCode(int printCode) {
        this.printCode = printCode;
    }

    public int getChildNum() {
        return childNum;
    }

    public void setChildNum(int childNum) {
        this.childNum = childNum;
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

    public String getPropertyShow(String key, String value) {
        for (PrintParamResponse printParam : paramList) {
            if (key.equals(printParam.getKey())) {
                for (PrintParamObj obj : printParam.getValueList()) {
                    if (value.equals(obj.getValue())) {
                        return obj.getShow();
                    }
                }
            }
        }

        return "";
    }

    public float getLittlePrice() {
        float price = 0;
        for (PrintPropertyPriceObj obj : printList) {
            if (obj.isSelect()) {
                price += obj.getPrice() * obj.getNum();
            }
        }
        return price;
    }


}
