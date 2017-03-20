package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * 圈二维码详情response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QrcodeResponse extends BaseResponse {
    private String QRcodeUrl;   //二维码图片

    public String getQRcodeUrl() {
        return QRcodeUrl;
    }

    public void setQRcodeUrl(String QRcodeUrl) {
        this.QRcodeUrl = QRcodeUrl;
    }
}
