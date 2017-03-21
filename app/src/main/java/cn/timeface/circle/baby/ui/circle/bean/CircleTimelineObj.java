package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * 圈时光对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleTimelineObj extends CircleContentObj implements Parcelable {
    protected CircleActivityAlbumObj activityAlbum;         //圈活动相册对象
    protected long circleTimelineId;                        //时光id
    protected int commentCount;                             //评论的数量
    protected List<CircleCommentObj> commmentList;          //评论列表
    protected int isSync;                                   //是否同步  0 否 1 是
    protected int likeCount;                                //点赞数量
    protected List<CircleUserInfo> likeList;                //点赞列表
    protected CircleUserInfo publisher;                     //发布者
    protected long recordDate;                              //时光记录时间
    protected int like;                                     //是否赞了该动态 0 否 1 是

    public CircleTimelineObj() {
    }

    @Override
    public String toString() {
        return "CircleTimelineObj{" +
                "activityAlbum=" + activityAlbum +
                ", circleTimelineId=" + circleTimelineId +
                ", commentCount=" + commentCount +
                ", commmentList=" + commmentList +
                ", isSync=" + isSync +
                ", likeCount=" + likeCount +
                ", likeList=" + likeList +
                ", publisher=" + publisher +
                ", recordDate=" + recordDate +
                ", like=" + like +
                '}';
    }

    public CircleActivityAlbumObj getActivityAlbum() {
        return activityAlbum;
    }

    public void setActivityAlbum(CircleActivityAlbumObj activityAlbum) {
        this.activityAlbum = activityAlbum;
    }

    public long getCircleTimelineId() {
        return circleTimelineId;
    }

    public void setCircleTimelineId(long circleTimelineId) {
        this.circleTimelineId = circleTimelineId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<CircleCommentObj> getCommmentList() {
        return commmentList;
    }

    public void setCommmentList(List<CircleCommentObj> commmentList) {
        this.commmentList = commmentList;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<CircleUserInfo> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<CircleUserInfo> likeList) {
        this.likeList = likeList;
    }

    public CircleUserInfo getPublisher() {
        return publisher;
    }

    public void setPublisher(CircleUserInfo publisher) {
        this.publisher = publisher;
    }

    public long getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(long recordDate) {
        this.recordDate = recordDate;
    }

    public int getIsSync() {
        return isSync;
    }

    public void setIsSync(int isSync) {
        this.isSync = isSync;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.activityAlbum, flags);
        dest.writeLong(this.circleTimelineId);
        dest.writeInt(this.commentCount);
        dest.writeTypedList(this.commmentList);
        dest.writeInt(this.isSync);
        dest.writeInt(this.likeCount);
        dest.writeTypedList(this.likeList);
        dest.writeParcelable(this.publisher, flags);
        dest.writeLong(this.recordDate);
        dest.writeInt(this.like);
    }

    protected CircleTimelineObj(Parcel in) {
        super(in);
        this.activityAlbum = in.readParcelable(CircleActivityAlbumObj.class.getClassLoader());
        this.circleTimelineId = in.readLong();
        this.commentCount = in.readInt();
        this.commmentList = in.createTypedArrayList(CircleCommentObj.CREATOR);
        this.isSync = in.readInt();
        this.likeCount = in.readInt();
        this.likeList = in.createTypedArrayList(CircleUserInfo.CREATOR);
        this.publisher = in.readParcelable(CircleUserInfo.class.getClassLoader());
        this.recordDate = in.readLong();
        this.like = in.readInt();
    }

    public static final Creator<CircleTimelineObj> CREATOR = new Creator<CircleTimelineObj>() {
        @Override
        public CircleTimelineObj createFromParcel(Parcel source) {
            return new CircleTimelineObj(source);
        }

        @Override
        public CircleTimelineObj[] newArray(int size) {
            return new CircleTimelineObj[size];
        }
    };
}
