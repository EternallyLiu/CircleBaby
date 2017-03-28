package cn.timeface.circle.baby.support.api.models.objs;

import android.media.ExifInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * media obj
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class MediaObj extends BaseObj implements Parcelable {
    private String content;//图片描述
    private int h;//长度
    private int w;//宽度
    private int id;
    private String imgUrl;//如果是视频则是视频某一帧图片
    private long length;//视频长度
    private String localPath;//图片本地路径
    private long photographTime;//照片or视频的拍摄时间
    private String videoUrl;//视频url
    private int selected;//成书的时候图片选中状态 1-选中 默认是0-不选中
    private int isCover;//是否是封面
    private int timeId;//时光id
    private long date;//时间戳
    private int imageOrientation;//图片旋转属性
    private int favoritecount;
    private int isFavorite;
    private String localIdentifier;
    private LocationObj location;
    private List<MediaTipObj> tips;
    private MediaTipObj tip;//当前所属哪个标签

    /**
     * {
     * "content": "",
     * "favoritecount": 0,
     * "h": 4000,
     * "id": 520,
     * "imageOrientation": 0,
     * "imgUrl": "http://img1.timeface.cn/baby/6d1c59dbf1122726441bafa82741bc16.jpg",
     * "isCover": 0,
     * "isFavorite": 0,
     * "length": 0,
     * "localIdentifier": "17522",
     * "localPath": "",
     * "location": {},
     * "photographTime": 1490175913000,
     * "selected": 0,
     * "tips": [],
     * "videoUrl": "",
     * "w": 3000
     * }
     */
    public MediaObj() {
    }

    public MediaObj(String imgUrl, String localPath) {
        this.imgUrl = imgUrl;
        this.localPath = localPath;
    }

    //图片
    public MediaObj(String content, String imgUrl, int w, int h, long photographTime) {
        this.content = content;
        this.imgUrl = imgUrl;
        this.w = w;
        this.h = h;
        this.photographTime = photographTime;
    }

    //视频
    public MediaObj(String imgUrl, long length, String videoUrl, long photographTime) {
        this.imgUrl = imgUrl;
        this.length = length;
        this.videoUrl = videoUrl;
        this.photographTime = photographTime;
    }


    public MediaObj(String content, int h, int w, int id, String imgUrl, long length, String localPath, long photographTime, String videoUrl) {
        this.content = content;
        this.h = h;
        this.w = w;
        this.id = id;
        this.imgUrl = imgUrl;
        this.length = length;
        this.localPath = localPath;
        this.photographTime = photographTime;
        this.videoUrl = videoUrl;
    }

    public int getFavoritecount() {
        if (favoritecount < 0)
            favoritecount = 0;
        return favoritecount;
    }

    public void setFavoritecount(int favoritecount) {
        this.favoritecount = favoritecount;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getLocalIdentifier() {
        return localIdentifier;
    }

    public void setLocalIdentifier(String localIdentifier) {
        this.localIdentifier = localIdentifier;
    }

    public LocationObj getLocation() {
        if (location.getLat() == 0 || location.getLog() == 0)
            return null;
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }

    public List<MediaTipObj> getTips() {
        return tips;
    }

    public void setTips(List<MediaTipObj> tips) {
        this.tips = tips;
    }

    public int getImageOrientation() {
        return imageOrientation;
    }

    public void setImageOrientation(int imageOrientation) {
        this.imageOrientation = imageOrientation;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getH() {
        if (imageOrientation == ExifInterface.ORIENTATION_ROTATE_90 || imageOrientation == ExifInterface.ORIENTATION_ROTATE_270)
            return w;
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW() {
        if (imageOrientation == ExifInterface.ORIENTATION_ROTATE_90 || imageOrientation == ExifInterface.ORIENTATION_ROTATE_270)
            return h;
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getPhotographTime() {
        return photographTime;
    }

    public void setPhotographTime(long photographTime) {
        this.photographTime = photographTime;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean select() {
        return selected == 1;
    }

    public MediaTipObj getTip() {
        return tip;
    }

    public void setTip(MediaTipObj tip) {
        this.tip = tip;
    }

    public ImgObj getImgObj() {
        ImgObj imgObj = new ImgObj(getLocalPath(), getImgUrl());
        imgObj.setDateMills(getPhotographTime());
        imgObj.setId(getLocalIdentifier());
        imgObj.setContent(getContent());
        imgObj.setWidth(getW());
        imgObj.setHeight(getH());
        return imgObj;
    }


    public static ArrayList<? extends MediaObj> getMediaArray(List<? extends MediaObj> list) {
        ArrayList<MediaObj> arrayList = new ArrayList<>(0);
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(list.get(i));
        }
        return arrayList;
    }

    public static ArrayList<String> getUrls(List<? extends MediaObj> list) {
        ArrayList<String> urls = new ArrayList<>(0);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() > 0)
                urls.add(list.get(i).getImgUrl());
            else urls.add(list.get(i).getLocalPath());
        }
        return urls;
    }

    public int getIsCover() {
        return isCover;
    }

    public void setIsCover(int isCover) {
        this.isCover = isCover;
    }

    public TFOResourceObj toTFOResourceObj() {
        return new TFOResourceObj(id + "", imgUrl, w, h, imageOrientation, "");
    }

    public boolean isVideo() {
        if (TextUtils.isEmpty(getVideoUrl()))
            return false;
        else return true;
    }

    @Override
    public String toString() {
        return "MediaObj{" +
                "content='" + content + '\'' +
                ", h=" + h +
                ", w=" + w +
                ", id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                ", length=" + length +
                ", localPath='" + localPath + '\'' +
                ", photographTime=" + photographTime +
                ", videoUrl='" + videoUrl + '\'' +
                ", selected=" + selected +
                ", isCover=" + isCover +
                ", timeId=" + timeId +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof MediaObj) {
            MediaObj mediaObj = (MediaObj) o;
            //mediaid 一样
            if (mediaObj != null && (getId() != 0 && getId() == mediaObj.getId()))
                return true;
            else if (mediaObj != null && getId() <= 0) {
                //判断本地唯一标记是否相同，相同那么就返回true 否则比较本地地址是否为空，不为空返回地址是否相同
                return !TextUtils.isEmpty(getLocalIdentifier()) && !TextUtils.isEmpty(mediaObj.getLocalIdentifier())
                        ? getLocalIdentifier().equals(mediaObj.getLocalIdentifier())
                        : !TextUtils.isEmpty(getLocalPath()) && !TextUtils.isEmpty(mediaObj.getLocalPath()) ? getLocalPath().equals(mediaObj.getLocalPath()) : !TextUtils.isEmpty(getImgUrl()) && !TextUtils.isEmpty(mediaObj.getImgUrl()) ? getImgUrl().equals(mediaObj.getImgUrl()) : false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + h;
        result = 31 * result + w;
        result = 31 * result + id;
        result = 31 * result + (imgUrl != null ? imgUrl.hashCode() : 0);
        result = 31 * result + (int) (length ^ (length >>> 32));
        result = 31 * result + (localPath != null ? localPath.hashCode() : 0);
        result = 31 * result + (int) (photographTime ^ (photographTime >>> 32));
        result = 31 * result + (videoUrl != null ? videoUrl.hashCode() : 0);
        result = 31 * result + selected;
        result = 31 * result + isCover;
        result = 31 * result + timeId;
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + imageOrientation;
        result = 31 * result + favoritecount;
        result = 31 * result + isFavorite;
        result = 31 * result + (localIdentifier != null ? localIdentifier.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (tips != null ? tips.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.content);
        dest.writeInt(this.h);
        dest.writeInt(this.w);
        dest.writeInt(this.id);
        dest.writeString(this.imgUrl);
        dest.writeLong(this.length);
        dest.writeString(this.localPath);
        dest.writeLong(this.photographTime);
        dest.writeString(this.videoUrl);
        dest.writeInt(this.selected);
        dest.writeInt(this.isCover);
        dest.writeInt(this.timeId);
        dest.writeLong(this.date);
        dest.writeInt(this.imageOrientation);
        dest.writeInt(this.favoritecount);
        dest.writeInt(this.isFavorite);
        dest.writeString(this.localIdentifier);
        dest.writeParcelable(this.location, flags);
        dest.writeTypedList(this.tips);
        dest.writeParcelable(this.tip, flags);
    }

    protected MediaObj(Parcel in) {
        super(in);
        this.content = in.readString();
        this.h = in.readInt();
        this.w = in.readInt();
        this.id = in.readInt();
        this.imgUrl = in.readString();
        this.length = in.readLong();
        this.localPath = in.readString();
        this.photographTime = in.readLong();
        this.videoUrl = in.readString();
        this.selected = in.readInt();
        this.isCover = in.readInt();
        this.timeId = in.readInt();
        this.date = in.readLong();
        this.imageOrientation = in.readInt();
        this.favoritecount = in.readInt();
        this.isFavorite = in.readInt();
        this.localIdentifier = in.readString();
        this.location = in.readParcelable(LocationObj.class.getClassLoader());
        this.tips = in.createTypedArrayList(MediaTipObj.CREATOR);
        this.tip = in.readParcelable(MediaTipObj.class.getClassLoader());
    }

    public static final Creator<MediaObj> CREATOR = new Creator<MediaObj>() {
        @Override
        public MediaObj createFromParcel(Parcel source) {
            return new MediaObj(source);
        }

        @Override
        public MediaObj[] newArray(int size) {
            return new MediaObj[size];
        }
    };
}
