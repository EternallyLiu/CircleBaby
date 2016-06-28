package cn.timeface.circle.baby.api.models.responses;


import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.UserObj;

/**获取当前作品印刷状态
 * Created by lidonglin on 2016/6/28.
 */
public class PrintStatusResponse extends BaseResponse {
    int printCode;

    public int getPrintCode() {
        return printCode;
    }

    public void setPrintCode(int printCode) {
        this.printCode = printCode;
    }
}
