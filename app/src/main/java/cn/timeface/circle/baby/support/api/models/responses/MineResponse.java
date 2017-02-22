package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;

/**
 * @author wxw
 * @from 2017/2/22
 * 首页 我的 相关数量
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class MineResponse extends BaseResponse {

    private int workcount;
    private int messagecount;
    private int ordercount;
    private int couponcount;
    private int printcarcount;

    private UserObj userInfo;

    public int getWorkcount() {
        return workcount;
    }

    public void setWorkcount(int workcount) {
        this.workcount = workcount;
    }

    public int getMessagecount() {
        return messagecount;
    }

    public void setMessagecount(int messagecount) {
        this.messagecount = messagecount;
    }

    public int getOrdercount() {
        return ordercount;
    }

    public void setOrdercount(int ordercount) {
        this.ordercount = ordercount;
    }

    public int getCouponcount() {
        return couponcount;
    }

    public void setCouponcount(int couponcount) {
        this.couponcount = couponcount;
    }

    public int getPrintcarcount() {
        return printcarcount;
    }

    public void setPrintcarcount(int printcarcount) {
        this.printcarcount = printcarcount;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }
}
