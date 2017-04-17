package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocationObj;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * Created by lidonglin on 2016/5/9.
 */
public class TimeLineObj extends BaseObj implements Parcelable, Comparable {


    String age;                             //年龄描述
    String dateEx;                          //时间附属信息
    UserObj author;                 //作者
    int commentCount;               //评论个数
    List<CommentObj> commentList;   //评论列表
    long date;                      //创建时间
    long dotime;                    //追溯时间
    int like;                       //是否已赞  0 否 1 是
    int likeCount;                  //赞的个数
    List<MediaObj> mediaList;       //图片列表
    List<UserObj> likeList;         //赞用户列表
    String milestone;               //里程碑
    int milestoneId;                //里程碑id
    int timeId;
    int type;                       //0 照片 1 视频 2 日记 3 识图
    String content;                 //时光内容
    NearLocationObj locationInfo;
    String growthCricleName;
    private int allDetailsListPosition;

    public int getAllDetailsListPosition() {
        return allDetailsListPosition;
    }

    public void setAllDetailsListPosition(int allDetailsListPosition) {
        this.allDetailsListPosition = allDetailsListPosition;
    }

    public long getDotime() {
        return dotime;
    }

    public void setDotime(long dotime) {
        this.dotime = dotime;
    }

    public UserObj getAuthor() {
        return author;
    }

    public void setAuthor(UserObj author) {
        this.author = author;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getLikeCount() {
        if (likeCount < 0) likeCount = 0;
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<MediaObj> getMediaList() {
        if (mediaList == null)
            mediaList = new ArrayList<>(0);
        return mediaList;
    }

    public ArrayList<MediaObj> getMediaArray() {
        if (mediaList == null)
            mediaList = new ArrayList<>(0);
        ArrayList<MediaObj> list = null;
        try {
            list = (ArrayList<MediaObj>) this.mediaList;
        } catch (Exception e) {
            list = new ArrayList<>();
            list.addAll(mediaList);
        }
        return list;
    }

    public ArrayList<String> getUrls() {
        ArrayList<String> list = new ArrayList<>(0);
        ArrayList<MediaObj> array = getMediaArray();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.get(i).getImgUrl());
        }
        return list;
    }

    public void setMediaList(List<MediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<CommentObj> getCommentList() {
        if (commentList == null)
            commentList = new ArrayList<>();
        return commentList;
    }

    public void setCommentList(List<CommentObj> commentList) {
        this.commentList = commentList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<UserObj> getLikeList() {
        if (likeList == null)
            likeList = new ArrayList<>();
        return likeList;
    }

    public void setLikeList(List<UserObj> likeList) {
        this.likeList = likeList;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }

    public TimeLineObj() {
    }

    public NearLocationObj getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(NearLocationObj locationInfo) {
        this.locationInfo = locationInfo;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDateEx() {
        return dateEx;
    }

    public void setDateEx(String dateEx) {
        this.dateEx = dateEx;
    }

    public List<TFOResourceObj> toTFOResourceObjs() {
        List<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
        for (MediaObj mediaObj : getMediaList()) {
            tfoResourceObjs.add(mediaObj.toTFOResourceObj());
        }
        return tfoResourceObjs;
    }

    public TFOContentObj toTFOContentObj() {
        TFOContentObj tfoContentObj = new TFOContentObj(DateUtil.formatDate("yyyy-MM-dd hh:kk:mm", getDotime()), toTFOResourceObjs());
        tfoContentObj.setContent(getContent());
        return tfoContentObj;
    }

    public String getGrowthCricleName() {
        return growthCricleName;
    }

    public void setGrowthCricleName(String growthCricleName) {
        this.growthCricleName = growthCricleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeLineObj that = (TimeLineObj) o;

        if (timeId > 0 && timeId == that.timeId) return true;
        if (commentCount != that.commentCount) return false;
        if (date != that.date) return false;
        if (dotime != that.dotime) return false;
        if (like != that.like) return false;
        if (likeCount != that.likeCount) return false;
        if (milestoneId != that.milestoneId) return false;
        if (type != that.type) return false;
        if (allDetailsListPosition != that.allDetailsListPosition) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (dateEx != null ? !dateEx.equals(that.dateEx) : that.dateEx != null) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (commentList != null ? !commentList.equals(that.commentList) : that.commentList != null)
            return false;
        if (mediaList != null ? !mediaList.equals(that.mediaList) : that.mediaList != null)
            return false;
        if (likeList != null ? !likeList.equals(that.likeList) : that.likeList != null)
            return false;
        if (milestone != null ? !milestone.equals(that.milestone) : that.milestone != null)
            return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        return locationInfo != null ? locationInfo.equals(that.locationInfo) : that.locationInfo == null;

    }

    @Override
    public int hashCode() {
        int result = age != null ? age.hashCode() : 0;
        result = 31 * result + (dateEx != null ? dateEx.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + commentCount;
        result = 31 * result + (commentList != null ? commentList.hashCode() : 0);
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (int) (dotime ^ (dotime >>> 32));
        result = 31 * result + like;
        result = 31 * result + likeCount;
        result = 31 * result + (mediaList != null ? mediaList.hashCode() : 0);
        result = 31 * result + (likeList != null ? likeList.hashCode() : 0);
        result = 31 * result + (milestone != null ? milestone.hashCode() : 0);
        result = 31 * result + milestoneId;
        result = 31 * result + timeId;
        result = 31 * result + type;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (locationInfo != null ? locationInfo.hashCode() : 0);
        result = 31 * result + allDetailsListPosition;
        return result;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        TimeLineObj another = (TimeLineObj) o;
        if (getDotime() > another.getDotime()) {
            return 1;
        } else if (getDotime() == another.getDotime()) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.age);
        dest.writeString(this.dateEx);
        dest.writeParcelable(this.author, flags);
        dest.writeInt(this.commentCount);
        dest.writeTypedList(this.commentList);
        dest.writeLong(this.date);
        dest.writeLong(this.dotime);
        dest.writeInt(this.like);
        dest.writeInt(this.likeCount);
        dest.writeTypedList(this.mediaList);
        dest.writeTypedList(this.likeList);
        dest.writeString(this.milestone);
        dest.writeInt(this.milestoneId);
        dest.writeInt(this.timeId);
        dest.writeInt(this.type);
        dest.writeString(this.content);
        dest.writeParcelable(this.locationInfo, flags);
        dest.writeString(this.growthCricleName);
        dest.writeInt(this.allDetailsListPosition);
    }

    protected TimeLineObj(Parcel in) {
        super(in);
        this.age = in.readString();
        this.dateEx = in.readString();
        this.author = in.readParcelable(UserObj.class.getClassLoader());
        this.commentCount = in.readInt();
        this.commentList = in.createTypedArrayList(CommentObj.CREATOR);
        this.date = in.readLong();
        this.dotime = in.readLong();
        this.like = in.readInt();
        this.likeCount = in.readInt();
        this.mediaList = in.createTypedArrayList(MediaObj.CREATOR);
        this.likeList = in.createTypedArrayList(UserObj.CREATOR);
        this.milestone = in.readString();
        this.milestoneId = in.readInt();
        this.timeId = in.readInt();
        this.type = in.readInt();
        this.content = in.readString();
        this.locationInfo = in.readParcelable(NearLocationObj.class.getClassLoader());
        this.growthCricleName = in.readString();
        this.allDetailsListPosition = in.readInt();
    }

    public static final Creator<TimeLineObj> CREATOR = new Creator<TimeLineObj>() {
        @Override
        public TimeLineObj createFromParcel(Parcel source) {
            return new TimeLineObj(source);
        }

        @Override
        public TimeLineObj[] newArray(int size) {
            return new TimeLineObj[size];
        }
    };
}
