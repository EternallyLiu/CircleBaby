package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lidonglin on 2016/5/16.
 */
public class SystemMsg implements Parcelable {

    String avatar;     //系统图标
    String content;    //内容
    String name;       //系统名称
    long time;         //时间戳
    long dataId;        //数据id(订单uid)
    int msgType;       //0订单未支付 1成功提交订单申请 2订单审核未通过 3订单配送中 4印刷书已送达 5新活动上线
    int id;            //消息id
    int isRead;
    String url;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.content);
        dest.writeString(this.name);
        dest.writeLong(this.time);
        dest.writeLong(this.dataId);
        dest.writeInt(this.msgType);
        dest.writeInt(this.id);
        dest.writeInt(this.isRead);
        dest.writeString(this.url);
    }

    protected SystemMsg(Parcel in) {
        this.avatar = in.readString();
        this.content = in.readString();
        this.name = in.readString();
        this.time = in.readLong();
        this.dataId = in.readLong();
        this.msgType = in.readInt();
        this.id = in.readInt();
        this.isRead = in.readInt();
        this.url = in.readString();
    }

    public static final Creator<SystemMsg> CREATOR = new Creator<SystemMsg>() {
        @Override
        public SystemMsg createFromParcel(Parcel source) {
            return new SystemMsg(source);
        }

        @Override
        public SystemMsg[] newArray(int size) {
            return new SystemMsg[size];
        }
    };
}
