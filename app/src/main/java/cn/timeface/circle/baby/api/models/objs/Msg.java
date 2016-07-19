package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lidonglin on 2016/5/16.
 */
public class Msg  implements Parcelable {
    String content;
    int id;
    long time;
    TimeLineObj timeInfo;
    UserObj userInfo;
    int type;
    int isRead;

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public TimeLineObj getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(TimeLineObj timeInfo) {
        this.timeInfo = timeInfo;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeInt(this.id);
        dest.writeLong(this.time);
        dest.writeParcelable(this.timeInfo,flags);
        dest.writeParcelable(this.userInfo,flags);
        dest.writeInt(this.type);
    }

    protected Msg(Parcel in) {
        this.content = in.readString();
        this.id = in.readInt();
        this.time = in.readLong();
        this.timeInfo = in.readParcelable(TimeLineObj.class.getClassLoader());
        this.userInfo = in.readParcelable(UserObj.class.getClassLoader());
        this.type = in.readInt();
    }

    public static final Creator<Msg> CREATOR = new Creator<Msg>() {
        @Override
        public Msg createFromParcel(Parcel source) {
            return new Msg(source);
        }

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }
    };
}
