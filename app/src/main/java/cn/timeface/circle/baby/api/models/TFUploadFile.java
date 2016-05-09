package cn.timeface.circle.baby.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.common.utils.MD5;

/**
 * author: rayboot  Created on 15/10/10.
 * email : sy0725work@gmail.com
 */
public class TFUploadFile extends UploadFileObj {
    public TFUploadFile(String filePath, String folder) {
        super(filePath, folder + "/" + MD5.encode(new File(filePath)) + filePath.substring(filePath.lastIndexOf(".")));
    }

    public TFUploadFile(String filePath) {
        super(filePath);
    }

    @Override
    public String getObjectKey() {
        return objectKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeString(this.objectKey);
    }

    public void setObjectKey(String objectKey){
        this.objectKey = objectKey;
    }

    protected TFUploadFile(Parcel in) {
        super(in.readString(), in.readString());
    }

    public static final Parcelable.Creator<UploadFileObj> CREATOR = new Parcelable.Creator<UploadFileObj>() {
        public TFUploadFile createFromParcel(Parcel source) {
            return new TFUploadFile(source);
        }

        public TFUploadFile[] newArray(int size) {
            return new TFUploadFile[size];
        }
    };
}