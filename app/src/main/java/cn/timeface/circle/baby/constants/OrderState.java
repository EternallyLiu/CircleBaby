package cn.timeface.circle.baby.constants;

/**
 * Created by zhsheng on 2016/6/22.
 */
public class OrderState {
    /**
     * 转换订单状态
     */
    public static String processStatus(int status) {
        String statusStr = null;
        switch (status) {
            case TypeConstant.STATUS_CHECKING:
                statusStr = "审核中";
                break;

            case TypeConstant.STATUS_CHECK_FAILED:
                statusStr = "审核未通过";
                break;

            case TypeConstant.STATUS_PRINTING:
                statusStr = "印刷中";
                break;

            case TypeConstant.STATUS_DELIVERING:
                statusStr = "配送中";
                break;

            case 4:
                statusStr = "退款中";
                break;

            case TypeConstant.STATUS_DELIVERY_SUCCESS:
                statusStr = "已送达";
                break;

            case 6:
                statusStr = "已关闭";
                break;

            case TypeConstant.STATUS_TRANSACATION_CLOSED:
                statusStr = "交易关闭";
                break;

            case TypeConstant.STATUS_NOT_CONFIRM:
                statusStr = "未确认";
                break;

            case TypeConstant.STATUS_NOT_PAY:
                statusStr = "未支付";
                break;
        }
        return statusStr;
    }
}
