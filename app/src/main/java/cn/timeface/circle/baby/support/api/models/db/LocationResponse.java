package cn.timeface.circle.baby.support.api.models.db;

import android.text.TextUtils;

/**
 * @author wxw
 * @from 2015/12/15
 * @TODO 地理位置反解 Response
 */
public class LocationResponse {

    /**
     * 返回结果状态值 值为0或1,0表示false；1表示true
     */
    private String status;

    /**
     * 返回状态说明 status为0时，info返回错误原因，否则返回“OK”
     */
    private String info;

    private String infocode;

    /**
     * 逆地理编码列表
     */
    private LocationModel regeocode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public LocationModel getRegeocode() {
        return regeocode;
    }

    public void setRegeocode(LocationModel regeocode) {
        this.regeocode = regeocode;
    }

    public boolean isSuccess() {
        return TextUtils.equals(status, "1");
    }
}
