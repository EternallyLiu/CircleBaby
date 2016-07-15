package cn.timeface.circle.baby.api.models.base;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class BaseResponse extends BaseObj {
    int errorCode;
    public String info;
    int status;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean success() {
        return status == 1;
    }
}
