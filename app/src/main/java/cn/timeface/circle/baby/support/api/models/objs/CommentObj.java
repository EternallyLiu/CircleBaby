package cn.timeface.circle.baby.support.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/12.
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentObj that = (CommentObj) o;

        if (commentId != that.commentId) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (commentDate ^ (commentDate >>> 32));
        result = 31 * result + commentId;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (toUserInfo != null ? toUserInfo.hashCode() : 0);
        result = 31 * result + (userInfo != null ? userInfo.hashCode() : 0);
        return result;
    }

    public CommentObj() {
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

    protected CommentObj(Parcel in) {
        this.commentDate = in.readLong();
        this.commentId = in.readInt();
        this.content = in.readString();
        this.toUserInfo = in.readParcelable(UserObj.class.getClassLoader());
        this.userInfo = in.readParcelable(UserObj.class.getClassLoader());
    }

    public static final Creator<CommentObj> CREATOR = new Creator<CommentObj>() {
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
