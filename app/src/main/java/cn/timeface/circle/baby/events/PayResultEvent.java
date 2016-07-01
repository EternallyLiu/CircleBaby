package cn.timeface.circle.baby.events;

/**
 * 支付结果event
 *
 * @author yusen (QQ:1522289706)
 * @from 2014年9月12日上午9:45:23
 * @TODO 支付结果event
 */
public class PayResultEvent {
    public String resultCode;
    public PayType type;
    public boolean isClose; //支付成功界面点击返回书架或查看订单

    public PayResultEvent(PayType type, String resultCode) {
        this.type = type;
        this.resultCode = resultCode;
    }

    public PayResultEvent() {
    }

    public PayResultEvent(boolean isClose) {
        this.isClose = isClose;
    }

    public boolean paySuccess() {
        if (this.type == PayType.TB && "9000".equals(resultCode)) {
            return true;
        } else if (this.type == PayType.WX && "0".equals(resultCode)) {
            return true;
        } else if (this.type == PayType.POINT && "200".equals(resultCode)) {
            return true;
        }
        return false;
    }

    public enum PayType {
        WX,
        TB,
        POINT
    }
}
