package cn.timeface.circle.baby.ui.settings.response;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.settings.beans.MessageBean;

/**
 * author : wangshuai Created on 2017/4/13
 * email : wangs1992321@gmail.com
 */
public class MyMessageResponse extends BaseResponse{

    private List<MessageBean> dataList;

    public List<MessageBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<MessageBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.dataList);
    }

    public MyMessageResponse() {
    }

    protected MyMessageResponse(Parcel in) {
        super(in);
        this.dataList = new ArrayList<MessageBean>();
        in.readList(this.dataList, MessageBean.class.getClassLoader());
    }

    public static final Creator<MyMessageResponse> CREATOR = new Creator<MyMessageResponse>() {
        @Override
        public MyMessageResponse createFromParcel(Parcel source) {
            return new MyMessageResponse(source);
        }

        @Override
        public MyMessageResponse[] newArray(int size) {
            return new MyMessageResponse[size];
        }
    };
}
