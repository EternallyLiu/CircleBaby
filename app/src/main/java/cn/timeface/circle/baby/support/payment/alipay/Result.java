package cn.timeface.circle.baby.support.payment.alipay;

import java.util.HashMap;
import java.util.Map;

public class Result {

    public static final Map<String, String> sResultStatus;

    static {
        sResultStatus = new HashMap<String, String>();
        sResultStatus.put("9000", "操作成功");
        sResultStatus.put("4000", "系统异常");
        sResultStatus.put("4001", "数据格式不正确");
        sResultStatus.put("4003", "该用户绑定的支付宝账户被冻结或不允许支付");
        sResultStatus.put("4004", "该用户已解除绑定");
        sResultStatus.put("4005", "绑定失败或没有绑定");
        sResultStatus.put("4006", "订单支付失败");
        sResultStatus.put("4010", "重新绑定账户");
        sResultStatus.put("6000", "支付服务正在进行升级操作");
        sResultStatus.put("6001", "用户中途取消支付操作");
        sResultStatus.put("7001", "网页支付失败");
        sResultStatus.put("6002", "网络连接异常");
    }
}
