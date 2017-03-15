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
    protected List<CircleActivityAlbumObj> activityAlbum;   //圈活动相册对象
    protected int circleTimelineId;                         //时光id
    protected int commentCount;                             //评论的数量
    protected List<CircleCommentObj> commmentList;          //评论列表
    protected boolean isSync;                               //是否同步
    protected int likeCount;                                //点赞数量
    protected List<CircleUserInfo> likeList;                //点赞列表
    protected CircleUserInfo publisher;                     //发布者
    protected long recordDate;                              //时光记录时间

    public CircleTimelineObj() {
    }

    protected CircleTimelineObj(Parcel in) {
        super(in);
        activityAlbum = in.createTypedArrayList(CircleActivityAlbumObj.CREATOR);
        circleTimelineId = in.readInt();
        commentCount = in.readInt();
        commmentList = in.createTypedArrayList(CircleCommentObj.CREATOR);
        isSync = in.readByte() != 0;
        likeCount = in.readInt();
        likeList = in.createTypedArrayList(CircleUserInfo.CREATOR);
        publisher = in.readParcelable(CircleUserInfo.class.getClassLoader());
        recordDate = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(activityAlbum);
        dest.writeInt(circleTimelineId);
        dest.writeInt(commentCount);
        dest.writeTypedList(commmentList);
        dest.writeByte((byte) (isSync ? 1 : 0));
        dest.writeInt(likeCount);
        dest.writeTypedList(likeList);
        dest.writeParcelable(publisher, flags);
        dest.writeLong(recordDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleTimelineObj> CREATOR = new Creator<CircleTimelineObj>() {
        @Override
        public CircleTimelineObj createFromParcel(Parcel in) {
            return new CircleTimelineObj(in);
        }

        @Override
        public CircleTimelineObj[] newArray(int size) {
            return new CircleTimelineObj[size];
        }
    };

    public List<CircleActivityAlbumObj> getActivityAlbum() {
        return activityAlbum;
    }

    public void setActivityAlbum(List<CircleActivityAlbumObj> activityAlbum) {
        this.activityAlbum = activityAlbum;
    }

    public int getCircleTimelineId() {
        return circleTimelineId;
    }

    public void setCircleTimelineId(int circleTimelineId) {
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

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
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
}
