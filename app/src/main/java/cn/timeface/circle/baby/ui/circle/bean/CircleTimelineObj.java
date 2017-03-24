package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

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

    protected CircleTimelineObj(Parcel in) {
        super(in);
        activityAlbum = in.readParcelable(CircleActivityAlbumObj.class.getClassLoader());
        circleTimelineId = in.readLong();
        commentCount = in.readInt();
        commmentList = in.createTypedArrayList(CircleCommentObj.CREATOR);
        isSync = in.readInt();
        likeCount = in.readInt();
        likeList = in.createTypedArrayList(CircleUserInfo.CREATOR);
        publisher = in.readParcelable(CircleUserInfo.class.getClassLoader());
        recordDate = in.readLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleTimelineObj that = (CircleTimelineObj) o;

        if (getCircleTimelineId() > 0 && getCircleTimelineId() == that.getCircleTimelineId())
            return true;
        return false;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(activityAlbum, flags);
        dest.writeLong(circleTimelineId);
        dest.writeInt(commentCount);
        dest.writeTypedList(commmentList);
        dest.writeInt(isSync);
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

        if (commmentList == null) commmentList = new ArrayList<>(0);
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
        if (likeList == null) likeList = new ArrayList<>(0);
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

    public TFOContentObj toTFOContentObj() {
        TFOContentObj tfoContentObj = new TFOContentObj(DateUtil.formatDate("yyyy-MM-dd hh:kk:mm", getRecordDate()), toTFOResourceObjs());
        tfoContentObj.setContent(getContent());
        return tfoContentObj;
    }

    public List<TFOResourceObj> toTFOResourceObjs() {
        List<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
        for (MediaObj mediaObj : getMediaList()) {
            tfoResourceObjs.add(mediaObj.toTFOResourceObj());
        }
        return tfoResourceObjs;
    }


}
