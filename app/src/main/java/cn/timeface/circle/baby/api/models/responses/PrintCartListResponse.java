package cn.timeface.circle.baby.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.api.models.PrintCartItem;
import cn.timeface.circle.baby.api.models.base.BaseResponse;

/**
 * @author YW.SUN
 * @from 2015/5/18
 * @TODO 印刷车列表response
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PrintCartListResponse extends BaseResponse {
    private List<PrintCartItem> dataList; //返回数据集
    private int promotion;//是否有优惠活动
    private String promotionInfo;//现在下单打印时光书，单笔订单（印书总金额）满158元，即送专属台历一本！" ;//优惠活动信息
    private int promotionTerm;//优惠活动需满足的金额
    private float promotionFee;//优惠掉的费用
    private String promotionUrl;//活动详情

    public int getPromotion() {
        return promotion;
    }

    public void setPromotion(int promotion) {
        this.promotion = promotion;
    }

    public String getPromotionInfo() {
        return promotionInfo;
    }

    public void setPromotionInfo(String promotionInfo) {
        this.promotionInfo = promotionInfo;
    }

    public int getPromotionTerm() {
        return promotionTerm;
    }

    public void setPromotionTerm(int promotionTerm) {
        this.promotionTerm = promotionTerm;
    }

    public float getPromotionFee() {
        return promotionFee;
    }

    public void setPromotionFee(float promotionFee) {
        this.promotionFee = promotionFee;
    }

    public String getPromotionUrl() {
        return promotionUrl;
    }

    public void setPromotionUrl(String promotionUrl) {
        this.promotionUrl = promotionUrl;
    }

    public List<PrintCartItem> getDataList() {
        return dataList;
    }

    public void setDataList(List<PrintCartItem> dataList) {
        this.dataList = dataList;
    }

    public float getTotalPrice() {
        float price = 0;
        for (PrintCartItem item : dataList) {
            price += item.getLittlePrice();
        }

        return price;
    }


    public boolean hasPromotion(){
        return promotion == 1;
    }
}
