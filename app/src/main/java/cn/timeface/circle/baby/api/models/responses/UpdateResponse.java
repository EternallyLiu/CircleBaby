package cn.timeface.circle.baby.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.api.models.base.BaseResponse;


/**
 * @author rayboot
 * @from 14-4-28 19:13
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class UpdateResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    private int version; // 最新版本
    private String downUrl; // 安装包下载地址(只对Android使用)
    private int enforce; // 是否强制升级

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public int getEnforce() {
        return enforce;
    }

    public void setEnforce(int enforce) {
        this.enforce = enforce;
    }
}
