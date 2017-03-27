package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 圈评论对象
 * Created by lidonglin on 2017/3/14.
 */
public class CircleCommentObj extends BaseObj implements Parcelable {
    protected String commentContent;                //评论/回复的内容
    protected long commentDate;                     //评论的日期
    protected long commentId;                       //评论的id
    protected CircleUserInfo commentUserInfo;       //评论的发布人
    protected long toCommentId;                      //被回复的评论的id
    protected CircleUserInfo toCommentUserInfo;     //被回复的评论的发布人

    public CircleCommentObj() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleCommentObj that = (CircleCommentObj) o;

        if (getCommentId() > 0 && getCommentId() == that.getCommentId()) return true;
        return false;
    }


    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public long getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(long commentDate) {
        this.commentDate = commentDate;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public CircleUserInfo getCommentUserInfo() {
        return commentUserInfo;
    }

    public void setCommentUserInfo(CircleUserInfo commentUserInfo) {
        this.commentUserInfo = commentUserInfo;
    }

    public long getToCommentId() {
        return toCommentId;
    }

    public void setToCommentId(long toCommentId) {
        this.toCommentId = toCommentId;
    }

    public CircleUserInfo getToCommentUserInfo() {
        return toCommentUserInfo;
    }

    public void setToCommentUserInfo(CircleUserInfo toCommentUserInfo) {
        this.toCommentUserInfo = toCommentUserInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.commentContent);
        dest.writeLong(this.commentDate);
        dest.writeLong(this.commentId);
        dest.writeParcelable(this.commentUserInfo, flags);
        dest.writeLong(this.toCommentId);
        dest.writeParcelable(this.toCommentUserInfo, flags);
    }

    protected CircleCommentObj(Parcel in) {
        super(in);
        this.commentContent = in.readString();
        this.commentDate = in.readLong();
        this.commentId = in.readLong();
        this.commentUserInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
        this.toCommentId = in.readLong();
        this.toCommentUserInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    public static final Creator<CircleCommentObj> CREATOR = new Creator<CircleCommentObj>() {
        @Override
        public CircleCommentObj createFromParcel(Parcel source) {
            return new CircleCommentObj(source);
        }

        @Override
        public CircleCommentObj[] newArray(int size) {
            return new CircleCommentObj[size];
        }
    };
}
