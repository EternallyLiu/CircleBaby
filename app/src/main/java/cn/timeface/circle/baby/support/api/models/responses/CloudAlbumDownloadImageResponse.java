package cn.timeface.circle.baby.support.api.models.responses;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

public class CloudAlbumDownloadImageResponse extends BaseResponse {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.url);
    }

    public CloudAlbumDownloadImageResponse() {
    }

    protected CloudAlbumDownloadImageResponse(Parcel in) {
        this.url = in.readString();
    }

    public static final Creator<CloudAlbumDownloadImageResponse> CREATOR = new Creator<CloudAlbumDownloadImageResponse>() {
        @Override
        public CloudAlbumDownloadImageResponse createFromParcel(Parcel source) {
            return new CloudAlbumDownloadImageResponse(source);
        }

        @Override
        public CloudAlbumDownloadImageResponse[] newArray(int size) {
            return new CloudAlbumDownloadImageResponse[size];
        }
    };

}
