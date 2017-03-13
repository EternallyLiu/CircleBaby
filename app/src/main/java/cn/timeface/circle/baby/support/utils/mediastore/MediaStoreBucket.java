/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.timeface.circle.baby.support.utils.mediastore;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


public class MediaStoreBucket implements Parcelable {

    private final String mBucketId;
    private final String mBucketName;
    private int totalCount = 0;
    private Uri photoUri;
    private String photoUriString;

    public MediaStoreBucket(String id, String name, Uri photoUri) {
        mBucketId = id;
        mBucketName = name;
        this.photoUri = photoUri;
        if(photoUri != null)
        this.photoUriString = photoUri.toString();
        totalCount = 0;
    }

    public static MediaStoreBucket getAllPhotosBucket(Context context) {
        return new MediaStoreBucket(null, "所有照片", null);
    }

    public String getId() {
        return mBucketId;
    }

    public String getName() {
        return mBucketName;
    }

    @Override
    public String toString() {
        return mBucketName;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
        if(photoUri != null)
            this.photoUriString = photoUri.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBucketId);
        dest.writeString(this.mBucketName);
        dest.writeInt(this.totalCount);
        dest.writeParcelable(this.photoUri, flags);
        dest.writeString(this.photoUriString);
    }

    protected MediaStoreBucket(Parcel in) {
        this.mBucketId = in.readString();
        this.mBucketName = in.readString();
        this.totalCount = in.readInt();
        this.photoUri = in.readParcelable(Uri.class.getClassLoader());
        this.photoUriString = in.readString();
    }

    public static final Creator<MediaStoreBucket> CREATOR = new Creator<MediaStoreBucket>() {
        @Override
        public MediaStoreBucket createFromParcel(Parcel source) {
            return new MediaStoreBucket(source);
        }

        @Override
        public MediaStoreBucket[] newArray(int size) {
            return new MediaStoreBucket[size];
        }
    };
}
