package cn.timeface.circle.baby.support.api.models.responses;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * 查询作品中的扩展信息
 * author : YW.SUN Created on 2017/3/3
 * email : sunyw10@gmail.com
 */
public class QueryProductionExtraResponse extends BaseResponse {
    private String extra;

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
