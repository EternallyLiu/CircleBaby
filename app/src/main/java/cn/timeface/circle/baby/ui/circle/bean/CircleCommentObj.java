package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 *  圈评论对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleCommentObj extends BaseObj implements Parcelable {
    protected String commentContent;                //评论/回复的内容
    protected long commentDate;                     //评论的日期
    protected long commentId;                       //评论的id
    protected CircleUserInfo commentUserInfo;       //评论的发布人
    protected long toCommentId;                      //被回复的评论的id
    protected CircleUserInfo toCommentUserInfo;     //被回复的评论的发布人

    public CircleCommentObj() {
    }

    protected CircleCommentObj(Parcel in) {
        super(in);
        commentContent = in.readString();
        commentDate = in.readLong();
        commentId = in.readLong();
        commentUserInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
        toCommentId = in.readLong();
        toCommentUserInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(commentContent);
        dest.writeLong(commentDate);
        dest.writeLong(commentId);
        dest.writeParcelable(commentUserInfo, flags);
        dest.writeLong(toCommentId);
        dest.writeParcelable(toCommentUserInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleCommentObj> CREATOR = new Creator<CircleCommentObj>() {
        @Override
        public CircleCommentObj createFromParcel(Parcel in) {
            return new CircleCommentObj(in);
        }

        @Override
        public CircleCommentObj[] newArray(int size) {
            return new CircleCommentObj[size];
        }
    };

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
}
