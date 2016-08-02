package cn.timeface.open.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class TFOUserObj implements Parcelable{

    String unionid;//用户唯一ID
    String nick_name;//用户昵称，会显示为时光书作者
    String phone;//用户手机号码
    String mail;//用户邮件地址
    int gender;//用户性别 1 男 2女
    String avatar;//用户头像绝对路

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public TFOUserObj() {
    }

    public static TFOUserObj genUserObj() {
        String json_data = "{\"gender\":0,\"phone\":\"18656130727\",\"nick_name\":\"Melvin\",\"mail\":\"melvin7@126.com\",\"avatar\":\"http://img1.timeface.cn/uploads/avator/162c581fc30d43cb61f52b962609cbba.png\"}";
        return new Gson().fromJson(json_data, TFOUserObj.class);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.unionid);
        dest.writeString(this.nick_name);
        dest.writeString(this.phone);
        dest.writeString(this.mail);
        dest.writeInt(this.gender);
        dest.writeString(this.avatar);
    }

    protected TFOUserObj(Parcel in) {
        this.unionid = in.readString();
        this.nick_name = in.readString();
        this.phone = in.readString();
        this.mail = in.readString();
        this.gender = in.readInt();
        this.avatar = in.readString();
    }

    public static final Creator<TFOUserObj> CREATOR = new Creator<TFOUserObj>() {
        @Override
        public TFOUserObj createFromParcel(Parcel source) {
            return new TFOUserObj(source);
        }

        @Override
        public TFOUserObj[] newArray(int size) {
            return new TFOUserObj[size];
        }
    };
}
