package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * author: rayboot  Created on 16/1/20.
 * email : sy0725work@gmail.com
 */
public class CommentObj extends BaseObj implements Parcelable {
    long commentDate;       //评论的时间
    int commentId;          //评论id
    String content;         //评论内容
    UserObj toUserInfo;     //被评论人
    UserObj userInfo;       //评论人

    public long getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(long commentDate) {
        this.commentDate = commentDate;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserObj getToUserInfo() {
        return toUserInfo;
    }

    public void setToUserInfo(UserObj toUserInfo) {
        this.toUserInfo = toUserInfo;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.commentDate);
        dest.writeInt(this.commentId);
        dest.writeString(this.content);
        dest.writeParcelable(this.toUserInfo, flags);
        dest.writeParcelable(this.userInfo, flags);
    }

    public CommentObj() {
    }

    protected CommentObj(Parcel in) {
        this.commentDate = in.readLong();
        this.commentId = in.readInt();
        this.content = in.readString();
        this.toUserInfo = in.readParcelable(UserObj.class.getClassLoader());
        this.userInfo = in.readParcelable(UserObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<CommentObj> CREATOR = new Parcelable.Creator<CommentObj>() {
        @Override
        public CommentObj createFromParcel(Parcel source) {
            return new CommentObj(source);
        }

        @Override
        public CommentObj[] newArray(int size) {
            return new CommentObj[size];
        }
    };
}
