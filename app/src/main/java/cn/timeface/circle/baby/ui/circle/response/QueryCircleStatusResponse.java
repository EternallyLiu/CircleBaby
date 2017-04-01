package cn.timeface.circle.baby.ui.circle.response;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * 查询圈子状态response 是否解散/被移除
 */
public class QueryCircleStatusResponse extends BaseResponse {

    private int code; // 1: 正常 2 ：被移除 3 ：圈子被解散

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isRemoved() {
        return code == 2;
    }

    public boolean isDisband() {
        return code == 3;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.code);
    }

    public QueryCircleStatusResponse() {
    }

    protected QueryCircleStatusResponse(Parcel in) {
        super(in);
        this.code = in.readInt();
    }

    public static final Creator<QueryCircleStatusResponse> CREATOR = new Creator<QueryCircleStatusResponse>() {
        @Override
        public QueryCircleStatusResponse createFromParcel(Parcel source) {
            return new QueryCircleStatusResponse(source);
        }

        @Override
        public QueryCircleStatusResponse[] newArray(int size) {
            return new QueryCircleStatusResponse[size];
        }
    };
}
