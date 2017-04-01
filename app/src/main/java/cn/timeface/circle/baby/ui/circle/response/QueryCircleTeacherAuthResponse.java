package cn.timeface.circle.baby.ui.circle.response;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;

/**
 * 查询成员是教师认证状态 response
 */
public class QueryCircleTeacherAuthResponse extends BaseResponse {

    private int authentication; // 1: 无需提示  2： 认证为老师  3：取消认证为老师
    private CircleUserInfo userInfo;

    public int getAuthentication() {
        return authentication;
    }

    public void setAuthentication(int authentication) {
        this.authentication = authentication;
    }

    public CircleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CircleUserInfo userInfo) {
        this.userInfo = userInfo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.authentication);
        dest.writeParcelable(this.userInfo, flags);
    }

    public QueryCircleTeacherAuthResponse() {
    }

    protected QueryCircleTeacherAuthResponse(Parcel in) {
        super(in);
        this.authentication = in.readInt();
        this.userInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    public static final Creator<QueryCircleTeacherAuthResponse> CREATOR = new Creator<QueryCircleTeacherAuthResponse>() {
        @Override
        public QueryCircleTeacherAuthResponse createFromParcel(Parcel source) {
            return new QueryCircleTeacherAuthResponse(source);
        }

        @Override
        public QueryCircleTeacherAuthResponse[] newArray(int size) {
            return new QueryCircleTeacherAuthResponse[size];
        }
    };
}
