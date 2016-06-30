package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/28.
 */
public class RecommendObj extends BaseObj implements Parcelable {
    String recommendContent;
    String url;
    String bgPicUrl;
    int actionType;

    public String getRecommendContent() {
        return recommendContent;
    }

    public void setRecommendContent(String recommendContent) {
        this.recommendContent = recommendContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBgPicUrl() {
        return bgPicUrl;
    }

    public void setBgPicUrl(String bgPicUrl) {
        this.bgPicUrl = bgPicUrl;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recommendContent);
        dest.writeString(this.url);
        dest.writeString(this.bgPicUrl);
        dest.writeInt(this.actionType);
    }

    public RecommendObj() {
    }

    protected RecommendObj(Parcel in) {
        this.recommendContent = in.readString();
        this.url = in.readString();
        this.bgPicUrl = in.readString();
        this.actionType = in.readInt();
    }

    public static final Creator<RecommendObj> CREATOR = new Creator<RecommendObj>() {
        @Override
        public RecommendObj createFromParcel(Parcel source) {
            return new RecommendObj(source);
        }

        @Override
        public RecommendObj[] newArray(int size) {
            return new RecommendObj[size];
        }
    };

}
