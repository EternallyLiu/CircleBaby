package cn.timeface.circle.baby.support.api.models.base;

import android.os.Parcel;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class BaseResponse extends BaseObj {
    public int errorCode;
    public String info;
    public int status;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.errorCode);
        dest.writeString(this.info);
        dest.writeInt(this.status);
    }

    public BaseResponse() {
    }

    protected BaseResponse(Parcel in) {
        super(in);
        this.errorCode = in.readInt();
        this.info = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<BaseResponse> CREATOR = new Creator<BaseResponse>() {
        @Override
        public BaseResponse createFromParcel(Parcel source) {
            return new BaseResponse(source);
        }

        @Override
        public BaseResponse[] newArray(int size) {
            return new BaseResponse[size];
        }
    };
}
