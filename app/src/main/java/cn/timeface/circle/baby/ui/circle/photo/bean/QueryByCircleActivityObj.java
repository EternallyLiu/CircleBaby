package cn.timeface.circle.baby.ui.circle.photo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;

/**
 * 活动相册对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryByCircleActivityObj extends BaseObj implements Parcelable {
    protected CircleActivityAlbumObj album;
    protected String albumUrl;

    public QueryByCircleActivityObj() {
    }


    protected QueryByCircleActivityObj(Parcel in) {
        super(in);
        album = in.readParcelable(CircleActivityAlbumObj.class.getClassLoader());
        albumUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(album, flags);
        dest.writeString(albumUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QueryByCircleActivityObj> CREATOR = new Creator<QueryByCircleActivityObj>() {
        @Override
        public QueryByCircleActivityObj createFromParcel(Parcel in) {
            return new QueryByCircleActivityObj(in);
        }

        @Override
        public QueryByCircleActivityObj[] newArray(int size) {
            return new QueryByCircleActivityObj[size];
        }
    };

    public CircleActivityAlbumObj getAlbum() {
        return album;
    }

    public void setAlbum(CircleActivityAlbumObj album) {
        this.album = album;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public void setAlbumUrl(String albumUrl) {
        this.albumUrl = albumUrl;
    }
}
