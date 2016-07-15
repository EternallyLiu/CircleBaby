package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * @author SUN
 * @from 2014/11/11
 * @TODO 广告数据module
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class ADObj implements Parcelable {
    private String adId; // 广告id
    private int adImgWidth; // 广告图片宽度（原图）
    private int adImgHeight; // 广告图片高度（原图）
    private String adImgUrl; // 图片地址
    private String adUri; // 广告执行的uri
    // 调用网页   web:http://**********
    // 调用时光  time:timeId
    // 调用话题  topic:topicId
    // 调用时光书  book:bookId
    // 调用POD预览  pod:podId
    // 调用个人中心  user:userId
    // 调用扫一扫  scan:
    private String showTime; // 停留时间

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public int getAdImgWidth() {
        return adImgWidth;
    }

    public void setAdImgWidth(int adImgWidth) {
        this.adImgWidth = adImgWidth;
    }

    public int getAdImgHeight() {
        return adImgHeight;
    }

    public void setAdImgHeight(int adImgHeight) {
        this.adImgHeight = adImgHeight;
    }

    public String getAdImgUrl() {
        return adImgUrl;
    }

    public void setAdImgUrl(String adImgUrl) {
        this.adImgUrl = adImgUrl;
    }

    public String getAdUri() {
        return adUri;
    }

    public void setAdUri(String adUri) {
        this.adUri = adUri;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.adId);
        dest.writeInt(this.adImgWidth);
        dest.writeInt(this.adImgHeight);
        dest.writeString(this.adImgUrl);
        dest.writeString(this.adUri);
        dest.writeString(this.showTime);
    }

    public ADObj() {
    }

    protected ADObj(Parcel in) {
        this.adId = in.readString();
        this.adImgWidth = in.readInt();
        this.adImgHeight = in.readInt();
        this.adImgUrl = in.readString();
        this.adUri = in.readString();
        this.showTime = in.readString();
    }

    public static final Creator<ADObj> CREATOR = new Creator<ADObj>() {
        public ADObj createFromParcel(Parcel source) {
            return new ADObj(source);
        }

        public ADObj[] newArray(int size) {
            return new ADObj[size];
        }
    };
}
