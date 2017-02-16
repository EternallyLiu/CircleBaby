package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocationObj;

/**
 * Created by lidonglin on 2016/5/9.
 */
public class TimeLineObj extends BaseObj implements Parcelable {


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

    public void clearMedias(List<String> urls) {
        MediaObj mediaObj = null;
        for (int i = 0; i < urls.size(); i++) {
            mediaObj = new MediaObj(null, urls.get(i), 0, 0, 0);
            LogUtil.showLog("contain:"+getMediaList().contains(mediaObj));
            if (getMediaList().contains(mediaObj))
            {
                getMediaList().remove(mediaObj);
            }
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
    }

    protected TimeLineObj(Parcel in) {
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
